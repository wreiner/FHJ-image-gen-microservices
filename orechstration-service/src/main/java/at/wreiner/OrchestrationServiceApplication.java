package at.wreiner;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class OrchestrationServiceApplication {
    @Bean
    MessageListenerAdapter ingressListenerAdapter(MqttReceiver mqttReceiver) {
        return new MessageListenerAdapter(mqttReceiver, "receiveIngressMessage");
    }

    @Bean
    MessageListenerAdapter generationResponseListenerAdapter(MqttReceiver mqttReceiver) {
        return new MessageListenerAdapter(mqttReceiver, "receiveGenerationResponseMessage");
    }

    @Bean
    SimpleMessageListenerContainer ingressContainer(ConnectionFactory connectionFactory,
                                                    @Qualifier("ingressListenerAdapter") MessageListenerAdapter ingressListenerAdapter) {
        return createContainer(connectionFactory, ingressListenerAdapter, new String[]{"ingress"});
    }

    @Bean
    SimpleMessageListenerContainer generationResponseContainer(ConnectionFactory connectionFactory,
                                                               @Qualifier("generationResponseListenerAdapter") MessageListenerAdapter generationResponseListenerAdapter) {
        return createContainer(connectionFactory, generationResponseListenerAdapter, new String[]{"generation_response"});
    }

    public SimpleMessageListenerContainer createContainer(ConnectionFactory connectionFactory,
                                                          MessageListenerAdapter listenerAdapter,
                                                          @Value("${app.queues}") String[] queues) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queues);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(OrchestrationServiceApplication.class, args);
    }
}
