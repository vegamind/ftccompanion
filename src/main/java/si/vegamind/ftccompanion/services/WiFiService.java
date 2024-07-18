package si.vegamind.ftccompanion.services;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import si.vegamind.ftccompanion.models.WiFiChannel;
import si.vegamind.ftccompanion.utils.WebUtils;

public class WiFiService {
	private WiFiService() {
	}

	public static void applySettings(String robotIp, String deviceName, WiFiChannel channel, String passphrase) {
		WebUtils.post(
				"http://" + robotIp + ":8080/changeNetworkSettings",
				MultipartEntityBuilder.create()
						.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
						.addTextBody("name", deviceName)
						.addTextBody("password", passphrase)
						.addTextBody("channelName", channel.getName())
						.build()
		);
	}
}
