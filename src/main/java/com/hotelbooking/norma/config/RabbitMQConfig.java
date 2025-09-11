// package com.hotelbooking.norma.config;
// import org.springframework.amqp.core.Queue;
// import org.springframework.amqp.rabbit.connection.ConnectionFactory;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
// import org.springframework.amqp.support.converter.MessageConverter;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class RabbitMQConfig {

//     public static final String BOOKING_QUEUE = "booking_requests_queue";
//     public static final String PAYMENT_QUEUE = "payment_initialization_queue";

//     @Bean
//     public Queue bookingQueue() {
//         return new Queue(BOOKING_QUEUE, true);
//     }

//     @Bean
//     public Queue paymentQueue() {
//         return new Queue(PAYMENT_QUEUE, true); // The 'true' makes the queue durable
//     }
//     @Bean
//     public MessageConverter jsonMessageConverter() {
//         // This bean tells Spring AMQP to use JSON for message serialization
//         return new Jackson2JsonMessageConverter();
//     }

//     @Bean
//     public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
//         final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//         // Set the message converter on the RabbitTemplate
//         rabbitTemplate.setMessageConverter(jsonMessageConverter());
//         return rabbitTemplate;
//     }
// }