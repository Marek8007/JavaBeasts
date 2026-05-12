package com.marcos.javabeasts_javafx;

import com.marcos.javabeasts_javafx.battle.BattleCreatureSnapshotData;
import com.marcos.javabeasts_javafx.battle.BattlePlayerSnapshotData;
import com.marcos.javabeasts_javafx.battle.BattleSnapshotData;
import com.marcos.javabeasts_javafx.socket.RoomStatusData;
import com.marcos.javabeasts_javafx.socket.RoomStatusPlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.Normalizer;
import java.util.Locale;

public final class BattleScreenFactory {

    private BattleScreenFactory() {
    }

    public static Scene createBattleScene(RoomStatusData roomStatus) {
        Label title = new Label("Combate");
        title.setFont(Font.font("System", FontWeight.BOLD, 34));
        title.setStyle("-fx-text-fill: #f8fafc;");

        Label subtitle = new Label("Pantalla base de combate");
        subtitle.setFont(Font.font(18));
        subtitle.setStyle("-fx-text-fill: #cbd5e1;");

        Label roomCodeLabel = new Label("Sala " + safeRoomCode(roomStatus));
        roomCodeLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 18));
        roomCodeLabel.setStyle("-fx-text-fill: #93c5fd;");

        VBox header = new VBox(8, title, subtitle, roomCodeLabel);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox playerOnePanel = createBattlePlayerPanel("Jugador 1", roomStatus.getPlayerOne());
        VBox playerTwoPanel = createBattlePlayerPanel("Jugador 2", roomStatus.getPlayerTwo());

        HBox battleRow = new HBox(24, playerOnePanel, playerTwoPanel);
        battleRow.setAlignment(Pos.CENTER);

        Label turnLabel = new Label("Turno 1");
        turnLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        turnLabel.setStyle("-fx-text-fill: #fcd34d;");

        Label statusLabel = new Label("Esperando la logica real de combate");
        statusLabel.setFont(Font.font(17));
        statusLabel.setStyle("-fx-text-fill: #e2e8f0;");

        Label footerHint = new Label("Este sera el punto de entrada para la futura pantalla de batalla");
        footerHint.setFont(Font.font(14));
        footerHint.setStyle("-fx-text-fill: #94a3b8;");

        VBox centerPanel = new VBox(12, turnLabel, statusLabel, footerHint);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(24));
        centerPanel.setStyle(
                "-fx-background-color: rgba(30, 41, 59, 0.85);" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: rgba(148, 163, 184, 0.35);"
        );

        VBox content = new VBox(28, header, battleRow, centerPanel);
        content.setAlignment(Pos.TOP_LEFT);

        BorderPane root = new BorderPane(content);
        root.setPadding(new Insets(32));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f172a, #1e293b);");

        return new Scene(root, 960, 540);
    }

    public static Scene createBattleScene(BattleSnapshotData snapshot) {
        return createBattleSceneView(snapshot).getScene();
    }

    public static BattleSceneView createBattleSceneView(BattleSnapshotData snapshot) {
        Label title = new Label("Combate");
        title.setFont(Font.font("System", FontWeight.BOLD, 34));
        title.setStyle("-fx-text-fill: #f8fafc;");

        Label subtitle = new Label();
        subtitle.setFont(Font.font(18));
        Label roomCodeLabel = new Label();
        roomCodeLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 18));
        roomCodeLabel.setStyle("-fx-text-fill: #93c5fd;");

        VBox header = new VBox(8, title, subtitle, roomCodeLabel);
        header.setAlignment(Pos.CENTER_LEFT);

        BattlePlayerPanelView playerOnePanel = createBattlePlayerPanelView("Jugador 1");
        BattlePlayerPanelView playerTwoPanel = createBattlePlayerPanelView("Jugador 2");

        HBox battleRow = new HBox(24, playerOnePanel.getRoot(), playerTwoPanel.getRoot());
        battleRow.setAlignment(Pos.CENTER);

        Label turnLabel = new Label();
        turnLabel.setFont(Font.font("System", FontWeight.BOLD, 22));

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font(17));
        statusLabel.setStyle("-fx-text-fill: #e2e8f0;");

        Label footerHint = new Label();
        footerHint.setFont(Font.font(14));
        footerHint.setStyle("-fx-text-fill: #94a3b8;");

        VBox centerPanel = new VBox(12, turnLabel, statusLabel, footerHint);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(24));
        centerPanel.setStyle(
                "-fx-background-color: rgba(30, 41, 59, 0.85);" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: rgba(148, 163, 184, 0.35);"
        );

        VBox content = new VBox(28, header, battleRow, centerPanel);
        content.setAlignment(Pos.TOP_LEFT);

        BorderPane root = new BorderPane(content);
        root.setPadding(new Insets(32));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f172a, #1e293b);");

        BattleSceneView sceneView = new BattleSceneView(
                new Scene(root, 960, 540),
                subtitle,
                roomCodeLabel,
                turnLabel,
                statusLabel,
                footerHint,
                playerOnePanel,
                playerTwoPanel
        );
        sceneView.update(snapshot);
        return sceneView;
    }

    private static VBox createBattlePlayerPanel(String slotTitle, RoomStatusPlayer player) {
        Label slotLabel = new Label(slotTitle);
        slotLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        slotLabel.setStyle("-fx-text-fill: #f8fafc;");

        String username = player != null && player.getUsername() != null && !player.getUsername().isBlank()
                ? player.getUsername()
                : "Jugador pendiente";
        Label usernameLabel = new Label(username);
        usernameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 24));
        usernameLabel.setStyle("-fx-text-fill: #e2e8f0;");

        Label activeJaBeaLabel = new Label("JaBea activo: pendiente");
        activeJaBeaLabel.setFont(Font.font(16));
        activeJaBeaLabel.setStyle("-fx-text-fill: #cbd5e1;");

        Label hpLabel = new Label("Vida: -- / --");
        hpLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        hpLabel.setStyle("-fx-text-fill: #86efac;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox panel = new VBox(12, slotLabel, usernameLabel, activeJaBeaLabel, spacer, hpLabel);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPadding(new Insets(24));
        panel.setPrefWidth(420);
        panel.setMinHeight(220);
        panel.setStyle(
                "-fx-background-color: rgba(15, 23, 42, 0.88);" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: rgba(148, 163, 184, 0.35);"
        );
        return panel;
    }

    private static VBox createBattlePlayerPanel(String slotTitle, BattlePlayerSnapshotData player) {
        Label slotLabel = new Label(slotTitle);
        slotLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        slotLabel.setStyle("-fx-text-fill: #f8fafc;");

        String username = player != null && player.getUsername() != null && !player.getUsername().isBlank()
                ? player.getUsername()
                : "Jugador pendiente";
        Label usernameLabel = new Label(username);
        usernameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 24));
        usernameLabel.setStyle("-fx-text-fill: #e2e8f0;");

        String teamName = player != null && player.getTeamName() != null && !player.getTeamName().isBlank()
                ? player.getTeamName()
                : "Equipo pendiente";
        Label teamLabel = new Label("Equipo: " + teamName);
        teamLabel.setFont(Font.font(16));
        teamLabel.setStyle("-fx-text-fill: #cbd5e1;");

        BattleCreatureSnapshotData activeJaBea = player != null ? player.getActiveJaBea() : null;
        String activeJaBeaName = activeJaBea != null && activeJaBea.getName() != null && !activeJaBea.getName().isBlank()
                ? activeJaBea.getName()
                : "Pendiente";
        ImageView jaBeaImage = createJaBeaImage(activeJaBeaName, 96);

        Label activeJaBeaLabel = new Label("JaBea activo: " + activeJaBeaName);
        activeJaBeaLabel.setFont(Font.font(16));
        activeJaBeaLabel.setStyle("-fx-text-fill: #cbd5e1;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox creatureRow = new HBox(18, jaBeaImage, new VBox(6, teamLabel, activeJaBeaLabel));
        creatureRow.setAlignment(Pos.CENTER_LEFT);

        VBox panel = new VBox(12, slotLabel, usernameLabel, creatureRow, spacer, createHealthBar(activeJaBea));
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPadding(new Insets(24));
        panel.setPrefWidth(420);
        panel.setMinHeight(220);
        panel.setStyle(
                "-fx-background-color: rgba(15, 23, 42, 0.88);" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: rgba(148, 163, 184, 0.35);"
        );
        return panel;
    }

    private static BattlePlayerPanelView createBattlePlayerPanelView(String slotTitle) {
        Label slotLabel = new Label(slotTitle);
        slotLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        slotLabel.setStyle("-fx-text-fill: #f8fafc;");

        Label usernameLabel = new Label();
        usernameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 24));
        usernameLabel.setStyle("-fx-text-fill: #e2e8f0;");

        Label teamLabel = new Label();
        teamLabel.setFont(Font.font(16));
        teamLabel.setStyle("-fx-text-fill: #cbd5e1;");

        ImageView jaBeaImage = createJaBeaImage("placeholder", 96);

        Label activeJaBeaLabel = new Label();
        activeJaBeaLabel.setFont(Font.font(16));
        activeJaBeaLabel.setStyle("-fx-text-fill: #cbd5e1;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox creatureRow = new HBox(18, jaBeaImage, new VBox(6, teamLabel, activeJaBeaLabel));
        creatureRow.setAlignment(Pos.CENTER_LEFT);

        Label hpLabel = new Label();
        hpLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        hpLabel.setStyle("-fx-text-fill: #e2e8f0;");

        Region healthTrack = new Region();
        healthTrack.setMinHeight(18);
        healthTrack.setPrefHeight(18);
        healthTrack.setMaxWidth(Double.MAX_VALUE);
        healthTrack.setStyle(
                "-fx-background-color: rgba(15, 23, 42, 0.95);" +
                "-fx-background-radius: 999;"
        );

        Region healthFill = new Region();
        healthFill.setMinHeight(18);
        healthFill.setPrefHeight(18);
        healthFill.setMaxWidth(Double.MAX_VALUE);

        StackPane healthBar = new StackPane(healthTrack, healthFill);
        healthBar.setAlignment(Pos.CENTER_LEFT);
        healthBar.setMaxWidth(Double.MAX_VALUE);

        VBox healthBox = new VBox(8, hpLabel, healthBar);
        healthBox.setMaxWidth(Double.MAX_VALUE);

        VBox panel = new VBox(12, slotLabel, usernameLabel, creatureRow, spacer, healthBox);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPadding(new Insets(24));
        panel.setPrefWidth(420);
        panel.setMinHeight(220);
        panel.setStyle(
                "-fx-background-color: rgba(15, 23, 42, 0.88);" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: rgba(148, 163, 184, 0.35);"
        );

        return new BattlePlayerPanelView(panel, usernameLabel, teamLabel, jaBeaImage, activeJaBeaLabel, hpLabel, healthTrack, healthFill);
    }

    private static String safeRoomCode(RoomStatusData roomStatus) {
        String roomCode = roomStatus.getRoomCode();
        return roomCode == null || roomCode.isBlank() ? "----" : roomCode;
    }

    private static String safeRoomCode(BattleSnapshotData snapshot) {
        String roomCode = snapshot.getRoomCode();
        return roomCode == null || roomCode.isBlank() ? "----" : roomCode;
    }

    private static int safeTurnNumber(BattleSnapshotData snapshot) {
        return snapshot.getTurnNumber() != null ? snapshot.getTurnNumber() : 1;
    }

    private static String safeInteger(Integer value) {
        return value != null ? String.valueOf(value) : "--";
    }

    private static String safeBattleMessage(BattleSnapshotData snapshot) {
        String message = snapshot.getMessage();
        return message != null && !message.isBlank()
                ? message
                : "Snapshot inicial cargado desde el backend";
    }

    private static String safeBattleResult(BattleSnapshotData snapshot) {
        String winnerUsername = snapshot.getWinnerUsername();
        return winnerUsername != null && !winnerUsername.isBlank()
                ? "Ganador: " + winnerUsername
                : "Combate finalizado";
    }

    private static VBox createHealthBar(BattleCreatureSnapshotData activeJaBea) {
        int currentHealth = activeJaBea != null && activeJaBea.getCurrentHealth() != null
                ? activeJaBea.getCurrentHealth()
                : 0;
        int maxHealth = activeJaBea != null && activeJaBea.getMaxHealth() != null && activeJaBea.getMaxHealth() > 0
                ? activeJaBea.getMaxHealth()
                : 1;
        double healthRatio = Math.max(0, Math.min(1, (double) currentHealth / maxHealth));

        Label hpLabel = new Label("Vida: " + currentHealth + " / " + maxHealth);
        hpLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        hpLabel.setStyle("-fx-text-fill: #e2e8f0;");

        Region track = new Region();
        track.setMinHeight(18);
        track.setPrefHeight(18);
        track.setMaxWidth(Double.MAX_VALUE);
        track.setStyle(
                "-fx-background-color: rgba(15, 23, 42, 0.95);" +
                "-fx-background-radius: 999;"
        );

        Region fill = new Region();
        fill.setMinHeight(18);
        fill.setPrefHeight(18);
        fill.setMaxWidth(Double.MAX_VALUE);
        fill.prefWidthProperty().bind(track.widthProperty().multiply(healthRatio));
        fill.setStyle(
                "-fx-background-color: " + healthColor(healthRatio) + ";" +
                "-fx-background-radius: 999;"
        );

        StackPane bar = new StackPane(track, fill);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setMaxWidth(Double.MAX_VALUE);

        VBox box = new VBox(8, hpLabel, bar);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private static String healthColor(double healthRatio) {
        if (healthRatio <= 0.25) {
            return "#ef4444";
        }

        if (healthRatio <= 0.5) {
            return "#f59e0b";
        }

        return "#22c55e";
    }

    private static ImageView createJaBeaImage(String jaBeaName, double size) {
        Image image = new Image(BattleScreenFactory.class.getResourceAsStream(resolveJaBeaImagePath(jaBeaName)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);
        return imageView;
    }

    private static String resolveJaBeaImagePath(String jaBeaName) {
        String normalizedName = normalizeJaBeaName(jaBeaName);
        String path = "/jabeas/" + normalizedName + ".png";

        return BattleScreenFactory.class.getResource(path) != null
                ? path
                : "/jabeas/placeholder.png";
    }

    private static String normalizeJaBeaName(String jaBeaName) {
        if (jaBeaName == null || jaBeaName.isBlank()) {
            return "placeholder";
        }

        String withoutAccents = Normalizer.normalize(jaBeaName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.trim().toLowerCase(Locale.ROOT);
    }

    public static final class BattleSceneView {
        private final Scene scene;
        private final Label subtitleLabel;
        private final Label roomCodeLabel;
        private final Label turnLabel;
        private final Label statusLabel;
        private final Label footerHintLabel;
        private final BattlePlayerPanelView playerOnePanel;
        private final BattlePlayerPanelView playerTwoPanel;

        private BattleSceneView(
                Scene scene,
                Label subtitleLabel,
                Label roomCodeLabel,
                Label turnLabel,
                Label statusLabel,
                Label footerHintLabel,
                BattlePlayerPanelView playerOnePanel,
                BattlePlayerPanelView playerTwoPanel
        ) {
            this.scene = scene;
            this.subtitleLabel = subtitleLabel;
            this.roomCodeLabel = roomCodeLabel;
            this.turnLabel = turnLabel;
            this.statusLabel = statusLabel;
            this.footerHintLabel = footerHintLabel;
            this.playerOnePanel = playerOnePanel;
            this.playerTwoPanel = playerTwoPanel;
        }

        public Scene getScene() {
            return scene;
        }

        public void update(BattleSnapshotData snapshot) {
            boolean finished = snapshot != null && snapshot.isFinished();

            subtitleLabel.setText(finished ? "Combate finalizado" : "Combate en curso");
            subtitleLabel.setStyle("-fx-text-fill: " + (finished ? "#86efac" : "#cbd5e1") + ";");
            roomCodeLabel.setText("Sala " + safeRoomCode(snapshot));
            turnLabel.setText(finished ? safeBattleResult(snapshot) : "Turno " + safeTurnNumber(snapshot));
            turnLabel.setStyle("-fx-text-fill: " + (finished ? "#86efac" : "#fcd34d") + ";");
            statusLabel.setText(safeBattleMessage(snapshot));
            footerHintLabel.setText(finished ? "Partida terminada" : "Esperando acciones de los jugadores");

            playerOnePanel.update(snapshot != null ? snapshot.getPlayerOne() : null);
            playerTwoPanel.update(snapshot != null ? snapshot.getPlayerTwo() : null);
        }
    }

    private static final class BattlePlayerPanelView {
        private final VBox root;
        private final Label usernameLabel;
        private final Label teamLabel;
        private final ImageView jaBeaImage;
        private final Label activeJaBeaLabel;
        private final Label hpLabel;
        private final Region healthTrack;
        private final Region healthFill;

        private BattlePlayerPanelView(
                VBox root,
                Label usernameLabel,
                Label teamLabel,
                ImageView jaBeaImage,
                Label activeJaBeaLabel,
                Label hpLabel,
                Region healthTrack,
                Region healthFill
        ) {
            this.root = root;
            this.usernameLabel = usernameLabel;
            this.teamLabel = teamLabel;
            this.jaBeaImage = jaBeaImage;
            this.activeJaBeaLabel = activeJaBeaLabel;
            this.hpLabel = hpLabel;
            this.healthTrack = healthTrack;
            this.healthFill = healthFill;
        }

        public VBox getRoot() {
            return root;
        }

        public void update(BattlePlayerSnapshotData player) {
            String username = player != null && player.getUsername() != null && !player.getUsername().isBlank()
                    ? player.getUsername()
                    : "Jugador pendiente";
            usernameLabel.setText(username);

            String teamName = player != null && player.getTeamName() != null && !player.getTeamName().isBlank()
                    ? player.getTeamName()
                    : "Equipo pendiente";
            teamLabel.setText("Equipo: " + teamName);

            BattleCreatureSnapshotData activeJaBea = player != null ? player.getActiveJaBea() : null;
            String activeJaBeaName = activeJaBea != null && activeJaBea.getName() != null && !activeJaBea.getName().isBlank()
                    ? activeJaBea.getName()
                    : "Pendiente";
            activeJaBeaLabel.setText("JaBea activo: " + activeJaBeaName);
            jaBeaImage.setImage(new Image(BattleScreenFactory.class.getResourceAsStream(resolveJaBeaImagePath(activeJaBeaName))));

            int currentHealth = activeJaBea != null && activeJaBea.getCurrentHealth() != null
                    ? activeJaBea.getCurrentHealth()
                    : 0;
            int maxHealth = activeJaBea != null && activeJaBea.getMaxHealth() != null && activeJaBea.getMaxHealth() > 0
                    ? activeJaBea.getMaxHealth()
                    : 1;
            double healthRatio = Math.max(0, Math.min(1, (double) currentHealth / maxHealth));

            hpLabel.setText("Vida: " + currentHealth + " / " + maxHealth);
            healthFill.prefWidthProperty().unbind();
            healthFill.prefWidthProperty().bind(healthTrack.widthProperty().multiply(healthRatio));
            healthFill.setStyle(
                    "-fx-background-color: " + healthColor(healthRatio) + ";" +
                    "-fx-background-radius: 999;"
            );
        }
    }
}
