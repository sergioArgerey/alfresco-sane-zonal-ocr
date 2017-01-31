package net.zylk.alfresco.addons.ocr.jfreeSane.test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Logger;

import au.com.southsky.jfreesane.SaneException;
import au.com.southsky.jfreesane.SaneSession;
import net.zylk.alfresco.addons.ocr.jfreeSane.device.JfreeSaneDeviceJson;
import org.junit.Test;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;


public class JfreeSaneDeviceJsonTest {
 
	private static final Logger log = Logger.getLogger(JfreeSaneDeviceJsonTest.class.getName());

	@Test
	public static void TestJfreeSaneDeviceJson() throws IOException, SaneException, JSONException {
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
