package controller;

import config.Config;
import config.annotation.Controller;
import db.DataBase;
import model.RequestMap;
import model.ResponseMap;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller(name = "/user/create")
public class UserCreate implements controller.Controller {

    private static final Logger log = LoggerFactory.getLogger(UserCreate.class);

    @Override
    public ResponseMap execute(final RequestMap req) {
        final String userId = req.postParam.get("userId");
        final String password = req.postParam.get("password");
        final String name = req.postParam.get("name");
        final String email = req.postParam.get("email");
        final User newUser = new User(userId, password, name, email);

        if(DataBase.findUserById(userId) == null) {
            DataBase.addUser(newUser);
            log.debug("new user : {}", newUser);
        }

        return new ResponseMap(req).redirect(Config.INDEX);
    }
}
