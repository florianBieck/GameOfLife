package com.fbieck.util;

import javafx.scene.image.Image;

public class ImageController {

    //Singleton Controller zur einmaligen Initierung der Bilder und ständigen Verfügbarkeit in weiteren Klassen

    private static ImageController instance;

    //Icons für Kontrollelemente und das Logo
    private final Image imgLogo = new Image(getClass().getClassLoader().getResourceAsStream("img/cells.png")),
            imgPlay = new Image(getClass().getClassLoader().getResourceAsStream("img/play-button.png")),
            imgPause = new Image(getClass().getClassLoader().getResourceAsStream("img/pause.png")),
            imgFullscreen = new Image(getClass().getClassLoader().getResourceAsStream("img/fullscreen.png")),
            imgBrush = new Image(getClass().getClassLoader().getResourceAsStream("img/paint-brush.png")),
            imgEraser = new Image(getClass().getClassLoader().getResourceAsStream("img/eraser.png")),
            imgSkip = new Image(getClass().getClassLoader().getResourceAsStream("img/skip.png")),
            imgSkipBack = new Image(getClass().getClassLoader().getResourceAsStream("img/skip-back.png")),
            imgError = new Image(getClass().getClassLoader().getResourceAsStream("img/error.png")),
            imgSuccess = new Image(getClass().getClassLoader().getResourceAsStream("img/success.png")),
            imgWarning = new Image(getClass().getClassLoader().getResourceAsStream("img/warning.png"));

    private ImageController() {
    }


    public static ImageController getInstance() {
        if (instance == null){
            instance = new ImageController();
        }
        return instance;
    }

    public Image getImgLogo() {
        return imgLogo;
    }

    public Image getImgPlay() {
        return imgPlay;
    }

    public Image getImgPause() {
        return imgPause;
    }

    public Image getImgFullscreen() {
        return imgFullscreen;
    }

    public Image getImgBrush() {
        return imgBrush;
    }

    public Image getImgEraser() {
        return imgEraser;
    }

    public Image getImgSkip() {
        return imgSkip;
    }

    public Image getImgSkipBack() {
        return imgSkipBack;
    }

    public Image getImgError() {
        return imgError;
    }

    public Image getImgSuccess() {
        return imgSuccess;
    }

    public Image getImgWarning() {
        return imgWarning;
    }
}
