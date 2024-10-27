package userMessages.messages;

import java.util.HashMap;
import java.util.Map;

public class MessageParser extends Message {
  public MessageParser(String action, String page, String msisdn, EnrichmentType enrichmentType) {
    super(new HashMap<>(Map.of(
        "action", action,
        "page", page,
        "msisdn", msisdn
    )), enrichmentType);
  }
}
