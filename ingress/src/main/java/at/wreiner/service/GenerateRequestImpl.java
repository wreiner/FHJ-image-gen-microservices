package at.wreiner.service;

import at.wreiner.dto.GenerateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenerateRequestImpl implements GenerateRequestService {

    private static Logger logger = LoggerFactory.getLogger(GenerateRequestImpl.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey}")
    private String routingKey;

    @Autowired
    public GenerateRequestImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public GenerateRequest saveUpdateGenerateRequest(GenerateRequest request) {
        logger.debug("in createGenerateRequest");

        sendToRabbitMQ(request);

        return request;
    }

    @Override
    public GenerateRequest getGenerateRequest(UUID uuid) {
        logger.info("in getGenerateRequest");
        return new GenerateRequest("lala");
    }

    private void sendToRabbitMQ(GenerateRequest request) {
        try {
            rabbitTemplate.convertAndSend("", routingKey, request.toJSON());
            logger.info("Successfully sent message to RabbitMQ");
        } catch (Exception e) {
            logger.error("Failed to send message to RabbitMQ", e);
        }
    }
}
