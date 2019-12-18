package mazegame.server;


import java.util.Random;

public class ObjectMaker extends Thread {

    private Manager manager;

    public ObjectMaker(Manager manager){
        this.manager = manager;
    }


    @Override
    public void run() {

        System.out.println("ObjectMaker started");

        Random random = new Random();

        while (true){
            try {
                sleep(2000);
            } catch (InterruptedException e ){
                e.printStackTrace();
            }

            if(manager.getNumberOfObjects() < 5){
                manager.changeNumberOfObjects(1);
                int objectType = random.nextInt(3)+5;
                int[] pos = manager.freePosition();
                manager.setPosition(objectType,pos[0],pos[1]);

            }
        }

    }
}
