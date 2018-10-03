package com.foundersrooms;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.foundersrooms.config.SSLUtil;
import com.foundersrooms.domain.Author;
import com.foundersrooms.domain.Book;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.elasticsearchreprository.BookRepository;
import com.foundersrooms.repository.JPABookRepository;
import com.foundersrooms.service.ChatService;
import com.foundersrooms.service.ContactService;
import com.foundersrooms.service.UserService;
import com.foundersrooms.service.UtilityService;
import com.github.javafaker.Faker;


@SpringBootApplication
//@EnableCaching
@EnableJpaRepositories("com.foundersrooms.repository")
@EnableElasticsearchRepositories(basePackages = "com.foundersrooms.elasticsearchreprository")
public class FoundersroomsApplication implements CommandLineRunner {

	@Autowired	
    private ElasticsearchTemplate template;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private JPABookRepository jpaBookRepository;
	
	@Autowired
	private UtilityService utilityService;
	
	@Autowired
	private ChatService chatService;
	
	@Autowired
	private ContactService contactService;
	
	@Autowired
	private UserService userService;
	
	Faker fake=new Faker();
	
	public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException {
		
		SpringApplication.run(FoundersroomsApplication.class, args);
		SSLUtil.turnOffSslChecking();
	}
	
	private Book findAndUpdateAuthors(String bookId) {
		Book book=bookRepository.findOne(bookId);
		Author author1=new Author();
		author1.setAge(fake.random().nextInt(5));
		author1.setName(fake.name().fullName());
		List<Author> authors=new ArrayList<Author>();
		authors.add(author1);
		book.setAuthors(authors);
		return bookRepository.save(book);
	}
	
	void resetContacts() {
		Iterable<User> users=userService.findAllElasticUsers();
		
		for(User item:users) {
			item.setContacts(new HashSet<>());
			item.setElasticContacts(new HashSet<>());
			userService.elasticSave(item);
		}	
	}

	@Override
	public void run(String... args) throws Exception {		
		//resetContacts();
		//contactService.clearAllContacts();
		/*
		chatService.clearAllChatsAndMessages();
		contactService.clearAllContacts();
		*/
		/*
		List<String> sectors=utilityService.loadSectors();
		List<List<String>> jobs=utilityService.loadJobs();
		List<List<String>> services=utilityService.loadServices();
			*/	
		//findAndUpdateAuthors("1");
		// TODO Auto-generated method stub
		/*
		template.putMapping(Book.class);		
		IndexQuery indexQuery = new IndexQuery();
		Book book1=new Book();
		book1.setTitle(fake.gameOfThrones().house());
		book1.setReleaseDate(fake.date().toString());
		Author author1=new Author();
		author1.setAge(fake.random().nextInt(5));
		author1.setName(fake.name().fullName());
		List<Author> authors=new ArrayList<Author>();
		authors.add(author1);
		book1.setAuthors(authors);
		book1.setId(Integer.toString(fake.number().randomDigit()));		
		indexQuery.setId(book1.getId());
		indexQuery.setObject(book1);
		template.index(indexQuery);
		template.refresh(User.class);
		bookRepository.save(book1);
		jpaBookRepository.save(book1);*/
	}
}
