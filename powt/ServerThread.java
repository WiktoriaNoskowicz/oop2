package org.example.powt.client;

import org.example.powt.model.Dot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ServerThread extends Thread {
    private final Socket socket; // Gniazdo TCP
    private PrintWriter out;     // Do wysyłania danych
    private BufferedReader in;   // Do odbierania danych
    private Consumer<Dot> consumer; // Callback, który przekaże otrzymany punkt dalej

    public ServerThread(String host, int port) throws IOException {
        // Tworzy połączenie z serwerem
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void send(int x, int y, String color, int radius) {
        // Wysyła punkt jako tekst
        out.println(Dot.toMessage(x, y, color, radius));
    }

    public void setConsumer(Consumer<Dot> consumer) {
        this.consumer = consumer; // Rejestruje odbiorcę wiadomości (np. rysowanie)
    }

    @Override
    public void run() {
        // Główna pętla odbierająca dane od serwera
        try {
            String line;
            while ((line = in.readLine()) != null) {
                Dot dot = Dot.fromMessage(line); // Parsuje wiadomość do Dot
                if (consumer != null) consumer.accept(dot); // Przekazuje punkt dalej (do narysowania)
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

