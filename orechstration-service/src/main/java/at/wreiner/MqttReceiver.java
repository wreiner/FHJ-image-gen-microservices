package at.wreiner;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MqttReceiver {
    public void receiveMessage(String message) {
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa Received <" + message + ">");
	}

	public void receiveMessage(byte[] message) {
		String messageStr = new String(message, StandardCharsets.UTF_8); // Convert byte[] to String
		System.out.println("Received message: " + messageStr);
	}
}
