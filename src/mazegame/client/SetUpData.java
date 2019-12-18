/**
 * Task a kapcsolat felepitesehez es a jatekba lepeshez
 * megkapja a szukseges adatokat majd modositja a gui-t
 */

package mazegame.client;


import javafx.concurrent.Task;
import javafx.scene.control.Button;
import mazegame.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class SetUpData extends Task<Void> {

    private Main main;
    Integer[] playerPoints;
    private Integer[][] mazeMatrix;
    private Socket socket;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private MazePane mazePane;
    private final Button resultLabel;
    private DataSender dataSender;
    private Boolean connectionAccept;
    private Integer rows, columns, x, y ,id, maxPoints;


    SetUpData(Main main, Button button){


        this.resultLabel = button;
        try {
            socket = new Socket("localhost", Server.PORT_NUMBER);
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e){
            e.printStackTrace();
        }
        this.main = main;
    }

    @Override
    protected Void call() throws Exception {



        connectionAccept = (Boolean) objectInputStream.readObject();
        if(connectionAccept == false){


        }
        else {
            maxPoints = (Integer) objectInputStream.readObject();
            playerPoints = (Integer[]) objectInputStream.readObject();
            mazeMatrix = (Integer[][]) objectInputStream.readObject();
            System.out.println("maze received");

            id = (Integer) objectInputStream.readObject();
            rows = (Integer) objectInputStream.readObject();
            columns = (Integer) objectInputStream.readObject();
            x = (Integer) objectInputStream.readObject();
            y = (Integer) objectInputStream.readObject();


            System.out.println("SetDataOk");
        }

        return null;
    }

    @Override
    protected void succeeded() {

        dataSender = new DataSender(objectOutputStream);

        //ha betelt a szabad helyek szama akkor ujra lehet probalkozni
        if(connectionAccept == false){
            resultLabel.setText("You can't connect, too many players. Click to retry");
            resultLabel.setOnAction(event ->
            {
                Thread thread = new Thread(new SetUpData(main, resultLabel));
                thread.setDaemon(true);
                thread.start();
            });

        }
        else {

            //ha nem akkor felepiti a labirintust
            main.setDataSender(dataSender);
            main.setId(id);
            mazePane = new MazePane(main, rows, columns, x, y, mazeMatrix, playerPoints,maxPoints);
            main.setMazePane(mazePane);
            Receiver receiver = new Receiver(main, mazePane, objectInputStream);
            main.setReceiver(receiver);
            receiver.start();
            main.changePane(this.mazePane);
        }


    }
}
