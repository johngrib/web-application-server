package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            final HttpRequestUtils requestUtils = new HttpRequestUtils();
            final BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            final String line = br.readLine();

            if(line == null) {
                return;
            }

            final String url = requestUtils.getUrl(line);
            final String decoded = URLDecoder.decode(url, "UTF-8");
            log.debug("url : {}", decoded);

            final List<String> requestLineList = requestUtils.getRequestLineList(br, line);

            // 회원가입인 경우
            if(url.startsWith("/user/create")) {
                final int contentLength = requestUtils.getRequestContentsLength(requestLineList);
                final String body = IOUtils.readData(br, contentLength);
                final Map<String, String> params = requestUtils.parseQueryString(body);

                final String userId = params.get("userId");
                final String password = params.get("password");
                final String name = params.get("name");
                final String email = params.get("email");
                final User newUser = new User(userId, password, name, email);
                log.debug("new user : {}", newUser);
            }

            final byte[] resultBody = Files.readAllBytes(new File("./webapp" + url).toPath());

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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
