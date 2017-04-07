package webserver;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class RequestHandlerTest {

    RequestHandler handler;

    @Before
    public void prepare_test() {
        handler = new RequestHandler(null);

    }
    @Test
    public void test_getUrl() {
        final String url = handler.getUrl("GET /index.html HTTP/1.1");
        assertThat("/index.html", is(url));
    }

}