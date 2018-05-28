package com.fbieck.dialogs.settingsdlg;

import com.fbieck.main.Main;
import com.fbieck.util.SettingsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.IOException;

public class SettingsDlg extends Dialog<Boolean> {

    //Dialog zur Bearbeitung der Einstellungen. Rückgabewert true, wenn Grafikeinstellungen geändert wurden

    @FXML
    private DialogPane dialogPane;
    @FXML
    private BorderPane windowParent;
    @FXML
    private Label lblTitle;
    @FXML
    private ScrollPane spGameSettings, spGraphicsSettings;
    @FXML
    private ColorPicker cpAlive, cpDead;
    @FXML
    private CheckBox cbRule1, cbRule2, cbRule3, cbRule4, cbRule5, cbRule6;
    @FXML
    private Spinner<Double> spinIntervall;
    @FXML
    private Button btnSettingsRules, btnSettingsGame,
            btnClChange,
            btnSave, btnCancel;

    //Wahl der Spiel - oder Grafikeinstellungen
    private int view = 0;

    public SettingsDlg() {

        initOwner(Main.MAINSTAGE);
        initStyle(StageStyle.DECORATED);
        initModality(Modality.WINDOW_MODAL);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/settingsdlg.fxml"));
        loader.setController(this);
        try {
            loader.load();
            setDialogPane(dialogPane);

            getDialogPane().getScene().getWindow().setOnCloseRequest( x -> realClose());
            setTitle("Einstellungen");

            //Farbe von lebenden Zellen. Darf nicht gleich Farbe von toten Zellen sein.
            cpAlive.setValue(SettingsController.getInstance().getClAlive());
            cpAlive.valueProperty().addListener( (x, y, z) -> {
                if (z.equals(cpDead.getValue())){
                    cpDead.setValue(y);
                }
            });
            //Farbe von toten Zellen. Darf nicht gleich Farbe von lebenden Zellen sein.
            cpDead.setValue(SettingsController.getInstance().getClDead());
            cpDead.valueProperty().addListener( (x, y, z) -> {
                if (z.equals(cpAlive.getValue())){
                    cpAlive.setValue(y);
                }
            });
            //Schnelltausch der Farben von lebenden und toten Zellen
            btnClChange.setOnAction( x -> {
                Color cl = cpAlive.getValue();
                cpAlive.setValue(cpDead.getValue());
                cpDead.setValue(cl);
            });

            //Boolische Konfiguration der Regeln
            cbRule1.setSelected(SettingsController.getInstance().getRuleset().get(0));
            cbRule2.setSelected(SettingsController.getInstance().getRuleset().get(1));
            cbRule3.setSelected(SettingsController.getInstance().getRuleset().get(2));
            cbRule4.setSelected(SettingsController.getInstance().getRuleset().get(3));
            cbRule5.setSelected(SettingsController.getInstance().getRuleset().get(4));

            //Intervall der Generationsberechnung in Sekunden
            spinIntervall.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100.0, SettingsController.getInstance().getGenerationIntervall()));

            btnSettingsRules.setOnAction( x -> changeView(0));
            btnSettingsGame.setOnAction( x -> changeView(1));

            btnSave.setOnAction( x -> {
                //Einstellungen in SettingsController übernehmen

                //Prüfen ob Grafikeinstellungen geändert wurden
                boolean changed = false;
                if (!cpAlive.getValue().equals(SettingsController.getInstance().getClAlive())) {
                    SettingsController.getInstance().setClAlive(cpAlive.getValue());
                    changed = true;
                }
                if (!cpDead.getValue().equals(SettingsController.getInstance().getClDead())) {
                    SettingsController.getInstance().setClDead(cpDead.getValue());
                    changed = true;
                }

                SettingsController.getInstance().getRuleset().set(0, cbRule1.isSelected());
                SettingsController.getInstance().getRuleset().set(1, cbRule2.isSelected());
                SettingsController.getInstance().getRuleset().set(2, cbRule3.isSelected());
                SettingsController.getInstance().getRuleset().set(3, cbRule4.isSelected());
                SettingsController.getInstance().getRuleset().set(4, cbRule5.isSelected());

                SettingsController.getInstance().setGenerationIntervall(spinIntervall.getValue());

                setResult(changed);
            });
            btnCancel.setOnAction( x -> realClose());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeView(int toView){
        //Tausch zwischen Ansichten der Spiel- und Grafikeinstellungen
        if (view != toView){
            btnSettingsGame.setId("");
            btnSettingsRules.setId("");
            spGameSettings.setVisible(false);
            spGraphicsSettings.setVisible(false);
            switch (toView){
                case 0: loadGameSettings(); break;
                case 1: loadGraphicsSettings(); break;
            }
        }
    }

    //Laden der Ansicht der Spieleinstellungen
    private void loadGameSettings(){
        btnSettingsRules.setId("activebutton");
        spGameSettings.setVisible(true);
        view = 0;
    }

    //Laden der Ansicht der Grafikeinstellungen
    private void loadGraphicsSettings(){
        btnSettingsGame.setId("activebutton");
        spGraphicsSettings.setVisible(true);
        view = 1;
    }

    private void realClose(){
        setResult(false);
    }
}
