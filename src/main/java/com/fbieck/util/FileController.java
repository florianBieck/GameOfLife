package com.fbieck.util;

import com.fbieck.game.Cell;
import com.fbieck.game.Game;
import com.fbieck.main.Main;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.Vector;

public class FileController {

    //SingletonController zur Kontrolle von Dateiströmen und Speicherung des aktuellen Spielfeldes

    private static FileController instance;

    //Dateipfad des aktuellen Spielfeldes. Wenn leer, dann noch nicht gespeichert
    private String filename = "";
    //FileChooser für serialisierte Spielfelder und für Bilder im PNG-Format
    private final FileChooser fcGame = new FileChooser(), fcImg = new FileChooser();

    private FileController() {
        fcGame.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML (.xml)", "*.xml"));
        fcGame.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game of Life (.gol)", "*.gol"));
        fcImg.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG (.png)", "*.png"));
    }

    public static FileController getInstance() {
        if (instance == null){
            instance = new FileController();
        }
        return instance;
    }

    public boolean writeGame(Game game){
        //Versuchen Spielfeld im aktuellen Dateipfad zu speichern
        if ( filename.isEmpty() ){
            return writeGameTo(game);
        }
        else{
            File f = new File(filename);
            if ( f.exists() ){
                if (f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf(".")).equals(".xml")){
                    return writeGameToAsXML(game, f);
                }
                else {
                    return writeObjectToFile(game, f);
                }
            }
            else{
                return writeGameTo(game);
            }
        }
    }

    public boolean writeGameTo(Game game){
        //Spielfeld in neue / im Dialog ausgewählte Datei speichern
        File f = fcGame.showSaveDialog(Main.MAINSTAGE);
        if ( f != null ){
            if (f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf(".")).equals(".xml")){
                return writeGameToAsXML(game, f);
            }
            else {
                return writeObjectToFile(game, f);
            }
        }
        return false;
    }

    public boolean writeGameToAsXML(Game game, File f){
        //XML Dokument erstellen mit JDOM2
        Document out = new Document();
        //Root node: <GameOfLife size='x' index='y'>
        Element root = new Element("GameOfLife");
        root.getAttributes().add(new Attribute("size", game.getIndexedGeneration().size()+""));
        root.getAttributes().add(new Attribute("index", game.getIndex()+""));
        for (int i=0; i<game.getGenerations().size(); i++){
            //Generation node: <Generation index='x'>
            Element gen = new Element("Generation");
            gen.getAttributes().add(new Attribute("index", i+""));
            for (int j=0; j<game.getGenerations().get(i).size(); j++){
                //Line node: <Line index='x'>
                Element line = new Element("Line");
                line.getAttributes().add(new Attribute("index", j+""));
                for (int k=0; k<game.getGenerations().get(i).get(j).size(); k++){
                    //Cell node: <Cell y='y' status='z' />
                    Element cell = new Element("Cell");
                    cell.getAttributes().add(new Attribute("x", k+""));
                    cell.getAttributes().add(new Attribute("status", game.getGenerations().get(i).get(j).get(k).getStatus()+""));
                    line.getChildren().add(cell);
                }
                gen.getChildren().add(line);
            }
            root.getChildren().add(gen);
        }
        //Dokument in Datei speichern
        out.setRootElement(root);
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            FileOutputStream fos = new FileOutputStream(f);
            xmlOutputter.output(out, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean writeImgTo(Image img){
        //Bild in neue / im Dialog ausgewählte Datei speichern
        File f = fcImg.showSaveDialog(Main.MAINSTAGE);
        if ( f != null ){
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", f);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean writeObjectToFile(Object o, File f){
        //Kontrolle vom Objectoutput
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(o);
            oos.flush();
            oos.close();
            filename = f.getAbsolutePath();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Game readGame(){
        //Lesen eines Spielfeldes aus im Dialog ausgewählter Datei
        File f = fcGame.showOpenDialog(Main.MAINSTAGE);
        if ( f != null && f.exists()){
            if (f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf(".")).equals(".xml")){
                Game game = null;
                try {
                    //XML Dokument aus Datei lesen
                    Document xml = new SAXBuilder().build(f);
                    int size = Integer.parseInt(xml.getRootElement().getAttributeValue("size"));
                    game = new Game(size);
                    game.getGenerations().clear();
                    Element root = xml.getRootElement();
                    for (int i=0; i<root.getChildren().size(); i++){
                        //Eine Generation
                        Vector<Vector<Cell>> gen = new Vector<>();
                        Element elemGen = xml.getRootElement().getChildren().get(i);
                        for (int j=0; j<elemGen.getChildren().size(); j++){
                            //Eine Zeile einer Generation
                            Element elemLine = elemGen.getChildren().get(j);
                            Vector<Cell> genLine = new Vector<>();
                            for (int k=0; k<elemLine.getChildren().size(); k++) {
                                //Eine Zelle einer Zeile einer Generation
                                Element elemCell = elemLine.getChildren().get(k);
                                int x = Integer.parseInt(elemCell.getAttributeValue("x"));
                                int status = Integer.parseInt(elemCell.getAttributeValue("status"));
                                //Zelle der Zeile hinzufügen
                                genLine.add(new Cell(status));
                            }
                            //Zeile der Generation hinzufügen
                            gen.add(genLine);
                        }
                        //Generation dem Game hinzufügen
                        game.getGenerations().add(gen);
                    }
                    //Index der aktuellen Generation laden
                    game.setIndex(Integer.parseInt(xml.getRootElement().getAttributeValue("index")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                filename = f.getAbsolutePath();
                return game;
            }
            else {
                Game g = (Game) readObjectFromFile(f);
                if (g!=null){
                    filename = f.getAbsolutePath();
                }
                return g;
            }
        }
        return null;
    }

    public WritableImage readImage(){
        //Lesen eines Bildes aus im Dialog ausgewählter Datei
        File f = fcImg.showOpenDialog(Main.MAINSTAGE);
        WritableImage wi = null;
        if ( f != null && f.exists()){
            try {
                Image img = new Image(new FileInputStream(f));
                wi = new WritableImage(img.getPixelReader(), (int) img.getWidth(), (int) img.getHeight());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return wi;
    }

    private Object readObjectFromFile(File f){
        //Kontrolle von ObjectInput
        Object o = null;
        if ( f != null && f.exists()){
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                o = ois.readObject();
                ois.close();
                filename = f.getAbsolutePath();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return o;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {

        this.filename = filename;
    }
}
