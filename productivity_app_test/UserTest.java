package productivity_app_test;

import org.junit.Test;
import static org.junit.Assert.*;

import productivity_app.User;

public class UserTest {

  @Test
  public void testUserConstructorWithValidName() {
    User user = new User("John");
    assertNotNull(user);
    assertEquals("John", user.getName());
  }

  @Test(expected = NullPointerException.class)
  public void testUserConstructorWithNullName() {
    new User(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUserConstructorWithEmptyName() {
    new User("   ");
  }

  @Test
  public void testSetNameWithValidName() {
    User user = new User("John");
    user.setName("Alice");
    assertEquals("Alice", user.getName());
  }

  @Test(expected = NullPointerException.class)
  public void testSetNameWithNullName() {
    User user = new User("John");
    user.setName(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNameWithEmptyName() {
    User user = new User("John");
    user.setName("   ");
  }
}
