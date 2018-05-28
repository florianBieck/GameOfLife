package com.fbieck.util;

import javafx.scene.paint.Color;

import java.util.Vector;

public class SettingsController {

    //Singleton Controller zur Kontrolle der Einstellungen

    private static SettingsController instance;

    //Zellenfarben tot und lebendig
    private Color clAlive = Color.rgb(73, 143, 204), clDead = Color.WHITE;
    //Ruleset -> Liste an booleans. Auswirkung der Regel im Quellcode von MainController
    private final Vector<Boolean> ruleset = new Vector<>();
    //Intervall der Generationsberechnung
    private Double generationIntervall = 1.0;

    private SettingsController() {
        //TODO INIT
        initRuleset();
    }

    private void initRuleset(){
        //Standard Ruleset
        ruleset.clear();
        ruleset.add(true);
        ruleset.add(true);
        ruleset.add(true);
        ruleset.add(false);
        ruleset.add(false);
    }

    public static SettingsController getInstance() {
        if (instance == null){
            instance = new SettingsController();
        }
        return instance;
    }

    public Color getClAlive() {
        return clAlive;
    }

    public void setClAlive(Color clAlive) {
        this.clAlive = clAlive;
    }

    public Color getClDead() {
        return clDead;
    }

    public void setClDead(Color clDead) {
        this.clDead = clDead;
    }

    public Vector<Boolean> getRuleset() {
        return ruleset;
    }

    public Double getGenerationIntervall() {
        return generationIntervall;
    }

    public void setGenerationIntervall(Double generationIntervall) {
        this.generationIntervall = generationIntervall;
    }
}
