package com.marcos.javabeasts_javafx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class JavaBeastsLobbyApp extends Application {

    @Override
    public void start(Stage stage) {
        Label title = new Label("JavaBeasts");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));

        Label subtitle = new Label("Pantalla de sala");
        subtitle.setFont(Font.font(18));

        Label placeholder = new Label("Lobby JavaFX en preparacion...");
        placeholder.setFont(Font.font(16));

        VBox root = new VBox(12, title, subtitle, placeholder);
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 960, 540);
        stage.setTitle("JavaBeasts");
        stage.setScene(scene);
        stage.setMinWidth(720);
        stage.setMinHeight(420);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
