package controller;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.swing.plaf.basic.BasicScrollPaneUI.VSBChangeListener;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import services.DbConnService;
import services.OauthServices;
import services.OauthServicesImpl;
import services.VisionServices;

@RestController
@RequestMapping("/site")
public class MainController {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private OauthServices oauthService;

	@Autowired
	private DbConnService dbService;

	@Autowired
	private VisionServices visionService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	/*
	 * @RequestMapping(value = "/{input:.+}") public ModelAndView
	 * getDomain(@PathVariable("input") String input) throws IOException {
	 */
	@RequestMapping(value = "/create/{input:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
	public StringBuilder getDeomain(@PathVariable("input") String input) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		ModelAndView modelandView = new ModelAndView("result");

		String userAgent = modelandView.addObject("user-agent", getUserAgent()).toString();
		String headers = modelandView.addObject("headers", getHeadersInfo()).toString();

		String u = request.getHeader("username");
		String p = request.getHeader("password");

		String username = "Neil"; // tmp
		String password = "Neil123"; // tmp

		String userpwd[] = dbService.putData(username, password, jdbcTemplate);

		stringBuilder.append("{\n" + "\"username\" : " + "\"" + userpwd[0] + "\"," + "\n" + "\"password\" : \""
				+ password + "\"" + "\n}");
		
		
		return stringBuilder;

	}

	@RequestMapping(value = "/getToken", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAccessToken() throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		String accessToken = null;
		String[] userpwd = new String[5];
		String username = request.getHeader("username");
		String password = request.getHeader("password");
		if (username == null || password == null) {
			stringBuilder.append("{\n" + "\"error\" : " + "\"" + "username or password cannot be null" + "\"\n}");
		} else {
			userpwd = dbService.checkUser(username, password, jdbcTemplate);
			if (userpwd[0] == "error") {
				stringBuilder.append("{\n" + "\"error\" : " + "\"" + "enter correct username or password" + "\"\n}");
			} else {
				accessToken = oauthService.getToken(userpwd[0], userpwd[1]);
				stringBuilder.append("{\n" + "\"access_token\" : " + "\"" + accessToken + "\"\n}");
			}
		}

		System.out.println("Returning token");

		return stringBuilder.toString();
	}

	@RequestMapping(value = "/getVision", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getVision() throws IOException {
		StringBuilder stringBuilder = new StringBuilder();

		String token = request.getHeader("token");
		String path = request.getHeader("path");
		String visionOp = visionService.getData(token, path);
		stringBuilder.append(visionOp);
		return stringBuilder.toString();
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	@RequestMapping("httpClient")
	public static Header[] getHedr1() throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("http://simplyfysolutions.com");
		HttpResponse response = client.execute(request);

		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			System.out.println(header.getName() + " : " + header.getValue());
		}

		return headers;
	}

	@RequestMapping(value = "url")
	public static String getHedr2() throws ClientProtocolException, IOException {
		String server = null;
		String content_type = null;
		String expires = null;
		try {
			URL obj = new URL("http://mkyong.com");
			URLConnection conn = obj.openConnection();
			Map<String, List<String>> map1 = conn.getHeaderFields();

			Map<String, List<String>> map = conn.getHeaderFields();

			System.out.println("Printing Response Header...\n");

			for (Map.Entry<String, List<String>> entry : map.entrySet()) {

				// System.out.println(entry.getKey() + " : " + entry.getValue());
			}

			server = conn.getHeaderField(server);
			content_type = conn.getHeaderField(content_type);
			expires = conn.getHeaderField(expires);

			if (server == null) {
				System.out.println("Key 'Server' is not found!");
			} else {
				System.out.println("Server - " + server);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "index";
	}

	private String getUserAgent() {
		return request.getHeader("user-agent");
	}

	private Map<String, String> getHeadersInfo() {

		Map<String, String> map = new HashMap<String, String>();

		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}

		return map;
	}

}
