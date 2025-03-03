package si.vegamind.ftccompanion.services;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import si.vegamind.ftccompanion.utils.WebUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RobotLogsService {
	private RobotLogsService() {
	}

	public static void downloadLogs(String robotIp, File logsFolder) {
		String[] allLogs = WebUtils.get("http://" + robotIp + ":8080/listLogs", String[].class);

		for(String log : allLogs) {
			try(FileOutputStream fos = new FileOutputStream(new File(logsFolder.getPath(), log));
				CloseableHttpClient httpClient = HttpClients.createDefault()
			) {
				HttpGet request = new HttpGet("http://" + robotIp + ":8080/downloadFile?name=" + log);
				httpClient.execute(request).getEntity().writeTo(fos);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
