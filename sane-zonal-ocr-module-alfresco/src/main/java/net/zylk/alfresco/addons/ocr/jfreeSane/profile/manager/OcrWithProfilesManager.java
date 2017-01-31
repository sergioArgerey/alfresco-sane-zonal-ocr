package net.zylk.alfresco.addons.ocr.jfreeSane.profile.manager;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.zylk.alfresco.addons.ocr.jfreeSane.profile.Profile;
import net.zylk.alfresco.addons.ocr.jfreeSane.webscript.SendDocToScanWebScript;
import net.zylk.alfresco.addons.ocr.jfreeSane.profile.utils.RectangleOCR;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

public class OcrWithProfilesManager {

	private static Log log = LogFactory.getLog(SendDocToScanWebScript.class);
	
	public OcrWithProfilesManager() {

	}

	public static List<BufferedImage> getSubImagesFromProfile(Profile profile, BufferedImage image) {
		ArrayList<BufferedImage> ocrZoneImages = new ArrayList<BufferedImage>();
		for (RectangleOCR rectangle : profile.getOcrZones()) {
			BufferedImage ocrImage = image.getSubimage(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
			ocrZoneImages.add(ocrImage);
		}

		return ocrZoneImages;
	}

	public static String getRectangleandOCRit(RectangleOCR rectangle, String language, BufferedImage image) {
		BufferedImage ocrImage = image.getSubimage(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
		String timeStamp = "" + Calendar.getInstance().getTimeInMillis();
		try {
			boolean dirFlag = false;
			File stockDir = new File("/tmp/alfresco/zylk/");
			if (!stockDir.exists()) {
				dirFlag = stockDir.mkdirs();
			}
			File file = new File("/tmp/alfresco/zylk/", timeStamp + ".png");
			file.createNewFile();
			ImageIO.write(ocrImage, "png", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			//running a 
			String[] command = { "tesseract", "/tmp/alfresco/zylk/" + timeStamp + ".png", "/tmp/alfresco/zylk/" + timeStamp, "-l " + language, "-psm " + rectangle.getPsm() };
			ProcessBuilder probuilder = new ProcessBuilder(command);

			//	You can set up your work directory
			//	probuilder.directory(new File("c:\\vaanidemo"));

			Process process = probuilder.start();

			//Read out dir output
			/*
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			System.out.printf("Output of running %s is:\n", Arrays.toString(command));
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
			*/

			//Wait to get exit value
			try {
				int exitValue = process.waitFor();
				System.out.println("\n\nExit Value is " + exitValue);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			BufferedReader br2 = null;

			try {

				String sCurrentLine;

				br2 = new BufferedReader(new FileReader("/tmp/alfresco/zylk/" + timeStamp + ".txt"));

				while ((sCurrentLine = br2.readLine()) != null) {
					System.out.println(sCurrentLine);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br2 != null)
						br2.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return "";
	}

	public static String getOCRFromRectangle(BufferedImage image, String language, int psm) {
		String result = "";
		String timeStamp = "" + Calendar.getInstance().getTimeInMillis();
		try {
			boolean dirFlag = false;
			File stockDir = new File("/tmp/alfresco/zylk/");
			if (!stockDir.exists()) {
				dirFlag = stockDir.mkdirs();
			}
			File file = new File("/tmp/alfresco/zylk/", timeStamp + ".png");
			file.createNewFile();
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			//running a 
			String[] command = { "tesseract", "/tmp/alfresco/zylk/" + timeStamp + ".png", "/tmp/alfresco/zylk/" + timeStamp, "-l " + language, "-psm " + psm };
			ProcessBuilder probuilder = new ProcessBuilder(command);

			//	You can set up your work directory
			//	probuilder.directory(new File("c:\\vaanidemo"));

			Process process = probuilder.start();

			//Read out dir output
			/*
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			System.out.printf("Output of running %s is:\n", Arrays.toString(command));
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
			*/

			//Wait to get exit value
			try {
				int exitValue = process.waitFor();
				System.out.println("\n\nExit Value is " + exitValue);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			BufferedReader br2 = null;

			try {

				String sCurrentLine;

				br2 = new BufferedReader(new FileReader("/tmp/alfresco/zylk/" + timeStamp + ".txt"));

				while ((sCurrentLine = br2.readLine()) != null) {
					System.out.println(sCurrentLine);
					result += " " + sCurrentLine;
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br2 != null)
						br2.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String toOCR(BufferedImage image) {
		//		String result = "";
		//		Tesseract tess = Tesseract.getInstance();
		//		try {
		//
		//			result = tess.doOCR(image);
		//			logger.info(result);
		//		} catch (TesseractException e) {
		//			e.printStackTrace();
		//		}

		ProcessBuilder pb = new ProcessBuilder("tesseract", "myArg1", "myArg2");
		Map<String, String> env = pb.environment();
		env.put("VAR1", "myValue");
		env.remove("OTHERVAR");
		env.put("VAR2", env.get("VAR1") + "suffix");
		pb.directory(new File("myDir"));
		File log = new File("log");
		pb.redirectErrorStream(true);
		//pb.redirectOutput(Redirect.appendTo(log));
		//Process p = pb.start();
		//assert pb.redirectInput() == Redirect.PIPE;
		//assert pb.redirectOutput().file() == log;
		//assert p.getInputStream().read() == -1;

		return "";
	}

	public static ArrayList<String> toOCRWithProfile(Profile profile, ArrayList<BufferedImage> images) {
		ArrayList<String> results = new ArrayList<String>();
		for (BufferedImage image : images) {
			List<BufferedImage> subImages = OcrWithProfilesManager.getSubImagesFromProfile(profile, image);

			for (int i = 0; i < subImages.size(); i++) {
				String result = OcrWithProfilesManager.getOCRFromRectangle(subImages.get(i), profile.getOcrZones().get(i).getLanguage(), profile.getOcrZones().get(i).getPsm());
				results.add(result);
			}
		}

		return results;
	}
	
	public static ArrayList<String> toOCRWithProfile(Profile profile, BufferedImage image) {
		ArrayList<String> results = new ArrayList<String>();
		List<BufferedImage> subImages = OcrWithProfilesManager.getSubImagesFromProfile(profile, image);
			//TODO calcular escalado de la imagen y diferencias entre ocr profile y la buffered image.  
			for (int i = 0; i < subImages.size(); i++) {
				String result = OcrWithProfilesManager.getOCRFromRectangle(subImages.get(i), profile.getOcrZones().get(i).getLanguage(), profile.getOcrZones().get(i).getPsm());
				results.add(result);
			}

		return results;
	}

	public static void main(String[] args) {
		BufferedImage image = null;
		try {
			//image = ImageIO.read(new File("/home/sergio/SCAN-1394701271872.png"));
			image = ImageIO.read(new File("/home/sergio/factura_scan.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//{"language":"spa","typography":"arial","category":"factura","name":"factura-solred","ocr-zones":[{"x":1,"y":12,"width":400,"height":300,"psm":0},{"x":1,"y":12,"width":400,"height":300,"psm":2}],"type":"scanner","vendor":"FUJITSU"}

		// Perfil de 
		//String strProfile = "{\"language\":\"spa\",\"typography\":\"arial\",\"category\":\"factura\",\"name\":\"factura-solred\",\"ocr-zones\":[{\"x\":909,\"y\":2000,\"width\":3427,\"height\":350,\"psm\":3},{\"x\":1,\"y\":12,\"width\":400,\"height\":300,\"psm\":2}]}";

		//String strProfile = "{\"language\":\"spa\",\"typography\":\"arial\",\"category\":\"factura\",\"name\":\"factura-solred\",\"ocr-zones\":[{\"x\":480,\"y\":1000,\"width\":4170,\"height\":1700,\"psm\":3},{\"x\":1,\"y\":12,\"width\":400,\"height\":300,\"psm\":2}]}";

		// Perfil de fox.png 
		//String strProfile = "{\"language\":\"spa\",\"typography\":\"arial\",\"category\":\"factura\",\"name\":\"factura-solred\",\"ocr-zones\":[{\"x\":500,\"y\":1000,\"width\":4100,\"height\":1800,\"psm\":3},{\"x\":1,\"y\":12,\"width\":400,\"height\":300,\"psm\":2}]}";
		// Perfil factura_orange.jpg
		//String strProfile = "{\"category\":\"factura\",\"name\":\"factura-solred\",\"ocr-zones\":[{\"x\":1,\"y\":1,\"width\":5000,\"height\":6000,\"language\":\"spa\",\"typography\":\"arial\",\"psm\":1}]}";
		// Perfil factura_orange.jpg
		String strProfile = "{\"category\":\"factura\",\"name\":\"factura-solred\",\"ocr-zones\":[{\"x\":2900,\"y\":4000,\"width\":1300,\"height\":600,\"language\":\"spa\",\"typography\":\"arial\",\"psm\":1},{\"x\":3267,\"y\":3750,\"width\":883,\"height\":97,\"language\":\"spa\",\"typography\":\"arial\",\"psm\":1}]}";

		JSONObject profileJSON;
		try {
			profileJSON = new JSONObject(strProfile);

			//JSONObject JSONProfile = new JSONObject(new Object());
			Profile profile = new Profile(profileJSON);
			int x = profile.getOcrZones().get(0).getX();
			int y = profile.getOcrZones().get(0).getY();
			int width = profile.getOcrZones().get(0).getWidth();
			int height = profile.getOcrZones().get(0).getHeight();
			String metadata = profile.getOcrZones().get(0).getMetadata();
			String language = profile.getOcrZones().get(0).getLanguage();
			String typography = profile.getOcrZones().get(0).getTypography();
			int psm = profile.getOcrZones().get(0).getPsm();
			RectangleOCR rectangle = new RectangleOCR(x, y, width, height, metadata, language, typography, psm);
			OcrWithProfilesManager.getRectangleandOCRit(rectangle, language, image);
			
			int x2 = profile.getOcrZones().get(1).getX();
			int y2 = profile.getOcrZones().get(1).getY();
			int width2 = profile.getOcrZones().get(1).getWidth();
			int height2 = profile.getOcrZones().get(1).getHeight();
			String metadata2 = profile.getOcrZones().get(1).getMetadata();
			String language2 = profile.getOcrZones().get(1).getLanguage();
			String typography2 = profile.getOcrZones().get(1).getTypography();
			int psm2 = profile.getOcrZones().get(1).getPsm();
			RectangleOCR rectangle2 = new RectangleOCR(x2, y2, width2, height2, metadata, language2, typography2, psm2);
			OcrWithProfilesManager.getRectangleandOCRit(rectangle2, language2, image);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
