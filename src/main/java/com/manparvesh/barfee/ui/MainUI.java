package com.manparvesh.barfee.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component("ui")
public class MainUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("BarfeeWindow.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        //set Stage boundaries to visible bounds of the main screen
        primaryStage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() * 0.1);
        primaryStage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() * 0.1);
        primaryStage.setWidth(primaryScreenBounds.getWidth() * 0.8);
        primaryStage.setHeight(primaryScreenBounds.getHeight() * 0.8);

        // todo set bounds for the text area

        // todo add menu that contains things like file open, etc

        // todo set title to Untitled first, then the filename of the file we open

        // todo set title with star when there is an edit

        // todo add support for multiple files

        primaryStage.show();
    }
}