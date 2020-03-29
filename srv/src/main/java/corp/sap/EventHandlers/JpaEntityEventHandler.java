package corp.sap.EventHandlers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.request.ParameterInfo;

import catalogservice.CatalogService_;
import corp.sap.EventHandlerFor;
import corp.sap.JpaEntityInterface;
import corp.sap.configuration.AppConfig;
import corp.sap.configuration.JpaConfigParameters;

import com.sap.cds.ql.CdsName;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;

import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.draft.DraftService;

@Component
@ServiceName(CatalogService_.CDS_NAME)
public class JpaEntityEventHandler implements EventHandler {
	
	private AppConfig appConfig;

	private static final String FINDALL_METHOD = "findAll";
	private static final String FINDBYID_METHOD = "findById";
	private static final String SAVE_METHOD = "save";
	private static final String SAVEALL_METHOD = "saveAll";
	private static final String DELETE_METHOD = "delete";

	// Methods related to Optional class
	private static final String ISPRESENT_METHOD = "isPresent";
	private static final String GET_METHOD = "get";

	// List Methods
	private static final String SIZE_METHOD = "size";

	public JpaEntityEventHandler(ApplicationContext appContext, AppConfig appConfig) {

		this.appConfig = appConfig;
	}

	@Before(event = CdsService.EVENT_READ)
	public void readEntities_Before(CdsReadEventContext context) {
		System.out.println("Before event..");
	}

	@On(event = CdsService.EVENT_READ)
	private void readEntities_On(CdsReadEventContext context)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IntrospectionException, NoSuchMethodException, ClassNotFoundException {

		Class<?> jpaClass = null;
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(EventHandlerFor.class));
		
		outerloop:
		for (BeanDefinition bd : scanner.findCandidateComponents("corp.sap")) {

			for (Annotation annotation : Class.forName(bd.getBeanClassName()).getDeclaredAnnotations()) {

				if (annotation instanceof EventHandlerFor) {

					EventHandlerFor eventHandlerFor = (EventHandlerFor) annotation;

					if (eventHandlerFor.CDSEntity().getDeclaredAnnotation(CdsName.class).value().equals(context.getTarget()
							.getQualifiedName())) {
						jpaClass = Class.forName(bd.getBeanClassName());
						break outerloop;						
					}						
					else break;
				}
			}
		}

		/*
		 * Annotation[] annotations = AuthorsHandler.class.getAnnotations(); for
		 * (Annotation annotation : annotations) {
		 * 
		 * if (annotation instanceof EventHandlerFor) {
		 * 
		 * EventHandlerFor eventHandlerFor = (EventHandlerFor) annotation; jpaClass =
		 * eventHandlerFor.CDSEntity(); } }
		 */
		
		if (jpaClass != null) {
			/*
			 * Map<Class<?>, JpaConfigParameters> JpaEntityConfigParams = (Map<Class<?>,
			 * JpaConfigParameters>) appContext .getBean("getJpaEntityConfigParams");
			 */
			this.appConfig.updateEntityConfiguration(jpaClass);
			Object jpaEntityRepository = this.appConfig.getClass()
					.getDeclaredMethod(this.appConfig.getJpaEntityConfigParams().get(jpaClass).keyValuePairs
							.get(JpaConfigParameters.CRUD_REPOSITORY_BEAN))
					.invoke(this.appConfig);

			List<Map<String, ?>> lstResult = new ArrayList<Map<String, ?>>();
			Map<String, Object> entityFields = null;

			ParameterInfo pi = context.getParameterInfo();
			if (pi.getQueryParameter("ID") != null) {

				Object persistentEntity = jpaEntityRepository.getClass().getDeclaredMethod(FINDBYID_METHOD, Object.class)
						.invoke(jpaEntityRepository, Integer.parseInt(pi.getQueryParameter("ID")));

				if ((boolean) persistentEntity.getClass().getDeclaredMethod(ISPRESENT_METHOD).invoke(persistentEntity)) {

					entityFields = new HashMap<String, Object>();
					for (Field field : jpaClass.getDeclaredFields()) {
						entityFields.put(field.getName(),
								new PropertyDescriptor(field.getName(), jpaClass).getReadMethod()
										.invoke(persistentEntity.getClass().getDeclaredMethod(GET_METHOD).invoke(persistentEntity)));
					}
					lstResult.add(entityFields);
				}
			}

			else {
				Object persistentEntities = jpaEntityRepository.getClass().getDeclaredMethod(FINDALL_METHOD)
						.invoke(jpaEntityRepository);

				for (int entityCounter = 0; entityCounter < (int) persistentEntities.getClass().getDeclaredMethod(SIZE_METHOD)
						.invoke(persistentEntities); entityCounter++) {

					Object persistentEntity = persistentEntities.getClass().getDeclaredMethod(GET_METHOD, int.class).invoke(persistentEntities,
							entityCounter);

					entityFields = new HashMap<String, Object>();
					for (Field field : jpaClass.getDeclaredFields()) {
						entityFields.put(field.getName(),
								new PropertyDescriptor(field.getName(), jpaClass).getReadMethod().invoke(persistentEntity));
					}
					entityFields.put("count", 3);
					lstResult.add(entityFields);

				}
			}

			context.setResult(lstResult);
			context.setCompleted();
		}

		System.out.println("On read event - Added the result..");
	}

	//@After(event = CdsService.EVENT_READ)
	public void readPersistentEntity_After(CdsReadEventContext context) {

		System.out.println("After read event..");
	}

	@Before(event = CdsService.EVENT_CREATE)
	public void savePersistentEntities_Before(CdsCreateEventContext context) throws NoSuchMethodException, SecurityException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {

		/*
		 * Method targetMethod =
		 * JpaEntityEventHandler.class.getDeclaredMethod("savePersistentEntities",
		 * CdsCreateEventContext.class, List.class);// .getAnnotation(On.class); On
		 * newAnnotation = new OnImpl();
		 * 
		 * Class<?> executableClass = Class.forName("java.lang.reflect.Executable");
		 * Field field = executableClass.getDeclaredField("declaredAnnotations");
		 * field.setAccessible(true);
		 * 
		 * @SuppressWarnings("unchecked") Map<Class<? extends Annotation>, Annotation>
		 * annotations = (Map<Class<? extends Annotation>, Annotation>) field
		 * .get(targetMethod);
		 */
		// annotations.put(On.class, newAnnotation);

	}

	@On(event = CdsService.EVENT_CREATE, entity = { "*" })
	public void savePersistentEntities(CdsCreateEventContext context, List<JpaEntityInterface> persistentEntities)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException,
			NoSuchMethodException, InstantiationException, ClassNotFoundException, SecurityException {

		List<Map<String, ?>> lst = new ArrayList<Map<String, ?>>();
		Map<String, Object> map = new HashMap<>();
		Class<?> jpaClass = null;

		List<Object> lstPersistentEntities_jpa = new ArrayList<Object>();

		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(EventHandlerFor.class));
		
		for (BeanDefinition bd : scanner.findCandidateComponents("corp.sap")) {

			for (Annotation annotation : Class.forName(bd.getBeanClassName()).getDeclaredAnnotations()) {

				if (annotation instanceof EventHandlerFor) {
	
					EventHandlerFor eventHandlerFor = (EventHandlerFor) annotation;
					
					if (eventHandlerFor.CDSEntity().getDeclaredAnnotation(CdsName.class).value().equals(context.getTarget()
							.getQualifiedName())) {
						jpaClass = Class.forName(bd.getBeanClassName());
						
						for (int entityCounter = 0; entityCounter < (int) persistentEntities.getClass().getDeclaredMethod(SIZE_METHOD)
								.invoke(persistentEntities); entityCounter++) {
		
							Object persistentEntity = persistentEntities.getClass().getDeclaredMethod(GET_METHOD, int.class).invoke(persistentEntities,
									entityCounter);
		
							map = new HashMap<String, Object>();
							Object persistentEntity_jpa = jpaClass.newInstance();
		
							for (Field field : jpaClass.getDeclaredFields()) {
		
								map.put(field.getName(),
										// new PropertyDescriptor("ID",
										// author.getClass()).getReadMethod().invoke(author)
										PropertyUtils.getProperty(persistentEntity, field.getName()));
		
								PropertyUtils.setProperty(persistentEntity_jpa, field.getName(), map.get(field.getName()));
							}
							lst.add(map);
							lstPersistentEntities_jpa.add(persistentEntity_jpa);
						}
					}
				}
			}
		}
		context.setResult(lst);
		context.setCompleted();

		if (jpaClass != null) {

			this.appConfig.updateEntityConfiguration(jpaClass);
			Object jpaEntityRepository = this.appConfig.getClass()
					.getDeclaredMethod(this.appConfig.getJpaEntityConfigParams().get(jpaClass).keyValuePairs
							.get(JpaConfigParameters.CRUD_REPOSITORY_BEAN))
					.invoke(this.appConfig);
			if(lstPersistentEntities_jpa.size() == 1) {
				jpaEntityRepository.getClass().getDeclaredMethod(SAVE_METHOD, Object.class).invoke(jpaEntityRepository,
						lstPersistentEntities_jpa.get(0));
				
			} else {
				jpaEntityRepository.getClass().getDeclaredMethod(SAVE_METHOD, List.class).invoke(jpaEntityRepository,
						lstPersistentEntities_jpa);
			}
			
		}
	}

	@On(event = DraftService.EVENT_UPDATE)

	public void updatePersistentEntities(CdsUpdateEventContext context, List<JpaEntityInterface> persistentEntities) {

		System.out.println("");
	}

	@On(event = CdsService.EVENT_DELETE, entity = { "*" })
	public void deletePersistentEntities(CdsDeleteEventContext context, List<JpaEntityInterface> persistentEntities) {
		System.out.println("On delete event..");
		context.setResult(new ArrayList<Map<String, ?>>() {
			{
				add(new HashMap<String, Object>());
			}
		});
		context.setCompleted();
	}

}
