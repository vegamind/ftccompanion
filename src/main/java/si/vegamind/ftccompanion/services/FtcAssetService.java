package si.vegamind.ftccompanion.services;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.xerces.impl.dv.util.Base64;
import si.vegamind.ftccompanion.models.FtcAsset;
import si.vegamind.ftccompanion.utils.WebUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FtcAssetService {
	private FtcAssetService() {
	}

	public static FtcAsset[] listAssets(String robotIp, String fmname) {
		return WebUtils.post(
				"http://" + robotIp + ":8080/list_files",
				MultipartEntityBuilder.create()
						.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
						.addTextBody("fmname", fmname)
						.build(),
				FtcAsset[].class
		);
	}

	public static void uploadAsset(String robotIp, String fmname, File assetFile) {
		try {
			HttpEntity body = MultipartEntityBuilder
					.create()
					.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
					.addTextBody("fmname", fmname)
					.addTextBody("name", assetFile.getName())
					.addTextBody(
							"content",
							Base64.encode(Files.readAllBytes(assetFile.toPath()))
					)
					.build();

			WebUtils.post("http://" + robotIp + ":8080/save_file", body);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void downloadAsset(String robotIp, String fmname, FtcAsset asset, File assetFile) {
		try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost request = new HttpPost("http://" + robotIp + ":8080/fetch_file");

			HttpEntity body = MultipartEntityBuilder
					.create()
					.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
					.addTextBody("fmname", fmname)
					.addTextBody("name", asset.getName())
					.build();
			request.setEntity(body);

			Files.write(
					assetFile.toPath(),
					Base64.decode(
							EntityUtils.toString(httpClient.execute(request).getEntity())
					)
			);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteAsset(String robotIp, String fmname, FtcAsset asset) {
		WebUtils.post(
				"http://" + robotIp + ":8080/delete_files",
				MultipartEntityBuilder.create()
						.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
						.addTextBody("fmname", fmname)
						.addTextBody("name", asset.getName())
						.build()
		);
	}

	public static void renameAsset(String robotIp, String fmname, FtcAsset asset, String newName) {
		WebUtils.post(
				"http://" + robotIp + ":8080/rename_file",
				MultipartEntityBuilder.create()
						.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
						.addTextBody("fmname", fmname)
						.addTextBody("name", asset.getName())
						.addTextBody("new_name", newName)
						.build()
		);
	}
}
