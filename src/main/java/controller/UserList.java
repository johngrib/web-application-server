package controller;

import config.annotation.Controller;
import db.DataBase;
import model.RequestMap;
import model.ResponseMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

@Controller(name = "/user/list")
public class UserList implements controller.Controller {

    private static final Logger log = LoggerFactory.getLogger(UserList.class);
    private static final String BR = "<br>";
    private static final String NONE = "None";

    @Override
    public ResponseMap execute(final RequestMap req) {

        final String cookie = req.headerParam.get("Cookie");
        final boolean logined = "true".equals(HttpRequestUtils.parseCookies(cookie).get("logined"));

        if(logined) {
            final String viewString = DataBase.findAll().stream()
                    .map(user -> user.toString())
                    .reduce((x, y) -> x + BR + y)
                    .orElse(NONE);
            return new ResponseMap(req).forwardBody(viewString);
        }
        return new ResponseMap(req).redirect("/user/login.html");
    }

}
