package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import config.Config;
import model.ResponseMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    final static String CHARSET = "UTF-8";

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            final HttpRequestUtils requestUtils = new HttpRequestUtils();
            final BufferedReader br = new BufferedReader(new InputStreamReader(in, CHARSET));
            final String line = br.readLine();

            if(line == null) {
                return;
            }

            final String url = URLDecoder.decode(requestUtils.getUrl(line), CHARSET);
            final String fullUrl = URLDecoder.decode(requestUtils.getFullUrl(line), CHARSET);

            log.debug("line : {}", line);
            log.debug("url : {}", fullUrl);

            final List<String> requestLineList = requestUtils.getRequestLineList(br, line);

            final String body = URLDecoder.decode(requestUtils.getBody(requestUtils, br, requestLineList), CHARSET);
            final Map<String, String> getParams = requestUtils.parseQueryString(fullUrl);
            final Map<String, String> postParams = requestUtils.parseQueryString(body);

            final ResponseMap res = ControllerRegister.get(url).execute(getParams, postParams);
            if(res.redirect != null) {
                final DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos, res.redirect);
                return;
            }

            final byte[] resultBody = Files.readAllBytes(new File(Config.WEBAPP_ADDR + url).toPath());

            log.debug("request line: {}", line);

            final DataOutputStream dos = new DataOutputStream(out);

            response200Header(dos, resultBody.length);
            responseBody(dos, resultBody);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(final DataOutputStream dos, final String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
