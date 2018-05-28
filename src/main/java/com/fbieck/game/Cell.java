package com.fbieck.game;

import java.io.Serializable;

public class Cell implements Serializable{

    //Einfache Darstellung einer Zelle mit einem Status zur Serialisierung.

    //Status 0: tot, Status 1: lebendig
    private int status = 0;

    public Cell(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
