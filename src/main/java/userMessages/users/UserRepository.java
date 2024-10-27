package userMessages.users;

public interface UserRepository {
  User findByMsisdn(String msisdn);
  void addUser(User user);
  void updateUserByMsisdn(String msisdn, User user);
}
