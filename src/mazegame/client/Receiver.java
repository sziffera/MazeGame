/**
 * adat fogado szal
 */


package mazegame.client;

import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Receiver extends Thread {

    private ObjectInputStream objectInputStream;
    private Integer[][] received;
    private Integer[] points;
    private Main main;
    private MazePane mazePane;
    private Boolean gameOver;

    Receiver(Main main, MazePane mazePane, ObjectInputStream o) {

        this.objectInputStream = o;
        this.mazePane = mazePane;
        this.main = main;

        points = new Integer[4];
        received = new Integer[mazePane.getRows()][mazePane.getColumns()];
    }

    @Override
    public void run() {
        System.out.println("receiver started");
        try {

            while (!isInterrupted()) {

                gameOver = (Boolean) objectInputStream.readObject();

                points = (Integer[]) objectInputStream.readObject();

                received = (Integer[][]) objectInputStream.readObject();
                mazePane.setMazeMatrix(received);


                Platform.runLater(() -> {

                    if(gameOver){
                        main.changePane(new GameOverPane(points,main.getId()));
                    }

                    mazePane.setPlayerPoints(points);
                    mazePane.repaint(received);
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
        System.out.println("RECEIVER ENDED");
    }

}
