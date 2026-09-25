package br.com.fintrack.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FinApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(FinApp.class.getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load(), 520, 520);
        scene.getStylesheets().add(FinApp.class.getResource("/css/estilo.css").toExternalForm());
        stage.setTitle("FinTrack - Controle Financeiro");
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
