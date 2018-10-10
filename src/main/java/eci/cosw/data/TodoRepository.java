package eci.cosw.data;

import org.springframework.data.mongodb.repository.MongoRepository;

import eci.cosw.data.model.Todo;

public interface TodoRepository extends MongoRepository<Todo, String>{
	
	Todo findByResponsible(String email);
}
