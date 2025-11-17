package productivity_app_test;

import org.junit.Test;
import static org.junit.Assert.*;

import productivity_app.User;

public class UserTest {
    @Test
    public void testUserConstructor_WithValidName() {
        User user = new User("John");
        assertNotNull(user);
    }

    @Test (expected = NullPointerException.class)
    public void testUserConstructor_WithNullName() {
        User user = new User(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testUserConstructor_WithEmptyName() {
        User user = new User("");
    }

    @Test
    public void testGetUserName_WithValidUsername_ReturnsInitialName() {
        User user = new User("John");
        assertEquals("John", user.getName());
    }

    @Test
    public void testGetUserName_WithValidUsername_ReturnsNewName() {
        User user = new User("John");
        user.setName("Johnny");
        assertEquals("Johnny", user.getName());
    }

    @Test
    public void testSetUserName_WithValidUsername() {
        User user = new User("John");
        user.setName("Johnny");
        assertEquals("Johnny", user.getName());
    }

    @Test (expected = NullPointerException.class)
    public void testSetUserName_WithNullUsername() {
        User user = new User("John");
        user.setName(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetUserName_WithEmptyUsername() {
        User user = new User("John");
        user.setName("");
    }
}
