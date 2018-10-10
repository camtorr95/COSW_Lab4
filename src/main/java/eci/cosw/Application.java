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
		System.out.println("Todo found with findByResponsible():");
		System.out.println(todoRepository.findByResponsible("charles@natural.com"));
		System.out.println();

		Query query = new Query();
		query.addCriteria(Criteria.where("firstName").is("Alice"));
		Customer customer = mongoOperation.findOne(query, Customer.class);
		System.out.println("-------------------------------");
		System.out.println("Customer found with query firstName is Alice:");
		System.out.println(customer.getFirstName() + " " + customer.getLastName());

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

		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		String[] emails = { "smeier@yahoo.com", "wenzlaff@yahoo.ca", "credmond@verizon.net", "jugalator@outlook.com",
				"dmath@comcast.net", "carcus@mac.com", "lydia@att.net", "emmanuel@hotmail.com", "frederic@hotmail.com",
				"rande@me.com" };
		String[] names = { "smeier", "wenzlaff", "credmond", "jugalator", "dmath", "carcus", "lydia", "emmanuel",
				"frederic", "rande" };
		String[] status = { "pending", "completed", "cancelled" };

		String[] todos = {

				"The river stole the gods.", "I would have gotten the promotion, but my attendance wasn’t good enough.",
				"A glittering gem is not enough.", "The shooter says goodbye to his love.",
				"She did not cheat on the test, for it was not the right thing to do.",
				"Sixty-Four comes asking for bread.",
				"I was very proud of my nickname throughout high school but today- I couldn’t be any different to what my nickname was.",
				"I checked to make sure that he was still alive.",
				"Should we start class now, or should we wait for everyone to get here?",
				"He ran out of money, so he had to stop playing poker.",
				"Joe made the sugar cookies; Susan decorated them.",
				"She borrowed the book from him many years ago and hasn't yet returned it.",
				"The quick brown fox jumps over the lazy dog.",
				"The clock within this blog and the clock on my laptop are 1 hour different from each other.",
				"She only paints with bold colors; she does not like pastels.",
				"She always speaks to him in a loud voice.",
				"A song can make or ruin a person’s day if they let it get to them.",
				"I hear that Nancy is very pretty.", "We have a lot of rain in June.",
				"The memory we used to share is no longer coherent.",
				"He said he was not there yesterday; however, many people saw him there.", "Hurry!",
				"He told us a very exciting adventure story.", "They got there early, and they got really good seats.",
				"I will never be this young again. Ever. Oh damn… I just got older.",
				"Malls are great places to shop; I can find everything I need under one roof.",
				"The shooter says goodbye to his love.", "They got there early, and they got really good seats.",
				"I checked to make sure that he was still alive.", "She did her best to help him."

		};

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

		todoRepository.save(new Todo("travel to Galapagos", 10, "Jan 10 - 1860", "charles@natural.com", "pending"));
		for (int i = 0; i < todos.length; ++i) {
			int month = gen.nextInt(12) + 1;
			int day = (month <= 7 ? (month == 2 ? gen.nextInt(28) : month % 2 == 0 ? gen.nextInt(30) : gen.nextInt(31))
					: (month % 2 == 0 ? gen.nextInt(31) : gen.nextInt(30))) + 1;
			String dueDate = String.format("%s %d - %d", months[month - 1], day, gen.nextInt(2018 + 1 - 2005) - 2005);
			todoRepository.save(new Todo(todos[i], gen.nextInt(10) + 1, dueDate, emails[gen.nextInt(emails.length)],
					status[gen.nextInt(status.length)]));
		}
	}
}