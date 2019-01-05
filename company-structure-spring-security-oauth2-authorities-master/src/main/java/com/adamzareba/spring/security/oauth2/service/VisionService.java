package com.adamzareba.spring.security.oauth2.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
//import com.adamzareba.spring.security.oauth2.controller.VisionController;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.ColorInfo;
import com.google.cloud.vision.v1.DominantColorsAnnotation;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.WebDetection.WebEntity;
import com.google.cloud.vision.v1.WebDetection.WebImage;
import com.google.protobuf.ByteString;

@Service
@Component
public class VisionService {

	Logger logger = Logger.getLogger(VisionService.class);

	@SuppressWarnings("unchecked")
	public String getGVision(String imageUrl, ImageAnnotatorClient imageAnnotatorClient, ResourceLoader resourceLoader)
			throws Exception {
		StringBuilder responseBuilder = new StringBuilder();

		String imgIndex = imageUrl.substring(0, 4);
		String http1 = "http";
		ByteString imageBytes;
		if (imgIndex.equals(http1)) {
			URL url = new URL(imageUrl);
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
			imageBytes = ByteString.copyFrom(img);
			// imageBytes = ByteBuffer.wrap(img);
			logger.info("Http Image is converted into Byte Array");

		} else {
			imageBytes = ByteString.readFrom(new FileInputStream(imageUrl));
			logger.info("Local Image is converted into Byte Array");
		}

		BatchAnnotateImagesResponse responses;
		Image image = Image.newBuilder().setContent(imageBytes).build();
		logger.info("Image Object is builded");
		// Sets the type of request to label detection, to detect broad sets of
		// categories in an image.

		List<Feature> featureList = new ArrayList<>();
		Feature labelDetectionFeature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
		featureList.add(labelDetectionFeature);
		logger.info("LabelDetectionFeature is builded");

		Feature webDetectionFeature = Feature.newBuilder().setType(Feature.Type.WEB_DETECTION).build();
		featureList.add(webDetectionFeature);
		logger.info("WebDetectionFeature is builded");

		Feature imagePropertiesFeature = Feature.newBuilder().setType(Feature.Type.IMAGE_PROPERTIES).build();
		featureList.add(imagePropertiesFeature);
		logger.info("ImagePropertiesFeature is builded");

		AnnotateImageRequest annotateImageRequest = AnnotateImageRequest.newBuilder().setImage(image)
				.addAllFeatures(featureList).build();
		logger.info("AnnotateImageRequest is builded");

		responses = imageAnnotatorClient.batchAnnotateImages(Collections.singletonList(annotateImageRequest));

		// We're only expecting one response.
		AnnotateImageResponse response = responses.getResponses(0);
		logger.info("Reponse object is created");

		if (responses.getResponsesCount() == 1) {
			if (response.hasError()) {
				throw new Exception(response.getError().getMessage());
			}

			responseBuilder.append("{\"labelAnnotations\": [");
			logger.info("LabelAnnotation Object is executed ");
			for (EntityAnnotation annotation : response.getLabelAnnotationsList()) {
				responseBuilder.append('{').append("\"mid\":").append('"').append(annotation.getMid()).append('"')
						.append(',').append(" \"description\":").append('"').append(annotation.getDescription())
						.append('"').append(',').append("\"score\":").append('"').append(annotation.getScore())
						.append('"').append(',').append("\"topicality\":").append('"')
						.append(annotation.getTopicality()).append('"').append('}').append(',');

			}
			responseBuilder.deleteCharAt(responseBuilder.lastIndexOf(","));
			responseBuilder.append("],");

			DominantColorsAnnotation dominantColor = response.getImagePropertiesAnnotation().getDominantColors();

			logger.info("ImageProperties dominantColor Object is executed");
			responseBuilder
					.append("\"imagePropertiesAnnotation\": {\n" + " \"dominantColors\": {\n" + "      \"colors\": [");
			for (ColorInfo ColorInfo : dominantColor.getColorsList()) {

				responseBuilder.append("{\"color\": {\"red\":").append(ColorInfo.getColor().getRed() + ",")
						.append("\"green\":").append(ColorInfo.getColor().getGreen() + ",").append("\"blue\":")
						.append(ColorInfo.getColor().getBlue()).append("},").append("\"score\":")
						.append(ColorInfo.getScore() + ",").append("\"pixelFraction\":")
						.append(ColorInfo.getPixelFraction());
				responseBuilder.append("},");
			}
			responseBuilder.deleteCharAt(responseBuilder.lastIndexOf(","));
			responseBuilder.append("]}},");

			/* Web Detection */
			logger.info("WebDetection object is executed");
			responseBuilder.append("\"webDetection\": {\"webEntities\": [");
			for (WebEntity webEntityList : response.getWebDetection().getWebEntitiesList()) {

				responseBuilder.append('{').append("\"entityId\":").append('"').append(webEntityList.getEntityId())
						.append('"').append(',').append("\"score\":").append('"').append(webEntityList.getScore())
						.append('"').append(',').append("\"description\":").append('"')
						.append(webEntityList.getDescription()).append('"').append("},");

			}
			responseBuilder.deleteCharAt(responseBuilder.lastIndexOf(","));
			responseBuilder.append("],");

			/* Visually Similar Images */
			int count = response.getWebDetection().getVisuallySimilarImagesCount();
			responseBuilder.append("\"visuallySimilarImages\":[");
			for (int i = 0; i < count; i++) {
				responseBuilder.append("{");
				WebImage ar = response.getWebDetection().getVisuallySimilarImages(i);
				String VisImg = ar.toString();
				responseBuilder.append(VisImg.replace("url", "\"url\""));
				responseBuilder.append("},");
			}
			responseBuilder.deleteCharAt(responseBuilder.lastIndexOf(","));
			responseBuilder.append("],");
			responseBuilder.append("\"bestGuessLabels\": [");
			int lblCount = response.getWebDetection().getBestGuessLabelsCount();
			for (int i = 0; i < lblCount; i++) {
				responseBuilder.append("{\"label\":")
						.append("\"" + response.getWebDetection().getBestGuessLabels(i).getLabel() + "\"}");
			}

			responseBuilder.append("]}}");

		}

		return responseBuilder.toString();

	}

	public String getMSVision(String imageToAnalyze) throws Exception {
		String msVisionString = new String();
		File file1 = new File("C:\\Users\\admin\\Documents\\MicrosoftVisionAPI\\subscriptionKey.txt");

		String uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v2.0/analyze";
		String subscriptionKey = FileUtils.readFileToString(file1, "UTF-8");
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		URIBuilder builder = new URIBuilder(uriBase);

		// Request parameters. All of them are optional.
		builder.setParameter("visualFeatures", "Categories,Description,Color");
		builder.setParameter("language", "en");

		// Prepare the URI for the REST API call.
		URI uri = builder.build();
		HttpPost request = new HttpPost(uri);

		String imgIndex = imageToAnalyze.substring(0, 4);
		ByteBuffer imageBytes;
		if (imgIndex == "http") {
			URL url = new URL(imageToAnalyze);
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
			request.setEntity(new ByteArrayEntity(img));
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

		} else {
			Path path = Paths.get(imageToAnalyze);

			byte[] img = Files.readAllBytes(path);
			request.setEntity(new ByteArrayEntity(img));

			request.setHeader("Content-Type", "application/octet-stream"); // For Local File System Image

			request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
		}
		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();

		if (entity != null) {

			msVisionString = EntityUtils.toString(entity);
			System.out.println("REST Response:\n");
			System.out.println("msVisionString " + msVisionString);
		}
		logger.info("EntityResponse Displayed");
		System.out.println(msVisionString);

		return msVisionString;
	}

	public String getAwsVision(String imagePath) throws IOException {
		AwsVisionService awsVision = new AwsVisionService();
		String awsOp = awsVision.getAwsVision(imagePath);
		return awsOp;
	}

}
