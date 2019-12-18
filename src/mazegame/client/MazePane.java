package mazegame.client;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;


public class MazePane extends BorderPane {

    private Canvas canvas;
    private GraphicsContext gc;
    private static final int WALL = -1;
    private static final int PATH = 0;
    private static final int OBJECT_1 = 5;
    private static final int OBJECT_2 = 6;
    private static final int OBJECT_3 = 7;
    private int y, x, tempX, tempY;
    private Color myColor = Color.GOLDENROD;
    private Color wallColor = Color.BLACK;
    private Color object1Color = Color.YELLOW;
    private Color object2Color = Color.GREEN;
    private Color object3Color = Color.RED;
    private Color pathColor = Color.WHITE;
    private Color otherPlayerColor = Color.LIGHTGREY;
    private Integer[][] mazeMatrix;
    private Integer myPoints;
    private Integer[] playerPoints;
    private Label text, object1Label, object2Label, object3Label, infoLabel;
    private Label myPointsText;
    private static final String MUSIC_FILE = "coins.wav";
    private MediaPlayer mediaPlayer;
    private Media sound;
    private int rows;
    private int columns;
    private Integer maxPoints;
    private int blockSize = 12;
    private Main m;

    MazePane(Main main, int rows, int columns, int x, int y, Integer[][] maze, Integer[] playerPoints, Integer maxPoints) {

        this.rows = rows;
        this.maxPoints = maxPoints;
        this.columns = columns;
        this.mazeMatrix = maze;
        this.m = main;
        myPoints = 0;

        sound = new Media(new File(MUSIC_FILE).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);

        this.playerPoints = playerPoints;

        text = new Label();

        myPointsText = new Label("MY POINTS: " + myPoints.toString(),new Rectangle(12,12,myColor));
        myPointsText.setTextFill(myColor);

        VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setFocusTraversable(false);
        vBox.setPadding(new Insets(20));
        vBox.setSpacing(15);
        vBox.setAlignment(Pos.TOP_LEFT);
        object1Label = new Label("1 POINT ",new Circle(3,object1Color));
        object2Label = new Label("3 POINTS", new Circle(4,object2Color));
        object3Label = new Label("5 POINTS", new Circle(6,object3Color));

        infoLabel = new Label("EARN " + maxPoints.toString() + " FIRST!");
        infoLabel.setTextFill(myColor);
        infoLabel.setPadding(new Insets(10,0,10,0));
        vBox.getChildren().addAll(object1Label,object2Label,object3Label,infoLabel);

        HBox hBox = new HBox();
        hBox.setFillHeight(true);
        hBox.setFocusTraversable(false);
        hBox.setSpacing(10);
        hBox.getChildren().addAll(myPointsText,text);

        canvas = new Canvas(columns * blockSize, rows * blockSize);
        canvas.setFocusTraversable(true);
        canvas.setOnKeyReleased(this::keyPressed);
        gc = canvas.getGraphicsContext2D();

        setLeft(canvas);
        setBottom(hBox);
        setRight(vBox);

        setMargin(hBox,new Insets(7));
        setMargin(vBox,new Insets(7));
        setMargin(canvas,new Insets(7));

        repaint(mazeMatrix);
    }


    void repaint(Integer[][] matrix){

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {

                if(matrix[i][j] == WALL){
                    setFill(i, j, wallColor);
                }
                else if(matrix[i][j] == PATH){
                    setFill(i, j, pathColor);
                }
                else if(matrix[i][j] == OBJECT_1){
                    setOval1(i, j);
                }
                else if(matrix[i][j] == OBJECT_2){
                    setOval2(i, j);
                }
                else if(matrix[i][j] == OBJECT_3){
                    setOval3(i, j);
                }
                else if(matrix[i][j] == m.getId()){
                    x = j;
                    y = i;
                    setFill(i, j, myColor);
                }
                else {
                    setFill(i,j,otherPlayerColor);
                    setTextFill(i,j,mazeMatrix[i][j]);
                }
            }
        }
    }


    private void setFill(int row, int column, Color color) {

        gc.setFill(color);
        int x = blockSize * column;
        int y = blockSize * row;
        gc.fillRect(x, y, blockSize, blockSize);
    }

    private void setTextFill(int row, int column, Integer text) {

        gc.setFill(Color.WHITE);
        int x = blockSize * column;
        int y = blockSize * row;
        gc.fillText(text.toString(),x + 2, y + blockSize -2 ,blockSize );
    }

    //objektumokat kirajzolo metodusok

    private void setOval1(int row, int column) {

        gc.setFill(object1Color);
        int x = blockSize * column;
        int y = blockSize * row;
        gc.fillOval(x+3, y+3, 6, 6);

    }
    private void setOval2(int row, int column) {

        gc.setFill(object2Color);
        int x = blockSize * column;
        int y = blockSize * row;
        gc.fillOval(x+2, y+2, 8, 8);
    }
    private void setOval3(int row, int column) {

        gc.setFill(object3Color);
        int x = blockSize * column;
        int y = blockSize * row;
        gc.fillOval(x, y, blockSize, blockSize);
    }

    private void keyPressed(KeyEvent ev) {

        Boolean escape;

        tempX = x;
        tempY = y;
        if (ev.getCode() == KeyCode.UP || ev.getCode() == KeyCode.DOWN || ev.getCode() == KeyCode.LEFT || ev.getCode() == KeyCode.RIGHT || ev.getCode() == KeyCode.ESCAPE) {

            if(ev.getCode() == KeyCode.ESCAPE){
                escape = true;
                m.sendEndRequest(escape,new Integer[]{x,y});
                m.getReceiver().interrupt();
                m.changePane(new Menu(m));
            }

            else {
                switch (ev.getCode()) {
                    case RIGHT:
                        tempX++;
                        break;

                    case LEFT:
                        tempX--;
                        break;

                    case UP:
                        tempY--;
                        break;

                    case DOWN:
                        tempY++;
                        break;
                }


                Integer[] send = new Integer[4];

                send[0] = tempX;
                send[1] = tempY;
                send[2] = x;
                send[3] = y;

                System.out.println();
                m.sendMessage(send);
            }
        }
    }

    /**
     * frissiti a jatekosok pontjait
     * frissiti a jatekos sajat pontjat, ha modosul -> animacio
     * @param points
     */

    synchronized void setPlayerPoints(Integer[] points){

        StringBuilder stringBuilder = new StringBuilder();

        if(playerPoints[m.getId()-1].intValue() != points[m.getId()-1].intValue()){

            System.out.println(playerPoints[m.getId()-1] + ", "+ points[m.getId()-1]);
            maxPoints -= points[m.getId()-1];
            mediaPlayer.play();
            myPointsText.setText("MY POINTS: " + points[m.getId()-1].toString());
            mediaPlayer.play();
            mediaPlayer.seek(mediaPlayer.getStartTime());
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), myPointsText);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1.0);
            fadeTransition.setCycleCount(1);
            fadeTransition.play();
        }

        playerPoints = points;

        for (int i = 0; i < points.length; i++) {
            if(i != m.getId()-1 && points[i] != -1){
                int temp = i+1;
                stringBuilder.append("Player" + temp + " points: " + points[i].toString() + " ");
            }
        }
        text.setText(stringBuilder.toString().toUpperCase());

    }

    void setMazeMatrix(Integer[][] matrix){
        mazeMatrix = matrix;
    }

    Integer[] getPos(){
        return new Integer[]{x,y};
    }

    int getRows(){
        return rows;
    }

    int getColumns() {
        return columns;
    }
}
