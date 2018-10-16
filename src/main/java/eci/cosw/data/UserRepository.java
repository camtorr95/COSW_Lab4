package eci.cosw.data;

import org.springframework.data.mongodb.repository.MongoRepository;

import eci.cosw.data.model.User;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String>{
	
	User findByEmail(String email);
	List<User> findByEmailIn(List<String> email);
}
