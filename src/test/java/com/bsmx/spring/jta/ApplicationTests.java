package com.bsmx.spring.jta;

import com.bsmx.spring.jta.service.JtaService;
import com.bsmx.spring.jta.service.ReceivedJtaMessagesService;
import com.github.dockerjava.api.model.Config;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.bytebuddy.matcher.ElementMatchers.isEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestApplicationContextInitializer.class)
@RequiredArgsConstructor
@ActiveProfiles("test")
class ApplicationTests {

	@LocalServerPort
	private int localPort;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ReceivedJtaMessagesService receivedJtaMessagesService;

	@Autowired
	private JtaService jtaService;

	@Test
	void saveMessagesInJmsAndDBWithoutErrors() {
		Map<String, Object> map = new HashMap<>();
		UUID uuid = UUID.randomUUID();
		map.put("message", uuid);

		String baseUrl = "http://localhost:" + localPort;
		String sendJmsMessageUrl = baseUrl + "/sendJmsMessage";

		String result = restTemplate.postForObject(sendJmsMessageUrl, map, String.class);
		assertEquals("OK", result);

		String getMessagesUrl = baseUrl + "/getMessages";
		ResponseEntity<String> response = restTemplate.getForEntity(getMessagesUrl, String.class);

		assertTrue(response.getBody().contains(uuid.toString()));

		boolean found = false;
		for (int i = 0; i < 500; i++) {
			String messagesInJms = receivedJtaMessagesService.getMessages();
			String messagesInDB = jtaService.getMessages();
			found = messagesInJms.contains(uuid.toString()) && messagesInDB.contains(uuid.toString());
			if (found) {
				break;
			}
		}
		assertTrue(found);
	}

	@Test
	void saveMessagesInJmsAndDBWithApplicationErrorsAtEndOfTheFlow() {
		Map<String, Object> map = new HashMap<>();
		UUID uuid = UUID.randomUUID();
		map.put("message", uuid);
		map.put("errorAtEnd", true);

		String baseUrl = "http://localhost:" + localPort;
		String sendJmsMessageUrl = baseUrl + "/sendJmsMessage";

		String result = restTemplate.postForObject(sendJmsMessageUrl, map, String.class);
		assertTrue(result.contains("Internal Server Error"));

		String getMessagesUrl = baseUrl + "/getMessages";
		ResponseEntity<String> response = restTemplate.getForEntity(getMessagesUrl, String.class);

		assertFalse(response.getBody() != null && response.getBody().contains(uuid.toString()));

		boolean foundMessagesInJms = false;
		boolean foundMessagesInDB = false;
		for (int i = 0; i < 500; i++) {
			String messagesInJms = receivedJtaMessagesService.getMessages();
			String messagesInDB = jtaService.getMessages();
			foundMessagesInJms = messagesInJms.contains(uuid.toString());
			foundMessagesInDB = messagesInDB.contains(uuid.toString());
			if (foundMessagesInJms && foundMessagesInDB) {
				break;
			}
		}
		assertFalse(foundMessagesInDB, "Found message in DB");
		assertFalse(foundMessagesInJms, "Found message in JMS");
	}

	@Test
	void saveMessagesInJmsAndDBWithSqlException() {
		Map<String, Object> map = new HashMap<>();
		UUID uuid = UUID.randomUUID();
		String uuidStr = uuid + "1";
		map.put("message", uuidStr);

		String baseUrl = "http://localhost:" + localPort;
		String sendJmsMessageUrl = baseUrl + "/sendJmsMessage";

		String result = restTemplate.postForObject(sendJmsMessageUrl, map, String.class);
		assertTrue(result.contains("Internal Server Error"));

		String getMessagesUrl = baseUrl + "/getMessages";
		ResponseEntity<String> response = restTemplate.getForEntity(getMessagesUrl, String.class);


		assertFalse(response.getBody() != null && response.getBody().contains(uuidStr));

		boolean foundMessagesInJms = false;
		boolean foundMessagesInDB = false;
		for (int i = 0; i < 500; i++) {
			String messagesInJms = receivedJtaMessagesService.getMessages();
			String messagesInDB = jtaService.getMessages();
			foundMessagesInJms = messagesInJms.contains(uuidStr);
			foundMessagesInDB = messagesInDB.contains(uuidStr);
			if (foundMessagesInJms && foundMessagesInDB) {
				break;
			}
		}
		assertFalse(foundMessagesInDB, "Found message in DB");
		assertFalse(foundMessagesInJms, "Found message in JMS");
	}

	@Test
	void saveMessagesInJmsAndDBWithJmsException() {
		Map<String, Object> map = new HashMap<>();
		UUID uuid = UUID.randomUUID();
		StringBuilder sb = new StringBuilder(uuid.toString());
		for (int i = 0; i < 100; i++) {
			sb.append(UUID.randomUUID().toString());
		}
		String uuidStr = sb.toString();
		map.put("largeMessage", uuidStr);

		String baseUrl = "http://localhost:" + localPort;
		String sendJmsMessageUrl = baseUrl + "/sendJmsMessage";

		String result = restTemplate.postForObject(sendJmsMessageUrl, map, String.class);
		assertTrue(result.contains("Internal Server Error"));

		String getMessagesUrl = baseUrl + "/getMessages";
		ResponseEntity<String> response = restTemplate.getForEntity(getMessagesUrl, String.class);


		assertFalse(response.getBody() != null && response.getBody().contains(uuidStr));

		boolean foundMessagesInJms = false;
		boolean foundMessagesInDB = false;
		for (int i = 0; i < 1000; i++) {
			String messagesInJms = receivedJtaMessagesService.getMessages();
			String messagesInDB = jtaService.getMessages();
			foundMessagesInJms = messagesInJms.contains(uuidStr);
			foundMessagesInDB = messagesInDB.contains(uuidStr);
			if (foundMessagesInJms && foundMessagesInDB) {
				break;
			}
		}
		assertFalse(foundMessagesInDB, "Found message in DB");
		assertFalse(foundMessagesInJms, "Found message in JMS");
	}

}
