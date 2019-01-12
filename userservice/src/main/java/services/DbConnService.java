package services;

import java.io.IOException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@EnableAutoConfiguration
@Configuration
@Component
public interface DbConnService {
	public String[] putData(String CLIENT_ID, String password, JdbcTemplate jdbcTemplate);

	public String[] checkUser(String CLIENT_ID, String password, JdbcTemplate jdbcTemplate);

}