package eci.cosw.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eci.cosw.data.model.Todo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TodoRepositoryTest {

	@Autowired
	private TodoRepository todoRepository;

	@Test
	public void testFindByResponsible() {
		Todo todo = todoRepository.findByResponsible("charles@natural.com");
		assertEquals("travel to Galapagos", todo.getDescription());
		assertEquals(10, todo.getPriority());
		assertEquals("Jan 10 - 1860", todo.getDueDate());
		assertEquals("charles@natural.com", todo.getResponsible());
		assertEquals("pending", todo.getStatus());
	}
}
