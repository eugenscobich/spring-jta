package com.bsmx.spring.jta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bsmx.spring.jta.service.JtaService;
import com.bsmx.spring.jta.service.ReceivedJtaMessagesService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestApplicationContextInitializer.class)
@RequiredArgsConstructor
@ActiveProfiles("test")
@Slf4j
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

		String getReceivedMessagesUrl = baseUrl + "/getReceivedMessages";
		boolean found = false;
		for (int i = 0; i < 10; i++) {
			ResponseEntity<String> jmsMessagesResponse = restTemplate.getForEntity(getReceivedMessagesUrl, String.class);
			found = jmsMessagesResponse.getBody().contains(uuid.toString());
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


		String getReceivedMessagesUrl = baseUrl + "/getReceivedMessages";
		boolean found = false;
		for (int i = 0; i < 10; i++) {
			ResponseEntity<String> jmsMessagesResponse = restTemplate.getForEntity(getReceivedMessagesUrl, String.class);
			found = jmsMessagesResponse.getBody().contains(uuid.toString());
			if (found) {
				break;
			}
		}
		assertFalse(found, "Found message in JMS");

	}

	@Test
	void saveMessagesInJmsAndDBWithSqlException() {
		log.info("==============================================");
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

		String getReceivedMessagesUrl = baseUrl + "/getReceivedMessages";
		boolean found = false;
		for (int i = 0; i < 10; i++) {
			ResponseEntity<String> jmsMessagesResponse = restTemplate.getForEntity(getReceivedMessagesUrl, String.class);
			found = jmsMessagesResponse.getBody() != null && jmsMessagesResponse.getBody().contains(uuid.toString());
			if (found) {
				break;
			}
		}
		assertFalse(found, "Found message in JMS");
	}

	@Test
	void saveMessagesInJmsAndDBWithSqlExceptionAndLocalTransaction() {
		Map<String, Object> map = new HashMap<>();
		UUID uuid = UUID.randomUUID();
		String uuidStr = uuid + "1";
		map.put("message", uuidStr);

		String baseUrl = "http://localhost:" + localPort;
		String sendJmsMessageUrl = baseUrl + "/sendJmsMessageInLocalTransaction";

		String result = restTemplate.postForObject(sendJmsMessageUrl, map, String.class);
		assertTrue(result.contains("Internal Server Error"));

		String getMessagesUrl = baseUrl + "/getMessages";
		ResponseEntity<String> response = restTemplate.getForEntity(getMessagesUrl, String.class);


		assertFalse(response.getBody() != null && response.getBody().contains(uuidStr));

		String getReceivedMessagesUrl = baseUrl + "/getReceivedMessages";
		boolean found = false;
		for (int i = 0; i < 10; i++) {
			ResponseEntity<String> jmsMessagesResponse = restTemplate.getForEntity(getReceivedMessagesUrl, String.class);
			found = jmsMessagesResponse.getBody() != null && jmsMessagesResponse.getBody().contains(uuid.toString());
			if (found) {
				break;
			}
		}
		assertFalse(found, "Found message in JMS");
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

		String getReceivedMessagesUrl = baseUrl + "/getReceivedMessages";
		boolean found = false;
		for (int i = 0; i < 10; i++) {
			ResponseEntity<String> jmsMessagesResponse = restTemplate.getForEntity(getReceivedMessagesUrl, String.class);
			found = jmsMessagesResponse.getBody() != null && jmsMessagesResponse.getBody().contains(uuid.toString());
			if (found) {
				break;
			}
		}
		assertFalse(found, "Found message in JMS");
	}

}
