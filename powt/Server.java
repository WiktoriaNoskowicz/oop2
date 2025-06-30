package org.example.powt.server;

import org.example.powt.model.Dot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private final List<ClientThread> clients = Collections.synchronizedList(new ArrayList<>());
    private Connection dbConnection;

    public Server(int port) throws IOException, SQLException {
        serverSocket = new ServerSocket(port);

        // Połączenie z bazą danych SQLite
        dbConnection = DriverManager.getConnection("jdbc:sqlite:dots.db");

        // Tworzy tabelę jeśli nie istnieje
        dbConnection.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS dot (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "x INTEGER, y INTEGER, color TEXT, radius INTEGER)"
        );

        // Startuje nowy wątek nasłuchujący klientów
        new Thread(() -> {
            while (true) try {
                Socket clientSocket = serverSocket.accept();
                ClientThread client = new ClientThread(this, clientSocket);
                clients.add(client);         // Dodaje klienta do listy
                sendSavedDots(client);       // Wysyła dotychczasowe punkty z bazy
                client.start();              // Startuje wątek klienta
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void broadcast(String message) {
        Dot dot = Dot.fromMessage(message);
        saveDot(dot); // Zapis do bazy
        synchronized (clients) {
            for (ClientThread c : clients) {
                c.send(message); // Wysyłka do wszystkich klientów
            }
        }
    }

    public void saveDot(Dot dot) {
        try (PreparedStatement ps = dbConnection.prepareStatement(
                "INSERT INTO dot (x, y, color, radius) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, dot.x());
            ps.setInt(2, dot.y());
            ps.setString(3, dot.color());
            ps.setInt(4, dot.radius());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Dot> getSavedDots() {
        List<Dot> dots = new ArrayList<>();
        try (ResultSet rs = dbConnection.createStatement().executeQuery("SELECT * FROM dot")) {
            while (rs.next()) {
                dots.add(new Dot(
                        rs.getInt("x"),
                        rs.getInt("y"),
                        rs.getString("color"),
                        rs.getInt("radius")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dots;
    }

    private void sendSavedDots(ClientThread client) {
        for (Dot d : getSavedDots()) {
            client.send(d.toNetworkMessage());
        }
    }
}

