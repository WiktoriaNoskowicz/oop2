package org.example.czatgui;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Odpowiada za odbieranie wiadomości z serwera i aktualizowanie GUI.
 * Obsługuje wiadomości broadcast, prywatne, login/logout oraz listę online.
 */
public class ClientReceiver implements Runnable {

    private BufferedReader in;            // Strumień wejściowy z serwera
    private TextArea chatArea;            // Pole do wyświetlania czatu
    private ListView<String> userList;    // Lista użytkowników

    public ClientReceiver(BufferedReader in, TextArea chatArea, ListView<String> userList) {
        this.in = in;
        this.chatArea = chatArea;
        this.userList = userList;
    }

    @Override
    public void run() {
        String line;
        try {
            while ((line = in.readLine()) != null) {
                final String msg = line;

                Platform.runLater(() -> {
                    // Broadcast wiadomość
                    if (msg.startsWith("MSG:")) {
                        chatArea.appendText(msg.substring(4).trim() + "\n");

                        // Powitanie / rozłączenie
                    } else if (msg.startsWith("CZAT:")) {
                        chatArea.appendText("[INFO] " + msg.substring(5).trim() + "\n");

                        // Prywatna wiadomość
                    } else if (msg.startsWith("PRYWATNA WIADOMOSC")) {
                        chatArea.appendText("[PRYWATNA] " + msg.substring("PRYWATNA WIADOMOSC".length()).trim() + "\n");

                        // Lista aktywnych użytkowników
                    } else if (msg.startsWith("Zalogowani użytkownicy:")) {
                        String[] users = msg.replace("Zalogowani użytkownicy:", "").split(",");
                        userList.getItems().setAll(Arrays.stream(users).map(String::trim).toList());

                        // Login broadcast – dodaj użytkownika
                    } else if (msg.contains("Dołączył do czatu")) {
                        String[] parts = msg.split(" ");
                        String newUser = parts[1].trim();
                        if (!userList.getItems().contains(newUser)) {
                            userList.getItems().add(newUser);
                        }
                        chatArea.appendText("[INFO] " + msg + "\n");

                        // Logout broadcast – usuń użytkownika
                    } else if (msg.contains("wyszedł z czatu")) {
                        String[] parts = msg.split(" ");
                        String goneUser = parts[1].trim();
                        userList.getItems().remove(goneUser);
                        chatArea.appendText("[INFO] " + msg + "\n");

                        // Domyślnie – wypisz wiadomość
                    } else {
                        chatArea.appendText(msg + "\n");
                    }
                });
            }
        } catch (IOException e) {
            Platform.runLater(() -> chatArea.appendText("Utracono połączenie z serwerem.\n"));
        }
    }
}
