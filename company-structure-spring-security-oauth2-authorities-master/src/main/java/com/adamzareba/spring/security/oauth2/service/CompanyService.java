package com.adamzareba.spring.security.oauth2.service;

import com.adamzareba.spring.security.oauth2.model.Company;
import com.google.cloud.vision.v1.ImageAnnotatorClient;

import java.util.List;

import org.springframework.core.io.ResourceLoader;

public interface CompanyService {

	Company get(Long id);

	Company get(String name);

	List<Company> getAll();

	void create(Company company);

	Company update(Company company);

	void delete(Long id);

	void delete(Company company);

	String GvisionAPI(String path, ImageAnnotatorClient imageAnnotatorClient, ResourceLoader resourceLoader)
			throws Exception;

	String MSVision(String imageToAnalyze) throws Exception;

	String AwsVision(String imagePath) throws Exception;

}
