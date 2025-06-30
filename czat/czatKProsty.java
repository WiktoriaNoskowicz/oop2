import java.io.*;
import java.net.*;

public class czatKProsty {
    private static final String SERVER_ADDRESS = "localhost"; // lub IP serwera
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        ) {
            System.out.println("Połączono z serwerem czatu!");

            // Wątek nasłuchujący wiadomości przychodzących
            Thread readerThread = new Thread(() -> {
                try {
                    String messageFromServer;
                    while ((messageFromServer = in.readLine()) != null) {
                        System.out.println(messageFromServer);
                    }
                } catch (IOException e) {
                    System.err.println("Utracono połączenie z serwerem.");
                }
            });
            readerThread.start();

            // Wysyłanie wiadomości do serwera
            String messageToSend;
            while ((messageToSend = userInput.readLine()) != null) {
                out.println(messageToSend);
            }

        } catch (IOException e) {
            System.err.println("Błąd klienta: " + e.getMessage());
        }
    }
}
