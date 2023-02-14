package com.example.toomanybarbershopgui;

import javafx.animation.Animation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;

public class MyApplication extends Application {
    public static AnchorPane animationPane;
    public static Configuration config;
    public static Animation.Status animationStatus = Animation.Status.STOPPED;

    @Override
    public void start(Stage stage) throws IOException {

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });

        stage.getIcons().add(new Image(MyApplication.class.getResource("icon.jpg").toString()));


        FXMLLoader fxmlLoader = new FXMLLoader(MyApplication.class.getResource("tooManyBarberShopView.fxml"));
        SplitPane root = (SplitPane) fxmlLoader.load();
        animationPane = (AnchorPane) root.getItems().get(0);
        Scene scene = new Scene(root, 1138, 800);
        stage.setTitle("TooManyBarberShopApplication");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}