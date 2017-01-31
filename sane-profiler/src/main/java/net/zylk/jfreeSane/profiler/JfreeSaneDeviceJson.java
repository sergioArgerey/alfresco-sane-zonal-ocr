package net.zylk.jfreeSane.profiler;

import au.com.southsky.jfreesane.*;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.Resty;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.logging.Logger;

public class JfreeSaneDeviceJson {

	private static final Logger logger = Logger.getLogger(JfreeSaneDeviceJson.class.getName());


	public JSONObject listAllDevicesJson(SaneSession session, String host) throws IOException, SaneException, JSONException {

		// JSON Model sample
		// use it, the parser http://www.jsoneditoronline.org/
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
			deviceJSON.put("host", host);
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
					try {
						if (option.getType() == OptionValueType.STRING) {
							optionJSON.put("value", option.getStringValue());
							optionJSON.put("constraints", option.getStringConstraints());
						} else if (option.getType() == OptionValueType.FIXED) {
							optionJSON.put("value", option.getFixedValue());
							if (option.isConstrained()) {
								try {
									RangeConstraint rc = option.getRangeConstraints();
									optionJSON.put("range", rc.getMinimumFixed() + "-" + rc.getMaximumFixed());
								} catch (Exception e) {
								}
							}
						} else if (option.getType() == OptionValueType.INT) {
							optionJSON.put("value", option.getIntegerValue());
							if (option.isConstrained()) {
								try {
									RangeConstraint rc = option.getRangeConstraints();
									optionJSON.put("range", rc.getMinimumInteger() + "-" + rc.getMaximumInteger());
								} catch (Exception e) {
								}
							}
						} else if (option.getType() == OptionValueType.BOOLEAN) {
							optionJSON.put("value", option.getBooleanValue());
						}
					} catch (Exception e) {
						// log.info("ERROR en: " + option.getTitle());
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

		Resty r = new Resty();
		return true;
	}

	public JSONObject getDeviceJson(SaneSession session, String name, String host, SaneDevice device) throws IOException, JSONException, SaneException {

		// JSON Model sample
		// use it, the parser http://www.jsoneditoronline.org/
		// {"devices":[{"name":"canon","vendor":"canon","model":"","type":"","optionGroups":[{"title":"Standard","options":[{"title":"","name":JSONObject:0,"units":"ADF"}]}]}]}

		device.open();
		// log.info(device.getName());
		JSONObject deviceJSON = new JSONObject();


		deviceJSON.put("name", device.getName());
		deviceJSON.put("vendor", device.getVendor());
		deviceJSON.put("model", device.getModel());
		deviceJSON.put("type", device.getType());
		deviceJSON.put("host", host);
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
				try {
					if (option.getType() == OptionValueType.STRING) {
						optionJSON.put("value", option.getStringValue());
						optionJSON.put("constraints", option.getStringConstraints());
					} else if (option.getType() == OptionValueType.FIXED) {
						optionJSON.put("value", option.getFixedValue());
						if (option.isConstrained()) {
							try {
								RangeConstraint rc = option.getRangeConstraints();
								optionJSON.put("range", rc.getMinimumFixed() + "-" + rc.getMaximumFixed());
							} catch (Exception e) {
							}
						}
					} else if (option.getType() == OptionValueType.INT) {
						optionJSON.put("value", option.getIntegerValue());
						if (option.isConstrained()) {
							try {
								RangeConstraint rc = option.getRangeConstraints();
								optionJSON.put("range", rc.getMinimumInteger() + "-" + rc.getMaximumInteger());
							} catch (Exception e) {
							}
						}
					} else if (option.getType() == OptionValueType.BOOLEAN) {
						optionJSON.put("value", option.getBooleanValue());
					}
				} catch (Exception e) {
					// log.info("ERROR en: " + option.getTitle());
					optionJSON.put("value", "");
				}

				optionJSON.put("units", option.getUnits().toString());
			}

		}
		device.close();

		return deviceJSON;
	}

	public void setJsonOptionstoDevice(SaneDevice device, JSONObject deviceJsonObj) throws JSONException, IOException, SaneException {

		JSONArray optionGroupsJsonArray = deviceJsonObj.getJSONArray("optionGroups");
		for (int i = 0; optionGroupsJsonArray.length() > i; i++) {
			JSONObject optionsGroupJson = optionGroupsJsonArray.getJSONObject(i);
			for (int j = 0; optionsGroupJson.getJSONArray("options").length() > j; j++) {

				String optionName = optionsGroupJson.getJSONArray("options").getJSONObject(j).getString("name");
				String optionType = optionsGroupJson.getJSONArray("options").getJSONObject(j).getString("type");
				SaneOption saneOpt = device.getOption(optionName);
				// log.info(""+saneOpt.isWriteable());
				try {
					if (optionType.equals("STRING")) {
						// log.info(optionsGroupJson.getJSONArray("options").getJSONObject(j).getString("value"));
						saneOpt.setStringValue(optionsGroupJson.getJSONArray("options").getJSONObject(j).getString("value"));
					} else if (optionType.equals("FIXED")) {
						// log.info("" + optionsGroupJson.getJSONArray("options").getJSONObject(j).getDouble("value"));
						saneOpt.setFixedValue(optionsGroupJson.getJSONArray("options").getJSONObject(j).getDouble("value"));
					} else if (optionType.equals("INT")) {
						// log.info("" + optionsGroupJson.getJSONArray("options").getJSONObject(j).getInt("value"));
						saneOpt.setIntegerValue(optionsGroupJson.getJSONArray("options").getJSONObject(j).getInt("value"));
					}
				} catch (Exception e) {
					System.out.println(saneOpt.getName() + " - " + saneOpt.getTitle());
				}
			}
		}

	}

	public static void main(String[] args) throws IOException, SaneException, JSONException {
		if (args.length == 0) {
			System.out.println("\nUsage:\n"

					+ "java -jar zk-generate-jfreesane-profile-json.jar <jsane-scanner-host> [name]\n\n"

					+ "Example:\n"

					+ "java -jar zk-generate-jfreesane-profile-json.jar scanner.zylk.net\n\n"

					+ "1135n Laser MFP on USB:0\n"
					+ "1135n Laser MFP on 192.168.1.3\n"
					+ "Dell 1135n Laser MFP\n\n"

					+ "java -jar zk-generate-jfreesane-profile-json.jar scanner.zylk.net \"Dell 1135n Laser MFP\"\n"
			);

		} else if (args.length == 1) {
			InetAddress address = null;
			address = InetAddress.getByName(args[0]);
			SaneSession session = null;
			//logger.info("antes de conectar");
			//logger.info("" + InetAddress.getByName("localhost"));
			session = SaneSession.withRemoteSane(address);
			//logger.info("Abre session");
			List<SaneDevice> devices = (List<SaneDevice>) session.listDevices();
			System.out.println("");
			for (SaneDevice saneDevice : devices) {
				System.out.println(saneDevice.getName());
			}
			if (devices.isEmpty()) {
				System.out.println("No se encuentra ningun dispositivo en esta dirección");
			}
			System.out.println("");


		} else if (args.length == 2) {
			InetAddress address = null;
			address = InetAddress.getByName(args[0]);
			SaneSession session = null;
			//logger.info("antes de conectar");
			//logger.info("" + InetAddress.getByName("localhost"));
			session = SaneSession.withRemoteSane(address);
			//logger.info("Abre session");
			List<SaneDevice> devices = (List<SaneDevice>) session.listDevices();
			JSONObject json = new JSONObject();

			for (SaneDevice saneDevice : devices) {

				if (saneDevice.getName().equals(args[1])) {
					JfreeSaneDeviceJson devicesJson = new JfreeSaneDeviceJson();
					json = devicesJson.getDeviceJson(session, args[1], args[0], saneDevice);
				}
			}
			System.out.println(json);

		}
	}
}