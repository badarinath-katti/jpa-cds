package corp.sap.jpa.crudrepositories;

import java.util.List;

import corp.sap.jpa.entities.Authors;

public interface AuthorsRepositoryCustom {
	
	Iterable<Authors> save(List<Object> author);

}
