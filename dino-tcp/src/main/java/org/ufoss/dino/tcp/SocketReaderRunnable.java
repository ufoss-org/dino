/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.tcp;

import java.io.IOException;
import java.net.Socket;

/**
 * Code from http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html
 */
public class SocketReaderRunnable implements Runnable {

    private final Socket clientSocket;

    public SocketReaderRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            final var input  = clientSocket.getInputStream();
            input.readAllBytes();
            final var time = System.currentTimeMillis();
            input.readAllBytes();
            input.close();
            System.out.println("Request processed: " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}
