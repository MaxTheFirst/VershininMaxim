package userMessages.messages;

import java.util.Map;

public class Message {
  public Map<String, String> content;

  public EnrichmentType getEnrichmentType() {
    return enrichmentType;
  }

  private final EnrichmentType enrichmentType;

  public Message(Map<String, String> content, EnrichmentType enrichmentType) {
    this.content = content;
    this.enrichmentType = enrichmentType;
  }
}
