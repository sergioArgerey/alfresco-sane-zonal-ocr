package net.zylk.alfresco.addons.ocr.jfreeSane.webscript;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import net.zylk.alfresco.addons.ocr.jfreeSane.device.JfreeSaneDeviceJson;
import net.zylk.alfresco.addons.ocr.jfreeSane.device.manager.ScanManager;
import net.zylk.alfresco.addons.ocr.jfreeSane.profile.manager.OcrWithProfilesManager;
import net.zylk.alfresco.addons.ocr.jfreeSane.profile.Profile;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import au.com.southsky.jfreesane.SaneDevice;
import au.com.southsky.jfreesane.SaneException;
import au.com.southsky.jfreesane.SaneSession;
import au.com.southsky.jfreesane.SaneStatus;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * The type Send doc to scan web script.
 */
public class SendDocToScanWebScript extends DeclarativeWebScript {
	private static Log log = LogFactory.getLog(SendDocToScanWebScript.class);

	private NamespacePrefixResolver namespacePrefixResolver;
	private ServiceRegistry m_serviceRegistry;

	private Properties properties;

	private SaneDevice saneDevice = null;
	private SaneSession session = null;
	
	

	private static final String FILE_TYPE_PROPERTY = "zk.jsaneocr.file.type";

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest request, Status status, Cache cache) {

		Map<String, Object> model = new HashMap<String, Object>();

		ArrayList<BufferedImage> images = null;
		NodeRef folderNodeRef = null;

		JSONObject ocrNodeRefJSON = null;
		boolean transformToPDF = false;
		boolean multipagePDF = false;

		// Con la estructura de opciones del json, cargamos las opciones del perfil en el scanner y ejecutamos el escanear.
		try {

			JSONObject jsonIn = new JSONObject(convertStreamToString(request.getContent().getInputStream()));

			NodeRef ocrTypeDocumentNodeRef = new NodeRef(jsonIn.getString("select-ocrTypeDocument"));

			ContentReader reader = m_serviceRegistry.getContentService().getReader(ocrTypeDocumentNodeRef,
					ContentModel.PROP_CONTENT);
			String ocrJsonStr = reader.getContentString();

			ocrNodeRefJSON = new JSONObject(ocrJsonStr);

			String noderefStr = request.getParameter("noderef");
			log.debug("[Zylk] Carpeta de escaneado: " + noderefStr);
			folderNodeRef = new NodeRef(noderefStr);

			String scanNodeRefStr = jsonIn.getString("select-scanner");

			log.debug("[Zylk] Scanner seleccionado: " + noderefStr);

			if (jsonIn.has("check-pdf-transform")) {
				log.debug("[Zylk] Transformamos a PDF: " + jsonIn.getString("check-pdf-transform"));
				transformToPDF = jsonIn.has("check-pdf-transform");
			}

			if (jsonIn.has("check-pdf-multipage")) {
				log.debug("[Zylk] Creamos PDF multihoja: " + jsonIn.getString("check-pdf-multipage"));
				multipagePDF = jsonIn.has("check-pdf-multipage");
			}

			images = scanDocs(request, scanNodeRefStr);
		} catch (Exception e) {
			log.error("Error Grave Escaneando");
			model.put("status", "NOK");
			model.put("message", "Error Grave guardando los documentos");
			e.printStackTrace();
			throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Error grave escaneando");
		}

		// Guardamos las imagenes en el nodo de alfresco.
		try {
			// Guardar imagenes en el nodo.
			// Ocr

			ArrayList<NodeRef> imageNodes = new ArrayList<NodeRef>();

			for (BufferedImage image : images) {
				imageNodes.add(saveFile(image, folderNodeRef, ocrNodeRefJSON, transformToPDF));
			}

			if (transformToPDF || multipagePDF) {
				transformImagesToPDF(imageNodes, multipagePDF, folderNodeRef);
			}
		} catch (Exception e) {
			log.error("Error Grave guardando los documentos");
			model.put("status", "NOK");
			model.put("message", "Error Grave guardando los documentos");
			e.printStackTrace();
			throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Error Grave guardando los documentos");

		}

		model.put("status", "OK");
		model.put("message", "Proceso de escaneado terminado");

		log.debug("[Zylk] Proceso de escaneado terminado.");
		return model;
	}

	/**
	 * Método para escanear los documentos
	 * 
	 * @param request
	 * @param scanNodeRefStr
	 * @return
	 */

	private ArrayList<BufferedImage> scanDocs(WebScriptRequest request, String scanNodeRefStr) {
		// String resp = "No ha llegado a escanear.";
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

		try {

			NodeRef scannerNodeRef = new NodeRef(scanNodeRefStr);

			JSONObject deviceJsonObj = null;

			ContentReader reader = m_serviceRegistry.getContentService().getReader(scannerNodeRef, ContentModel.PROP_CONTENT);
			String deviceJsonStr = reader.getContentString();
			deviceJsonObj = new JSONObject(deviceJsonStr);

			log.debug("[Zylk] Obtenido y creado el objeto json del escaner seleccionado: " + deviceJsonObj.has("model"));

			InetAddress address = InetAddress.getByName(deviceJsonObj.getString("host"));

			session = SaneSession.withRemoteSane(address, 6566);

			log.debug("[Zylk] Creando sesión remota con el escaner... " + deviceJsonObj.getString("host"));

			String model = deviceJsonObj.getString("model");
			String name = deviceJsonObj.getString("name");
			String type = deviceJsonObj.getString("type");
			String vendor = deviceJsonObj.getString("vendor");

			log.debug("[Zylk] El Escaner seleccionado tiene las siguientes propiedades, Model: [" + model + "], Name: [" + name
					+ "], Type: [" + type + "], Vendor: [" + vendor + "]");

			List<SaneDevice> devices = session.listDevices();
			if (!devices.isEmpty()) {
				log.debug("[Zylk] Recoge la lista de dispositivos SANE");
				for (SaneDevice device : devices) {
					log.debug(device.getModel() + " - " + device.getName() + " - " + device.getType() + " - "
							+ device.getVendor());

					if (model.equals(device.getModel()) && name.equals(device.getName()) && type.equals(device.getType())
							&& vendor.equals(device.getVendor())) {
						log.debug("[Zylk] Coincide el json del escaner con algun dispositivo de la lista.");
						device.open();
						JfreeSaneDeviceJson jfreeSaneDeviceJson = new JfreeSaneDeviceJson();
						jfreeSaneDeviceJson.setJsonOptionstoDevice(device, deviceJsonObj);

						images = ScanManager.adfAcquisitionSucceeds(device);
						log.debug("[Zylk] Comienza a Escanear...");
						if (images.isEmpty()) {
							log.debug("[Zylk] No hay hojas para escanear!!!!!!!!!");
							throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No hay hojas para escanear");
							// resp = "No hay hojas para escanear";
						} else {
							log.debug("[Zylk] Se ha escaneado alguna imagen");
							// resp = "OK";
						}
					} else {
						throw new WebScriptException(Status.STATUS_BAD_REQUEST,
								"El perfil seleccionado no se encuentra en la lista de escaneres disponibles");
					}
				}
			} else {
				log.debug("[Zylk] SANE no recoge ningun dispositivo.");
				// resp = "SANE no recoge ningun dispositivo.";
			}
		} catch (SaneException e) {
			if (e.getStatus() == SaneStatus.STATUS_NO_DOCS) {
				// out of documents to read, that's fine
				log.error("[Zylk] No hay hojas en la bandeja o para escanear.");

			} else {
				log.error("[Zylk] Ha ocurrido una excepción en SANE");
			}
			e.printStackTrace();
		} catch (IOException e) {
			log.error("[Zylk] Ha ocurrido una excepción en de IO");
			log.error("[Zylk] La sesión remota no existe.");
		} catch (JSONException e) {
			log.error("[Zylk] Ha ocurrido una excepción en la manipulación del JSON");
		} finally {
			try {
				if (saneDevice != null && saneDevice.isOpen()) {
					saneDevice.close();
				}
				session.close();
				log.debug("[Zylk] Cerrando sesión del escaner");
			} catch (IOException e) {
				log.error("[Zylk] ERROR a la hora de cerrar la conexión con el scanner o la SANEsession.");
			}
		}

		return images;
	}

	/**
	 * Método para crear la imagen escaneada en el repositorio. Si el valor de transformToPDF es true, crearemos una copia de la
	 * imagen en PDF
	 * 
	 * @param image
	 * @param folderNodeRef
	 * @param ocrNodeRefJSON
	 * @param transformToPDF
	 */

	private NodeRef saveFile(BufferedImage image, NodeRef folderNodeRef, JSONObject ocrNodeRefJSON, boolean transformToPDF) {

		Calendar cal = Calendar.getInstance();
		String fileTypeExtension = "";

		try {
			fileTypeExtension = properties.getProperty(FILE_TYPE_PROPERTY);
		} catch (Exception e) {
			log.warn("File type extension is not set in alfresco-global.properties (zk.jsaneocr.file.type), default type is set to png");
			e.printStackTrace();
		}

		if (fileTypeExtension == null) {
			fileTypeExtension = "png";
		}

		log.debug("[Zylk] Extension de la imagen a crear: " + fileTypeExtension);

		File temp = null;
		FileInfo nodeContent = null;

		try {

			String docName = "SCAN-" + cal.getTimeInMillis() + "." + fileTypeExtension;

			temp = File.createTempFile("SCAN-" + cal.getTimeInMillis(), "." + fileTypeExtension);

			ImageIO.write(image, fileTypeExtension, temp);

			// Creamos la imagen escaneada en el repositorio

			FileFolderService fileFolderService = m_serviceRegistry.getFileFolderService();
			nodeContent = fileFolderService.create(folderNodeRef, docName, ContentModel.TYPE_CONTENT);

			ContentWriter imgWriter = m_serviceRegistry.getContentService().getWriter(nodeContent.getNodeRef(),
					ContentModel.PROP_CONTENT, true);

			imgWriter.setMimetype(m_serviceRegistry.getMimetypeService().guessMimetype(docName));
			imgWriter.putContent(temp);

						
			Profile profile = new Profile(ocrNodeRefJSON);
			ArrayList<String> ocrResults = OcrWithProfilesManager.toOCRWithProfile(profile, image);

			// Taggeamos la imagen con los metadatos del OCR
			taggingOCR(nodeContent.getNodeRef(), profile, ocrResults);

		} catch (IOException e) {
			log.debug("Error al convertir la imagen en un Array de bytes.");
			e.printStackTrace();
		} catch (ContentIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (temp != null) {
				temp.deleteOnExit();
			}
		}

		return nodeContent.getNodeRef();
	}

	/**
	 * Método para asignar los metadatos al documento con los parámetros del OCR
	 * 
	 * @param fileNodeRef
	 * @param profile
	 * @param ocrResults
	 */

	private void taggingOCR(NodeRef fileNodeRef, Profile profile, ArrayList<String> ocrResults) {

		if (profile.getType() != null) {

			// Creamos el documento del tipo que hemos seleccionado en la configuración del perfil OCR

			QName typeQName = QName.createQName(profile.getType(), namespacePrefixResolver);
			m_serviceRegistry.getNodeService().setType(fileNodeRef, typeQName);

			log.debug("[Zylk] Creando documento del tipo: " + profile.getType());

			// Con el DictionaryService obtenemos las propiedades del tipo que hemos seleccionado
			Map<QName, PropertyDefinition> properties = m_serviceRegistry.getDictionaryService().getType(typeQName)
					.getProperties();

			for (int i = 0; i < profile.getOcrZones().size(); i++) {

				// Obtenemos las propiedades que hemos "mapeado" durante la configuración del perfil OCR
				QName metadata = QName.createQName(profile.getOcrZones().get(i).getMetadata(), namespacePrefixResolver);

				if (properties.get(metadata) != null
						&& properties.get(metadata).getDataType().getName().equals(DataTypeDefinition.DATE)) {
					// Si la propiedad es de tipo fecha, formateamos la fecha
					log.debug("[Zylk] el metadato es de tipo fecha : " + metadata);
					log.debug("[Zylk] el valor que hemos obtenido del OCR es : " + ocrResults.get(i));

					try {
						DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
						Date date = dateFormat.parse(ocrResults.get(i).trim().replaceAll(" ", ""));

						m_serviceRegistry.getNodeService().setProperty(fileNodeRef, metadata, ISO8601DateFormat.format(date));
					} catch (ParseException e) {
						log.error("[Zylk ] ERROR formateando la fecha");
						e.printStackTrace();
					}

				} else {
					log.debug("[Zylk] El metadato del tipo es: " + metadata);
					log.debug("[Zylk] el valor que hemos obtenido del OCR es : " + ocrResults.get(i));
					m_serviceRegistry.getNodeService().setProperty(fileNodeRef, metadata, ocrResults.get(i));
				}
			}

		} else {
			throw new WebScriptException(Status.STATUS_BAD_REQUEST, "El tipo del documento es obligatorio");
		}

	}

	private void transformImagesToPDF(ArrayList<NodeRef> imageNodes, boolean multipagePDF, NodeRef folderNodeRef) {

		Calendar cal = Calendar.getInstance();

		ArrayList<NodeRef> createdPDFNodes = new ArrayList<NodeRef>();

		try {

			int contador = 1;

			for (NodeRef imgNode : imageNodes) {

				log.debug("[Zylk] Transformamos a PDF");
				// Creamos un PDF Vacio
				String pdfName = "SCAN-" + cal.getTimeInMillis() + "_" + contador + ".pdf";

				FileInfo pdfContent = m_serviceRegistry.getFileFolderService().create(folderNodeRef, pdfName,
						ContentModel.TYPE_CONTENT);

				ContentWriter pdfWriter = m_serviceRegistry.getContentService().getWriter(pdfContent.getNodeRef(),
						ContentModel.PROP_CONTENT, true);

				pdfWriter.setMimetype(MimetypeMap.MIMETYPE_PDF);

				ContentReader imgReader = m_serviceRegistry.getContentService().getReader(imgNode, ContentModel.PROP_CONTENT);

				Document doc = new Document(PageSize.A4);
				doc.setMargins(0, 0, 0, 0);
				doc.setMarginMirroring(true);
				// Create PdfWriter for Document to hold physical file
				PdfWriter.getInstance(doc, pdfWriter.getContentOutputStream());
				doc.open();
				// Get the input image to Convert to PDF
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				imgReader.getContent(baos);
				Image img = Image.getInstance(baos.toByteArray());
				img.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
				
								
				// Add image to Document
				doc.add(img);
				// Close Document
				doc.close();

				createdPDFNodes.add(pdfContent.getNodeRef());
				if(!multipagePDF){
					copyMetadata(imgNode, pdfContent.getNodeRef());
				}
				
				// Taggeamos el pdf con los metadatos del OCR
				contador++;
				log.debug("[Zylk] Transformada la imagen a PDF --> " + pdfName);
			}

			if (multipagePDF) {

				log.debug("[Zylk] Concatenamos los PDF");

				String multiPDFName = "SCAN-multi-" + cal.getTimeInMillis() + ".pdf";

				FileInfo multiPDFContent = m_serviceRegistry.getFileFolderService().create(folderNodeRef, multiPDFName,
						ContentModel.TYPE_CONTENT);

				ContentWriter multiPDFWriter = m_serviceRegistry.getContentService().getWriter(multiPDFContent.getNodeRef(),
						ContentModel.PROP_CONTENT, true);

				multiPDFWriter.setMimetype(MimetypeMap.MIMETYPE_PDF);

				// Concatenamos todos los pdf escaneados a un unico documento pdf
				concatPDFFiles(createdPDFNodes, multiPDFWriter);			
				
				// Eliminamos los pdf individuales
				for(NodeRef pdfNode : createdPDFNodes){
					m_serviceRegistry.getNodeService().deleteNode(pdfNode);
				}
				
			}
			
			// Eliminamos las imagenes escaneadas
			// Eliminamos los pdf individuales
			for(NodeRef imageNode : imageNodes){
				m_serviceRegistry.getNodeService().deleteNode(imageNode);
			}

		} catch (DocumentException de) {
			de.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

	}

	/**
	 * Método para concatenar los PDF que se han creado a partir de las imagenes escaneadas
	 * 
	 * @param pdfNodes
	 * @return
	 */

	private void concatPDFFiles(ArrayList<NodeRef> pdfNodes, ContentWriter multiPDFWriter) {

		try {

			Document document = new Document(PageSize.A4);
			PdfCopy copy = new PdfCopy(document, multiPDFWriter.getContentOutputStream());

			document.open();

			int numberOfPages;

			for (NodeRef pdfNode : pdfNodes) {

				log.debug("[Zylk] PDF to concat: " + pdfNode);

				ContentReader pdfReader = m_serviceRegistry.getContentService().getReader(pdfNode, ContentModel.PROP_CONTENT);

				PdfReader reader = new PdfReader(pdfReader.getContentInputStream());

				numberOfPages = reader.getNumberOfPages();
				for (int page = 0; page < numberOfPages;) {
					copy.addPage(copy.getImportedPage(reader, ++page));
				}
				copy.freeReader(reader);
				reader.close();

			}

			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void copyMetadata(NodeRef sourceNodeRef, NodeRef targetNodeRef){
		
		log.debug("[Zylk] Copiamos los metadatos de la imagen al PDF creado");
		
		List<QName> notToCopyProps = new ArrayList<QName>();
		notToCopyProps.add(ContentModel.PROP_NAME);
		notToCopyProps.add(ContentModel.PROP_NODE_REF);
		notToCopyProps.add(ContentModel.PROP_NODE_DBID);
		notToCopyProps.add(ContentModel.PROP_NODE_UUID);
		notToCopyProps.add(ContentModel.PROP_CONTENT);
		
		m_serviceRegistry.getNodeService().setType(targetNodeRef, m_serviceRegistry.getNodeService().getType(sourceNodeRef));
		
		Map<QName, Serializable> props = m_serviceRegistry.getNodeService().getProperties(sourceNodeRef);
		
		for(Map.Entry<QName, Serializable> prop : props.entrySet()){
			log.debug("prop to copy: " +  prop.getKey() + " : " +prop.getValue());
			if(!notToCopyProps.contains(prop.getKey())){			
				m_serviceRegistry.getNodeService().setProperty(targetNodeRef, prop.getKey(), prop.getValue());
			}
		}
	}
	
	private String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;

		try {

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();

	}

	/**
	 * Sets service registry.
	 *
	 * @param serviceRegistry the service registry
	 */
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		m_serviceRegistry = serviceRegistry;
	}

	/**
	 * Sets namespace prefix resolver.
	 *
	 * @param namespacePrefixResolver the namespace prefix resolver
	 */
	public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver) {
		this.namespacePrefixResolver = namespacePrefixResolver;
	}

	/**
	 * Sets properties.
	 *
	 * @param properties the properties
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
