package com.adamzareba.spring.security.oauth2.repository;

import com.adamzareba.spring.security.oauth2.model.Address;
import com.adamzareba.spring.security.oauth2.model.Car;
import com.adamzareba.spring.security.oauth2.model.Company;
import com.adamzareba.spring.security.oauth2.model.Company_;
import com.adamzareba.spring.security.oauth2.model.Department;
import com.adamzareba.spring.security.oauth2.model.Department_;
import com.adamzareba.spring.security.oauth2.model.Employee;
import com.adamzareba.spring.security.oauth2.model.Employee_;
import com.adamzareba.spring.security.oauth2.model.Office;
import com.adamzareba.spring.security.oauth2.model.Office_;
import com.adamzareba.spring.security.oauth2.service.VisionService;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository
public class CompanyRepositoryImpl implements CompanyRepository {

	@PersistenceContext
	private EntityManager entityManager;

	VisionService visionService = new VisionService();

	@Override
	public Company find(Long id) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Company> query = builder.createQuery(Company.class);

		Root<Company> root = query.from(Company.class);
		Fetch<Company, Car> carsFetch = root.fetch(Company_.cars, JoinType.LEFT);

		Fetch<Company, Department> departmentFetch = root.fetch(Company_.departments, JoinType.LEFT);

		Fetch<Department, Employee> employeeFetch = departmentFetch.fetch(Department_.employees, JoinType.LEFT);

		// employeeFetch.fetch(Employee_.address, JoinType.LEFT);
		Fetch<Employee, Address> addressFetch = employeeFetch.fetch(Employee_.address, JoinType.LEFT);

		// departmentFetch.fetch(Department_.offices, JoinType.LEFT);
		Fetch<Department, Office> officeFetch = departmentFetch.fetch(Department_.offices, JoinType.LEFT);

		Fetch<Office, Address> addrFetch = officeFetch.fetch(Office_.address, JoinType.LEFT);

		query.select(root).distinct(true);
		Predicate idPredicate = builder.equal(root.get(Company_.id), id);
		query.where(builder.and(idPredicate));

		return DataAccessUtils.singleResult(entityManager.createQuery(query).getResultList());
	}

	@Override
	public Company find(String name) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Company> query = builder.createQuery(Company.class);

		Root<Company> root = query.from(Company.class);
		root.fetch(Company_.cars, JoinType.LEFT);
		Fetch<Company, Department> departmentFetch = root.fetch(Company_.departments, JoinType.LEFT);
		Fetch<Department, Employee> employeeFetch = departmentFetch.fetch(Department_.employees, JoinType.LEFT);
		employeeFetch.fetch(Employee_.address, JoinType.LEFT);
		departmentFetch.fetch(Department_.offices, JoinType.LEFT);

		query.select(root).distinct(true);
		Predicate idPredicate = builder.equal(root.get(Company_.name), name);
		query.where(builder.and(idPredicate));

		return DataAccessUtils.singleResult(entityManager.createQuery(query).getResultList());
	}

	@Override
	public List<Company> findAll() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);
		query.select(root).distinct(true);
		TypedQuery<Company> allQuery = entityManager.createQuery(query);
		return allQuery.getResultList();
	}

	@Override
	public void create(Company company) {
		entityManager.persist(company);
	}

	@Override
	public Company update(Company company) {
		return entityManager.merge(company);
	}

	@Override
	public void delete(Long id) {
		Company company = entityManager.find(Company.class, id);
		delete(company);
	}

	@Override
	public void delete(Company company) {
		entityManager.remove(company);
	}

	@Override
	public String GvisionAPI(String path, ImageAnnotatorClient imageAnnotatorClient, ResourceLoader resourceLoader)
			throws Exception {
		return visionService.getGVision(path, imageAnnotatorClient, resourceLoader);

	}

	public String MSvisionAPI(String path) throws Exception {
		return visionService.getMSVision(path);

	}

	@Override
	public String AwsVisionAPI(String imagePath) throws IOException {
		return visionService.getAwsVision(imagePath);
	}
}
