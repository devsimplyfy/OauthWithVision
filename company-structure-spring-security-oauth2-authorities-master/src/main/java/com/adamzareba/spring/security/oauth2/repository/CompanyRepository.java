package com.adamzareba.spring.security.oauth2.repository;

import com.adamzareba.spring.security.oauth2.model.Company;
import com.google.cloud.vision.v1.ImageAnnotatorClient;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ResourceLoader;

public interface CompanyRepository {

	Company find(Long id);

	Company find(String name);

	List<Company> findAll();

	void create(Company company);

	Company update(Company company);

	void delete(Long id);

	void delete(Company company);

	String GvisionAPI(String path, ImageAnnotatorClient imageAnnotatorClient, ResourceLoader resourceLoader)
			throws Exception;

	String MSvisionAPI(String imageToAnalyze) throws Exception;

	String AwsVisionAPI(String imagePath) throws IOException;
}
