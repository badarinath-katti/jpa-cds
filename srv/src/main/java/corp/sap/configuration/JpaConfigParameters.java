package corp.sap.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class JpaConfigParameters {
	
	public static final String CRUD_REPOSITORY_BEAN = "Crud_Repository_Bean";

	public Map<String, String> keyValuePairs;
	
	public JpaConfigParameters() {
		this.keyValuePairs = new HashMap<String, String>();
	}
	
}
