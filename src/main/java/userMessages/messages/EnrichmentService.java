package userMessages.messages;

import java.util.List;

public class EnrichmentService {
  private final List<Enriching> enrichings;

  public EnrichmentService(List<Enriching> enrichings) {
    this.enrichings = enrichings;
  }

  public synchronized Message enrich(Message message) {
    if (message == null) {
      throw new IllegalMessage("This message is null");
    }
    for (Enriching enriching: enrichings) {
      if (enriching.type().equals(message.getEnrichmentType())) {
        enriching.enrich(message);
        return message;
      }
    }
    return message;
  }
}
