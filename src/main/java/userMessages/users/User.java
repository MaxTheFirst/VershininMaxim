package userMessages.users;

public class User {
  private String phone;
  private String firstName;
  private String lastName;

  public User(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public User(String firstName, String lastName, String phone) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.phone = phone;
  }

  public synchronized void setPhone(String phone) {
    this.phone = phone;
  }

  public String getPhone() {
    if (phone == null) {
      throw new UserHaventNumber("User number is null");
    }
    return phone;
  }

  public synchronized void updateUserData(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

}
