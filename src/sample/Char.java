package sample;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Char")
@XmlAccessorType(XmlAccessType.FIELD)
public class Char {
    @XmlElement(name = "imageArray")
    private int [][] imageArray;
    @XmlElement(name = "word")
    private char word;
    @XmlElement(name = "num")
    private int num;

    private StringProperty name;
    public String path;


    private static final String FILE_PATH = Main.RESOURCES_PATH + "/xmlChars";
    private static final String FILE_EXPANSION = ".xml";

    public Char() {
        this.name = new SimpleStringProperty();
        path = "";
    }

    public Char(int[][] imageArray, char name) {
        this.imageArray = imageArray;
        this.word = name;
        this.num = NeuralNetwork.getOutputNumber(name);
        this.name = new SimpleStringProperty();
    }

    public static void saveChar(Char aChar){
        try {
            File dir = new File(FILE_PATH);
            int max_index = 0;
            boolean flag = false;
            if(dir.isDirectory())
            {
                // получаем все вложенные объекты в каталоге
                int index = 0;
                for(File item : dir.listFiles()){
                    String name = item.getName().split("\\.")[0];
                    char ch = name.charAt(0);
                    if(ch == aChar.getWord()){
                        if(name.length() > 1){
                            flag = true;
                            index = Integer.valueOf(item.getName().split("\\.")[0].substring(1, name.length()));
                        }
                        max_index = max_index<index?index:max_index;
                    }
                }
            }
            if (flag) {
                max_index++;
            }
            else { max_index = 0; }
            String charName = "" + aChar.getWord() + max_index;

            File file = new File(FILE_PATH + File.separator + charName + FILE_EXPANSION);
            JAXBContext context = JAXBContext.newInstance(Char.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(aChar, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static List<Char> loadChars(){
        List<Char> chars = new ArrayList<>();
        Char aChar = null;
        try {
            File dir = new File(FILE_PATH);
            if(dir.isDirectory())
            {
                // получаем все вложенные объекты в каталоге
                for(File item : dir.listFiles()){
                    JAXBContext jaxbContext = JAXBContext.newInstance(Char.class);
                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                    aChar = (Char) jaxbUnmarshaller.unmarshal(item);
                    aChar.setName(item.getName());
                    aChar.path = item.getAbsolutePath();
                    chars.add(aChar);
                }
            }
        }
        catch (JAXBException e){
            e.printStackTrace();
        }
        return chars;
    }


    public int[][] getImageArray() {
        return imageArray;
    }

    public char getWord() {
        return word;
    }

    public int getNum() {
        return num;
    }

    public void setImageArray(int[][] imageArray) {
        this.imageArray = imageArray;
    }

    public void setWord(char word) {
        this.word = word;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String toString() {
        return name.get();
    }
}
