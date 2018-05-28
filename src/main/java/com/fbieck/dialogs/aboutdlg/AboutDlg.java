package com.fbieck.dialogs.aboutdlg;

import com.fbieck.main.Main;
import com.fbieck.util.ImageController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AboutDlg extends Dialog<Integer> {

    //Dialog zur Darstellung einer About-Seite

    @FXML
    private DialogPane dialogPane;
    @FXML
    private BorderPane windowParent;
    @FXML
    private Label lblTitle, lblMsg;
    @FXML
    private ImageView ivImg;
    @FXML
    private Button btnClose;

    public AboutDlg() {

        initOwner(Main.MAINSTAGE);
        initStyle(StageStyle.DECORATED);
        initModality(Modality.WINDOW_MODAL);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/aboutdlg.fxml"));
        loader.setController(this);
        try {
            loader.load();
            setDialogPane(dialogPane);
            setTitle("About");
            getDialogPane().getScene().getWindow().setOnCloseRequest( x ->
                    //Dialog schließen
                    setResult(0)
            );

            ivImg.setImage(ImageController.getInstance().getImgLogo());
            btnClose.setOnAction(x ->
                //Dialog schließen
                setResult(0)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
