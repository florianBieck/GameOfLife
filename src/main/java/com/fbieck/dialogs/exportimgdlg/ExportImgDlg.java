package com.fbieck.dialogs.exportimgdlg;

import com.fbieck.dialogs.messagedlg.MessageDlg;
import com.fbieck.game.CellContainer;
import com.fbieck.game.Game;
import com.fbieck.main.Main;
import com.fbieck.util.FileController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ExportImgDlg extends Dialog<Integer> {

    //Dialog zum Export der Canvas

    @FXML
    private DialogPane dialogPane;
    @FXML
    private BorderPane windowParent, processParent;
    @FXML
    private Label lblTitle;
    @FXML
    private Spinner<Integer> spinSize;
    @FXML
    private Button btnExport, btnCancel;

    private final Game game;
    private final boolean borders, labels;

    public ExportImgDlg(Game game, boolean borders, boolean labels) {

        //Übergebene Einstellungen übernehmen
        this.game = game;
        this.borders = borders;
        this.labels = labels;

        initOwner(Main.MAINSTAGE);
        initStyle(StageStyle.DECORATED);
        initModality(Modality.WINDOW_MODAL);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/exportimg.fxml"));
        loader.setController(this);
        try {
            loader.load();
            setDialogPane(dialogPane);
            getDialogPane().getScene().getWindow().setOnCloseRequest( x ->
                    //Dialog schließen
                    setResult(0)
            );
            setTitle("Canvas exportieren");

            //Größe einer Zelle auf dem Bild
            spinSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 512, 32));
            spinSize.setEditable(true);

            btnExport.setOnAction(x -> export());
            btnCancel.setOnAction( x -> {
                //Dialog schließen
                setResult(0);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void export(){
        //Zelgröße und Bildgröße bestimmen.
        int cellSize = spinSize.getValue();
        int outputSize = (game.getIndexedGeneration().size() * cellSize)+2;
        //Bild in externem Task erstellen
        Task<Pane> exportTask = new Task<Pane>() {
            @Override
            protected Pane call() throws Exception {
                Pane export = null;
                //Wenn überhaupt ein Spielfeld vorliegt
                if (game.getIndexedGeneration() != null && game.getIndexedGeneration().size() > 0) {
                    //Container für alle Zellen schaffen
                    export = new Pane();
                    export.setPrefWidth(outputSize);
                    export.setPrefHeight(outputSize);
                    //Für jede Zelle der aktuellen Generation
                    for (int i = 0; i<game.getIndexedGeneration().size(); i++){
                        for (int j = 0; j<game.getIndexedGeneration().get(i).size(); j++){
                            //Container einer Zelle schaffen
                            CellContainer cc = new CellContainer(game.getIndexedGeneration().get(i).get(j), i, j);
                            cc.setWidth(cellSize);
                            cc.setHeight(cellSize);
                            cc.setLayoutX(cellSize*i);
                            cc.setLayoutY(cellSize*j);
                            //Wenn Border gewollt -> hinzufügen
                            if (borders) {
                                cc.setStroke(Color.BLACK);
                                cc.setStrokeWidth(1);
                                cc.setStrokeType(StrokeType.INSIDE);
                            }
                            export.getChildren().add(cc);
                            //Wenn Labels gewollt -> hinzufügen
                            if (labels){
                                Label lblStatus = new Label();
                                lblStatus.setLayoutX(cc.getLayoutX());
                                lblStatus.setLayoutY(cc.getLayoutY());
                                lblStatus.setPrefWidth(cellSize);
                                lblStatus.setPrefHeight(cellSize);
                                lblStatus.setAlignment(Pos.CENTER);
                                lblStatus.setFont(Font.font(lblStatus.getPrefHeight()/1.5));
                                export.getChildren().add(lblStatus);
                            }
                        }
                    }
                }
                return export;
            }
        };
        exportTask.setOnFailed( x -> {
            exportTask.getException().printStackTrace();
            windowParent.setVisible(true);
            processParent.setVisible(false);
        });
        exportTask.setOnSucceeded( x -> {
            Pane p = exportTask.getValue();
            if ( p != null ) {
                //Parameter für Ausgabe bestimmen
                SnapshotParameters sp = new SnapshotParameters();
                sp.setFill(Color.TRANSPARENT);
                WritableImage wi;
                try {
                    //Snapshot der Zellencontainer auf ein Image projezieren
                    wi = new WritableImage(outputSize, outputSize);
                    p.snapshot(sp, wi);
                    if (FileController.getInstance().writeImgTo(wi)) {
                        MessageDlg messageDlg = new MessageDlg(2, "Export erfolgreich!", "Der Export war erfolgreich!");
                        messageDlg.showAndWait();
                        //Dialog schließen
                        setResult(0);
                    }
                }
                catch (Exception e){
                    MessageDlg messageDlg = new MessageDlg(0, "Fehler beim Export", "Es ist ein Fehler beim Export aufgetreten.\nEventuell " +
                            "wurde der Arbeitsspeicher zu sehr ausgelastet.\nVersuchen Sie bitte eine kleinere Feldgröße.");
                    messageDlg.showAndWait();
                }
                windowParent.setVisible(true);
                processParent.setVisible(false);
            }
        });
        windowParent.setVisible(false);
        processParent.setVisible(true);
        Thread t1 = new Thread(exportTask);
        t1.setDaemon(true);
        t1.start();
    }
}
