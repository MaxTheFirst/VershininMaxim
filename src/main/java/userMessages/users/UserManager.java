package userMessages.users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserManager implements UserRepository {
  private final List<User> users = Collections.synchronizedList(new ArrayList<>());

  @Override
  public synchronized User findByMsisdn(String msisdn) {
    for (User user : users) {
      try {
        if (user.getPhone().equals(msisdn)) {
          return user;
        }
      } catch (UserHaventNumber e) {
        // pass
      }
    }
    return null;
  }

  @Override
  public synchronized void updateUserByMsisdn(String msisdn, User user) {
    user = findByMsisdn(msisdn);
    if (user != null) {
      users.remove(user);
      users.add(user);
    } else {
      throw new UserNotFound("User with this number not found");
    }
  }

  @Override
  public synchronized void addUser(User user) {
    users.add(user);
  }
}
