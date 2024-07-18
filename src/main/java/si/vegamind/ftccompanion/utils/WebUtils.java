package si.vegamind.ftccompanion.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class WebUtils {
	private static final ObjectMapper om;

	static {
		om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private WebUtils() {
	}

	public static void post(String url, HttpEntity body) {
		post(url, body, null);
	}

	public static <T> T post(String url, HttpEntity body, Class<T> clazz) {
		try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost request = new HttpPost(url);
			request.setEntity(body);

			HttpEntity res = httpClient.execute(request).getEntity();

			if(clazz != null) return om.readValue(res.getContent(), clazz);
		} catch(IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static <T> T get(String url, Class<T> clazz) {
		try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet request = new HttpGet(url);
			return om.readValue(httpClient.execute(request).getEntity().getContent(), clazz);
		} catch(IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
