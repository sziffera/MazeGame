package mazegame.client;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;


class GameOverPane extends BorderPane {



    GameOverPane(Integer[] points, int myId) {



        Canvas canvas = new Canvas(360, 340);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.GOLDENROD);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("GAME OVER!", 150, 30);


        int myPos = 1;
        int myPoints = points[myId-1];

        for (int i = 0; i < points.length; i++) {

            if(points[i] != -1)
                if(points[i] > myPoints)
                    myPos++;

        }
        String pos;
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        if(myPos == 1){
            pos = "CONGRATULATIONS";
            gc.fillText(pos, 150, 60);
            pos = "YOU WON!";
            gc.fillText(pos, 150, 87);

        }
        else{
            pos = "YOU FINISHED " + myPos + ".";
            gc.fillText(pos, 150, 60);
        }


        setCenter(canvas);
    }


}
