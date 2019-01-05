package com.adamzareba.spring.security.oauth2.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.Image;
import com.google.protobuf.ByteString;

@Component
public class AwsVisionService {


	@SuppressWarnings("unchecked")
	public String getAwsVision(String imagePath) throws IOException {
		String LDetect=null;
		String imgIndex = imagePath.substring(0, 4);
		String http1 = "http";
		ByteBuffer imageBytes;
		if (imgIndex.equals(http1)) {
			URL url = new URL(imagePath);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "Firefox");
			try (InputStream inputStream = conn.getInputStream()) {
				int n = 0;
				byte[] buffer = new byte[1024];
				while (-1 != (n = inputStream.read(buffer))) {
					baos.write(buffer, 0, n);
				}
			}

			byte[] img = baos.toByteArray();

			imageBytes = ByteBuffer.wrap(img);
		} else {
			try (InputStream inputStream = new FileInputStream(new File(imagePath))) {
				imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
			}
		}

		AWSCredentials credentials = new ProfileCredentialsProvider("myProfile").getCredentials();
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_WEST_2)
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

		DetectLabelsRequest request = new DetectLabelsRequest().withImage(new Image().withBytes(imageBytes))
				.withMaxLabels(10).withMinConfidence(77F);
		try {

			DetectLabelsResult result = rekognitionClient.detectLabels(request);
			List<Label> labels = result.getLabels();

			System.out.println("Detected labels for " + imagePath);
			for (Label label : labels) {

				LDetect = labels.toString();
			}

		} catch (AmazonRekognitionException e) {
			e.printStackTrace();
		}

		return LDetect;
	}

}
