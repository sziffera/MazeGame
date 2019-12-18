/**
 * Klienseket kezelo szalak
 */



package mazegame.server;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;


class Client extends Thread {

    static List<Client> clients = new LinkedList<>();

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private Socket socket;
    private int id;
    private int points;

    Client(Socket socket) throws IOException {

        this.socket = socket;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());


        //ha nincs mar szabad hely akkor visszautasitja a kapcsolatot
        if (clients.size() >= Server.MAX_NUMBER_OF_PLAYERS) {
            objectOutputStream.writeObject(false);
            System.out.println("TOO MANY PLAYERS");

        //kulonben keres egy szabad id-t
        } else {

            boolean used;

            for (int i = 1; i <= Server.MAX_NUMBER_OF_PLAYERS; i++) {
                used = false;

                for (Client c : clients) {
                    if (c.getClientId() == i) {
                        used = true;
                        break;
                    }

                }
                if (!used) {
                    this.id = i;
                    break;
                }

            }

            clients.add(this);

            //ha mar van ket jatekos akkor az ujaknak kuldi a labirintust
            if (Server.gameStart) {
                Server.manager.sendDataToNewClient(id);
            }
        }
    }


    @Override
    public void run() {
        try {
            Object o;

            while (!isInterrupted()) {

                o = objectInputStream.readObject();

                //lecsatlakozasi kerelem, leveszi a tablarol a jatekost, bontja a kapcsolatot
                if (o.getClass() == Boolean.class) {
                    Integer[] pos = (Integer[]) objectInputStream.readObject();
                    System.out.println(id + " disconnected");
                    Server.manager.closeConnection(id, pos[0], pos[1]);
                } else {
                    //lepes ellenorzese
                    Integer[] array = (Integer[]) o;
                    Server.manager.validateStep(id, array[0], array[1], array[2], array[3]);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }


    ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public Socket getSocket() {
        return socket;
    }

    void changePoints(int add) {
        points += add;
    }

    int getClientId() {
        return id;
    }
}
