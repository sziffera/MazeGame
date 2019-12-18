package mazegame.server;

import java.io.*;
import java.util.*;

public class Manager {

    private BufferedReader bf;
    private int rows, columns;
    private Integer[] playerPoints;
    private Integer[][] mazeMatrix;
    private Boolean gameOver = false;
    private volatile int numberOfObjects;

    private static final int WALL = -1;
    private static final int PATH = 0;
    private static final int OBJECT_1 = 5;
    private static final int OBJECT_2 = 6;
    private static final int OBJECT_3 = 7;
    private static final int O_1_POINT = 1;
    private static final int O_2_POINT = 3;
    private static final int O_3_POINT = 5;
    private static final Integer MAX_POINTS = 100;


    public Manager() {

        ObjectMaker objectMaker = new ObjectMaker(this);
        objectMaker.start();

        this.rows = 31;
        this.columns = 41;

        numberOfObjects = 0;

        //-1, ha nincs jatekban az adott id
        playerPoints = new Integer[]{-1, -1, -1, -1};

        mazeMatrix = new Integer[rows][columns];

        try {
            Random random = new Random();
            Integer fileName = random.nextInt(3)+1;
            FileReader fileReader = new FileReader(fileName.toString() + ".txt");
            bf = new BufferedReader(fileReader);
            fileReader();
            System.out.println("file read operation succeed");


            for (Client c : Client.clients) {
                int[] temp = freePosition();
                mazeMatrix[temp[0]][temp[1]] = c.getClientId();
                playerPoints[c.getClientId() - 1] = 0;
            }

            //az elso ket jatekos inditasa
            for (Client c : Client.clients) {

                c.getObjectOutputStream().writeObject(new Boolean(true));
                c.getObjectOutputStream().writeObject(MAX_POINTS);
                c.getObjectOutputStream().writeObject(playerPoints);
                c.getObjectOutputStream().writeObject(mazeMatrix);
                System.out.println("maze sent");
                c.getObjectOutputStream().writeObject(c.getClientId());
                c.getObjectOutputStream().writeObject(rows);
                c.getObjectOutputStream().writeObject(columns);
                c.getObjectOutputStream().writeObject(1);
                c.getObjectOutputStream().writeObject(1);
                c.getObjectOutputStream().flush();
                c.getObjectOutputStream().reset();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void fileReader() throws IOException {

        String line;
        for (int i = 0; i < rows; i++) {

            line = bf.readLine();
            int j = 0;

            for (char c : line.toCharArray()) {
                if (c == '1') {
                    mazeMatrix[i][j++] = WALL;
                } else {
                    mazeMatrix[i][j++] = PATH;
                }
            }
        }
    }

    /**
     * kapcsolat lezarasa, levetel a tablarol
     * @param id
     * @param x, pos
     * @param y, pos
     */

    synchronized void closeConnection(int id, int x, int y) {

        playerPoints[id - 1] = -1;
        changeNumberOfObjects(-1);

        mazeMatrix[y][x] = PATH;
        for (Client c : Client.clients) {
            if (c.getClientId() == id) {
                try {
                    c.getSocket().close();
                    c.getObjectOutputStream().close();
                    c.getObjectInputStream().close();
                    c.interrupt();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Iterator<Client> iterator = Client.clients.iterator(); iterator.hasNext(); ) {
            Client temp = iterator.next();
            if (temp.getClientId() == id) {
                iterator.remove();
            }
        }

        send();
    }

    protected synchronized void setPosition(int state, int r, int c) {


        mazeMatrix[r][c] = state;
        send();


    }

    protected synchronized void changeNumberOfObjects(int add) {
        numberOfObjects += add;
    }

    protected synchronized int getNumberOfObjects() {
        return numberOfObjects;
    }

    /**
     * adatok kuldese minden kliensnek
     */
    private synchronized void send() {
        for (Client c : Client.clients) {
            try {
                c.getObjectOutputStream().reset();
                c.getObjectOutputStream().writeObject(gameOver);
                c.getObjectOutputStream().reset();
                c.getObjectOutputStream().writeObject(playerPoints);
                c.getObjectOutputStream().reset();
                c.getObjectOutputStream().writeObject(mazeMatrix);
                c.getObjectOutputStream().flush();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * szabad poziciot keres, ami osveny
     * @return koordinatak
     */
    synchronized int[] freePosition() {
        int[] pos = new int[2];
        Random random = new Random();

        while (true) {
            int r = random.nextInt(rows);
            int c = random.nextInt(columns);
            if (mazeMatrix[r][c] == PATH) {
                pos[0] = r;
                pos[1] = c;
                break;
            }
        }
        return pos;
    }

    /**
     * ha mar ket kliens van ezt hasznalja
     * @param id jatekose
     */
    synchronized void sendDataToNewClient(int id) {

        try {


            int[] temp = freePosition();
            mazeMatrix[temp[0]][temp[1]] = id;
            playerPoints[id - 1] = 0;


            for (Client c : Client.clients) {

                if (c.getClientId() == id) {
                    c.getObjectOutputStream().writeObject(new Boolean(true));
                    c.getObjectOutputStream().writeObject(MAX_POINTS);
                    c.getObjectOutputStream().writeObject(playerPoints);
                    c.getObjectOutputStream().writeObject(mazeMatrix);
                    System.out.println("maze sent to new player");
                    c.getObjectOutputStream().writeObject(c.getClientId());
                    c.getObjectOutputStream().writeObject(rows);
                    c.getObjectOutputStream().writeObject(columns);
                    c.getObjectOutputStream().writeObject(1);
                    c.getObjectOutputStream().writeObject(1);
                    c.getObjectOutputStream().flush();
                    c.getObjectOutputStream().reset();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * lepes ellenorzese + pontok osztasa
     * @param id
     * @param x
     * @param y
     * @param oldX, hogy felszabaduljon a regi hely
     * @param oldY
     */

    synchronized void validateStep(int id, int x, int y, int oldX, int oldY) {


        int points = 0;
        System.out.println(mazeMatrix[y][x]);
        System.out.println(x + " " + y);

        if (mazeMatrix[y][x] == PATH || mazeMatrix[y][x] == OBJECT_1 || mazeMatrix[y][x] == OBJECT_2 || mazeMatrix[y][x] == OBJECT_3) {
            System.out.println("LÉPÉS");
            if (mazeMatrix[y][x] == OBJECT_1) {
                points = O_1_POINT;
                changeNumberOfObjects(-1);
            } else if (mazeMatrix[y][x] == OBJECT_2) {
                points = O_2_POINT;
                changeNumberOfObjects(-1);
            } else if (mazeMatrix[y][x] == OBJECT_3) {
                points = O_3_POINT;
                changeNumberOfObjects(-1);
            }

            playerPoints[id - 1] += points;

            if (playerPoints[id - 1].intValue() >= MAX_POINTS.intValue()) {
                gameOver = true;
            }


            mazeMatrix[y][x] = id;
            mazeMatrix[oldY][oldX] = PATH;

            for (Client c : Client.clients) {
                if (c.getClientId() == id) {
                    c.changePoints(points);
                }
            }

            send();

        }
    }


}
