package services;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class VisionServicesImpl implements VisionServices {

	private static final String tokenUrl = "http://localhost:8080/secured/company/vision";

	public String getData(String token, String path) throws IOException {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		URL myUrl = new URL(tokenUrl);
		HttpURLConnection myUrlCon = (HttpURLConnection) myUrl.openConnection();
		String jsonString = null;
		try {
			URIBuilder builder = new URIBuilder(tokenUrl);

			builder.setParameter("path", path);

			// Prepare the URI for the REST API call.
			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);

			// Request headers.
			request.setHeader("Authorization", token);
			request.setHeader("Content-Type", "application/json;charset=UTF-8");

			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			JSONObject json = new JSONObject();

			if (entity != null) { // Format and display the JSON response. String
				jsonString = EntityUtils.toString(entity);
				// json = new JSONObject(jsonString);
			}

		} catch (Exception e) {
			// Display error message.
			System.out.println(e.getMessage());
		}
		return jsonString;
	}

}
