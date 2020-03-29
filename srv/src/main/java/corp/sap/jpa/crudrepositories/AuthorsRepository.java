package corp.sap.jpa.crudrepositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import corp.sap.jpa.entities.Authors;

@Repository
public interface AuthorsRepository extends CrudRepository<Authors, Integer>, AuthorsRepositoryCustom {

}
