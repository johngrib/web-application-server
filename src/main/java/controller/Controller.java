package controller;

import model.RequestMap;
import model.ResponseMap;

/**
 * 주소에 따라 처리할 로직을 정의하는 Controller Inteface
 */
public interface Controller {
    public ResponseMap execute(final RequestMap req);
}
