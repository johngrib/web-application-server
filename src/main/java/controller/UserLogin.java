package controller;

import config.Config;
import config.annotation.Controller;
import db.DataBase;
import model.RequestMap;
import model.ResponseMap;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller(name = "/user/login")
public class UserLogin implements controller.Controller {

    private static final Logger log = LoggerFactory.getLogger(UserLogin.class);
    private static final String ID = "userId";
    private static final String PASSWORD = "password";

    @Override
    public ResponseMap execute(final RequestMap req) {

        final User user = new User(req.postParam.get(ID), req.postParam.get(PASSWORD));

        if(isLoginFailed(user)) {
            log.debug("login failed : {}", user);
            return new ResponseMap(req)
                    .setCookie("logined=false")
                    .redirect("/user/login_failed.html");
        }

        log.debug("login success : {}", user);
        return new ResponseMap(req)
                .setCookie("logined=true")
                .redirect(Config.INDEX)
                ;
    }

    private boolean isLoginFailed(final User user) {
        return ! isLoginSuccess(user);
    }

    private boolean isLoginSuccess(final User user) {

        final User ourUser = DataBase.findUserById(user.getUserId());
        final String requestPassword = user.getPassword();

        if(ourUser == null) {
            return false;
        }

        if(ourUser.getPassword().equals(requestPassword)) {
            return true;
        }
        return false;
    }

}
