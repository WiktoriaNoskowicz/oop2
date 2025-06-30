package org.example.powt;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.example.powt.client.ServerThread;
import org.example.powt.model.Dot;
import org.example.powt.server.Server;

import java.io.IOException;
import java.sql.SQLException;

public class Controller {
    @FXML private Canvas canvas;
    @FXML private ColorPicker colorPicker;
    @FXML private Slider radiusSlider;
    @FXML private TextField addressField;
    @FXML private TextField portField;

    private Server server;
    private ServerThread serverThread;

    @FXML
    public void initialize() {
        // Ustawia nasłuchiwanie na kliknięcia myszy na canvasie
        canvas.setOnMouseClicked(this::onMouseClicked);
    }

    @FXML
    public void onStartServerClicked() throws SQLException, IOException {
        int port = Integer.parseInt(portField.getText());
        server = new Server(port); // Tworzy nowy serwer
        connectToServer();         // Klient łączy się z lokalnym serwerem
    }

    @FXML
    public void onConnectClicked() {
        connectToServer(); // Łączy się jako klient z danym adresem i portem
    }

    private void connectToServer() {
        try {
            String host = addressField.getText();
            int port = Integer.parseInt(portField.getText());
            serverThread = new ServerThread(host, port); // Łączy się do serwera
            serverThread.setConsumer(this::drawDot);     // Ustawia callback do rysowania
            serverThread.start();                        // Startuje wątek odbierający dane
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMouseClicked(MouseEvent e) {
        if (serverThread != null) {
            Color color = colorPicker.getValue();
            int radius = (int) radiusSlider.getValue();
            serverThread.send((int) e.getX(), (int) e.getY(), color.toString(), radius);
        }
    }

    private void drawDot(Dot dot) {
        // Wywołuje się przy otrzymaniu nowego punktu z serwera
        Platform.runLater(() -> {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.web(dot.color()));
            gc.fillOval(dot.x() - dot.radius(), dot.y() - dot.radius(), dot.radius() * 2, dot.radius() * 2);
        });
    }
}
