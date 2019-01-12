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
public class OauthServicesImpl implements OauthServices {

	private static final String tokenUrl = "http://localhost:8080/oauth/token";

	public String getToken(String username, String password) throws IOException {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		URL myUrl = new URL(tokenUrl);
		HttpURLConnection myUrlCon = (HttpURLConnection) myUrl.openConnection();
		String access_token = null;
		try {
			URIBuilder builder = new URIBuilder(tokenUrl);

			// Request parameters. All of them are optional.

			String basicCredentials = username + ":" + password;

			String basicAuth = "Basic " + new String(Base64.getEncoder().encode(basicCredentials.getBytes()));

			builder.setParameter("Authorization", basicAuth);
			builder.setParameter("grant_type", "password");
			builder.setParameter("username", "admin");
			builder.setParameter("password", "admin1234");
			builder.setParameter("client_id", username);

			// Prepare the URI for the REST API call.
			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);

			// Request headers.
			request.setHeader("Authorization", basicAuth);
			request.setHeader("Content-Type", "application/json;charset=UTF-8");

			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			JSONObject json = new JSONObject();
			if (entity != null) { // Format and display the JSON response. String
				String jsonString = EntityUtils.toString(entity);
				json = new JSONObject(jsonString);
			}
			access_token = json.get("access_token").toString();

			System.out.println(access_token);
			// request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

		} catch (Exception e) {
			// Display error message.
			System.out.println(e.getMessage());
		}
		return access_token;
	}

}
