package eci.cosw.data;

import org.springframework.data.mongodb.repository.MongoRepository;

import eci.cosw.data.model.User;

public interface UserRepository extends MongoRepository<User, String>{
	
	User findByEmail(String email);
}
