package at.wreiner;

import at.wreiner.entity.GenerationRequest;
import at.wreiner.repository.GenerationRequestRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MqttReceiver {

	@Autowired
	private GenerationRequestRepository generationRequestRepository;

	private final ObjectMapper objectMapper = new ObjectMapper();

    public void receiveMessage(String message) {
		System.out.println("mqtt message received <" + message + ">");

		try {
			GenerationRequest generationRequest = objectMapper.readValue(message, GenerationRequest.class);
			generationRequest.setStatus("new");
			generationRequestRepository.save(generationRequest);
		} catch (Exception e) {
			System.err.println("error processing message: " + e.getMessage());
		}
	}

	public void receiveMessage(byte[] message) {
		String messageStr = new String(message, StandardCharsets.UTF_8); // Convert byte[] to String
		System.out.println("Received message: " + messageStr);
	}
}
