package net.zylk.alfresco.addons.ocr.jfreeSane.device;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.Resty;
import au.com.southsky.jfreesane.OptionGroup;
import au.com.southsky.jfreesane.OptionValueType;
import au.com.southsky.jfreesane.RangeConstraint;
import au.com.southsky.jfreesane.SaneDevice;
import au.com.southsky.jfreesane.SaneException;
import au.com.southsky.jfreesane.SaneOption;
import au.com.southsky.jfreesane.SaneSession;

public class JfreeSaneDeviceJson {

	private static Log log = LogFactory.getLog(JfreeSaneDeviceJson.class);

	public JSONObject listAllDevicesJson(SaneSession session) throws IOException, SaneException, JSONException {

		// JSON Model sample
		// use the parser http://www.jsoneditoronline.org/
		// {"devices":[{"name":"canon","vendor":"canon","model":"","type":"","optionGroups":[{"title":"Standard","options":[{"title":"","name":JSONObject:0,"units":"ADF"}]}]}]}
		JSONObject dumpJSON = new JSONObject();
		JSONArray devicesJSON = new JSONArray();
		dumpJSON.put("devices", devicesJSON);

		List<SaneDevice> devices = session.listDevices();
		for (SaneDevice device : devices) {
			device.open();
			// log.info(device.getName());
			JSONObject deviceJSON = new JSONObject();
			devicesJSON.put(deviceJSON);
			deviceJSON.put("name", device.getName());
			deviceJSON.put("vendor", device.getVendor());
			deviceJSON.put("model", device.getModel());
			deviceJSON.put("type", device.getType());
			JSONArray optionGroupsJSon = new JSONArray();
			deviceJSON.put("optionGroups", optionGroupsJSon);

			for (OptionGroup optionGroup : device.getOptionGroups()) {
				JSONObject optionGroupJSON = new JSONObject();
				optionGroupsJSon.put(optionGroupJSON);
				optionGroupJSON.put("title", optionGroup.getTitle());
				JSONArray optionsJSON = new JSONArray();
				optionGroupJSON.put("options", optionsJSON);
				for (SaneOption option : optionGroup.getOptions()) {

					JSONObject optionJSON = new JSONObject();
					optionsJSON.put(optionJSON);

					optionJSON.put("title", option.getTitle());
					optionJSON.put("name", option.getName());
					optionJSON.put("type", option.getType());
					// optionJSON.put("active", option.isActive());
					// optionJSON.put("constrained", option.isConstrained());
					// optionJSON.put("readable", option.isReadable());
					// optionJSON.put("writeable", option.isWriteable());

					try {
						if (option.getType() == OptionValueType.STRING) {
							try {
								optionJSON.put("value", option.getStringValue());
							} catch (Exception ex) {
								if (option.isConstrained()) {
									optionJSON.put("value", option.getStringConstraints().get(0));
								} else {
									optionJSON.put("value", "");
								}
							}
							if (option.isConstrained()) {
								optionJSON.put("constraints", option.getStringConstraints());
							}
						} else if (option.getType() == OptionValueType.FIXED) {
							try {
								optionJSON.put("value", option.getFixedValue());
							} catch (Exception ex) {
								if (option.isConstrained()) {
									RangeConstraint rc = option.getRangeConstraints();
									optionJSON.put("value", rc.getMinimumFixed());
								} else {
									optionJSON.put("value", 0);
								}
							}
							if (option.isConstrained()) {
								RangeConstraint rc = option.getRangeConstraints();
								optionJSON.put("range", rc.getMinimumFixed() + "-" + rc.getMaximumFixed());
							}
						} else if (option.getType() == OptionValueType.INT) {
							try {
								optionJSON.put("value", option.getIntegerValue());
							} catch (Exception ex) {
								if (option.isConstrained()) {
									RangeConstraint rc = option.getRangeConstraints();
									optionJSON.put("value", rc.getMinimumInteger());
								} else {
									optionJSON.put("value", 0);
								}
							}
							if (option.isConstrained()) {
								try {
									RangeConstraint rc = option.getRangeConstraints();
									optionJSON.put("range", rc.getMinimumInteger() + "-" + rc.getMaximumInteger());
								} catch (Exception e) {
									log.info("ERROR INT");
								}
							}
						} else if (option.getType() == OptionValueType.BOOLEAN) {
							try {
								optionJSON.put("value", option.getBooleanValue());
							} catch (Exception ex) {
								optionJSON.put("value", Boolean.FALSE);
							}
						}
					} catch (Exception e) {
						System.out.println("ERROR en: " + option.getTitle() + " - " + option.getType());
						optionJSON.put("value", "");
					}

					optionJSON.put("units", option.getUnits().toString());
				}

			}
			device.close();
		}
		return dumpJSON;
	}

	public boolean throwJSON(JSONObject JSONresp, String url) {

		@SuppressWarnings("unused")
		Resty r = new Resty();
		return true;
	}

	public JSONObject doJsonGET(String url) throws IOException, JSONException {

		Resty r = new Resty();
		JSONObject JSONobj = new JSONObject(r.text(url).toString());
		log.info(JSONobj.toString());

		return JSONobj;
	}

	public void setJsonOptionstoDevice(SaneDevice device, JSONObject deviceJsonObj) {
		try {
			JSONArray optionGroupsJsonArray = deviceJsonObj.getJSONArray("optionGroups");
			log.info("Recoge el array de opciones.");
			for (int i = 0; optionGroupsJsonArray.length() > i; i++) {
				log.debug("Primer for: " + i);
				JSONObject optionsGroupJson = optionGroupsJsonArray.getJSONObject(i);
				JSONArray optionsArray = optionsGroupJson.getJSONArray("options");
				for (int j = 0; optionsGroupJson.getJSONArray("options").length() > j; j++) {
					log.debug("segundo for: " + j);
					String optionName = optionsGroupJson.getJSONArray("options").getJSONObject(j).getString("name");
					String optionTitle = optionsGroupJson.getJSONArray("options").getJSONObject(j).getString("title");
					String optionType = optionsGroupJson.getJSONArray("options").getJSONObject(j).getString("type");
					log.info("Option: N:" + optionName + " Ti:" + optionTitle + " Ty:" + optionType);
					SaneOption saneOpt = null;
					try {
						saneOpt = device.getOption(optionName);
					} catch (IOException e) {
						log.info("ERROR en:" + optionName);
						e.printStackTrace();
					}
					//log.info("despues saneOpt");

					try {
						if (optionType.equals("STRING") && saneOpt.isActive() && saneOpt.isWriteable()) {
							log.info(optionName + " - " + optionType + " - writeable:" + saneOpt.isWriteable() + " - " + optionsArray.getJSONObject(j).getString("value"));
							if (!optionsArray.getJSONObject(j).getString("value").equals("")){
								saneOpt.setStringValue("" + optionsArray.getJSONObject(j).getString("value"));
							}
						} else if (optionType.equals("FIXED") && saneOpt.isActive() && saneOpt.isWriteable()) {
							log.info(optionName + " - " + optionType + " - writeable:" + saneOpt.isWriteable() + " - " + optionsArray.getJSONObject(j).getDouble("value"));
							saneOpt.setFixedValue(0 + optionsArray.getJSONObject(j).getDouble("value"));
						} else if (optionType.equals("INT") && saneOpt.isActive() && saneOpt.isWriteable()) {
							log.info(optionName + " - " + optionType + " - writeable:" + saneOpt.isWriteable() + " - " + optionsArray.getJSONObject(j).getInt("value"));
							saneOpt.setIntegerValue(0 + optionsArray.getJSONObject(j).getInt("value"));
						} else if (optionType.equals("BOOLEAN") && saneOpt.isActive() && saneOpt.isWriteable()) {
							log.info(optionName + " - " + optionType + " - writeable:" + saneOpt.isWriteable() + " - " + optionsArray.getJSONObject(j).getBoolean("value"));
							saneOpt.setBooleanValue(optionsArray.getJSONObject(j).getBoolean("value"));
						}

					} catch (JSONException e) {
						log.info("Error en: " + saneOpt.getName() + " - " + saneOpt.getTitle());
					} catch (IOException e) {
						log.info("Error IO en: " + saneOpt.getName() + " - " + saneOpt.getTitle());
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			log.debug("Fin setJsonOptionstoDevice()");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException, SaneException, JSONException {
		InetAddress address = null;
		address = InetAddress.getByName("192.168.1.193");
		SaneSession session = null;
		session = SaneSession.withRemoteSane(address);
		JfreeSaneDeviceJson devicesJson = new JfreeSaneDeviceJson();
		JSONObject json = devicesJson.listAllDevicesJson(session);
		log.info(json.toString());
		session.close();
	}
}
