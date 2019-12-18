package mazegame.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Main extends Application {

    private DataSender dataSender;
    private static int id;
    private VBox root;
    private Stage stage;
    private Menu menu;
    private MazePane mazePane = null;
    private Receiver receiver;


    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        root = new VBox(10);

        primaryStage.setTitle("Maze Game");
        primaryStage.getIcons().add(new Image("mazegame/icon.png"));
        menu = new Menu(this);

        primaryStage.setOnCloseRequest(event -> {

            if(mazePane != null){
                sendEndRequest(true,mazePane.getPos());
            }
            System.exit(0);
        }
        );

        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.setScene(new Scene(menu, 650, 420, Color.WHITE));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    void setId(int value) {
        id = value;
    }

    int getId() {
        return id;
    }


    void setMazePane(MazePane mazePane) {
        this.mazePane = mazePane;
    }

    void setDataSender(DataSender dataSender) {
        this.dataSender = dataSender;
    }

    void sendMessage(Object o) {
        dataSender.sendData(o);
    }

    void sendEndRequest(Object o, Object o2) {dataSender.sendEndRequest(o,o2);}

    void changePane(Pane newPane) {
        stage.setScene(new Scene(newPane, 650, 420));
    }

    void setReceiver(Receiver receiver){
        this.receiver = receiver;
    }

    Receiver getReceiver() {
        return receiver;
    }
}
