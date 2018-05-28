package com.fbieck.util;

import com.fbieck.game.Cell;
import com.fbieck.game.CellContainer;
import com.fbieck.game.Game;

import java.util.Vector;

public class GameAnalysis {

    /*
        Analysemethoden, um das Spielfeld nach statischen Objekten zu durchsuchen.
        Objekte stammen von der Wikipedia-Seite zu Convays Game of life:
        https://de.wikipedia.org/wiki/Conways_Spiel_des_Lebens
    */

    private GameAnalysis() {
    }

    public static Vector<Vector<Integer>> isPieceOfStaticObject(CellContainer cc, Game game, Vector<Integer> counter){
        Vector<Vector<Integer>> erg = isPieceOfRectangle(cc, game);
        if (erg.size() > 0){
            //Rechteck
            counter.set(0, counter.get(0)+1);
        }
        else{
            erg = isPieceOfCrosshair(cc, game);
            if (erg.size() > 0){
                //Fadenkreuz
                counter.set(1, counter.get(1)+1);
            }
            else{
                erg = isPieceOfStretchedCrosshair(cc, game);
                if (erg.size() > 0){
                    //Hohes Fadenkreuz
                    counter.set(2, counter.get(2)+1);
                }
                else{
                    erg = isPieceOfArrow(cc, game);
                    if (erg.size() > 0){
                        //Pfeil
                        counter.set(3, counter.get(3)+1);
                    }
                    else{
                        erg = isPieceOfDoubledCrosshair(cc, game);
                        if ( erg.size() > 0){
                            //Doppeltes Fadenkreuz
                            counter.set(4, counter.get(4)+1);
                        }
                        else{
                            erg = isPieceOfDiagonal(cc, game);
                            if (erg.size() > 0){
                                //Diagonale
                                counter.set(5, counter.get(5)+1);
                            }
                        }
                    }
                }
            }
        }
        return erg;
    }

    public static Vector<Vector<Integer>> isPieceOfRectangle(CellContainer cc, Game game){
        boolean erg = true;
        int posX = cc.getIndX(), posY = cc.getIndY();
        Vector<Vector<Cell>> gen = game.getIndexedGeneration();
        /*
                0       0       0       0
                0       x/y     x+1/y   0
                0       x/y+1   x+1/y+1 0
                0       0       0       0
         */
        //1. Rahmen:
        //a) erste Reihe
        for (int i=posX-1; erg && i<=posX+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
                erg = false;
            }
        }
        //b) zweite Reihe
        for (int i=posX-1; erg && i<=posX+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
                erg = false;
            }
        }
        //c) erste Spalte
        for (int i=posY-1; erg && i<=posY+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX-1, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //d) zweite Spalte
        for (int i=posY-1; erg && i<=posY+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX+2, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //2. Rechteck:
        Vector<Vector<Integer>> rects = new Vector<>();
        for (int i=posX; erg && i<=posX+1; i++){
            for (int j=posY; erg && j<=posY+1; j++) {
                int tmpX = GameCalc.getInBorders(i, game), tmpY = GameCalc.getInBorders(j, game);
                if (gen.get(tmpX).get(tmpY).getStatus() == 0) {
                    erg = false;
                }
                else{
                    Vector<Integer> rect = new Vector<>();
                    rect.add(tmpX);
                    rect.add(tmpY);
                    rects.add(rect);
                }
            }
        }
        if (!erg){
            rects.clear();
        }
        return rects;
    }

    public static Vector<Vector<Integer>> isPieceOfCrosshair(CellContainer cc, Game game){
        boolean erg = true;
        int posX = cc.getIndX(), posY = cc.getIndY();
        Vector<Vector<Cell>> gen = game.getIndexedGeneration();
        /*
                        0       0       0
                0       0       x+1/y-1 0       0
                0       x/y     0       x+2/y   0
                0       0       x+1/y+1 0       0
                        0       0       0
         */
        //1. Rahmen:
        //a) erste Reihe
        for (int i=posX; erg && i<=posX+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY-2, game)).getStatus()==1){
                erg = false;
            }
        }
        //b) zweite Reihe
        for (int i=posX; erg && i<=posX+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
                erg = false;
            }
        }
        //c) erste Spalte
        for (int i=posY-1; erg && i<=posY+1; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX-1, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //d) zweite Spalte
        for (int i=posY-1; erg && i<=posY+1; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX+3, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //e) Mitte
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(posY).getStatus()==1){
            erg = false;
        }
        //f) Ecken
        if (gen.get(posX).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        //2. Crosshair:
        Vector<Vector<Integer>> crosshair = new Vector<>();
        if (gen.get(posX).get(posY).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(posX);
            rect.add(posY);
            crosshair.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==0){
            erg = false;
            //System.out.println("Fail 2.2): "+getInBorders(posX+1)+":"+getInBorders(posY-1));
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY-1, game));
            crosshair.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==0){
            erg = false;
            //System.out.println("Fail 2.3): "+getInBorders(posX+1)+":"+getInBorders(posY+1));
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY+1, game));
            crosshair.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(posY).getStatus()==0){
            erg = false;
            //System.out.println("Fail 2.4): "+getInBorders(posX+2)+":"+posY);
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+2, game));
            rect.add(posY);
            crosshair.add(rect);
        }
        if (!erg){
            crosshair.clear();
        }
        return crosshair;
    }

    public static Vector<Vector<Integer>> isPieceOfStretchedCrosshair(CellContainer cc, Game game){
        boolean erg = true;
        int posX = cc.getIndX(), posY = cc.getIndY();
        Vector<Vector<Cell>> gen = game.getIndexedGeneration();
        /*
                        0       0       0
                0       0       x+1/y-1 0       0
                0       x/y     0       x+2/y   0
                0       x/y+1   0       x+2/y+1 0
                0       0       x+1/y+2 0       0
                        0       0       0
         */
        //1. Rahmen:
        //a) erste Reihe
        for (int i=posX; erg && i<=posX+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY-2, game)).getStatus()==1){
                erg = false;
            }
        }
        //b) zweite Reihe
        for (int i=posX; erg && i<=posX+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY+3, game)).getStatus()==1){
                erg = false;
            }
        }
        //c) erste Spalte
        for (int i=posY-1; erg && i<=posY+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX-1, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //d) zweite Spalte
        for (int i=posY-1; erg && i<=posY+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX+3, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //e) Mitte
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(posY).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
            //System.out.println("Fail e): "+getInBorders(posX+1)+":"+posY);
        }
        //f) Ecken
        if (gen.get(posX).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
            //System.out.println("Fail f1): "+posX+":"+getInBorders(posY-1));
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
            erg = false;
            //System.out.println("Fail f2): "+posX+":"+getInBorders(posY+1));
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
            erg = false;
            //System.out.println("Fail f3): "+getInBorders(posX+2)+":"+getInBorders(posY+1));
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
            //System.out.println("Fail f4): "+getInBorders(posX+2)+":"+getInBorders(posY-1));
        }
        //2. Crosshair:
        Vector<Vector<Integer>> crosshair = new Vector<>();
        if (gen.get(posX).get(posY).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(posX);
            rect.add(posY);
            crosshair.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY-1, game));
            crosshair.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY+2, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY+2, game));
            crosshair.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(posY).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+2, game));
            rect.add(posY);
            crosshair.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+2, game));
            rect.add(GameCalc.getInBorders(posY+1, game));
            crosshair.add(rect);
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY+1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(posX);
            rect.add(GameCalc.getInBorders(posY+1, game));
            crosshair.add(rect);
        }
        if (!erg){
            crosshair.clear();
        }
        Vector<Vector<Integer>> csMirrored = isPieceOfStretchedCrosshairMirrored(cc, game);
        for (Vector<Integer> indizes:csMirrored){
            crosshair.add(indizes);
        }
        return crosshair;
    }

    private static Vector<Vector<Integer>> isPieceOfStretchedCrosshairMirrored(CellContainer cc, Game game){
        boolean erg = true;
        int posX = cc.getIndX(), posY = cc.getIndY();
        Vector<Vector<Cell>> gen = game.getIndexedGeneration();
        /*
                        0       0       0       0
                0       0       x+1/y-1 x+2/y-1 0       0
                0       x/y     0       0       x+3/y   0
                0       0       x+1/y+1 x+2/y+1 0       0
                        0       0       0       0
         */
        //1. Rahmen:
        //a) erste Reihe
        for (int i=posX; erg && i<=posX+3; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY-2, game)).getStatus()==1){
                erg = false;
            }
        }
        //b) zweite Reihe
        for (int i=posX; erg && i<=posX+3; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
                erg = false;
            }
        }
        //c) erste Spalte
        for (int i=posY-1; erg && i<=posY+1; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX-1, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //d) zweite Spalte
        for (int i=posY-1; erg && i<=posY+1; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX+4, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //e) Mitte
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(posY).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(posY).getStatus()==1){
            erg = false;
        }
        //f) Ecken
        if (gen.get(posX).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        //2. Crosshair:
        Vector<Vector<Integer>> crosshair = new Vector<>();
        if (gen.get(posX).get(posY).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(posX);
            rect.add(posY);
            crosshair.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY-1, game));
            crosshair.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+2, game));
            rect.add(GameCalc.getInBorders(posY-1, game));
            crosshair.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(posY).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+3, game));
            rect.add(posY);
            crosshair.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+2, game));
            rect.add(GameCalc.getInBorders(posY+1, game));
            crosshair.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY+1, game));
            crosshair.add(rect);
        }
        if (!erg){
            crosshair.clear();
        }
        return crosshair;
    }

    public static Vector<Vector<Integer>> isPieceOfArrow(CellContainer cc, Game game){
        boolean erg = true;
        int posX = cc.getIndX(), posY = cc.getIndY();
        Vector<Vector<Cell>> gen = game.getIndexedGeneration();
        /*
                        0       0       0       0
                0       0       x+1/y-1 x+2/y-1 0       0
                0       x/y     0       0       x+3/y   0
                0       0       x+1/y+1 0       x+3/y+1 0
                        0       0       x+2/y+2 0       0
                                0       0       0
         */
        //a) erste Reihe
        for (int i=posX; erg && i<=posX+3; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY-2, game)).getStatus()==1){
                erg = false;
            }
        }
        //b) zweite Reihe
        for (int i=posX+1; erg && i<=posX+3; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY+3, game)).getStatus()==1){
                erg = false;
            }
        }
        //c) erste Spalte
        for (int i=posY-1; erg && i<=posY+1; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX-1, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //d) zweite Spalte
        for (int i=posY-1; erg && i<=posY+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX+4, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //e) Mitte
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(posY).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(posY).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
        }
        //f) Ecken
        if (gen.get(posX).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        //2. Pfeil:
        Vector<Vector<Integer>> arrow = new Vector<>();
        if (gen.get(posX).get(posY).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(posX);
            rect.add(posY);
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY-1, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+2, game));
            rect.add(GameCalc.getInBorders(posY-1, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY+1, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY+2, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+2, game));
            rect.add(GameCalc.getInBorders(posY+2, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+3, game));
            rect.add(GameCalc.getInBorders(posY+1, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+3, game));
            rect.add(GameCalc.getInBorders(posY, game));
            arrow.add(rect);
        }
        if (!erg){
            arrow.clear();
        }
        Vector<Vector<Integer>> arrowMirrored = isPieceOfArrowMirroredA(cc, game);
        for (Vector<Integer> indizes:arrowMirrored){
            arrow.add(indizes);
        }
        arrowMirrored = isPieceOfArrowMirroredB(cc, game);
        for (Vector<Integer> indizes:arrowMirrored){
            arrow.add(indizes);
        }
        arrowMirrored = isPieceOfArrowMirroredC(cc, game);
        for (Vector<Integer> indizes:arrowMirrored){
            arrow.add(indizes);
        }
        return arrow;
    }

    private static Vector<Vector<Integer>> isPieceOfArrowMirroredA(CellContainer cc, Game game){
        boolean erg = true;
        int posX = cc.getIndX(), posY = cc.getIndY();
        Vector<Vector<Cell>> gen = game.getIndexedGeneration();
        /*
                        0       0       0       0
                0       0       x+1/y-1 x+2/y-1 0       0
                0       x/y     0       0       x+3/y   0
                0       x/y+1   0       x+2/y+1 0       0
                        0       x+1/y+2 0       0
                                0       0
         */
        //1. Rahmen:
        //a) erste Reihe
        for (int i=posX; erg && i<=posX+3; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY-2, game)).getStatus()==1){
                erg = false;
            }
        }
        //b) zweite Reihe
        for (int i=posX; erg && i<=posX+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY+3, game)).getStatus()==1){
                erg = false;
            }
        }
        //c) erste Spalte
        for (int i=posY-1; erg && i<=posY+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX-1, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //d) zweite Spalte
        for (int i=posY-1; erg && i<=posY+1; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX+4, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //e) Mitte
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(posY).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(posY).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
        }
        //f) Ecken
        if (gen.get(posX).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
            erg = false;
        }
        //2. Pfeil:
        Vector<Vector<Integer>> arrow = new Vector<>();
        if (gen.get(posX).get(posY).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(posX);
            rect.add(posY);
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY-1, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+2, game));
            rect.add(GameCalc.getInBorders(posY-1, game));
            arrow.add(rect);
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY+1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(posX);
            rect.add(GameCalc.getInBorders(posY+1, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY+2, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY+2, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+2, game));
            rect.add(GameCalc.getInBorders(posY+1, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(posY).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+3, game));
            rect.add(posY);
            arrow.add(rect);
        }
        if (!erg){
            arrow.clear();
        }
        return arrow;
    }

    private static Vector<Vector<Integer>> isPieceOfArrowMirroredB(CellContainer cc, Game game){
        boolean erg = true;
        int posX = cc.getIndX(), posY = cc.getIndY();
        Vector<Vector<Cell>> gen = game.getIndexedGeneration();
        /*
                        0       0       0
                0       0       x+1/y-1 0       0
                0       x/y     0       x+2/y   0       0
                0       x/y+1   0       0       x+3/y+1 0
                0       0       x+1/y+2 x+2/y+2 0       0
                        0       0       0       0
         */
        //1. Rahmen:
        //a) erste Reihe
        for (int i=posX; erg && i<=posX+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY-2, game)).getStatus()==1){
                erg = false;
            }
        }
        //b) zweite Reihe
        for (int i=posX; erg && i<=posX+3; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY+3, game)).getStatus()==1){
                erg = false;
            }
        }
        //c) erste Spalte
        for (int i=posY-1; erg && i<=posY+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX-1, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //d) zweite Spalte
        for (int i=posY; erg && i<=posY+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX+4, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //e) Mitte
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(posY).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
        }
        //f) Ecken
        if (gen.get(posX).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(posY).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
            erg = false;
        }
        //2. Pfeil:
        Vector<Vector<Integer>> arrow = new Vector<>();
        if (gen.get(posX).get(posY).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(posX);
            rect.add(posY);
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY-1, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(posY).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+2, game));
            rect.add(posY);
            arrow.add(rect);
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY+1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(posX);
            rect.add(GameCalc.getInBorders(posY+1, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY+2, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY+2, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY+2, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+2, game));
            rect.add(GameCalc.getInBorders(posY+2, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+3, game));
            rect.add(GameCalc.getInBorders(posY+1, game));
            arrow.add(rect);
        }
        if (!erg){
            arrow.clear();
        }
        return arrow;
    }

    private static Vector<Vector<Integer>> isPieceOfArrowMirroredC(CellContainer cc, Game game){
        boolean erg = true;
        int posX = cc.getIndX(), posY = cc.getIndY();
        Vector<Vector<Cell>> gen = game.getIndexedGeneration();
        /*
                                0       0       0
                        0       0       x+2/y-2 0       0
                0       0       x+1/y-1 0       x+3/y-1 0
                0       x/y     0       0       x+3/y   0
                0       0       x+1/y+1 x+2/y+1 0       0
                        0       0       0       0
         */
        //1. Rahmen:
        //a) erste Reihe
        for (int i=posX+1; erg && i<=posX+3; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY-3, game)).getStatus()==1){
                erg = false;
            }
        }
        //b) zweite Reihe
        for (int i=posX; erg && i<=posX+3; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
                erg = false;
            }
        }
        //c) erste Spalte
        for (int i=posY-1; erg && i<=posY+1; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX-1, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //d) zweite Spalte
        for (int i=posY-2; erg && i<=posY+1; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX+4, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //e) Mitte
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(posY).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(posY).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        //f) Ecken
        if (gen.get(posX).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY-2, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY-2, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY-2, game)).getStatus()==1){
            erg = false;
        }
        //2. Pfeil:
        Vector<Vector<Integer>> arrow = new Vector<>();
        if (gen.get(posX).get(posY).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(posX);
            rect.add(posY);
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY-1, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY-2, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+2, game));
            rect.add(GameCalc.getInBorders(posY-2, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+1, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+1, game));
            rect.add(GameCalc.getInBorders(posY+1, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+2, game)).get(GameCalc.getInBorders(posY+1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+2, game));
            rect.add(GameCalc.getInBorders(posY+1, game));
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(posY).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+3, game));
            rect.add(posY);
            arrow.add(rect);
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==0){
            erg = false;
        }
        else{
            Vector<Integer> rect = new Vector<>();
            rect.add(GameCalc.getInBorders(posX+3, game));
            rect.add(GameCalc.getInBorders(posY-1, game));
            arrow.add(rect);
        }
        if (!erg){
            arrow.clear();
        }
        return arrow;
    }

    public static Vector<Vector<Integer>> isPieceOfDoubledCrosshair(CellContainer cc, Game game){
        boolean erg = true;
        int posX = cc.getIndX(), posY = cc.getIndY();
        Vector<Vector<Cell>> gen = game.getIndexedGeneration();
        /*
                        0       0       0       0
                0       0       x+1/y-1 x+2/y-1 0       0
                0       x/y     0       0       x+3/y   0
                0       x/y+1   0       0       x+3/y+1 0
                0       0       x+1/y+2 x+2/y+2 0       0
                        0       0       0       0
         */
        //1. Rahmen:
        //a) erste Reihe
        for (int i=posX; erg && i<=posX+3; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY-2, game)).getStatus()==1){
                erg = false;
            }
        }
        //b) zweite Reihe
        for (int i=posX; erg && i<=posX+3; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY+3, game)).getStatus()==1){
                erg = false;
            }
        }
        //c) erste Spalte
        for (int i=posY-1; erg && i<=posY+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX-1, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //d) zweite Spalte
        for (int i=posY-1; erg && i<=posY+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX+4, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //e) Mitte
        for (int i=GameCalc.getInBorders(posX+1, game); erg && i<=GameCalc.getInBorders(posX+2, game); i++){
            for (int j=posY; erg && j<=GameCalc.getInBorders(posY+1, game); j++){
                if (gen.get(i).get(j).getStatus()==1){
                    erg = false;
                }
            }
        }
        //f) Ecken
        if (gen.get(posX).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
            erg = false;
        }
        //2. Pfeil:
        Vector<Vector<Integer>> arrow = new Vector<>();
        for (int i=posY; i<=GameCalc.getInBorders(posY+1, game); i++){
            if (gen.get(posX).get(i).getStatus()==0){
                erg = false;
            }
            else{
                Vector<Integer> rect = new Vector<>();
                rect.add(posX);
                rect.add(i);
                arrow.add(rect);
            }
        }
        for (int i=posY; i<=GameCalc.getInBorders(posY+1, game); i++){
            if (gen.get(GameCalc.getInBorders(posX+3, game)).get(i).getStatus()==0){
                erg = false;
            }
            else{
                Vector<Integer> rect = new Vector<>();
                rect.add(GameCalc.getInBorders(posX+3, game));
                rect.add(i);
                arrow.add(rect);
            }
        }
        for (int i=GameCalc.getInBorders(posX+1, game); i<=GameCalc.getInBorders(posX+2, game); i++){
            if (gen.get(i).get(GameCalc.getInBorders(posY-1, game)).getStatus()==0){
                erg = false;
            }
            else{
                Vector<Integer> rect = new Vector<>();
                rect.add(i);
                rect.add(GameCalc.getInBorders(posY-1, game));
                arrow.add(rect);
            }
        }
        for (int i=GameCalc.getInBorders(posX+1, game); i<=GameCalc.getInBorders(posX+2, game); i++){
            if (gen.get(i).get(GameCalc.getInBorders(posY+2, game)).getStatus()==0){
                erg = false;
            }
            else{
                Vector<Integer> rect = new Vector<>();
                rect.add(i);
                rect.add(GameCalc.getInBorders(posY+2, game));
                arrow.add(rect);
            }
        }
        if (!erg){
            arrow.clear();
        }
        return arrow;
    }

    public static Vector<Vector<Integer>> isPieceOfDiagonal(CellContainer cc, Game game){
        boolean erg = true;
        int posX = cc.getIndX(), posY = cc.getIndY();
        Vector<Vector<Cell>> gen = game.getIndexedGeneration();
        /*
                                0       0       0
                        0       0       x+2/y-2 0       0
                0       0       x+1/y-1 0       x+3/y-1 0
                0       x/y     0       x+2/y   0       0
                0       0       x+1/y+1 0       0
                        0       0       0
         */
        //1. Rahmen:
        //a) erste Reihe
        for (int i=posX+1; erg && i<=posX+3; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY-3, game)).getStatus()==1){
                erg = false;
            }
        }
        //b) zweite Reihe
        for (int i=posX; erg && i<=posX+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
                erg = false;
            }
        }
        //c) erste Spalte
        for (int i=posY-1; erg && i<=posY+1; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX-1, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //d) zweite Spalte
        for (int i=posY-2; erg && i<=posY; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX+4, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //e) Mitte
        int tmp = 1;
        for (int i=posX; erg && i<=GameCalc.getInBorders(posX+3, game); i++){
            if (gen.get(i).get(GameCalc.getInBorders(posY+tmp, game)).getStatus()==1){
                erg = false;
            }
            tmp--;
        }
        //f) Ecken
        for (int i=posX; erg && i<=GameCalc.getInBorders(posX+1, game); i++){
            if (gen.get(i).get(GameCalc.getInBorders(posY-2, game)).getStatus()==1){
                erg = false;
            }
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
            erg = false;
        }
        for (int i=GameCalc.getInBorders(posX+2, game); erg && i<=GameCalc.getInBorders(posX+3, game); i++){
            if (gen.get(i).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
                erg = false;
            }
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(posY).getStatus()==1){
            erg = false;
        }
        //2. Diagonale:
        Vector<Vector<Integer>> diagonale = new Vector<>();
        tmp = 0;
        for (int i=posX; i<=posX+2; i++){
            int tmpX = GameCalc.getInBorders(i, game);
            if (gen.get(tmpX).get(GameCalc.getInBorders(posY+tmp, game)).getStatus()==0){
                erg = false;
            }
            else{
                Vector<Integer> rect = new Vector<>();
                rect.add(tmpX);
                rect.add(GameCalc.getInBorders(posY+tmp, game));
                diagonale.add(rect);
            }
            tmp--;
        }
        tmp = 1;
        for (int i=posX+1; i<=posX+3; i++){
            int tmpX = GameCalc.getInBorders(i, game);
            if (gen.get(tmpX).get(GameCalc.getInBorders(posY+tmp, game)).getStatus()==0){
                erg = false;
            }
            else{
                Vector<Integer> rect = new Vector<>();
                rect.add(tmpX);
                rect.add(GameCalc.getInBorders(posY+tmp, game));
                diagonale.add(rect);
            }
            tmp--;
        }
        if (!erg){
            diagonale.clear();
        }
        Vector<Vector<Integer>> diaMirrored = isPieceOfDiagonalMirrored(cc, game);
        for (Vector<Integer> indizes:diaMirrored){
            diagonale.add(indizes);
        }
        return diagonale;
    }

    private static Vector<Vector<Integer>> isPieceOfDiagonalMirrored(CellContainer cc, Game game){
        boolean erg = true;
        int posX = cc.getIndX(), posY = cc.getIndY();
        Vector<Vector<Cell>> gen = game.getIndexedGeneration();
        /*
                        0       0       0
                0       0       x+1/y-1 0       0
                0       x/y     0       x+2/y   0       0
                0       0       x+1/y+1 0       x+3/y+1 0
                        0       0       x+2/y+2 0       0
                                0       0       0
         */
        //1. Rahmen:
        //a) erste Reihe
        for (int i=posX; erg && i<=posX+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY-2, game)).getStatus()==1){
                erg = false;
            }
        }
        //b) zweite Reihe
        for (int i=posX+1; erg && i<=posX+3; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(tmp).get(GameCalc.getInBorders(posY+3, game)).getStatus()==1){
                erg = false;
            }
        }
        //c) erste Spalte
        for (int i=posY-1; erg && i<=posY+1; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX-1, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //d) zweite Spalte
        for (int i=posY; erg && i<=posY+2; i++){
            int tmp = GameCalc.getInBorders(i, game);
            if (gen.get(GameCalc.getInBorders(posX+4, game)).get(tmp).getStatus()==1){
                erg = false;
            }
        }
        //e) Mitte
        int tmp = -1;
        for (int i=posX; erg && i<=GameCalc.getInBorders(posX+3, game); i++){
            if (gen.get(i).get(GameCalc.getInBorders(posY+tmp, game)).getStatus()==1){
                erg = false;
            }
            tmp++;
        }
        //f) Ecken
        for (int i=posX; erg && i<=GameCalc.getInBorders(posX+1, game); i++){
            if (gen.get(i).get(GameCalc.getInBorders(posY+2, game)).getStatus()==1){
                erg = false;
            }
        }
        if (gen.get(posX).get(GameCalc.getInBorders(posY+1, game)).getStatus()==1){
            erg = false;
        }
        for (int i=GameCalc.getInBorders(posX+2, game); erg && i<=GameCalc.getInBorders(posX+3, game); i++){
            if (gen.get(i).get(GameCalc.getInBorders(posY-1, game)).getStatus()==1){
                erg = false;
            }
        }
        if (gen.get(GameCalc.getInBorders(posX+3, game)).get(posY).getStatus()==1){
            erg = false;
        }
        //2. Diagonale:
        Vector<Vector<Integer>> diagonale = new Vector<>();
        tmp = 0;
        for (int i=posX; i<=posX+2; i++){
            int tmpX = GameCalc.getInBorders(i, game);
            if (gen.get(tmpX).get(GameCalc.getInBorders(posY+tmp, game)).getStatus()==0){
                erg = false;
            }
            else{
                Vector<Integer> rect = new Vector<>();
                rect.add(tmpX);
                rect.add(GameCalc.getInBorders(posY+tmp, game));
                diagonale.add(rect);
            }
            tmp++;
        }
        tmp = -1;
        for (int i=posX+1; i<=posX+3; i++){
            int tmpX = GameCalc.getInBorders(i, game);
            if (gen.get(tmpX).get(GameCalc.getInBorders(posY+tmp, game)).getStatus()==0){
                erg = false;
            }
            else{
                Vector<Integer> rect = new Vector<>();
                rect.add(tmpX);
                rect.add(GameCalc.getInBorders(posY+tmp, game));
                diagonale.add(rect);
            }
            tmp++;
        }
        if (!erg){
            diagonale.clear();
        }
        return diagonale;
    }
}
