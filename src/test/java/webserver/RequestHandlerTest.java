package webserver;

import org.junit.Before;
import org.junit.Test;
import util.HttpRequestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class RequestHandlerTest {

    private RequestHandler handler;
    private HttpRequestUtils requestUtils;
    private List<String> requestLines = new LinkedList<>();

    @Before
    public void prepare_test() {
        handler = new RequestHandler(null);
        requestUtils = new HttpRequestUtils();

        requestLines.add("Host: localhost:8080");
        requestLines.add("Connection: keep-alive");
        requestLines.add("Content-Length: 54");
        requestLines.add("Cache-Control: max-age=0");
        requestLines.add("Origin: http://localhost:8080");
        requestLines.add("Upgrade-Insecure-Requests: 1");
        requestLines.add("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        requestLines.add("Content-Type: application/x-www-form-urlencoded");
        requestLines.add("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        requestLines.add("Referer: http://localhost:8080/user/form.html");
        requestLines.add("Accept-Encoding: gzip, deflate, br");
        requestLines.add("Accept-Language: ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4,ja;q=0.2");
        requestLines.add("Cookie: _ga=GA1.1.1960786810.1485090788");
    }

    @Test
    public void test_getUrl() {
        final String url = requestUtils.getUrl("GET /index.html HTTP/1.1");
        assertThat("/index.html", is(url));
    }

    @Test
    public void test_getQueryString() {
        final String query = "userId=test&password=testpass&name=name&email=email%40test";
        final String url = "/user/create?" + query;
        final String queryString = requestUtils.getQueryString(url);
        assertThat(query, is(queryString));
    }

    @Test
    public void test_parse() throws UnsupportedEncodingException {
        final String url = "/user/create?userId=test&password=testpass&name=name&email=email%40test";
        final String decodedUrl = URLDecoder.decode(url, "UTF-8");
        final String queryString = requestUtils.getQueryString(decodedUrl);

        final Map<String, String> params = requestUtils.parseQueryString(queryString);

        assertThat(params.get("userId"), is("test"));
        assertThat(params.get("password"), is("testpass"));
        assertThat(params.get("name"), is("name"));
        assertThat(params.get("email"), is("email@test"));
    }

    @Test
    public void test_content_length() {
        final int length = requestUtils.getRequestContentsLength(requestLines);
        assertThat(length, is(54));
    }
}