

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;




public class TestFX extends Application {

    boolean dragging;
    double startX;
    double startY;

    @Override public void start(Stage stage) {

        Group g = new Group();
        final Rectangle r = new Rectangle(100,100);
        g.getChildren().add(r);
        Scene scene = new Scene(g);


        r.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dragging = true;
                startX = event.getScreenX();
                startY = event.getScreenY();
                System.out.println("start");
            }
        });


        r.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("moving");
                if(dragging) {
                    double x = event.getScreenX() - startX;
                    double y = event.getScreenY() - startY;
                    System.out.println(String.format("%s:%s", x, y));
                    r.getScene().getWindow().setX(x - startX);
                    //r.getScene().getWindow().setY(y - startY);
                }
            }
        });

        r.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("stop");
                dragging = false;
            }
        });

        stage.setScene(scene);
        stage.sizeToScene();
        //stage.setAlwaysOnTop(true);
        //stage.setFullScreen(true);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();






    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}