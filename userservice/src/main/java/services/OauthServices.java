package services;

import java.io.IOException;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@EnableAutoConfiguration
@Configuration
@Component
public interface OauthServices {
	public String getToken(String username, String password) throws IOException;
}