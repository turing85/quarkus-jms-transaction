package de.turing85.quarkus.jms.transaction;

import java.time.Duration;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import io.quarkus.logging.Log;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;

@Path("send")
@RequiredArgsConstructor
public class Endpoint {
  private final ConnectionFactory connectionFactory;

  @POST
  @Transactional
  public Response send() {
    Log.info("Sending");
    try (JMSContext context = connectionFactory.createContext()) {
      JMSProducer producer = context.createProducer();
      for (int i = 0; i < 10; i++) {
        producer.send(new ActiveMQTopic("numbers"), context.createTextMessage(Integer.toString(i)));
      }
    }
    Log.info("Sleeping");
    try {
      Thread.sleep(Duration.ofSeconds(10).toMillis());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    Log.info("Returning");
    return Response.ok("sent").build();
  }
}
