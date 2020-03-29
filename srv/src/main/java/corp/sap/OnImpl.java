package corp.sap;

import java.lang.annotation.Annotation;
import com.sap.cds.services.handler.annotations.On;


public class OnImpl implements On {

	public String[] entities;
	
	public OnImpl() {
		this.entities = new String[]{catalogservice.Authors_.CDS_NAME};
	}
	
	@Override
	public Class<? extends Annotation> annotationType() {
		// TODO Auto-generated method stub
		return this.getClass();
	}

	@Override
	public String[] event() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] entity() {
		// TODO Auto-generated method stub
		return this.entities;
	}

	@Override
	public String service() {
		// TODO Auto-generated method stub
		return null;
	}

}
