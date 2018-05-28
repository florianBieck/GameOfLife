package com.fbieck.util;

import com.fbieck.game.Cell;
import com.fbieck.game.Game;

import java.util.Vector;

public class GameCalc {

    //Berechnungen wie Generationsberechnung, Status einer Zelle in der nächsten Generation, Anzahl lebender Nachbarn und Index in Grenzen.

    private GameCalc() {
    }

    public static Vector<Vector<Integer>> cycle(Game game){
        //Generationsberechnung
        Vector<Vector<Integer>> changes = new Vector<>();
        //Change Indizes: 0: status, 1: indexX, 2: indexY
        //Nur wirkliche Veränderungen bearbeiten und nicht alle Zellen neu laden
        for (int i=0; i<game.getIndexedGeneration().size(); i++){
            for (int j=0; j<game.getIndexedGeneration().get(i).size(); j++){
                Cell c = game.getIndexedGeneration().get(i).get(j);
                int status = c.getStatus();
                int newStatus = getNewStatus(c, i, j, game);
                //Wenn der aktuelle Status != dem neuen Status, dann liegt eine Veränderung vor
                if (status != newStatus) {
                    Vector<Integer> change = new Vector<>();
                    change.add(newStatus);
                    change.add(i);
                    change.add(j);
                    changes.add(change);
                }
            }
        }
        return changes;
    }

    private static Integer getNewStatus(Cell c, int i, int j, Game game) {

        //Abhängig von den verschiedenen, eingeschalteten Regeln und lebendigen Nachbarn neuen Status vergeben
        int status = c.getStatus();
        int neighbours = getNeighbourAliveCount(i, j, game);
        if (c.getStatus()==0){
            if ((neighbours == 1 || neighbours == 3 || neighbours == 5 || neighbours == 7)
                    && SettingsController.getInstance().getRuleset().get(4)){
                //Geburtsregel
                status = 1;
            }
            if (neighbours == 3
                    && SettingsController.getInstance().getRuleset().get(0)){
                //Wiedergeburt
                status = 1;
            }
        }
        else {
            if ((neighbours == 0 || neighbours == 2 || neighbours == 4 || neighbours == 6 || neighbours == 8)
                    && SettingsController.getInstance().getRuleset().get(3)){
                //Todes-Regel
                status = 0;
            }
            else if (neighbours < 2
                    && SettingsController.getInstance().getRuleset().get(1)){
                //Einsamkeit
                status = 0;
            }
            else if (neighbours > 3
                    && SettingsController.getInstance().getRuleset().get(2)){
                //Überbevölkerung
                status = 0;
            }
        }
        return status;
    }

    public static int getNeighbourAliveCount(int posX, int posY, Game game){
        //Berechnen wie viele lebendige Nachbarn die Zelle an (posX/posY) hat
        int alive = 0;
        //Letzter index
        /*
            x-1/y-1     x/y-1   x+1/y-1     |   0   1   0
            x-1/y       x/y     x+1/y       |   0   0   0   => alive = 3
            x-1/y+1     x/y+1   x+1/y+1     |   1   1   0
         */
        for (int x=posX-1; x<=posX+1; x++){
            for (int y=posY-1; y<=posY+1; y++){
                //Falls Indizes außerhalb der Grenzen, dann anpassen
                int tmpX = getInBorders(x, game), tmpY = getInBorders(y, game);

                if ( game.getIndexedGeneration().get(tmpX).get(tmpY).getStatus() == 1 &&
                        !(tmpX == posX && tmpY == posY)){
                    //Falls Status der Zelle lebendig ist und die Zelle nicht die übergebene Zelle ist, dann alive++
                    alive++;
                }
            }
        }
        return alive;
    }

    public static int getInBorders(int index, Game game){
        //Index muss in Spielfeldgrenzen liegen. Bewegt sich der Pointer aus dem Spielfeld, erscheint er auf der anderen Seite.
        int max = game.getIndexedGeneration().size()-1;
        int tmp = index;
        if ( tmp < 0 ){
            tmp = max+tmp+1;
        }
        else if ( tmp > max ){
            tmp = tmp-max-1;
        }
        return tmp;
    }
}
