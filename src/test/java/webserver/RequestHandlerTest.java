package webserver;

import org.junit.Before;
import org.junit.Test;
import util.HttpRequestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class RequestHandlerTest {

    private RequestHandler handler;
    private HttpRequestUtils requestUtils;

    @Before
    public void prepare_test() {
        handler = new RequestHandler(null);
        requestUtils = new HttpRequestUtils();
    }

    @Test
    public void test_getUrl() {
        final String url = requestUtils.getUrl("GET /index.html HTTP/1.1");
        assertThat("/index.html", is(url));
    }

    @Test
    public void test_getQueryString() {

        final String url = "/user/create?userId=test&password=testpass&name=name&email=email%40test";
        String query = requestUtils.getQueryString(url);
        System.out.println(query);
    }

    @Test
    public void test_parse() throws UnsupportedEncodingException {
        final String url = "/user/create?userId=test&password=testpass&name=name&email=email%40test";
        final String decodedUrl = URLDecoder.decode(url, "UTF-8");
        final String queryString = requestUtils.getQueryString(decodedUrl);

        System.out.println(decodedUrl);
        Map<String, String> params = requestUtils.parseQueryString(queryString);

        assertThat(params.get("userId"), is("test"));
        assertThat(params.get("password"), is("testpass"));
        assertThat(params.get("name"), is("name"));
        assertThat(params.get("email"), is("email@test"));
    }
}