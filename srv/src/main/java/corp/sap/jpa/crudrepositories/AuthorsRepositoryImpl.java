package corp.sap.jpa.crudrepositories;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.stereotype.Repository;

import corp.sap.jpa.entities.Authors;

@Repository
@Transactional
public class AuthorsRepositoryImpl implements AuthorsRepositoryCustom {

	//@PersistenceContext
	private EntityManager entityManager;
	private JpaEntityInformation<Authors, ?> jpaEntityInformation;

	public AuthorsRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
		this.jpaEntityInformation = JpaEntityInformationSupport.getEntityInformation(Authors.class, this.entityManager);
	}

	@Override
	public Iterable<Authors> save(List<Object> lstAuthors) {

		List<Authors> authors = new ArrayList<Authors>();
		for (Object author : lstAuthors) {
			authors.add((Authors) author);

			if (jpaEntityInformation.isNew((Authors) author))
				entityManager.persist(author);
			else
				entityManager.merge((Authors) author);
		}

		return authors;
	}

}
