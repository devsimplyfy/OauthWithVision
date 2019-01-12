package services;

import java.io.IOException;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@EnableAutoConfiguration
@Configuration
@Component
public interface VisionServices {
	public String getData(String token, String path) throws IOException;
}