package controller;

import config.annotation.Controller;
import model.ResponseMap;

import java.util.Map;

@Controller(name = "/")
public class Home implements controller.Controller{

    @Override
    public ResponseMap execute(Map<String, String> getParams, Map<String, String> postParams) {

        return new ResponseMap("/index.html");
    }
}
