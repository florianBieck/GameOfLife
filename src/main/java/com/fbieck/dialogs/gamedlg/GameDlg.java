package com.fbieck.dialogs.gamedlg;

import com.fbieck.game.Cell;
import com.fbieck.game.Game;
import com.fbieck.main.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Vector;

public class GameDlg extends Dialog<Game> {

    //Dialog zur Erstellung und Bearbeitung von Spielfeldern

    @FXML
    private DialogPane dialogPane;
    @FXML
    private BorderPane windowParent;
    @FXML
    private Label lblTitle;
    @FXML
    private Spinner<Integer> spinSize;
    @FXML
    private Button btnCreate, btnCancel;

    public GameDlg(Game game) {

        initOwner(Main.MAINSTAGE);
        initStyle(StageStyle.DECORATED);
        initModality(Modality.WINDOW_MODAL);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/gamedlg.fxml"));
        loader.setController(this);
        try {
            loader.load();
            setDialogPane(dialogPane);

            getDialogPane().getScene().getWindow().setOnCloseRequest( x -> realClose());
            btnCancel.setOnAction( x -> realClose());
            setTitle("Spielfeld erstellen");
            //Spielfeldgröße
            spinSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100000, 25));
            spinSize.setEditable(true);

            //Wenn Spielfeld übergeben wurde, dann in Bearbeitungsmodus
            if (game != null){
                setTitle("Spielfeld bearbeiten");
                lblTitle.setText("Spielfeld bearbeiten");
                btnCreate.setText("Speichern");
                spinSize.getValueFactory().setValue(game.getGenerations().size());
            }

            btnCreate.setOnAction( x -> {
                //Eventhandling davon abhängig, ob im Bearbeitungs- oder im Erstellungsmodus
                if (btnCreate.getText().equals("Erstellen")){
                    setResult(new Game(spinSize.getValue()));
                }
                else{
                    Game g = new Game(spinSize.getValue());
                    if (game != null) {
                        for (int i = 0; i < game.getGenerations().size() && i < g.getGenerations().size(); i++) {
                            Vector<Cell> line = game.getIndexedGeneration().get(i);
                            for (int j = 0; j < line.size() && j < g.getGenerations().get(i).size(); j++) {
                                g.getIndexedGeneration().get(i).get(j).setStatus(game.getIndexedGeneration().get(i).get(j).getStatus());
                            }
                        }
                    }
                    setResult(g);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void realClose(){
        //Bei null Results erfordern JavaFX-Dialoge bestimmte Behandlung
        setResult(null);
        ((Stage)getDialogPane().getScene().getWindow()).close();
        close();
    }
}
