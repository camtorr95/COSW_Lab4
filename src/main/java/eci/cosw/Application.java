package eci.cosw;

import eci.cosw.data.CustomerRepository;
import eci.cosw.data.TodoRepository;
import eci.cosw.data.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private TodoRepository todoRepository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

//		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);
//		MongoOperations mongoOperation = (MongoOperations) applicationContext.getBean("mongoTemplate");

//		customerRepository.deleteAll();

		customerRepository.save(new Customer("Alice", "Smith"));
		customerRepository.save(new Customer("Bob", "Marley"));
		customerRepository.save(new Customer("Jimmy", "Page"));
		customerRepository.save(new Customer("Freddy", "Mercury"));
		customerRepository.save(new Customer("Michael", "Jackson"));

		System.out.println("Customers found with findAll():");
		System.out.println("-------------------------------");
		for (Customer customer : customerRepository.findAll()) {
			System.out.println(customer);
		}
		System.out.println();

		System.out.println("Todo found with findByResponsible():");
		System.out.println("-------------------------------");
		System.out.println(todoRepository.findByResponsible("charles@natural.com"));
		System.out.println();

//		Query query = new Query();
//		query.addCriteria(Criteria.where("firstName").is("Alice"));
//
//		Customer customer = mongoOperation.findOne(query, Customer.class);
		System.out.println("-------------------------------");
//		System.out.println(customer.getFirstName() + " " + customer.getLastName());

	}
}