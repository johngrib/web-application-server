package controller;

import config.annotation.Controller;
import model.ResponseMap;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.util.Map;

@Controller(name = "/user/create")
public class UserCreate implements controller.Controller {

    private static final Logger log = LoggerFactory.getLogger(UserCreate.class);

    @Override
    public ResponseMap execute(final Map<String, String> getParams, final Map<String, String> postParams, final DataOutputStream dos) {

        final String userId = postParams.get("userId");
        final String password = postParams.get("password");
        final String name = postParams.get("name");
        final String email = postParams.get("email");
        final User newUser = new User(userId, password, name, email);

        log.debug("new user : {}", newUser);

        return new ResponseMap("/index.html");
    }
}
