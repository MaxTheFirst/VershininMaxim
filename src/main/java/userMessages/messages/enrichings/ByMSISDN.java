package userMessages.messages.enrichings;

import userMessages.messages.Enriching;
import userMessages.messages.EnrichmentType;
import userMessages.messages.Message;
import userMessages.users.User;
import userMessages.users.UserManager;

public class ByMSISDN implements Enriching {
  private final UserManager userManager;

  public ByMSISDN(UserManager userManager) {
    this.userManager = userManager;
  }

  @Override
  public EnrichmentType type() {
    return EnrichmentType.MSISDN;
  }

  @Override
  public Message enrich(Message input) {
    if (input.content.containsKey("msisdn")) {
      User user = userManager.findByMsisdn(input.content.get("msisdn"));
      if (user != null) {
        input.content.put("firstName", user.getFirstName());
        input.content.put("lastName", user.getLastName());
      }
    }
    return input;
  }
}
