package org.example.czatgui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

/**
 * Klient czatu z interfejsem graficznym JavaFX.
 * Obsługuje logowanie użytkownika, wysyłanie i odbieranie wiadomości, listę użytkowników online.
 */
public class ChatClientGUI extends Application {

    private TextArea chatArea;            // Pole do wyświetlania rozmowy
    private TextField inputField;         // Pole do wpisywania wiadomości
    private ListView<String> userList;    // Lista zalogowanych użytkowników
    private PrintWriter out;              // Strumień do wysyłania danych na serwer
    private BufferedReader in;            // Strumień do odbierania danych z serwera
    private String userName;              // Nazwa użytkownika klienta
    private Socket socket;                // Gniazdo połączenia z serwerem

    @Override
    public void start(Stage primaryStage) {
        // Konfiguracja elementów GUI
        chatArea = new TextArea();
        chatArea.setEditable(false); // użytkownik nie może edytować czatu

        inputField = new TextField();
        inputField.setOnAction(e -> sendMessage()); // Wysyłaj po Enterze

        Button sendButton = new Button("Wyślij");
        sendButton.setOnAction(e -> sendMessage()); // Wysyłaj po kliknięciu przycisku

        HBox bottom = new HBox(10, inputField, sendButton); // Dolny pasek GUI z polem i przyciskiem

        userList = new ListView<>(); // Lista aktywnych użytkowników

        BorderPane root = new BorderPane(); // Układ aplikacji
        root.setCenter(chatArea);
        root.setBottom(bottom);
        root.setRight(userList);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Czat JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();

        connectToServer(); // Łączenie z serwerem po uruchomieniu GUI
    }

    /**
     * Nawiązuje połączenie z serwerem czatu.
     */
    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 12345); // Połączenie z serwerem
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Platform.runLater(this::showLoginDialog); // Pokaż okno logowania
            } catch (IOException e) {
                showError("Nie można połączyć się z serwerem.");
            }
        }).start();
    }

    /**
     * Wyświetla okno logowania i wysyła login do serwera.
     */
    private void showLoginDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Logowanie");
        dialog.setHeaderText("Podaj swój login:");

        dialog.showAndWait().ifPresent(name -> {
            userName = name.trim();
            if (userName.isEmpty()) {
                showError("Login nie może być pusty.");
                Platform.runLater(this::showLoginDialog); // Pokaż ponownie
            } else {
                out.println(userName); // Wysyłamy login na serwer

                new Thread(() -> {
                    try {
                        String response = in.readLine();
                        if (response != null && response.startsWith("NAME_TAKEN")) {
                            Platform.runLater(() -> {
                                showError("Login zajęty. Wybierz inny.");
                                showLoginDialog();
                            });
                        } else {
                            out.println("/online"); // Poproś serwer o listę online
                            new Thread(new ClientReceiver(in, chatArea, userList)).start(); // Odbieraj wiadomości
                        }
                    } catch (IOException e) {
                        showError("Błąd komunikacji z serwerem.");
                    }
                }).start();
            }
        });
    }

    /**
     * Wysyła wiadomość z pola tekstowego do serwera.
     */
    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty()) {
            out.println(msg);      // Wyślij do serwera
            inputField.clear();   // Wyczyść pole tekstowe
        }
    }

    /**
     * Pokazuje wyskakujące okno błędu.
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Metoda główna uruchamiająca GUI klienta.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
