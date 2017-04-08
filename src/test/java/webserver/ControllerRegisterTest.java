package webserver;

import controller.Controller;
import controller.UserCreate;
import org.junit.Test;

import static org.junit.Assert.*;

public class ControllerRegisterTest {

    @Test
    public void test_register() {
        final String subject = "주소 값으로 Controller를 찾아낼 수 있어야 한다.";
        assertTrue(ControllerRegister.get("/user/create") instanceof UserCreate);
    }
}