package corp.sap.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import catalogservice.Authors;
import corp.sap.EventHandlerFor;
import corp.sap.JpaEntity;
import corp.sap.EventHandlers.JpaEntityEventHandler;
import corp.sap.jpa.crudrepositories.AuthorsRepository;
import corp.sap.jpa.crudrepositories.BooksRepository;
import corp.sap.jpa.crudrepositories.RandomEntityRepository;
import corp.sap.jpa.entities.Books;
import javafx.util.Pair;

@Component(value = "AppConfig")
//@EnableJpaRepositories
public class AppConfig {

	private Map<Class<?>, JpaConfigParameters> jpaEntityConfigParams;
	
	@Autowired
	private AuthorsRepository authorsRepository;
	@Autowired
	private RandomEntityRepository randomEntityRepository;

	public AppConfig() {
		jpaEntityConfigParams = new HashMap<>();
		//this.authorsRepository = authorsRepository;
	}

	public Map<Class<?>, JpaConfigParameters> getJpaEntityConfigParams() {
		return this.jpaEntityConfigParams;
	}
	
	public void updateEntityConfiguration(Class<?> entityType) {
		
		if(this.jpaEntityConfigParams.containsKey(entityType)) return;
		
		
		JpaConfigParameters jpaConfigParameters = new JpaConfigParameters();
		
		Method[] methods = this.getClass().getDeclaredMethods();		
		for(Method method : methods) {
			Annotation[] annotations = method.getDeclaredAnnotations();
			for(Annotation annotation : annotations) {
				if(annotation instanceof EventHandlerFor) {
					EventHandlerFor jpaEntity = (EventHandlerFor) annotation;
					if(jpaEntity.CDSEntity() == entityType) {
						jpaConfigParameters.keyValuePairs.put(JpaConfigParameters.
								  CRUD_REPOSITORY_BEAN, method.getName());
					}
				}
			}
		}		  
		  
		this.jpaEntityConfigParams.put(entityType, jpaConfigParameters);
	}
	
	
	
	
	

	@EventHandlerFor(CDSEntity = corp.sap.jpa.entities.Authors.class)
	public AuthorsRepository getAuthorsRepository() {	  
	  
	  return this.authorsRepository;
	  
	}
	
	@EventHandlerFor(CDSEntity = corp.sap.jpa.entities.RandomEntity.class)
	public RandomEntityRepository getRandomEntityRepository() {	  
	  
	  return this.randomEntityRepository;
	  
	}
}
