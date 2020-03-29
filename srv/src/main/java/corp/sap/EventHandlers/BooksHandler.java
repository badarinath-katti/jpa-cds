/*
 * package corp.sap.EventHandlers;
 * 
 * import java.util.ArrayList; import java.util.HashMap; import java.util.List;
 * import java.util.Map; import java.util.Optional;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.stereotype.Component;
 * 
 * import com.sap.cds.services.cds.CdsReadEventContext; import
 * com.sap.cds.services.cds.CdsService; import
 * com.sap.cds.services.handler.EventHandler; import
 * com.sap.cds.services.handler.annotations.On; import
 * com.sap.cds.services.handler.annotations.ServiceName; import
 * com.sap.cds.services.request.ParameterInfo;
 * 
 * import catalogservice.Books_; import catalogservice.CatalogService_; import
 * corp.sap.jpa.crudrepositories.BooksRepository; import
 * corp.sap.jpa.entities.Books;
 * 
 * @Component
 * 
 * @ServiceName(CatalogService_.CDS_NAME) public class BooksHandler implements
 * EventHandler {
 * 
 * @Autowired public BooksRepository booksRepository;
 * 
 * 
 * public BooksHandler(BooksRepository booksRepository) {
 * 
 * this.booksRepository = booksRepository; }
 * 
 * 
 * @On(event = CdsService.EVENT_READ, entity = Books_.CDS_NAME) public void
 * getBooks(CdsReadEventContext context) {
 * 
 * Iterable<Books> lstAuthors = this.booksRepository.findAll();
 * 
 * List<Map<String, ?>> lst = new ArrayList<Map<String, ?>>(); Map<String,
 * Object> map = null;
 * 
 * ParameterInfo pi = context.getParameterInfo(); if(pi.getQueryParameter("ID")
 * != null) {
 * 
 * Optional<Books> books =
 * this.booksRepository.findById(Integer.parseInt(pi.getQueryParameter("ID")));
 * 
 * if(books.isPresent()) { map = new HashMap<String, Object>(); map.put("ID",
 * Integer.parseInt(pi.getQueryParameter("ID"))); map.put("title",
 * books.get().getTitle()); map.put("stock", books.get().getStock());
 * map.put("author_id", books.get().getAuthor()); lst.add(map); }
 * 
 * } else {
 * 
 * Iterable<Books> books = this.booksRepository.findAll();
 * 
 * for (Books book : books) {
 * 
 * map = new HashMap<String, Object>(); map.put("ID", book.getID());
 * map.put("title", book.getTitle()); map.put("stock", book.getStock());
 * map.put("author_id", book.getAuthor()); lst.add(map); } }
 * context.setResult(lst); context.setCompleted();
 * System.out.println("On read event - Added the result.."); }
 * 
 * }
 */