import java.io.*;
import java.net.*;
import java.util.*;

public class czatSProsty {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Serwer czatu uruchomiony na porcie " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) { //serwer caigle nasluchuje i czeka na klientow
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowy klient: " + clientSocket);
                new Thread(new ClientHandler(clientSocket)).start(); //serwer uruchamia nowy watek dla klientow
            }
        } catch (IOException e) {
            System.err.println("Błąd serwera: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {
                out = new PrintWriter(socket.getOutputStream(), true);
                clientWriters.add(out);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Odebrano: " + message);
                    broadcast(message);
                }
            } catch (IOException e) {
                System.err.println("Błąd klienta: " + e.getMessage());
            } finally {
                if (out != null) clientWriters.remove(out);
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        private void broadcast(String message) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}
