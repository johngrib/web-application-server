package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RequestMap {

    private static final Logger log = LoggerFactory.getLogger(RequestMap.class);

    public final Map<String, String> getParam;
    public final Map<String, String> postParam;
    public final String url;
    public final String fullUrl;
    private final static String CHARSET = "UTF-8";
    public final InputStream is;
    public final OutputStream out;
    public final Map<String, String> headerParam;

    public RequestMap(final Map<String, String> getParam, final Map<String, String> postParam,
                      Map<String, String> headerParam, final String url, final InputStream is, final OutputStream out) {
        this.getParam = Collections.unmodifiableMap(getParam);
        this.postParam = Collections.unmodifiableMap(postParam);
        this.headerParam = Collections.unmodifiableMap(headerParam);
        this.url = url;
        this.fullUrl = null;
        this.is = is;
        this.out = out;
    }

    static public RequestMap build(final InputStream is, OutputStream out) {
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(is, CHARSET));
            String line = br.readLine();

            log.debug("requested line : {}", line);

            if (line == null) {
                return null;
            }

            final String url = URLDecoder.decode(HttpRequestUtils.getUrl(line), CHARSET);
            final String fullUrl = URLDecoder.decode(HttpRequestUtils.getFullUrl(line), CHARSET);

            final List<String> requestLineList = HttpRequestUtils.getRequestLineList(br, line);
            final String body = URLDecoder.decode(HttpRequestUtils.getBody(br, requestLineList), CHARSET);

            final Map<String, String> getParam = Collections.unmodifiableMap(HttpRequestUtils.parseQueryString(fullUrl));
            final Map<String, String> postParam = Collections.unmodifiableMap(HttpRequestUtils.parseQueryString(body));
            final Map<String, String> headerParam = Collections.unmodifiableMap(HttpRequestUtils.getHeaderMap(requestLineList));

            log.debug("url : {}", url);
            log.debug("fullUrl : {}", fullUrl);
            log.debug("body : {}", body);

            return new RequestMap(getParam, postParam, headerParam, url, is, out);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
