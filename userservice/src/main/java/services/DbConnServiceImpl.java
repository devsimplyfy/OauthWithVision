package services;

import java.sql.ResultSet;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DbConnServiceImpl implements DbConnService {

	// (CLIENT_ID, - spring-security-oauth2-read-write-client
	// RESOURCE_IDS, - resource-server-rest-api
	// CLIENT_SECRET, -
	// spring-security-oauth2-read-write-client-password1234'$iGxcMGdu.a5hvfY4W
	// SCOPE, - read,write
	// AUTHORIZED_GRANT_TYPES, - password,authorization_code,refresh_token,implicit
	// AUTHORITIES, - USER
	// ACCESS_TOKEN_VALIDITY, - 10800
	// REFRESH_TOKEN_VALIDITY) - 2592000

	public String[] putData(String CLIENT_ID, String password, JdbcTemplate jdbcTemplate) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// System.out.println(userAgent + "\n" + headers);
		String userpwd[] = new String[5];
		String CLIENT_SECRET = encoder.encode(password);
		String insertSql = "INSERT INTO OAUTH_CLIENT_DETAILS(CLIENT_ID, RESOURCE_IDS, CLIENT_SECRET, SCOPE, AUTHORIZED_GRANT_TYPES, AUTHORITIES, ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY)VALUES ('"
				+ CLIENT_ID + "','resource-server-rest-api','" + CLIENT_SECRET
				+ "','read,write','password,authorization_code,refresh_token,implicit','USER',10800,2592000)";

		String sql1 = "select * from oauth_client_details";
		List count = jdbcTemplate.queryForList(sql1);

		userpwd[0] = CLIENT_ID;
		userpwd[1] = password;

		System.out.println("Query executed");

		return userpwd;
	}

	public String[] checkUser(String CLIENT_ID, String password, JdbcTemplate jdbcTemplate) {

		
		// System.out.println(userAgent + "\n" + headers);
		String userpwd[] = new String[5];
		String checkSql = "SELECT CLIENT_ID FROM OAUTH_CLIENT_DETAILS WHERE CLIENT_ID = '" + CLIENT_ID + "'";

		List count = jdbcTemplate.queryForList(checkSql);

		if (count.size() < 1) {
			userpwd[0] = "error";
		} else {
			userpwd[0] = CLIENT_ID;
			userpwd[1] = password;
		}

		System.out.println("Query executed");

		return userpwd;
	}

}
