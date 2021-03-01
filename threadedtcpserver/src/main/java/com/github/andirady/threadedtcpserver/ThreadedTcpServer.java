package com.github.andirady.threadedtcpserver;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.stream.*;

public class ThreadedTcpServer {

    private static Logger LOG = Logger.getLogger("main");

    public static void main(String[] args) throws IOException {
        var serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(5000));
        serverSocket.setSoTimeout(2_000);
        LOG.info("Listening on port 5000");

        var pool = Executors.newFixedThreadPool(10);
        while (true) {
            try {
                pool.execute(new Handler(serverSocket.accept()));
            } catch (SocketTimeoutException e) {
                continue;
            }
        }
    }

    public static class Handler implements Runnable {

        private Socket clientSocket;

        public Handler(Socket socket) {
            clientSocket = socket;
        }

        @Override
        public void run() {
            LOG.info("New client: " + clientSocket);
            try (var s = clientSocket) {
                // The request must have a predefined format, else, the BufferedReader will be blocking since it
                // continues to wait for lines from the client.
                // In this case, the format is a single line request.
                var req = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).lines().findFirst().orElse("");
                LOG.info("req = " + req);
                var out = new DataOutputStream(clientSocket.getOutputStream());
                out.writeBytes(req.toString());
                out.flush();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Failure", e);
            }
        }
    }

}
