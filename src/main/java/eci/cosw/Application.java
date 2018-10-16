package eci.cosw;

import eci.cosw.data.CustomerRepository;
import eci.cosw.data.TodoRepository;
import eci.cosw.data.UserRepository;
import eci.cosw.data.model.Customer;
import eci.cosw.data.model.Todo;
import eci.cosw.data.model.User;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Random;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private TodoRepository todoRepository;

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		boolean poblar = false;

		if (poblar)
			init();

		querys();
	}

	private void querys() throws Exception {
		@SuppressWarnings("resource")
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);
		MongoOperations mongoOperation = (MongoOperations) applicationContext.getBean("mongoTemplate");

		System.out.println("-------------------------------");
		System.out.println("Customers found with findAll():");
		for (Customer customer : customerRepository.findAll()) {
			System.out.println(customer);
		}
		System.out.println();

		System.out.println("-------------------------------");
		System.out.println("Todo found with findByResponsible(charles@natural):");
		System.out.println(todoRepository.findByResponsible("charles@natural.com"));
		System.out.println();

		Query query = new Query();
		query.addCriteria(Criteria.where("firstName").is("Alice"));
		Customer customer = mongoOperation.findOne(query, Customer.class);
		System.out.println("-------------------------------");
		System.out.println("Customer found with query firstName is Alice:");
		System.out.println(customer.getFirstName() + " " + customer.getLastName());

		System.out.println("-------------------------------");
		System.out.println("Todos that the due date has expired: " + new Date());
		query = new Query();
		query.addCriteria(Criteria.where("dueDate").lt(new Date()));
		List<Todo> todos = mongoOperation.find(query, Todo.class);
		todos.forEach(System.out::println);
		
		System.out.println("-------------------------------");
		System.out.println("Todos that are assigned to given user and have priority greater equal to 5: griffith@behelit.com");
		query = new Query();
		String given_user = "griffith@behelit.com";
		query.addCriteria(Criteria.where("priority").gte(5).and("responsible").is(given_user));
		todos = mongoOperation.find(query, Todo.class);
		todos.forEach(System.out::println);
		
		System.out.println("-------------------------------");
		System.out.println("List users that have assigned more than 2 Todos.");
		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.group("responsible").count().as("responsibles"),
			Aggregation.match(Criteria.where("responsibles").gt(2)),
			Aggregation.project("_id"));
		List<String> mappedResult = mongoOperation.aggregate(aggregation, "todo", String.class).getMappedResults();
		List<String> emails = mappedResult.stream()
							.map(bson -> bson.split(" ")[3].trim())
							.map(email -> email.substring(1, email.length() -1))
							.collect(Collectors.toList());
		userRepository.findByEmailIn(emails).forEach(System.out::println);
		
		System.out.println("-------------------------------");
		System.out.println("Todo list that contains the description with a length greater than 30 characters");
		aggregation = Aggregation.newAggregation(
                Aggregation.project("_id", "description", "priority", "dueDate", "responsible", "status", "_class").andExpression("strLenCP(description)").as("length"),
                Aggregation.match(Criteria.where("length").gt(30)));
		todos = mongoOperation.aggregate(aggregation, "todo", Todo.class).getMappedResults();
		todos.forEach(System.out::println);
	}

	private void init() throws Exception {
		customerRepository.deleteAll();
		userRepository.deleteAll();
		todoRepository.deleteAll();
		genData();
	}

	private void genData() throws Exception {
		Random gen = new Random();

		customerRepository.save(new Customer("Alice", "Smith"));
		customerRepository.save(new Customer("Bob", "Marley"));
		customerRepository.save(new Customer("Jimmy", "Page"));
		customerRepository.save(new Customer("Freddy", "Mercury"));
		customerRepository.save(new Customer("Michael", "Jackson"));

		String[] emails = { "smeier@yahoo.com", "griffith@behelit.com", "credmond@verizon.net", "jugalator@outlook.com",
				"dmath@comcast.net", "carcus@mac.com", "lydia@att.net", "emmanuel@hotmail.com", "frederic@hotmail.com",
				"rande@me.com" };
		String[] names = { "smeier", "wenzlaff", "credmond", "jugalator", "dmath", "carcus", "lydia", "emmanuel",
				"frederic", "rande" };
		String[] status = { "pending", "completed", "cancelled" };

		String[] todos = new String[30];
		for(int i=0; i<30; ++i){
			todos[i] = "This is the TODO #" + i + (gen.nextInt(10) < 3 ? ". This TODO also has more than 30 characters" : "");
		}

		userRepository.save(new User(12345, "Charles Darwin", "charles@natural.com"));
		HashSet<Integer> idSet = new HashSet<>();
		idSet.add(12345);
		for (int i = 0; i < emails.length; ++i) {
			int id = gen.nextInt(99999 + 1 - 10000) + 10000;
			while (idSet.contains(id)) {
				id = gen.nextInt(99999 + 1 - 10000) + 10000;
			}
			userRepository.save(new User(id, names[i % names.length], emails[i]));
		}

		Date charlesDate = new SimpleDateFormat("dd-MM-yyyy").parse("10-01-1860");
		todoRepository.save(new Todo("travel to Galapagos", 10, charlesDate, "charles@natural.com", "pending"));
		for (int i = 0; i < todos.length; ++i) {
			todoRepository.save(new Todo(todos[i], gen.nextInt(10) + 1, getRandomDate(gen), emails[gen.nextInt(emails.length)],
					status[gen.nextInt(status.length)]));
		}
	}
	
	private static Date getRandomDate(Random gen) throws Exception{
		int month = gen.nextInt(12) + 1;
		int day = (month <= 7 ? (month == 2 ? gen.nextInt(28) : month % 2 == 0 ? gen.nextInt(30) : gen.nextInt(31))
				: (month % 2 == 0 ? gen.nextInt(31) : gen.nextInt(30))) + 1;
		int year = gen.nextInt(2025 - 2009 + 1) + 2009;
		return new SimpleDateFormat("dd-MM-yyyy").parse(String.format("%d-%d-%d", day,month,year));
	}
}