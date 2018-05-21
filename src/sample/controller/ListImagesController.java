package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sample.Char;
import sample.Main;
import sample.NeuralNetwork;
import sample.StudyingThread;
import sample.model.Image;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ListImagesController {
    private static ListImagesController controller;

    @FXML
    private TableView<Image> imageTable;
    @FXML
    private TableColumn<Image, String> pathColumn;
    @FXML
    private TableColumn<Image, String> nameColumn;

    @FXML
    private ListView<Image> nameList;

    @FXML
    private ListView<String> resultList;

    @FXML
    private ListView<Char> customList;

    @FXML
    private ImageView imageView;

    @FXML
    private Canvas canvas;

    @FXML
    private Canvas drawCanvas;

    @FXML
    private GridPane drawGrid;

    @FXML
    public Label percent;

    @FXML
    public Label nameLabel;

    @FXML
    public Button studying;
    @FXML
    public Button clear;
    @FXML
    public Button recognize;
    @FXML
    public Button save;

    @FXML
    public TextField nameChar;

    @FXML
    public javafx.scene.image.Image image;

    @FXML
    public LineChart<Number, Number> convergence;

    @FXML
    NumberAxis xAxisIteration;
    @FXML
    NumberAxis yAxisError;

    private static ObservableList<String> obResList;

    private static StudyingThread studyingThread;

    @FXML
    public void initialize(){
        convergence.setTitle("Ошибка от эпохи");

        studying.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                studyingThread = new StudyingThread(main.getNeuralNetwork(), main.getChars());
                Thread myThready = new Thread(studyingThread);	//Создание потока "myThready"

                myThready.start();

  /*              Task<Void> task = new Task<Void>() {
                    @Override protected Void call() throws Exception {
                        main.getNeuralNetwork().study(main.getChars());
                        main.getNeuralNetwork().save();
                        return null;
                    }
                };
                ProgressBar bar = new ProgressBar();
                bar.progressProperty().bind(task.progressProperty());
                new Thread(task).start();*/
                //main.getNeuralNetwork().study(main.getChars());
                //main.getNeuralNetwork().save();
            }
        });


        clear.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                for(int i = 0; i < rec.length; i++ ){
                    for(int j = 0; j< rec[i].length; j++){
                        rec[i][j].setFill(Color.WHITE);
                    }
                }
            }
        });
        recognize.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int[][] imageArray = getDrawImageArray();
                main.getNeuralNetwork().calculate(imageArray);
                writePercent();
            }
        });
        save.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String chName = nameChar.getText();
                if(chName != null && chName.length() == 1){
                    char charAt = chName.charAt(0);
                    try {
                        NeuralNetwork.getOutputNumber(charAt);
                        Char ch = new Char(getDrawImageArray(), charAt);
                        Char.saveChar(ch);
                        main.getChars().add(ch);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });



        series1 = new XYChart.Series();
        series1.setName("Зависимость ошибки от эпохи");

        convergence.getData().addAll(series1);
    }

    private int[][] getDrawImageArray(){
        int[][] imageArray = new int[COUNT_PIXEL][COUNT_PIXEL];
        for(int i = 0; i < rec.length; i++ ){
            for(int j = 0; j< rec[i].length; j++){
                if(rec[i][j].getFill() == Color.RED){
                    imageArray[i][j] = 1;
                }
            }
        }
        return imageArray;
    }

    public XYChart.Series series1;

    private Main main;

    private static final int WIDTH_RECTANGLE = 15;
    private static final int COUNT_PIXEL = 30;

    private Rectangle[][] rec;

    public ListImagesController() {
        controller = this;
    }

    public static ListImagesController getController() {
        return controller;
    }
    /*    @FXML void initialize(){
        pathColumn.setCellValueFactory(cellData -> cellData.getValue().pathProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    }*/


    private int i = 0;

    private void initDraw(GraphicsContext gc){
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        gc.setFill(Color.LIGHTGRAY);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);

        gc.fill();
        gc.strokeRect(
                0,              //x of the upper left corner
                0,              //y of the upper left corner
                canvasWidth,    //width of the rectangle
                canvasHeight);  //height of the rectangle

        gc.setFill(Color.RED);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
    }

    public void setMain(Main main){
        this.main = main;

        imageTable.setItems(main.getImageData());

        imageTable.setVisible(false);
        nameList.setItems(main.getImageData());


        nameList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        imageView.setSmooth(false);


        xAxisIteration.setLabel("Epoch");
        yAxisError.setLabel("Error");

        canvas.setVisible(false);

        makeGrid();



        ObservableList<Char> chars = FXCollections.observableArrayList();

        chars.addAll(Char.loadChars());



        customList.setItems(chars);


        //.setLabel("Month");



        /*XYChart.Series series2 = new XYChart.Series();
        series2.setWord("Portfolio 2");

        series2.getData().add(new XYChart.Data(5, 6));
        series2.getData().add(new XYChart.Data(12, 7));
        series2.getData().add(new XYChart.Data(22, 8));*/
        //convergence.getData().addAll(series2);
        //System.out.println(imageView.isSmooth());

        /*Timeline timelines = new Timeline(new KeyFrame(
                Duration.millis(17),
                ae -> testNext()));
        timelines.setCycleCount(Animation.INDEFINITE);
        timelines.play();*/

        /*Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Test");
                    }
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
*/
        resultList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        nameList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Image image = imageTable.getItems().get(newValue.intValue());
                imageView.setImage(image.getImage());
                GraphicsContext gc = canvas.getGraphicsContext2D();

                int[][] imageArray = image.getImageArray();
                for(int i = 0; i < imageArray.length; i++){
                    for (int j = 0; j < imageArray[i].length; j++){
                        if(imageArray[i][j] == 0) gc.setFill(Color.WHITE);
                        else gc.setFill(Color.BLACK);
                        gc.fillRect(20*i,20*j,20,20);

                    }
                }

                for(int i = 0; i < imageArray.length; i++){
                    for (int j = 0; j < imageArray.length; j++){
                        if(imageArray[i][j] == 0) rec[i][j].setFill(Color.WHITE);
                        else rec[i][j].setFill(Color.RED);
                    }
                }
                main.getNeuralNetwork().calculate(imageArray);

                writePercent();

            }
        });

        customList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Char ch = customList.getItems().get(newValue.intValue());
                System.out.println(ch.path);
                int[][] imageArray = ch.getImageArray();
                for(int i = 0; i < imageArray.length; i++){
                    for (int j = 0; j < imageArray.length; j++){
                        if(imageArray[i][j] == 0) rec[i][j].setFill(Color.WHITE);
                        else rec[i][j].setFill(Color.RED);
                    }
                }
            }
        });
    }

    private void writePercent(){
        DecimalFormat decimalFormatter = new DecimalFormat("###.###");
        decimalFormatter.setMinimumIntegerDigits(2);
        decimalFormatter.setMinimumFractionDigits(3);

        obResList = FXCollections.observableArrayList();


        List<Sorted> sortList = new ArrayList<>();
        for(int i = 0; i <= 32; i++){
            double error = main.getNeuralNetwork().getError(i);
            sortList.add(new Sorted("" + NeuralNetwork.getOutputChar(i) + " = " + decimalFormatter.format(error) +"%", error));

            //obResList.add("" + NeuralNetwork.getOutputChar(i) + " = "+ decimalFormatter.format(main.getNeuralNetwork().getError(i)) +"%");
        }
        System.out.println();
        Collections.sort(sortList);

        for(int i = sortList.size() - 1; i >= 0 ; i--){
            obResList.add(sortList.get(i).strVal);
        }
        resultList.setItems(obResList);
        System.out.println();
    }

    class Sorted implements Comparable{
        String strVal;
        double dblVal;

        public Sorted() {
        }

        public Sorted(String strVal, double dblVal) {
            this.strVal = strVal;
            this.dblVal = dblVal;
        }

        @Override
        public int compareTo(Object o) {
            Sorted oSort = (Sorted) o;
            if (oSort.dblVal > this.dblVal) return -1;
            if (oSort.dblVal < this.dblVal) return 1;
            return 0;
        }
    }

    public GridPane makeGrid(){

        drawGrid.setGridLinesVisible(true);
        rec = new Rectangle [COUNT_PIXEL][COUNT_PIXEL];

        for(int i=0; i< rec.length ; i++){
            for(int j=0; j< rec[i].length; j++){
                rec[i][j] = new Rectangle();
                rec[i][j].setX(i * WIDTH_RECTANGLE);
                rec[i][j].setY(j * WIDTH_RECTANGLE);
                rec[i][j].setWidth(WIDTH_RECTANGLE);
                rec[i][j].setHeight(WIDTH_RECTANGLE);
                rec[i][j].setFill(null);
                rec[i][j].setStroke(Color.BLACK);
                //p.getChildren().add(rec[i][j]);
                drawGrid.add(rec[i][j], i, j);
            }
        }

        drawGrid.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                draw(event);
            }
        });

        drawGrid.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                draw(event);
            }
        });

        return drawGrid;
    }

    private void draw(MouseEvent event){
        double posX = event.getX();
        double posY = event.getY();

        System.out.println("posX = "+ posX +" posY = "+ posY);

        posX -= (posX / 15);
        posY -= (posY / 15);

        int colX = (int)((posX / WIDTH_RECTANGLE)) ;
        int colY = (int)((posY / WIDTH_RECTANGLE)) ;
        System.out.println("colX = "+ colX +" colY = "+colY);

        if(colX < COUNT_PIXEL && colY < COUNT_PIXEL){
            if (event.getButton() == MouseButton.SECONDARY){
                rec[colX][colY].setFill(Color.WHITE);
            }
            else {
                rec[colX][colY].setFill(Color.RED);
            }
        }
    }
}
