package net.zylk.alfresco.addons.ocr.jfreeSane.profile;

import java.util.ArrayList;
import java.util.List;

import net.zylk.alfresco.addons.ocr.jfreeSane.profile.utils.RectangleOCR;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

public class Profile {

	private static Log log = LogFactory.getLog(Profile.class);

	private String type;
	private List<RectangleOCR> ocrZones;
	private List<String> ocrZonesResult;

	public Profile(JSONObject profile) {

		try {
			//Recoge los datos basicos de un perfil 

			this.type = profile.getString("type");
		
		} catch (JSONException e1) {
			log.info("ERROR JSON datos basicos");
			e1.printStackTrace();
		}

		JSONArray jsonOcrZones = new JSONArray();
		try {
			//recoge la lista de zonas que se quieren pasar por el ocr
			jsonOcrZones = profile.getJSONArray("ocr-zones");
			ocrZones = new ArrayList<RectangleOCR>();
			for (int i = 0; jsonOcrZones.length() > i; i++) {

				JSONObject ocrZone = jsonOcrZones.getJSONObject(i);
				int x = ocrZone.getInt("x");
				int y = ocrZone.getInt("y");
				int width = ocrZone.getInt("width");
				int height = ocrZone.getInt("height");
				String metadata = ocrZone.getString("metadata");
				String language = ocrZone.getString("language");
				String typography = ocrZone.getString("typography");
				int psm = ocrZone.getInt("psm");
				RectangleOCR rectangle = new RectangleOCR(x, y, width, height, metadata, language, typography, psm);
				ocrZones.add(rectangle);
			}
		} catch (JSONException e) {
			log.info("ERROR JSON ocr-zones ");
			e.printStackTrace();
		}
	}

	

	public List<RectangleOCR> getOcrZones() {
		return ocrZones;
	}

	public void setOcrZones(List<RectangleOCR> ocrZones) {
		this.ocrZones = ocrZones;
	}

	public List<String> getOcrZonesResult() {
		return ocrZonesResult;
	}

	public void setOcrZonesResult(List<String> ocrZonesResult) {
		this.ocrZonesResult = ocrZonesResult;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}
}
