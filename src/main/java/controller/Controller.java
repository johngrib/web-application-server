package controller;

import model.ResponseMap;

import java.util.Map;

/**
 * 주소에 따라 처리할 로직을 정의하는 Controller Inteface
 */
public interface Controller {
    public ResponseMap execute(final Map<String, String> getParams, Map<String, String> postParams);
}
