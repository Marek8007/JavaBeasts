package com.marcos.javabeasts_javafx;

import com.marcos.javabeasts_javafx.socket.LobbyTcpClient;
import com.marcos.javabeasts_javafx.socket.RoomStatusData;
import com.marcos.javabeasts_javafx.socket.RoomStatusPlayer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class JavaBeastsLobbyApp extends Application {

    private static final String LOBBY_HOST = "127.0.0.1";
    private static final int LOBBY_PORT = 7878;

    @Override
    public void start(Stage stage) {
        LobbyTcpClient lobbyTcpClient = new LobbyTcpClient(LOBBY_HOST, LOBBY_PORT);

        Label title = new Label("JavaBeasts");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: #f3f4f6;");

        Label subtitle = new Label("Pantalla de sala");
        subtitle.setFont(Font.font(18));
        subtitle.setStyle("-fx-text-fill: #cbd5e1;");

        Label roomCodeLabel = new Label("Sala ----");
        roomCodeLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 20));
        roomCodeLabel.setStyle("-fx-text-fill: #f8fafc;");

        VBox header = new VBox(8, title, subtitle, roomCodeLabel);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox playerOneCard;
        VBox playerTwoCard;
        Label matchStatusLabel;
        Label connectionLabel;

        try {
            RoomStatusData roomStatus = lobbyTcpClient.fetchRoomStatus();

            roomCodeLabel.setText("Sala " + safeRoomCode(roomStatus));
            playerOneCard = createPlayerCard("Jugador 1", roomStatus.getPlayerOne());
            playerTwoCard = createPlayerCard("Jugador 2", roomStatus.getPlayerTwo());
            matchStatusLabel = createMatchStatusLabel(roomStatus.isCanStart());
            connectionLabel = createConnectionLabel("Conectado al lobby TCP", "#86efac");
        } catch (Exception e) {
            playerOneCard = createUnavailableCard("Jugador 1");
            playerTwoCard = createUnavailableCard("Jugador 2");
            matchStatusLabel = createMatchStatusLabel(false);
            connectionLabel = createConnectionLabel("Sin conexion con el lobby TCP", "#fca5a5");
        }

        HBox playersRow = new HBox(24, playerOneCard, playerTwoCard);
        playersRow.setAlignment(Pos.CENTER);

        VBox footer = new VBox(12, matchStatusLabel, connectionLabel);
        footer.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(28, header, playersRow, footer);
        content.setAlignment(Pos.TOP_LEFT);

        BorderPane root = new BorderPane(content);
        root.setPadding(new Insets(32));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #111827, #1f2937);");

        Scene scene = new Scene(root, 960, 540);
        stage.setTitle("JavaBeasts");
        stage.setScene(scene);
        stage.setMinWidth(720);
        stage.setMinHeight(420);
        stage.show();
    }

    private VBox createPlayerCard(String slotTitle, RoomStatusPlayer player) {
        Label slotLabel = new Label(slotTitle);
        slotLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        slotLabel.setStyle("-fx-text-fill: #f8fafc;");

        String username = player != null && player.getUsername() != null && !player.getUsername().isBlank()
                ? player.getUsername()
                : "Esperando jugador";
        Label usernameLabel = new Label(username);
        usernameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 24));
        usernameLabel.setStyle("-fx-text-fill: #e2e8f0;");

        String readyText = player != null && player.isReady() ? "Listo" : "No listo";
        String readyColor = player != null && player.isReady() ? "#86efac" : "#fca5a5";
        Label readyLabel = new Label(readyText);
        readyLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        readyLabel.setStyle("-fx-text-fill: " + readyColor + ";");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox card = new VBox(12, slotLabel, usernameLabel, spacer, readyLabel);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(24));
        card.setPrefWidth(420);
        card.setMinHeight(220);
        card.setStyle(
                "-fx-background-color: rgba(15, 23, 42, 0.88);" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: rgba(148, 163, 184, 0.35);"
        );
        return card;
    }

    private VBox createUnavailableCard(String slotTitle) {
        Label slotLabel = new Label(slotTitle);
        slotLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        slotLabel.setStyle("-fx-text-fill: #f8fafc;");

        Label messageLabel = new Label("Sin datos de sala");
        messageLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 24));
        messageLabel.setStyle("-fx-text-fill: #cbd5e1;");

        Label readyLabel = new Label("No disponible");
        readyLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        readyLabel.setStyle("-fx-text-fill: #fca5a5;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox card = new VBox(12, slotLabel, messageLabel, spacer, readyLabel);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(24));
        card.setPrefWidth(420);
        card.setMinHeight(220);
        card.setStyle(
                "-fx-background-color: rgba(15, 23, 42, 0.88);" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: rgba(248, 113, 113, 0.35);"
        );
        return card;
    }

    private Label createMatchStatusLabel(boolean canStart) {
        String text = canStart ? "Partida lista" : "Esperando a que ambos jugadores esten listos";
        String color = canStart ? "#fcd34d" : "#cbd5e1";

        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 20));
        label.setStyle("-fx-text-fill: " + color + ";");
        return label;
    }

    private Label createConnectionLabel(String text, String color) {
        Label label = new Label(text);
        label.setFont(Font.font(15));
        label.setStyle("-fx-text-fill: " + color + ";");
        return label;
    }

    private String safeRoomCode(RoomStatusData roomStatus) {
        String roomCode = roomStatus.getRoomCode();
        return roomCode == null || roomCode.isBlank() ? "----" : roomCode;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
