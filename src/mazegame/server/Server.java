package mazegame.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class Server {
    static Manager manager;
    public static final int PORT_NUMBER = 13232;
    static final int MAX_NUMBER_OF_PLAYERS = 4;
    static boolean gameStart = false;

    static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException, InterruptedException {
        serverSocket  = new ServerSocket(Server.PORT_NUMBER);
        Thread listener = new Listener();
        listener.start();
    }

}