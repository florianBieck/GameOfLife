package com.fbieck.dialogs.messagedlg;

import com.fbieck.main.Main;
import com.fbieck.util.ImageController;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MessageDlg extends Dialog<Integer> {

    //Dialog zur Vermittlung von Fehler-, Erfolgs- und Warnungsmitteilungen

    @FXML
    private DialogPane dialogPane;
    @FXML
    private BorderPane windowParent;
    @FXML
    private Label lblTitle, lblMsg;
    @FXML
    private ImageView ivImg;
    @FXML
    private Button btnYes, btnNo, btnClose;

    public MessageDlg(int type, String title, String message) {

        initOwner(Main.MAINSTAGE);
        initStyle(StageStyle.DECORATED);
        initModality(Modality.WINDOW_MODAL);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/messagedlg.fxml"));
        loader.setController(this);
        try {
            loader.load();
            setDialogPane(dialogPane);
            getDialogPane().getScene().getWindow().setOnCloseRequest( x -> setResult(0));

            //Initierung abhängig vom Typ
            switch (type){
                case 0:
                    //Fehlermeldung
                    setTitle("Fehler!");
                    ivImg.setImage(ImageController.getInstance().getImgError());
                    btnYes.setVisible(false);
                    btnNo.setVisible(false);
                    break;
                case 1:
                    //Warnungsmeldung
                    setTitle("Warnung!");
                    ivImg.setImage(ImageController.getInstance().getImgWarning());
                    btnYes.setVisible(false);
                    btnNo.setVisible(false);
                    break;
                case 2:
                    //Erfolgsmeldung
                    setTitle("Erfolg!");
                    ivImg.setImage(ImageController.getInstance().getImgSuccess());
                    btnYes.setVisible(false);
                    btnNo.setVisible(false);
                    break;
                case 3:
                    //Meldung mit Usereingabe (Ja, Nein, Abbrechen)
                    setTitle("Achtung!");
                    ivImg.setImage(ImageController.getInstance().getImgWarning());
                    break;
                case 4:
                    //Meldung, die sich automatisch schließt
                    new AnimationTimer() {
                        private long tmp;
                        private int count = 0;
                        private final int anz = 3;
                        @Override
                        public void handle(long now) {
                            btnClose.setText("Schließen ("+(anz-count)+")");
                            if (now-tmp>1000000000){
                                tmp = now;
                                if ( count == anz ){
                                    setResult(0);
                                    stop();
                                }
                                count++;
                            }
                        }
                    }.start();
                    setTitle("Achtung!");
                    ivImg.setImage(ImageController.getInstance().getImgWarning());
                    btnYes.setVisible(false);
                    btnNo.setVisible(false);
                    windowParent.setOnKeyPressed( x -> {
                        //Bei erneutem F11 Dialog schließen und Vollbildmodus beenden
                        if (x.getCode().equals(KeyCode.F11)){
                            setResult(0);
                            Main.MAINCONTROLLER.fullscreenProperty().setValue(false);
                        }
                    });
                    break;
            }

            lblTitle.setText(title);
            lblMsg.setText(message);

            btnYes.setOnAction( x -> {
                //YES - Result
                setResult(1);
            });
            btnNo.setOnAction( x -> {
                //NO - Result
                setResult(2);
            });
            btnClose.setOnAction(x -> {
                //'Null' - Result
                setResult(0);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
