package com.fbieck.game;

import com.fbieck.util.SettingsController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CellContainer extends Rectangle {

    //Container von Zellen

    //Zelle merken
    private Cell cell;
    //Indizes im Vector der Generation merken
    private final int indX, indY;
    //Status der Zelle in eine Property laden, um Veränderungen zu vereinfachen
    private final IntegerProperty statusProperty = new SimpleIntegerProperty();
    //Label zur Anzeige des Status
    private Label lblStatus = new Label();

    public CellContainer(Cell cell, int indX, int indY) {
        this.cell = cell;
        this.indX = indX;
        this.indY = indY;
        setStatusProperty(cell.getStatus());
        setFill(getCellColorByStatus(cell.getStatus()));
    }

    public int getIndX() {
        return indX;
    }

    public int getIndY() {
        return indY;
    }

    public void setCell(Cell cell) {

        this.cell = cell;
        refill();
    }

    public int getStatusProperty() {
        return statusProperty.get();
    }

    public IntegerProperty statusProperty() {
        return statusProperty;
    }

    public void setStatusProperty(int statusProperty) {
        this.statusProperty.set(statusProperty);
        //Statusveränderung des Containers auch auf Zelle anwenden
        cell.setStatus(statusProperty);
    }

    private Color getCellColorByStatus(int status){
        //Zellenfarbe bestimmen
        switch (status){
            case 0: return SettingsController.getInstance().getClDead();
            case 1: return SettingsController.getInstance().getClAlive();
            default: return Color.BLACK;
        }
    }

    public void refill(){
        setFill(getCellColorByStatus(getStatusProperty()));
    }

    public Label getLblStatus() {
        return lblStatus;
    }

    public void setLblStatus(Label lblStatus) {
        this.lblStatus = lblStatus;
    }
}
