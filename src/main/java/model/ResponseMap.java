package model;

import config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.RegexUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ResponseMap {

    private static final Logger log = LoggerFactory.getLogger(ResponseMap.class);

    private final RequestMap req;
    private final DataOutputStream dos;
    private final Map<String, String> headerMap = new HashMap<>();
    private static final Map<String, String> contentMap = new HashMap<>();
    {
        contentMap.put("html", "text/html;charset=utf-8");
        contentMap.put("js", "application/javascript");
        contentMap.put("css", "text/css");
    }

    private static final String DEFAULT_TYPE = "html";
    private static final String SET_COOKIE = "Set-Cookie";

    public ResponseMap(final RequestMap req) {
        this.req = req;
        this.dos = new DataOutputStream(req.out);
    }

    public ResponseMap setCookie(final String cookie) {
        return this.add(SET_COOKIE, cookie);
    }

    public ResponseMap add(final String key, final String value) {
        headerMap.put(key, value);
        return this;
    }

    public ResponseMap redirect(final String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            responseHeader();
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ResponseMap forward(final String url) {
        try {

            final File file = new File(Config.WEBAPP_ADDR + url);
            final Path path = file.toPath();
            final byte[] body = Files.readAllBytes(path);

            final String extension = RegexUtil.exec("^.*\\.(.*)$", url).get(1);
            final String typeStr = (contentMap.containsKey(extension))
                    ? extension
                    : DEFAULT_TYPE;

            headerMap.put("Content-Type", contentMap.get(typeStr));
            headerMap.put("Content-Length", String.valueOf(body.length));

            responseOkay();
            responseBody(body);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return this;
    }

    public ResponseMap forwardBody(final String body) {
        final byte[] bodyString = body.getBytes();
        headerMap.put("Content-Type", contentMap.get(DEFAULT_TYPE));
        headerMap.put("Content-Length", String.valueOf(bodyString.length));
        responseOkay();
        responseBody(bodyString);
        return this;
    }

    private void responseOkay() {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            responseHeader();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void responseBody(final byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void responseHeader() {
        try {
            for (final String key : headerMap.keySet()) {
                dos.writeBytes(key + ": " + headerMap.get(key) + " \r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
