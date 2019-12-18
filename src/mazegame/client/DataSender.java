/**
 * Adatküldő osztály
 */


package mazegame.client;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class DataSender {

    private ObjectOutputStream objectOutputStream;

    public DataSender(ObjectOutputStream o){
        this.objectOutputStream = o;
    }


    void sendData(Object o){
        new Thread(() -> {
            try {
                objectOutputStream.writeObject(o);
                objectOutputStream.flush();
                objectOutputStream.reset();
                System.out.println("data sent");

            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }
    void sendEndRequest(Object o, Object o2){
        new Thread(() -> {
            try {
                objectOutputStream.writeObject(o);
                objectOutputStream.reset();
                objectOutputStream.writeObject(o2);
                objectOutputStream.flush();
                objectOutputStream.reset();
                System.out.println("data sent");

            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }
}
