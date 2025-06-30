package org.example.powt.server;

import org.example.powt.model.Dot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientThread extends Thread {
    private final Server server;
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientThread(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void send(String msg) {
        out.println(msg); // Wysyła wiadomość do klienta
    }

    @Override
    public void run() {
        // Nasłuchuje danych od klienta i przekazuje je do broadcastu
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                server.broadcast(msg); // Wysyła dalej do wszystkich klientów
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

