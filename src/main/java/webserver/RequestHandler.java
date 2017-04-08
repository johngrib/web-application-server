package webserver;

import java.io.*;
import java.net.Socket;
import model.RequestMap;
import model.ResponseMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            final RequestMap request = RequestMap.build(in, out);

            ControllerRegister.get(request.url).execute(request);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
