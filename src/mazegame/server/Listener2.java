/**
 * listener szal a tovabbi kliensek kezelesehez
 */

package mazegame.server;

import java.io.IOException;
import java.net.Socket;

public class Listener2 extends Thread {

    @Override
    public void run() {

            System.out.println("listener started");

            try {
                while (!interrupted()) {

                        Socket clientSocket = Server.serverSocket.accept();
                        new Client(clientSocket).start();

                }

                Server.serverSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }



}

