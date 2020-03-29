package corp.sap.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

import corp.sap.EventHandlerFor;
import corp.sap.JpaEntityInterface;

@Entity
@EventHandlerFor(CDSEntity = catalogservice.Authors.class)
public class Authors{

	@Id
	private int ID;
	private String name;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	private String placeOfBirth;
}
