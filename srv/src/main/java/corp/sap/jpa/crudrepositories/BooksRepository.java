package corp.sap.jpa.crudrepositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import corp.sap.jpa.entities.Books;

@Repository
public interface BooksRepository extends CrudRepository<Books, Integer> {

}
