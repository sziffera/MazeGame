package mazegame.client;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;


class Menu extends BorderPane {


    private Button button;
    private Main main;

    Menu(Main main) {

        this.main = main;

        button = new Button("JOIN GAME");
        button.setTextFill(Color.GOLDENROD);
        button.setPadding(new Insets(7));
        setCenter(button);

        button.setOnAction(event -> {
            button.setText("Waiting for other players");
            Thread thread = new Thread(new SetUpData(main, button));
            thread.setDaemon(true);
            thread.start();
        });
    }

}
