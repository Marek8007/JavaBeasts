package com.marcos.javabeasts_javafx;

import com.marcos.javabeasts_javafx.socket.LobbyTcpClient;
import com.marcos.javabeasts_javafx.socket.RoomStatusData;
import com.marcos.javabeasts_javafx.socket.RoomStatusPlayer;
import javafx.application.Application;
import javafx.application.Platform;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JavaBeastsLobbyApp extends Application {

    private static final String DEFAULT_LOBBY_HOST = "127.0.0.1";
    private static final int DEFAULT_LOBBY_PORT = 7878;
    private static final long REFRESH_INTERVAL_SECONDS = 2;

    private final LobbyTcpClient lobbyTcpClient = new LobbyTcpClient(resolveLobbyHost(), resolveLobbyPort());
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private Label roomCodeLabel;
    private Label phaseLabel;
    private VBox playerOneCard;
    private VBox playerTwoCard;
    private Label matchStatusLabel;
    private Label connectionLabel;
    private HBox playersRow;
    private boolean matchReady;
    private boolean battleScreenShown;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        Label title = new Label("JavaBeasts");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: #f3f4f6;");

        Label subtitle = new Label("Pantalla de sala");
        subtitle.setFont(Font.font(18));
        subtitle.setStyle("-fx-text-fill: #cbd5e1;");

        roomCodeLabel = new Label("Sala ----");
        roomCodeLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 20));
        roomCodeLabel.setStyle("-fx-text-fill: #f8fafc;");

        phaseLabel = new Label("Lobby activo");
        phaseLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        phaseLabel.setStyle("-fx-text-fill: #93c5fd;");

        VBox header = new VBox(8, title, subtitle, roomCodeLabel, phaseLabel);
        header.setAlignment(Pos.CENTER_LEFT);

        playerOneCard = createUnavailableCard("Jugador 1");
        playerTwoCard = createUnavailableCard("Jugador 2");
        matchStatusLabel = createMatchStatusLabel(false);
        connectionLabel = createConnectionLabel("Conectando con el lobby TCP...", "#fcd34d");

        playersRow = new HBox(24, playerOneCard, playerTwoCard);
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

        refreshLobbyState();
        startPolling();
    }

    @Override
    public void stop() {
        scheduler.shutdownNow();
    }

    private void startPolling() {
        scheduler.scheduleAtFixedRate(
                this::refreshLobbyState,
                REFRESH_INTERVAL_SECONDS,
                REFRESH_INTERVAL_SECONDS,
                TimeUnit.SECONDS
        );
    }

    private void refreshLobbyState() {
        if (battleScreenShown) {
            return;
        }

        try {
            RoomStatusData roomStatus = lobbyTcpClient.fetchRoomStatus();
            Platform.runLater(() -> applyRoomStatus(roomStatus));
        } catch (Exception e) {
            Platform.runLater(this::showDisconnectedState);
        }
    }

    private void applyRoomStatus(RoomStatusData roomStatus) {
        roomCodeLabel.setText("Sala " + safeRoomCode(roomStatus));
        replacePlayerCards(
                createPlayerCard("Jugador 1", roomStatus.getPlayerOne()),
                createPlayerCard("Jugador 2", roomStatus.getPlayerTwo())
        );
        if (roomStatus.isCanStart()) {
            handleMatchReady(roomStatus);
        } else {
            matchReady = false;
            phaseLabel.setText("Lobby activo");
            phaseLabel.setStyle("-fx-text-fill: #93c5fd;");
            matchStatusLabel.setText("Esperando a que ambos jugadores esten listos");
            matchStatusLabel.setStyle("-fx-text-fill: #cbd5e1;");
        }
        connectionLabel.setText("Conectado al lobby TCP");
        connectionLabel.setStyle("-fx-text-fill: #86efac;");
    }

    private void showDisconnectedState() {
        if (battleScreenShown) {
            return;
        }

        matchReady = false;
        roomCodeLabel.setText("Sala ----");
        phaseLabel.setText("Lobby no disponible");
        phaseLabel.setStyle("-fx-text-fill: #fca5a5;");
        replacePlayerCards(
                createUnavailableCard("Jugador 1"),
                createUnavailableCard("Jugador 2")
        );
        matchStatusLabel.setText("Esperando a que ambos jugadores esten listos");
        matchStatusLabel.setStyle("-fx-text-fill: #cbd5e1;");
        connectionLabel.setText("Sin conexion con el lobby TCP");
        connectionLabel.setStyle("-fx-text-fill: #fca5a5;");
    }

    private void replacePlayerCards(VBox newPlayerOneCard, VBox newPlayerTwoCard) {
        playersRow.getChildren().setAll(newPlayerOneCard, newPlayerTwoCard);
        playerOneCard = newPlayerOneCard;
        playerTwoCard = newPlayerTwoCard;
    }

    private void handleMatchReady(RoomStatusData roomStatus) {
        if (!matchReady) {
            matchReady = true;
        }

        phaseLabel.setText("Partida lista");
        phaseLabel.setStyle("-fx-text-fill: #fcd34d;");
        matchStatusLabel.setText("Preparando la transicion al combate");
        matchStatusLabel.setStyle("-fx-text-fill: #fde68a;");

        if (!battleScreenShown) {
            battleScreenShown = true;
            scheduler.shutdownNow();
            showBattleScreen(roomStatus);
        }
    }

    private void showBattleScreen(RoomStatusData roomStatus) {
        Scene battleScene = BattleScreenFactory.createBattleScene(roomStatus);
        primaryStage.setScene(battleScene);
        primaryStage.setTitle("JavaBeasts - Combate");
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

    private static String resolveLobbyHost() {
        return System.getProperty("javabeasts.lobby.host", DEFAULT_LOBBY_HOST);
    }

    private static int resolveLobbyPort() {
        String configuredPort = System.getProperty("javabeasts.lobby.port", String.valueOf(DEFAULT_LOBBY_PORT));

        try {
            return Integer.parseInt(configuredPort);
        } catch (NumberFormatException e) {
            return DEFAULT_LOBBY_PORT;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
