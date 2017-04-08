package controller;

import config.annotation.Controller;
import model.ResponseMap;

import java.io.DataOutputStream;
import java.util.Map;

@Controller(name = "/nothing")
public class Nothing implements controller.Controller{

    @Override
    public ResponseMap execute(final Map<String, String> getParams, final Map<String, String> postParams, final DataOutputStream dos) {

        return new ResponseMap(null);
    }
}
