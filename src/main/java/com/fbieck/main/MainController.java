package com.fbieck.main;

import com.fbieck.dialogs.aboutdlg.AboutDlg;
import com.fbieck.dialogs.exportimgdlg.ExportImgDlg;
import com.fbieck.dialogs.gamedlg.GameDlg;
import com.fbieck.dialogs.messagedlg.MessageDlg;
import com.fbieck.dialogs.settingsdlg.SettingsDlg;
import com.fbieck.game.Cell;
import com.fbieck.game.CellContainer;
import com.fbieck.game.Game;
import com.fbieck.util.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

public class MainController implements Initializable {

    /*
        Wichtigster Controller der App, die meisten Berechnungen befinden sich hier.
        Die wichtigsten Funktionen sind getNeighbourAliveCount, initGame und cycle.
     */

    //Referenzen an 'main.fxml'-View
    @FXML
    private BorderPane windowParent;
    @FXML
    private MenuBar mainMb;
    @FXML
    private Menu menuFile, menuEdit, menuAnalysis, menuOptions, menuHelp;
    @FXML
    private MenuItem menuFileNew, menuFileOpen, menuFileSaveAs, menuFileSave, menuFileExportCanvas, menuFileImportCanvas, menuFileExit,
        menuEditGame, menuEditClearGame, menuEditInvertStatus,
        menuAnalysisCountAll, menuAnalysisCountRect, menuAnalysisCountCrosshair, menuAnalysisCountStretchedCrosshair, menuAnalysisCountArrow,
            menuAnalysisCountDoubledCrosshair, menuAnalysisCountDiagonale,
        menuAnalysisMarkAll, menuAnalysisMarkRect, menuAnalysisMarkCrosshair, menuAnalysisMarkStretchedCrosshair, menuAnalysisMarkArrow,
            menuAnalysisMarkDoubledCrosshair, menuAnalysisMarkDiagonale,
        menuOptionsSettings,
        menuHelpAbout;
    @FXML
    private CheckMenuItem menuOptionsLabels, menuOptionsBorders;
    @FXML
    private VBox vbSwitch, vbFullscreen, vbBrush, vbEraser, vbSkipBack, vbSkip;
    @FXML
    private ImageView ivSwitch, ivFullscreen, ivBrush, ivEraser, ivSkip, ivSkipBack;
    @FXML
    private CheckBox cbLabels, cbBorders;
    @FXML
    private Label lblSwitch, lblGeneration;
    @FXML
    private ProgressIndicator piGeneration;
    @FXML
    private Pane gamePane;

    //Game initialisieren
    private Game game = new Game(8);
    //Timer für Generationsberechnung
    private AnimationTimer generation;
    private final BooleanProperty generationRunning = new SimpleBooleanProperty(false),
            fullscreenProperty = new SimpleBooleanProperty(false),
            brushingProperty = new SimpleBooleanProperty(false),
            erasingProperty = new SimpleBooleanProperty(false);
    //Container für Darstellung von Zellen
    private final Vector<Vector<CellContainer>> cellContainers = new Vector<>();
    //Properties für Zellengröße, um Spielfeld der Anwendungsgröße anzupassen
    private final DoubleProperty cellWidthProperty = new SimpleDoubleProperty(32.0), cellHeightProperty = new SimpleDoubleProperty(32.0);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        menuFile.setOnShowing( x -> {
            //Bei Aufruf Berechnung stoppen und Items ggf. deaktivieren
            stopGame();
            menuFileSave.setDisable(FileController.getInstance().getFilename().isEmpty() || game == null || game.getGenerations().size() == 0);
            menuFileSaveAs.setDisable(game == null || game.getGenerations().size() == 0);
            menuFileExportCanvas.setDisable(game == null || game.getGenerations().size() == 0);
        });
        menuFileNew.setOnAction( x -> {
            int result;
            //result == 2 bedeutet, der Dialog für ein neues Spiel kann aufgerufen werden
            if (!FileController.getInstance().getFilename().isEmpty()){
                result = 2;
            }
            else {
                MessageDlg messageDlg = new MessageDlg(3, "Wollen Sie noch speichern?", "Sie haben Änderungen vorgenommen, die noch nicht" +
                        " gespeichert wurden.\nWollen Sie diese Änderungen noch speichern?");
                messageDlg.showAndWait();
                result = messageDlg.getResult();
            }
            if (result == 2) {
                GameDlg gameDlg = new GameDlg(null);
                gameDlg.showAndWait();
                Game g = gameDlg.getResult();
                if (g != null) {
                    game = g;
                    initGame();
                    FileController.getInstance().setFilename("");
                }
            }
        });
        menuFileOpen.setOnAction( x -> {
            int result;
            //result == 2 bedeutet, der Dialog für ein neues Spiel kann aufgerufen werden
            if (!FileController.getInstance().getFilename().isEmpty()){
                result = 2;
            }
            else {
                MessageDlg messageDlg = new MessageDlg(3, "Wollen Sie noch speichern?", "Sie haben Änderungen vorgenommen, die noch nicht" +
                        " gespeichert wurden.\nWollen Sie diese Änderungen noch speichern?");
                messageDlg.showAndWait();
                result = messageDlg.getResult();
            }
            if (result == 2) {
                Game g = FileController.getInstance().readGame();
                if (g != null) {
                    game = g;
                    initGame();
                }
            }
        });
        menuFileSave.setOnAction( x -> {
            //FileController versucht auf gespeicherten Dateipfad zu speichern

            if (!FileController.getInstance().writeGame(game)){
                MessageDlg messageDlg = new MessageDlg(0, "Fehler!", "Spielfeld konnte nicht gespeichert werden!");
                messageDlg.showAndWait();
            }
        });
        menuFileSaveAs.setOnAction( x -> {
            //FileController speichert in neuen Dateipfad
            MessageDlg messageDlg;
            if (FileController.getInstance().writeGameTo(game)){
                messageDlg = new MessageDlg(2, "Erfolg!", "Spielfeld wurde erfolgreich gespeichert!");
            }
            else{
                messageDlg = new MessageDlg(0, "Fehler!", "Spielfeld konnte nicht gespeichert werden!");
            }
            messageDlg.showAndWait();
        });
        menuFileExportCanvas.setOnAction( x -> {
            //ExportImgDlg erstellen und zeigen. Weiteres Handling wird dort durchgeführt
            ExportImgDlg exportImgDlg = new ExportImgDlg(game, cbBorders.isSelected(), cbLabels.isSelected());
            exportImgDlg.showAndWait();
        });
        menuFileImportCanvas.setOnAction( x -> {
            //Benutzer Feedback geben, dass geladen wird.
            piGeneration.setVisible(true);
            disableControls(2);
            MessageDlg messageDlg = new MessageDlg(0, "Fehler beim Import", "Das Bild konnte leider nicht importiert werden.\n" +
                    "Vielleicht handelt es sich dabei nicht um ein reines Spielfeld und es wurden zu viele Farben benutzt (max. 3 Farben)?");
            //Bild vom FileController anfordern
            WritableImage wi = FileController.getInstance().readImage();
            //Task erstellen, um Ladeprozess in anderen Thread durchzuführen
            Task<Game> importer = new Task<Game>() {
                @Override
                protected Game call() throws Exception {
                    //Falls überhaupt ein Bild geladen wurde
                    if ( wi != null ){
                        //Farbumfang speichern
                        Vector<Color> colors = new Vector<>();
                        //Anzahl der aufgenommen Farben speichern
                        Vector<Integer> colorCount = new Vector<>();
                        //Jeden Pixel des Bildes durchlaufen und Farbe verarbeiten
                        for (int i=0; i<wi.getWidth() && colors.size() < 400; i++){
                            for (int j=0; j<wi.getHeight() && colors.size() < 400; j++){
                                Color cl = wi.getPixelReader().getColor(i, j);
                                //Transparente Farbe ignorieren
                                if (!cl.equals(Color.TRANSPARENT)) {
                                    if (!colors.contains(cl)) {
                                        colors.add(cl);
                                        colorCount.add(1);
                                    } else {
                                        colorCount.set(colors.indexOf(cl), colorCount.get(colors.indexOf(cl)) + 1);
                                    }
                                }
                            }
                        }
                        int minCount = Integer.MAX_VALUE, minIndex = -1;
                        Color borderColor = null;
                        if (colors.size() == 3) {
                            //Borderfarbe ermitteln. -> Borderfarbe am seltesten vorhanden
                            for (int i = 0; i < colors.size(); i++) {
                                if (colorCount.get(i) < minCount) {
                                    minCount = colorCount.get(i);
                                    minIndex = i;
                                }
                            }
                            if (minIndex != -1){
                                borderColor = colors.get(minIndex);
                                colors.remove(borderColor);
                            }
                        }
                        if (colors.size()==2 || (colors.size() == 3 && minIndex != -1)) {
                            //Borderweite und Zellengröße ermitteln
                            int cellSize = Integer.MAX_VALUE, tmpSize = 0, minBorderSize = Integer.MAX_VALUE, tmpBorderSize = 0;
                            Color tmpColor = null;
                            for (int i = 0; i < wi.getWidth(); i++) {
                                for (int j = 0; j < wi.getHeight(); j++) {
                                    Color cl = wi.getPixelReader().getColor(i, j);
                                    if (tmpColor == null) {
                                        tmpColor = cl;
                                    }

                                    if (borderColor != null && cl.equals(borderColor)){
                                        tmpBorderSize++;
                                    }
                                    else {
                                        if (minBorderSize > tmpBorderSize && tmpBorderSize > 0){
                                            minBorderSize = tmpBorderSize;
                                        }
                                        tmpBorderSize = 0;
                                        if (tmpColor.equals(cl)) {
                                            tmpSize++;
                                        }
                                        else {
                                            tmpColor = cl;
                                            if (cellSize > tmpSize && tmpSize > 5 ) {
                                                cellSize = tmpSize;
                                            }
                                            tmpSize = 0;
                                        }
                                    }
                                }
                            }
                            //Zellgröße anpassen
                            if (minBorderSize == Integer.MAX_VALUE){
                                minBorderSize = 0;
                            }
                            cellSize = cellSize + (minBorderSize*2) + 1;
                            int gameSize = (int)wi.getWidth()/cellSize;
                            Game g = new Game(gameSize);
                            //Spielfeld anhand der Zellengröße, Bordergröße und Farben generieren
                            for (int i=0; i<g.getIndexedGeneration().size(); i++) {
                                for (int j=0; j<g.getIndexedGeneration().get(i).size(); j++) {
                                    int xx = i*(cellSize)+(minBorderSize*2)+1, yy= j*(cellSize)+(minBorderSize*2)+1;
                                    Color cl = wi.getPixelReader().getColor(xx, yy);
                                    if (cl.equals(colors.get(0))){
                                        g.getIndexedGeneration().get(i).get(j).setStatus(0);
                                    }
                                    else if(cl.equals(colors.get(1))){
                                        g.getIndexedGeneration().get(i).get(j).setStatus(1);
                                    }
                                }
                            }
                            return g;
                        }
                    }
                    return null;
                }
            };
            importer.setOnFailed( y -> {
                disableControls(0);
                messageDlg.showAndWait();
            });
            importer.setOnSucceeded( y -> {
                //Spielfeld setzen und initialisieren
                Game g = importer.getValue();
                if ( g != null ) {
                    game = g;
                    initGame();
                }
                else{
                    messageDlg.showAndWait();
                }
                piGeneration.setVisible(false);
                disableControls(0);
            });
            //Task in neuem Thread ausführen
            Thread t1 = new Thread(importer);
            t1.setDaemon(true);
            t1.start();
        });
        menuFileExit.setOnAction( x -> {
            int result;
            //result == 2 bedeutet, der Dialog für ein neues Spiel kann aufgerufen werden
            if (!FileController.getInstance().getFilename().isEmpty()){
                result = 2;
            }
            else {
                MessageDlg messageDlg = new MessageDlg(3, "Wollen Sie noch speichern?", "Sie haben Änderungen vorgenommen, die noch nicht" +
                        " gespeichert wurden.\nWollen Sie diese Änderungen noch speichern?");
                messageDlg.showAndWait();
                result = messageDlg.getResult();
            }
            if (result == 2) {
                Platform.exit();
            }
        });
        menuEditGame.setOnAction( x -> {
            //GameDlg öffnen, um Spielfeldgröße zu verändern
            GameDlg gameDlg = new GameDlg(game);
            gameDlg.showAndWait();
            Game g = gameDlg.getResult();
            if ( g != null ){
                game = g;
                initGame();
            }
        });
        menuEditClearGame.setOnAction( x -> {
            //Alle Zellen töten
            piGeneration.setVisible(true);
            disableControls(2);
            Task<Void> death = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    for (Vector<CellContainer> cellContainer : cellContainers) {
                        for (CellContainer aCellContainer : cellContainer) {
                            aCellContainer.setStatusProperty(0);
                        }
                    }
                    return null;
                }
            };
            death.setOnScheduled( y -> {
                //Visuelles Feedback im Hauptthread
                initLabels();
                piGeneration.setVisible(false);
                disableControls(0);
            });
            Thread t1 = new Thread(death);
            t1.setDaemon(true);
            t1.start();
        });
        menuEditInvertStatus.setOnAction( x -> {
            //Status aller Zellen invertieren
            piGeneration.setVisible(true);
            disableControls(2);
            Task<Void> inverter = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    for (Vector<CellContainer> cellContainer : cellContainers) {
                        for (CellContainer cc : cellContainer) {
                            if (cc.getStatusProperty() == 0) {
                                cc.setStatusProperty(1);
                            } else {
                                cc.setStatusProperty(0);
                            }
                        }
                    }
                    return null;
                }
            };
            inverter.setOnScheduled( y -> {
                initGame();
                //Visuelles Feedback im Hauptthread
                piGeneration.setVisible(false);
                disableControls(0);
            });
            Thread t1 = new Thread(inverter);
            t1.setDaemon(true);
            t1.start();
        });
        menuAnalysisCountAll.setOnAction( x -> {
            //TODO
            Vector<Integer> counter = new Vector<>();
            counter.add(0); //Rechteck
            counter.add(0); //Fadenkreuz
            counter.add(0); //Hohes Fadenkreuz
            counter.add(0); //Pfeil
            counter.add(0); //Doppeltes Fadenkreuz
            counter.add(0); //Diagonale
            //Vector mit bereits überprüften Zellen
            Vector<Vector<Integer>> checkes = new Vector<>();
            for (int i=0; i<cellContainers.size(); i++){
                for (int j=0; j<cellContainers.get(i).size(); j++){
                    CellContainer cc = cellContainers.get(i).get(j);
                    boolean checked = false;
                    for (Vector<Integer> check:checkes){
                        if (check.get(0) == i && check.get(1) == j){
                            checked = true;
                        }
                    }
                    if (!checked && cc.getStatusProperty() == 1){
                        //TODO
                        //Zelle kann nur Teil eines Objektes sein
                        Vector<Vector<Integer>> erg = GameAnalysis.isPieceOfStaticObject(cc, game, counter);
                        for (Vector<Integer> obj:erg){
                            checkes.add(obj);
                        }
                    }
                }
            }
            MessageDlg messageDlg = new MessageDlg(2, "Anzahl statischer Objekte",
                    "Rechtecke:\t\t\t"+counter.get(0)+"\n"+
                    "Fadenkreuze:\t\t\t"+counter.get(1)+"\n"+
                    "Hohes Fadenkreuze:\t"+counter.get(2)+"\n"+
                    "Pfeile:\t\t\t\t"+counter.get(3)+"\n"+
                    "Doppeltes Fadenkreuze:\t"+counter.get(4)+"\n"+
                    "Diagonalen:\t\t\t"+counter.get(5)+"\n");
            messageDlg.showAndWait();
        });
        menuAnalysisCountRect.setOnAction( x -> {
            MessageDlg messageDlg = new MessageDlg(2, "Anzahl Rechtecke", "Die Anzahl an Rechtecken in diesem Spielfeld " +
                    "beträgt "+countObjects(0)+".");
            messageDlg.showAndWait();
        });
        menuAnalysisCountCrosshair.setOnAction( x -> {
            MessageDlg messageDlg = new MessageDlg(2, "Anzahl Fadenkreuze", "Die Anzahl an Fadenkreuzen in diesem Spielfeld " +
                    "beträgt "+countObjects(1)+".");
            messageDlg.showAndWait();
        });
        menuAnalysisCountStretchedCrosshair.setOnAction( x -> {
            MessageDlg messageDlg = new MessageDlg(2, "Anzahl hoher Fadenkreuze", "Die Anzahl an hohen Fadenkreuzen in diesem Spielfeld " +
                    "beträgt "+countObjects(2)+".");
            messageDlg.showAndWait();
        });
        menuAnalysisCountArrow.setOnAction( x -> {
            MessageDlg messageDlg = new MessageDlg(2, "Anzahl Pfeile", "Die Anzahl an Pfeilen in diesem Spielfeld " +
                    "beträgt "+countObjects(3)+".");
            messageDlg.showAndWait();
        });
        menuAnalysisCountDoubledCrosshair.setOnAction( x -> {
            MessageDlg messageDlg = new MessageDlg(2, "Anzahl doppelter Fadenkreuze", "Die Anzahl an doppelten Fadenkreuzen in diesem " +
                    "Spielfeld beträgt "+countObjects(4)+".");
            messageDlg.showAndWait();
        });
        menuAnalysisCountDiagonale.setOnAction( x -> {
            MessageDlg messageDlg = new MessageDlg(2, "Anzahl Diagonalen", "Die Anzahl an Diagonalen in diesem Spielfeld " +
                    "beträgt "+countObjects(5)+".");
            messageDlg.showAndWait();
        });
        menuAnalysisMarkAll.setOnAction( x -> {
            Vector<Integer> counter = new Vector<>();
            counter.add(0); //Rechteck
            counter.add(0); //Fadenkreuz
            counter.add(0); //Hohes Fadenkreuz
            counter.add(0); //Pfeil
            counter.add(0); //Doppeltes Fadenkreuz
            counter.add(0); //Diagonale
            //Vector mit bereits überprüften Zellen
            Vector<Vector<Integer>> checkes = new Vector<>();
            for (int i=0; i<cellContainers.size(); i++){
                for (int j=0; j<cellContainers.get(i).size(); j++){
                    CellContainer cc = cellContainers.get(i).get(j);
                    boolean checked = false;
                    for (Vector<Integer> check:checkes){
                        if (check.get(0) == i && check.get(1) == j){
                            checked = true;
                        }
                    }
                    if (!checked && cc.getStatusProperty() == 1){
                        //TODO
                        //Zelle kann nur Teil eines Objektes sein
                        Vector<Vector<Integer>> erg = GameAnalysis.isPieceOfStaticObject(cc, game, counter);
                        for (Vector<Integer> obj:erg){
                            cellContainers.get(obj.get(0)).get(obj.get(1)).setFill(Color.GREEN);
                            //System.out.println(obj.get(0)+":"+obj.get(1));
                            checkes.add(obj);
                        }
                    }
                }
            }
        });
        menuAnalysisMarkRect.setOnAction( x -> markObjects(0));
        menuAnalysisMarkCrosshair.setOnAction( x -> markObjects(1));
        menuAnalysisMarkStretchedCrosshair.setOnAction( x -> markObjects(2));
        menuAnalysisMarkArrow.setOnAction( x -> markObjects(3));
        menuAnalysisMarkDoubledCrosshair.setOnAction( x -> markObjects(4));
        menuAnalysisMarkDiagonale.setOnAction( x -> markObjects(5));
        menuOptionsSettings.setOnAction( x -> {
            //SettingsDlg aufrufen
            SettingsDlg settingsDlg = new SettingsDlg();
            settingsDlg.showAndWait();
            boolean graphicChanged = settingsDlg.getResult();
            //Wenn Grafikveränderungen gemacht wurden, dann Spielfeld aktualisieren
            if (graphicChanged) {
                initGame();
            }
        });
        menuHelpAbout.setOnAction( x -> {
            AboutDlg aboutDlg = new AboutDlg();
            aboutDlg.showAndWait();
        });

        generation = new AnimationTimer() {
            //Timer der anhand der BooleanProperty generationRunning kontrolliert wird.
            //Ruft Generationsberechnung im eingestellten Intervall auf
            private long tmp;
            private double intervall = 1000000000; //entspricht einer Sekunde
            @Override
            public void handle(long now) {
                intervall = SettingsController.getInstance().getGenerationIntervall()*1000000000;
                windowParent.requestFocus();
                if (now-tmp>intervall){
                    tmp = now;
                    applyNextGeneration();
                }
            }
        };

        //Schatteneffekt für die Icons der Kontrollelemente
        DropShadow ivShadow = new DropShadow(BlurType.GAUSSIAN, Color.WHITE, 10, 0, 0, 0);
        ivSwitch.setImage(ImageController.getInstance().getImgPlay());
        ivSwitch.setOnMouseEntered( x -> ivSwitch.setEffect(ivShadow));
        ivSwitch.setOnMouseExited( x -> ivSwitch.setEffect(null));
        ivSwitch.setOnMouseClicked( x -> {
            //Generationsberechnung steuern
            switchGame();
            brushingProperty.setValue(false);
            erasingProperty.setValue(false);
        });
        ivFullscreen.setImage(ImageController.getInstance().getImgFullscreen());
        ivFullscreen.setOnMouseEntered( x -> ivFullscreen.setEffect(ivShadow));
        ivFullscreen.setOnMouseExited( x -> ivFullscreen.setEffect(null));
        ivFullscreen.setOnMouseClicked( x -> fullscreenProperty.set(!fullscreenProperty.get()));

        ivBrush.setImage(ImageController.getInstance().getImgBrush());
        ivBrush.setOnMouseEntered( x -> {
            if (!brushingProperty.getValue()) {
                ivBrush.setEffect(ivShadow);
            }
        });
        ivBrush.setOnMouseExited( x -> {
            if (!brushingProperty.getValue()) {
                ivBrush.setEffect(null);
            }
        });
        ivBrush.setOnMouseClicked( x -> brushingProperty.set(!brushingProperty.getValue()));
        brushingProperty.addListener( (x, y, z) -> {
            if (z){
                int result;
                //result == 2 bedeutet, der Dialog für ein neues Spiel kann aufgerufen werden
                if (game.getGenerations().size() == 1){
                    result = 1;
                }
                else {
                    MessageDlg messageDlg = new MessageDlg(3, "Fatale Veränderung!", "Wenn Sie mit der Beleben-Funktion fortsetzen" +
                            " möchten, werden alle Generationen außer der jetzigen gelöscht!\nMöchten Sie fortfahren?");
                    messageDlg.showAndWait();
                    result = messageDlg.getResult();
                }
                if (result==1) {
                    ivBrush.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.WHITE, 50, 0, 0 ,0));
                    gamePane.setCursor(Cursor.CROSSHAIR);
                    erasingProperty.setValue(false);
                    //Wenn gemalt werden soll, dann alle Generationen außer der jetzigen löschen
                    Vector<Vector<Cell>> generationCopy = new Vector<>();
                    //Aktuelle Generation kopieren und als letzte Generation einsetzen
                    for (int i = 0; i < game.getIndexedGeneration().size(); i++) {
                        Vector<Cell> generationLine = new Vector<>();
                        for (int j = 0; j < game.getIndexedGeneration().get(i).size(); j++) {
                            generationLine.add(new Cell(game.getIndexedGeneration().get(i).get(j).getStatus()));
                        }
                        generationCopy.add(generationLine);
                    }
                    game.getGenerations().clear();
                    game.getGenerations().add(generationCopy);
                    game.setIndex(0);
                    initGame();
                }
                else{
                    ivBrush.setEffect(null);
                    brushingProperty.setValue(false);
                }
            }
            else{
                ivBrush.setEffect(null);
                gamePane.setCursor(Cursor.DEFAULT);
            }
        });

        ivEraser.setImage(ImageController.getInstance().getImgEraser());
        ivEraser.setOnMouseEntered( x -> {
            if (!erasingProperty.getValue()) {
                ivEraser.setEffect(ivShadow);
            }
        });
        ivEraser.setOnMouseExited( x -> {
            if (!erasingProperty.getValue()) {
                ivEraser.setEffect(null);
            }
        });
        ivEraser.setOnMouseClicked(x -> erasingProperty.setValue(!erasingProperty.getValue()));
        erasingProperty.addListener( (x, y, z) -> {
            if (z){
                int result;
                if (game.getGenerations().size() == 1){
                    result = 1;
                }
                else {
                    MessageDlg messageDlg = new MessageDlg(3, "Fatale Veränderung!", "Wenn Sie mit der Beleben-Funktion fortsetzen" +
                            " möchten, werden alle Generationen außer der jetzigen gelöscht!\nMöchten Sie fortfahren?");
                    messageDlg.showAndWait();
                    result = messageDlg.getResult();
                }
                if (result==1) {
                    brushingProperty.setValue(false);
                    ivEraser.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.WHITE, 50, 0, 0 ,0));
                    gamePane.setCursor(Cursor.CROSSHAIR);
                    //Wenn gelöscht werden soll, dann alle Generationen außer der jetzigen löschen
                    Vector<Vector<Cell>> generationCopy = new Vector<>();
                    //Aktuelle Generation kopieren und als letzte Generation einsetzen
                    for (int i = 0; i < game.getIndexedGeneration().size(); i++) {
                        Vector<Cell> generationLine = new Vector<>();
                        for (int j = 0; j < game.getIndexedGeneration().get(i).size(); j++) {
                            generationLine.add(new Cell(game.getIndexedGeneration().get(i).get(j).getStatus()));
                        }
                        generationCopy.add(generationLine);
                    }
                    game.getGenerations().clear();
                    game.getGenerations().add(generationCopy);
                    game.setIndex(0);
                    initGame();
                }
                else{
                    ivEraser.setEffect(null);
                    erasingProperty.setValue(false);
                }
            }
            else{
                ivEraser.setEffect(null);
                gamePane.setCursor(Cursor.DEFAULT);
            }
        });

        ivSkip.setImage(ImageController.getInstance().getImgSkip());
        ivSkip.setOnMouseEntered( x -> ivSkip.setEffect(ivShadow));
        ivSkip.setOnMouseExited( x -> ivSkip.setEffect(null));
        ivSkip.setOnMouseClicked( x -> skip());
        ivSkipBack.setImage(ImageController.getInstance().getImgSkipBack());
        ivSkipBack.setOnMouseEntered( x -> ivSkipBack.setEffect(ivShadow));
        ivSkipBack.setOnMouseExited( x -> ivSkipBack.setEffect(null));
        ivSkipBack.setOnMouseClicked( x -> skipBack());

        generationRunning.addListener( (x, y, z) -> {
            //Gibt Userfeedback ob Generationsberechnung läuft
            piGeneration.setVisible(z);
            if (z){
                lblSwitch.setText("Stop (F9)");
            }
            else{
                lblSwitch.setText("Start (F9)");
            }
        });

        windowParent.setOnKeyPressed( x -> {
            //Tastenbelegungen
            switch (x.getCode()){
                case B:         brushingProperty.setValue(!brushingProperty.getValue()); break;
                case T:         erasingProperty.setValue(!erasingProperty.getValue()); break;
                case ESCAPE:    fullscreenProperty.set(false); break;
                case F6:        skipBack(); break;
                case F7:        skip(); break;
                case F9:        switchGame(); break;
                case F10:       cbLabels.setSelected(!cbLabels.isSelected()); break;
                case F11:       fullscreenProperty.set(!fullscreenProperty.get()); break;
                case F12:       cbBorders.setSelected(!cbBorders.isSelected()); break;
                case Q:
                    SnapshotParameters sn = new SnapshotParameters();
                    sn.setFill(Color.TRANSPARENT);
                    double width = Screen.getPrimary().getBounds().getWidth()*0.85;
                    double height = Screen.getPrimary().getBounds().getHeight()*0.7;
                    FileController.getInstance().writeImgTo(windowParent.snapshot(sn,
                            new WritableImage((int)width, (int)height)));
                    break;
            }
            windowParent.requestFocus();
        });

        fullscreenProperty.addListener( (x, y, z) -> {
            piGeneration.setVisible(true);
            disableControls(2);
            if (z){
                //Vollbild schalten
                Main.MAINSTAGE.setFullScreen(true);
                double scale = Screen.getPrimary().getBounds().getHeight()/gamePane.heightProperty().get();
                double trans = (gamePane.getHeight()*(scale-1))/2;
                //Spielfeld skalieren, um es aus Containern zu lösen
                gamePane.setScaleY(scale);
                gamePane.setTranslateY(-trans);
                //Feedback, wie Vollbildmodus verlassen werden kann
                MessageDlg messageDlg = new MessageDlg(4, "Vollbildmodus", "Sie befinden sich im Vollbildmodus!\n" +
                        "Zum Verlassen bitte F11 drücken.");
                messageDlg.showAndWait();
            }
            else{
                //Vollbild abschalten und Skalierung zurückstellen
                Main.MAINSTAGE.setFullScreen(false);
                gamePane.setScaleY(1);
                gamePane.setTranslateY(0);
            }
            piGeneration.setVisible(false);
            disableControls(0);
        });

        gamePane.widthProperty().addListener( (x, y, z) -> {
            //Zellenweite abhängig von Spielfeldweite
            cellWidthProperty.set(z.doubleValue()/game.getIndexedGeneration().size());
        });
        gamePane.heightProperty().addListener( (x, y, z) -> {
            //Zellenhähe abhängig von Spielfeldhöhe
            cellHeightProperty.set(z.doubleValue()/game.getIndexedGeneration().size());
        });

        cbBorders.selectedProperty().addListener( (x, y, z) -> {
            piGeneration.setVisible(true);
            disableControls(2);
            int width = 0;
            if (z){
                width = 1;
            }
            menuOptionsBorders.setSelected(z);
            for (Vector<CellContainer> line : cellContainers ){
                for (CellContainer cc:line){
                    cc.setStrokeWidth(width);
                }
            }
            piGeneration.setVisible(false);
            disableControls(0);
        });
        cbLabels.selectedProperty().addListener( (x, y, z) -> {
            menuOptionsLabels.setSelected(z);
            initLabels();
        });
        //Checkboxes im Menu mit Checkboxes in der Toolbar 'verbinden'
        menuOptionsBorders.selectedProperty().addListener( (x, y, z) -> cbBorders.setSelected(z));
        menuOptionsLabels.selectedProperty().addListener( (x, y, z) -> cbLabels.setSelected(z));
        //Eventhandlers initialisiert

        //Spielfeld initialisieren
        initGame();
    }

    private void switchGame(){
        if (!generationRunning.getValue()){
            startGame();
        }
        else{
            stopGame();
        }
    }

    private void startGame(){
        //Generationsberechnung starten
        generation.start();
        //Visuelles Feedback
        generationRunning.set(true);
        ivSwitch.setImage(ImageController.getInstance().getImgPause());
        disableControls(1);
    }

    private void stopGame(){
        //Generationsberechnung stoppen
        generation.stop();
        //Visuelles Feedback
        generationRunning.set(false);
        ivSwitch.setImage(ImageController.getInstance().getImgPlay());
        disableControls(0);
    }

    private void applyNextGeneration(){
        Vector<Vector<Integer>> changes = GameCalc.cycle(game);
        if (changes.size()==0){
            //Bei keiner Veränderung Generationsberechnung automatisch stoppen.
            stopGame();
            initLabels();
        }
        else {
            if (game.getIndex()==game.getGenerations().size()-1) {
                //Wenn aktuelle Generation die letzte berechnete Generation ist
                Vector<Vector<Cell>> generationCopy = new Vector<>();
                //Aktuelle Generation kopieren und als letzte Generation einsetzen
                for (int i = 0; i < game.getIndexedGeneration().size(); i++) {
                    Vector<Cell> generationLine = new Vector<>();
                    for (int j = 0; j < game.getIndexedGeneration().get(i).size(); j++) {
                        generationLine.add(new Cell(game.getIndexedGeneration().get(i).get(j).getStatus()));
                    }
                    generationCopy.add(generationLine);
                }
                game.getGenerations().add(generationCopy);
                game.increaseIndex();
                //Veränderungen auf die eben kopierte und letzte Generation anwenden
                for (Vector<Integer> change : changes) {
                    CellContainer cc = cellContainers.get(change.get(1)).get(change.get(2));
                    cc.setCell(game.getIndexedGeneration().get(change.get(1)).get(change.get(2)));
                    cc.setStatusProperty(change.get(0));
                }
                initLabels();
            }
            else{
                //Wenn aktuelle Generation nicht die letzte berechnete Generation, dann einfach die nächste berechnete laden
                game.increaseIndex();
                initGame();
            }
            lblGeneration.setText("Generation: " + (game.getIndex() + 1) + "/" + game.getGenerations().size());
        }
        refillCellContainers();
    }

    private void skip(){
        //Nächste Generation laden / neu berechnen
        if (game.getIndex() == game.getGenerations().size()-1) {
            applyNextGeneration();
        }
        else{
            game.increaseIndex();
            initGame();
        }
        brushingProperty.setValue(false);
        erasingProperty.setValue(false);
        refillCellContainers();
    }

    private void skipBack(){
        if(game.getIndex()>0) {
            game.decreaseIndex();
            initGame();
        }
        brushingProperty.setValue(false);
        erasingProperty.setValue(false);
        refillCellContainers();
    }

    private void refillCellContainers(){
        for (Vector<CellContainer> cellcs:cellContainers){
            for (CellContainer cc:cellcs){
                cc.refill();
            }
        }
    }

    private void disableControls(int mode){
        //Kontrollelemente aktivieren/deaktivieren und Opazität setzen
        //mode 0: alle aktiviert; 1: alle deaktiviert außer ivSwitch; 2: alle deaktiviert
        boolean state = false;
        double opacity = 1.0;
        if (mode==0){
            ivSwitch.setDisable(false);
            vbSwitch.setOpacity(1.0);
        }
        if (mode==1){
            state = true;
            ivSwitch.setDisable(false);
            vbSwitch.setOpacity(1.0);
        }
        else if (mode == 2) {
            state = true;
            ivSwitch.setDisable(true);
            vbSwitch.setOpacity(0.5);
        }
        ivFullscreen.setDisable(state);
        cbLabels.setDisable(state);
        cbBorders.setDisable(state);
        ivBrush.setDisable(state);
        ivEraser.setDisable(state);
        ivSkip.setDisable(state);
        ivSkipBack.setDisable(state);
        mainMb.setDisable(state);
        if (state){
            opacity = 0.5;
        }
        vbFullscreen.setOpacity(opacity);
        vbBrush.setOpacity(opacity);
        vbEraser.setOpacity(opacity);
        vbSkipBack.setOpacity(opacity);
        vbSkip.setOpacity(opacity);
    }

    private void initGame(){
        //Spielfeld initialisieren

        //Visuelles Feedback
        piGeneration.setVisible(true);
        gamePane.getChildren().clear();
        cellContainers.clear();
        lblGeneration.setText("Generation: "+(game.getIndex()+1)+"/"+game.getGenerations().size());
        //Zellengröße nochmals berechnen
        cellWidthProperty.set(gamePane.widthProperty().getValue()/game.getIndexedGeneration().size());
        cellHeightProperty.set(gamePane.heightProperty().getValue()/game.getIndexedGeneration().size());
        Task<Vector<Vector<CellContainer>>> process = new Task<Vector<Vector<CellContainer>>>() {
            @Override
            protected Vector<Vector<CellContainer>> call() throws Exception {
                //Temporär Liste von Zellencontainer in Nebenthread aufbauen und später im main-thread übernehmen
                Vector<Vector<CellContainer>> tmpContainers = new Vector<>();
                //Für alle Zellen der aktuellen Generation
                for (int i = 0; i<game.getIndexedGeneration().size(); i++){
                    Vector<CellContainer> containerLine = new Vector<>();
                    for (int j = 0; j<game.getIndexedGeneration().get(i).size(); j++){
                        CellContainer cc = new CellContainer(game.getIndexedGeneration().get(i).get(j), i, j);
                        cc.widthProperty().bind(cellWidthProperty);
                        cc.heightProperty().bind(cellHeightProperty);
                        cc.layoutXProperty().bind(cellWidthProperty.multiply(cc.getIndX()));
                        cc.layoutYProperty().bind(cellHeightProperty.multiply(cc.getIndY()));
                        cc.setStroke(Color.BLACK);
                        if (cbBorders.isSelected()) {
                            cc.setStrokeWidth(1);
                        }
                        cc.statusProperty().addListener( (x, y, z) -> cc.refill());
                        cc.setOnMouseEntered( x -> {
                            if (brushingProperty.getValue()) {
                                cc.setFill(Color.BLUE);
                            }
                            else if (erasingProperty.getValue()){
                                cc.setFill(Color.WHITE);
                            }
                        });
                        cc.setOnMouseExited( x -> cc.refill());
                        cc.setOnMouseClicked( x -> {
                            if (brushingProperty.getValue()) {
                                cc.setStatusProperty(1);
                                initLabels();
                                cc.refill();
                            }
                            else if (erasingProperty.getValue()) {
                                cc.setStatusProperty(0);
                                initLabels();
                                cc.refill();
                            }
                        });
                        cc.setOnMouseDragOver( x -> {
                            if (brushingProperty.getValue()){
                                cc.setStatusProperty(1);
                                initLabels();
                            }
                            else if (erasingProperty.getValue()){
                                cc.setStatusProperty(0);
                                initLabels();
                            }
                            cc.refill();
                        });
                        Label lblStatus = new Label();
                        lblStatus.setMouseTransparent(true);
                        lblStatus.layoutXProperty().bind(cc.layoutXProperty());
                        lblStatus.layoutYProperty().bind(cc.layoutYProperty());
                        lblStatus.prefWidthProperty().bind(cc.widthProperty());
                        lblStatus.prefHeightProperty().bind(cc.heightProperty());
                        lblStatus.setAlignment(Pos.CENTER);
                        lblStatus.visibleProperty().bind(cbLabels.selectedProperty());
                        lblStatus.prefHeightProperty().addListener( (x, y, z) -> lblStatus.setFont(Font.font(z.doubleValue()/1.5)));
                        lblStatus.setFont(Font.font(lblStatus.getPrefHeight()/1.5));
                        cc.setLblStatus(lblStatus);
                        cc.getLblStatus().setText(GameCalc.getNeighbourAliveCount(i, j, game)+"");
                        containerLine.add(cc);
                    }
                    tmpContainers.add(containerLine);
                }
                return tmpContainers;
            }
        };
        process.setOnSucceeded( x -> {
            gamePane.getChildren().clear();
            cellContainers.clear();
            //Temporäre Liste übernehmen -> Last nun im main-thread
            for (Vector<CellContainer> line:process.getValue()){
                for (CellContainer cc:line){
                    gamePane.getChildren().add(cc);
                    gamePane.getChildren().add(cc.getLblStatus());
                }
                cellContainers.add(line);
            }
            //Visuelles Feedback
            if (!generationRunning.getValue()) {
                piGeneration.setVisible(false);
            }
        });
        //Nebenthread für oben definierten Task
        Thread t1 = new Thread(process);
        t1.setDaemon(true);
        t1.start();
    }

    private void initLabels(){
        //Labels der Zellcontainer initialisieren (Status als Text setzen)
        for (int i = 0; i < game.getIndexedGeneration().size(); i++) {
            for (int j = 0; j < game.getIndexedGeneration().get(i).size(); j++) {
                cellContainers.get(i).get(j).getLblStatus().setText(GameCalc.getNeighbourAliveCount(i, j, game)+"");
            }
        }
    }

    public BooleanProperty fullscreenProperty() {
        return fullscreenProperty;
    }

    private void markObjects(int mode){
        //Vector mit bereits überprüften Zellen
        Vector<Vector<Integer>> checkes = new Vector<>();
        for (int i=0; i<cellContainers.size(); i++){
            for (int j=0; j<cellContainers.get(i).size(); j++){
                CellContainer cc = cellContainers.get(i).get(j);
                boolean checked = false;
                for (Vector<Integer> check:checkes){
                    if (check.get(0) == i && check.get(1) == j){
                        checked = true;
                    }
                }
                if (!checked && cc.getStatusProperty() == 1){
                    //TODO
                    //Zelle kann nur Teil eines Objektes sein
                    Vector<Vector<Integer>> erg = new Vector<>();
                    switch (mode){
                        case 0: erg = GameAnalysis.isPieceOfRectangle(cc, game); break;
                        case 1: erg = GameAnalysis.isPieceOfCrosshair(cc, game); break;
                        case 2: erg = GameAnalysis.isPieceOfStretchedCrosshair(cc, game); break;
                        case 3: erg = GameAnalysis.isPieceOfArrow(cc, game); break;
                        case 4: erg = GameAnalysis.isPieceOfDoubledCrosshair(cc, game); break;
                        case 5: erg = GameAnalysis.isPieceOfDiagonal(cc, game); break;
                    }
                    for (Vector<Integer> obj:erg){
                        cellContainers.get(obj.get(0)).get(obj.get(1)).setFill(Color.GREEN);
                        //System.out.println(obj.get(0)+":"+obj.get(1));
                        checkes.add(obj);
                    }
                }
            }
        }
    }

    private int countObjects(int mode){
        int count = 0;
        //Vector mit bereits überprüften Zellen
        Vector<Vector<Integer>> checkes = new Vector<>();
        for (int i=0; i<cellContainers.size(); i++){
            for (int j=0; j<cellContainers.get(i).size(); j++){
                CellContainer cc = cellContainers.get(i).get(j);
                boolean checked = false;
                for (Vector<Integer> check:checkes){
                    if (check.get(0) == i && check.get(1) == j){
                        checked = true;
                    }
                }
                if (!checked && cc.getStatusProperty() == 1){
                    //TODO
                    //Zelle kann nur Teil eines Objektes sein
                    Vector<Vector<Integer>> erg = new Vector<>();
                    switch (mode){
                        case 0: erg = GameAnalysis.isPieceOfRectangle(cc, game); break;
                        case 1: erg = GameAnalysis.isPieceOfCrosshair(cc, game); break;
                        case 2: erg = GameAnalysis.isPieceOfStretchedCrosshair(cc, game); break;
                        case 3: erg = GameAnalysis.isPieceOfArrow(cc, game); break;
                        case 4: erg = GameAnalysis.isPieceOfDoubledCrosshair(cc, game); break;
                        case 5: erg = GameAnalysis.isPieceOfDiagonal(cc, game); break;
                    }
                    for (Vector<Integer> obj:erg){
                        checkes.add(obj);
                    }
                    if (erg.size() > 0){
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
