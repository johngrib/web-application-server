package controller;

import config.Config;
import config.annotation.Controller;
import model.RequestMap;
import model.ResponseMap;

@Controller(name = Config.DEFAULT)
public class Default implements controller.Controller{

    @Override
    public ResponseMap execute(final RequestMap req) {

        final String redirect = (Config.DEFAULT.equals(req.url))
                ? Config.HOME
                : req.url;

        return new ResponseMap(req).forward(redirect);
    }
}
