package at.wreiner;

import at.wreiner.dto.GenerationRequestDto;
import at.wreiner.entity.GenerationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqSendService {

    private static final Logger log = LoggerFactory.getLogger(RestClient.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey}")
    private String routingKey;

    @Autowired
    public RabbitMqSendService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendToRabbitMQ(GenerationRequest generationRequest) {
        GenerationRequestDto request = new GenerationRequestDto(generationRequest.getPrompt());
        request.setUuid(generationRequest.getUuid());

        log.info("Sending message to RabbitMQ: {}", request.toJSON());

        try {
            rabbitTemplate.convertAndSend("", routingKey, request.toJSON());
        } catch (Exception e) {
            log.warn("Failed to send message to RabbitMQ: {}", e.getMessage());
            return;
        }

        log.info("Message sent to RabbitMQ");
    }
}
