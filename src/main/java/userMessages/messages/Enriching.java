package userMessages.messages;

public interface Enriching {
  EnrichmentType type();

  Message enrich(Message input);
}
