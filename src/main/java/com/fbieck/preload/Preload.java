package com.fbieck.preload;

import com.fbieck.main.Main;
import com.fbieck.util.ImageController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Preload extends Preloader {

    private Scene scn;

    @FXML
    private ImageView imgView;

    //Boolische Variable für 2 Sekunden Delay
    private boolean go = false;

    @Override
    public void start(final Stage primaryStage) throws Exception {

        //Preloader initiieren und zeigen
        initScene();
        primaryStage.setScene(scn);
        primaryStage.setTitle("Convays Game of Life");
        primaryStage.getIcons().add(ImageController.getInstance().getImgLogo());
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();

        //Delay von 2 Sekunden einbauen, um Logo zu zeigen
        new AnimationTimer() {
            private long timeout = 2000000000L, tmp = 0;
            @Override
            public void handle(long now) {
                if (tmp == 0){
                    tmp = now;
                }
                else {
                    if (go) {
                        primaryStage.hide();
                        Main.MAINSTAGE.show();
                        stop();
                    }
                    if (now - tmp > timeout) {
                        go = true;
                        tmp = now;
                    }
                }
            }
        }.start();
    }

    public void initScene(){
        //Scene vom Preloader laden
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/preload.fxml"));
        loader.setController(this);
        try {
            Parent root = loader.load();
            scn = new Scene(root);
            scn.setFill(Color.TRANSPARENT);
            imgView.setImage(ImageController.getInstance().getImgLogo());
        } catch (IOException e) {
            //Ignore
        }
    }

    public static void main(String[] args) {
        //Startpunkt der Anwendung. Erst wird preloader gestartet, dann die MainApp geladen
        System.setProperty("javafx.preloader", Preload.class.getName());
        Application.launch(Main.class, args);
    }
}
