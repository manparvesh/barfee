package com.manparvesh.barfee.ui;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

@Component("ui")
@Slf4j
public class MainUI extends Application {

    private static final String FILE_NAME_FORMAT = "Barfee - %s%s";
    private static final String FILE_MODIFIED = "*";
    private static File currentFile;

    public static void main(String[] args) {
        launch(args);
    }

    public void run(String[] args) {
        launch(args);
    }

    private void saveFile(String content, File file) {
        try {
            FileWriter fileWriter;
            fileWriter = new FileWriter(file, false);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Parent root = FXMLLoader.load(getClass().getResource("BarfeeWindow.fxml"));

        TextArea mainTextArea = new TextArea();
        GridPane gridPane = new GridPane();
        gridPane.add(mainTextArea, 0, 0, 100, 30);

        primaryStage.setTitle(String.format(FILE_NAME_FORMAT, "Untitled", FILE_MODIFIED));
        Scene scene = new Scene(gridPane, 300, 275);
        primaryStage.setScene(scene);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        double windowWidth = primaryScreenBounds.getWidth();
        double windowHeight = primaryScreenBounds.getHeight();

        //set Stage boundaries to visible bounds of the main screen
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(windowWidth);
        primaryStage.setHeight(windowHeight);
        log.info("x={}, y={}, width={}, height={}",
                primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth(), primaryStage.getHeight());

        // set bounds for the text area
        mainTextArea.setPrefWidth(windowWidth);
        mainTextArea.setPrefHeight(windowHeight);

        log.info("TextArea: x={}, y={}, width={}, height={}",
                mainTextArea.getLayoutX(), mainTextArea.getLayoutY(),
                mainTextArea.getPrefWidth(), mainTextArea.getPrefHeight());

        FileChooser fileChooser = new FileChooser();

        // save option
        primaryStage.getScene().getAccelerators().put(
                KeyCombination.keyCombination("CTRL+S"),
                () -> {
                    log.info("CTRL+S pressed");

                    // Show save file dialog if not editing any file already
                    if (currentFile == null) {
                        File file = fileChooser.showSaveDialog(primaryStage);
                        currentFile = file;

                        // save file if something is selected
                        if (file != null) {
                            saveFile(mainTextArea.getText(), file);

                            // update title and set not edited
                            primaryStage.setTitle(String.format(FILE_NAME_FORMAT, file.getName(), ""));
                        }
                    } else {
                        saveFile(mainTextArea.getText(), currentFile);

                        // update title and set not edited
                        primaryStage.setTitle(String.format(FILE_NAME_FORMAT, currentFile.getName(), ""));
                    }
                }
        );

        // open file
        primaryStage.getScene().getAccelerators().put(
                KeyCombination.keyCombination("CTRL+O"),
                () -> {
                    log.info("CTRL+O pressed");

                    //Show save file dialog
                    File file = fileChooser.showOpenDialog(primaryStage);
                    currentFile = file;

                    if (file != null) {
                        try {
                            mainTextArea.setText(readFile(file.getPath()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // update title and set not edited
                        primaryStage.setTitle(String.format(FILE_NAME_FORMAT, file.getName(), ""));
                    }
                }
        );

        // handle file edit
        mainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && currentFile != null) {
                // update title
                primaryStage.setTitle(String.format(FILE_NAME_FORMAT, currentFile.getName(), FILE_MODIFIED));
            }
        });

        primaryStage.show();
    }

    private String readFile(String pathname) throws IOException {
        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        String lineSeparator = System.getProperty("line.separator");

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine()).append(lineSeparator);
            }
            return fileContents.toString();
        }
    }
}