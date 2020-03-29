package corp.sap.jpa.crudrepositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import catalogservice.RandomEntity;
import corp.sap.jpa.entities.Authors;

@Repository
public interface RandomEntityRepository extends CrudRepository<corp.sap.jpa.entities.RandomEntity, Integer> {

}
