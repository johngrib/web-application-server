package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            final BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            final String line = br.readLine();

            if(line == null) {
                return;
            }

            final String url = getUrl(line);

            final byte[] resultBody = Files.readAllBytes(new File("./webapp" + url).toPath());

            log.debug("request line: {}", line);

            final DataOutputStream dos = new DataOutputStream(out);

            response200Header(dos, resultBody.length);
            responseBody(dos, resultBody);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    String getUrl(final String requestLine) {
        if(requestLine == null) {
            return "";
        }

        final String[] strs = requestLine.split("\\s");

        if(strs.length < 2) {
            return "";
        }

        return strs[1];
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
