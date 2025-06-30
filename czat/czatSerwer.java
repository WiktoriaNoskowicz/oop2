import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class czatSerwer {

    private static final int PORT = 12345; // Port na którym będzie działał server
    // Zad. 1
    //private static Set<PrintWriter> clientWriters = new HashSet<>(); // Przechowuje output streamy wszystkich klientów
    //Zad. 3a
    private static Map<String, PrintWriter> clientWriters = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("Chat Server is running on port " + PORT);
        ExecutorService pool = Executors.newFixedThreadPool(50); // Pula wątków, do obsługiwania klientów

        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                pool.execute(new ClientHandler(listener.accept())); // Akceptujemy połączenie klientów i tworzymy nowe watki
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            pool.shutdown(); // Wyłączamy pulę wątków
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String userName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                //ustawiamy wyjscie i wejscie dla klienta
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Pytamu klienta o imię
                userName = in.readLine();
                while (userName == null || userName.trim().isEmpty()) {
                    out.println("Imie nie może być puste, podaj imie"); // Pytamy ponownie jeśli jest puste
                    userName = in.readLine();
                }
                System.out.println(userName + " Dołączył do czatu.");
                broadcastMessage("CZAT: " + userName + " Dołączył do czatu."); //rozeslanie wiadomosci do wszytskich aktywnych uzytkownikow
                //W typowym serwerze czatowym funkcja broadcastMessage(String msg):
                //
                //iteruje po wszystkich połączeniach (np. List<Socket> albo List<ClientHandler>),
                //dla każdego klienta wysyła tę samą wiadomość,


                // Zad. 1
//                synchronized (clientWriters) {
//                    clientWriters.add(out);
//                }

                synchronized (clientWriters){ //sprawdzamy czy login jest unikalny
                    if(clientWriters.containsKey(userName)){
                        out.println("NAME_TAKEN " + userName + "Ten login jest zajęty, wybierz coś innego");
                        return; //konczymy watek kilenta
                    }
                    clientWriters.put(userName, out); //// Dodajemy klienta do listy aktywnych uzytkownikow
                }


                // Odczytujemy wiadmość klienta i ją nadajemy
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(userName + ": " + message);
                    broadcastMessage("MSG: " + userName + ": " + message);


                    // Komenda /online – wypisuje zalogowanych użytkowników
                    if (message.equals("/online")) {
                        out.println("Zalogowani użytkownicy: " + String.join(", ", clientWriters.keySet()));
                        continue;
                    }

                    // Komenda prywatna /w <login> <wiadomość>
                    if (message.startsWith("/w ")) {
                        String[] parts = message.split(" ", 3);
                        if (parts.length < 3) {
                            out.println("Niepoprawny format. Użyj: /w odbiorca wiadomość");
                            continue;
                        }
                        String recipient = parts[1];
                        String privateMessage = parts[2];
                        PrintWriter recipientOut = clientWriters.get(recipient);
                        if (recipientOut != null) {
                            recipientOut.println("PRYWATNA WIADOMOSC od " + userName + ": " + privateMessage);
                            out.println("Wysłano prywatną wiadomość do " + recipient);
                        } else {
                            out.println("Użytkownik '" + recipient + "' nie jest zalogowany.");
                        }
                        continue;
                    }


                }
            } catch (IOException e) {
                System.err.println("Error handling client " + userName + ": " + e.getMessage());
            } finally {
                if (userName != null) {
                    synchronized (clientWriters){
                        clientWriters.remove(userName); //usuwamy klienta po rozlaczeniu
                    }
                    System.out.println(userName + " wyszedł z czatu.");
                    broadcastMessage("CZAT: " + userName + " wyszedł z czatu.");
                }
                if (out != null) {
                    synchronized (clientWriters) {
                        clientWriters.remove(out); // Usuwamy writer klienta i się rozłączamy
                    }
                }
                try {
                    socket.close(); // Zamykamy socket
                } catch (IOException e) {
                    System.err.println("Error closing socket for " + userName + ": " + e.getMessage());
                }
            }
        }
    }

    // Wysyłamy wiadomość do wszystkich klientów
    private static void broadcastMessage(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer: clientWriters.values()) {
                writer.println(message);
            }
        }
    }
}
