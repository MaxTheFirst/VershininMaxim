package userMessages.messages;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import userMessages.messages.enrichings.ByMSISDN;
import userMessages.users.User;
import userMessages.users.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class EnrichmentServiceTest {

  @Test
  public void shouldReturnEnrichedMessage() {
    Message message = new MessageParser("button_click", "book_card", "88005553535", EnrichmentType.MSISDN);
    UserManager manager = new UserManager();
    manager.addUser(
        new User("Vasya", "Ivanov", "88005553535")
    );
    EnrichmentService service = new EnrichmentService(
        List.of(new ByMSISDN(manager))
    );
    Message enrichMessage = service.enrich(message);
    assertEquals(enrichMessage.content.get("firstName"), "Vasya");
    assertEquals(enrichMessage.content.get("lastName"), "Ivanov");
  }

  @Test
  public void shouldSucceedEnrichmentInConcurrentEnvironmentSuccessfully() throws InterruptedException {
    UserManager manager = new UserManager();
    List<Message> messages = new ArrayList<>();

    for (int i = 0; i < 5; i++) {
      String data = Integer.toString(i);
      manager.addUser(new User("firstName", "lastName", data));
      messages.add(new MessageParser("button_click", "book_card", data, EnrichmentType.MSISDN));
    }

    EnrichmentService service = new EnrichmentService(
        List.of(new ByMSISDN(manager))
    );
    List<Message> enrichmentResults = new CopyOnWriteArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    CountDownLatch latch = new CountDownLatch(5);

    for (Message message : messages) {
      executorService.submit(() -> {
        enrichmentResults.add(service.enrich(message));
        latch.countDown();
      });
    }

    latch.await();

    for (Message enrichmentResult : enrichmentResults) {
      assertEquals(enrichmentResult.content.get("firstName"), "firstName");
      assertEquals(enrichmentResult.content.get("lastName"), "lastName");
    }
  }
}