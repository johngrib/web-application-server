package controller;

import config.Config;
import config.annotation.Controller;
import model.RequestMap;
import model.ResponseMap;

@Controller(name = Config.HOME)
public class Home implements controller.Controller{

    @Override
    public ResponseMap execute(final RequestMap req) {
        return new ResponseMap(req).redirect(Config.INDEX);
    }
}
