package com.fbieck.game;

import java.io.Serializable;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class Game implements Serializable {

    //Verwirklichung eines Spielfeldes

    //Vector von Generationen (Vector von Zeilen von Zellen)
    private final Vector<Vector<Vector<Cell>>> generations = new Vector<>();
    //Index der aktuellen Generation
    private int index = 0;

    public Game(int amount) {

        generate(amount);
    }

    private void generate(int amount){
        //Zufällige Generierung einer Generation
        generations.clear();
        Vector<Vector<Cell>> generation = new Vector<>();
        for (int i=0; i<amount; i++){
            Vector<Cell> row = new Vector<>();
            for (int j=0; j<amount; j++) {
                row.add(new Cell(ThreadLocalRandom.current().nextInt(0, 2)));
            }
            generation.add(row);
        }
        generations.add(generation);
    }

    public Vector<Vector<Vector<Cell>>> getGenerations() {
        return generations;
    }

    public Vector<Vector<Cell>> getIndexedGeneration(){
        return generations.get(index);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void increaseIndex(){
        index++;
        if (index>=generations.size()){
            index = generations.size()-1;
        }
    }

    public void decreaseIndex(){
        index--;
        if (index<0){
            index = 0;
        }
    }
}
