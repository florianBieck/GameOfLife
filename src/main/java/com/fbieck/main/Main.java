package com.fbieck.main;

import com.fbieck.util.ImageController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    //Hauptstage und Controller statisch merken
    public static MainController MAINCONTROLLER;
    public static Stage MAINSTAGE;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Anwendung starten

        //PrimaryStage statisch merken
        MAINSTAGE = primaryStage;
        primaryStage.setMinWidth(1368);
        primaryStage.setMinHeight(768);

        //Aufbau der View anhand von 'main.fxml'
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/main.fxml"));
        MAINCONTROLLER = new MainController();
        //MAINCONTROLLER als Controller setzen
        loader.setController(MAINCONTROLLER);
        Parent root = loader.load();
        primaryStage.setTitle("Game of Life");
        primaryStage.getIcons().add(ImageController.getInstance().getImgLogo());
        //Vollbildeinstellungen
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreenExitHint("ESC zum Verlassen");
        //Szene laden mit 70% Bildschirmgröße
        double width = Screen.getPrimary().getBounds().getWidth()*0.85;
        double height = Screen.getPrimary().getBounds().getHeight()*0.7;
        Scene scn = new Scene(root, width, height);
        scn.addEventFilter(MouseEvent.DRAG_DETECTED , mouseEvent -> scn.startFullDrag());
        primaryStage.setScene(scn);
    }
}
