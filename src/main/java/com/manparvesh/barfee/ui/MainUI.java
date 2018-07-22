package com.manparvesh.barfee.ui;

import com.manparvesh.barfee.syntax.SyntaxManager;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.reactfx.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.Scanner;

@Component("ui")
@Slf4j
public class MainUI extends Application {

    private static final String FILE_NAME_FORMAT = "Barfee - %s%s";
    private static final String FILE_MODIFIED = "*";

    private static final String sampleCode = String.join("\n", new String[] {
            "package com.example;",
            "",
            "import java.util.*;",
            "",
            "public class Foo extends Bar implements Baz {",
            "",
            "    /*",
            "     * multi-line comment",
            "     */",
            "    public static void main(String[] args) {",
            "        // single-line comment",
            "        for(String arg: args) {",
            "            if(arg.length() != 0)",
            "                System.out.println(arg);",
            "            else",
            "                System.err.println(\"Warning: empty string as argument\");",
            "        }",
            "    }",
            "",
            "}"
    });
    private static File currentFile;
    private final SyntaxManager syntaxManager;
    private CodeArea mainTextArea;
    private FileChooser fileChooser = new FileChooser();
    private Subscription subscription;
    private Stage stage;

    @Autowired
    public MainUI(SyntaxManager syntaxManager) {
        this.syntaxManager = syntaxManager;
    }

    public MainUI() {
        syntaxManager = new SyntaxManager();
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

    private void initTextArea() {
        mainTextArea = new CodeArea();
        mainTextArea.setParagraphGraphicFactory(LineNumberFactory.get(mainTextArea));

        syntaxManager.setLanguage("");
        setSyntaxHighlighting();
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        initTextArea();

        GridPane gridPane = new GridPane();
        gridPane.add(mainTextArea, 0, 0, 100, 30);

        primaryStage.setTitle(String.format(FILE_NAME_FORMAT, "Untitled", FILE_MODIFIED));
        Scene scene = new Scene(gridPane, 300, 275);
        scene.getStylesheets().add(MainUI.class.getResource("/java-keywords.css").toExternalForm());
        primaryStage.setScene(scene);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        double windowWidth = primaryScreenBounds.getWidth();
        double windowHeight = primaryScreenBounds.getHeight();

        //set Stage boundaries to visible bounds of the main screen
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(windowWidth);
        primaryStage.setHeight(windowHeight);

        // set bounds for the text area
        mainTextArea.setPrefWidth(windowWidth);
        mainTextArea.setPrefHeight(windowHeight);

        initializeAccelerators(primaryStage);
        addTextAreaListener(primaryStage);

        primaryStage.show();
    }

    private void addTextAreaListener(Stage primaryStage) {
        log.info("Text area listener added");
        // handle file edit
        mainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            log.info("Text area listener called");
            if (!Objects.equals(oldValue, newValue) && currentFile != null) {
                // update title
                primaryStage.setTitle(String.format(FILE_NAME_FORMAT, currentFile.getName(), FILE_MODIFIED));
            }
        });
    }

    private void setSyntaxHighlighting() {
        if (subscription != null) {
            subscription.unsubscribe();

            // set initial highlighting
            mainTextArea.setStyleSpans(0, syntaxManager.computeHighlighting(mainTextArea.getText()));
        }

        // addTextAreaListener(this.stage);

        log.info("Inside setSyntaxHighlighting method. textArea = {} syntaxManager = {}", mainTextArea, syntaxManager);

        // recompute the syntax highlighting 500 ms after user stops editing area
        subscription = mainTextArea

                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()

                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(50))

                // run the following code block when previous stream emits an event
                .subscribe(ignore -> {
                    try {
                        mainTextArea.setStyleSpans(0, syntaxManager.computeHighlighting(mainTextArea.getText()));
                    } catch (NullPointerException e) {
                        log.warn(
                                "No syntax highlighting styleSpans received, which usually happens when the file is not saved yet.");
                    }
                });
    }

    private void initializeAccelerators(Stage primaryStage) {
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

                            syntaxManager.setLanguage(file.getName());
                            setSyntaxHighlighting();
                        }
                    } else {
                        saveFile(mainTextArea.getText(), currentFile);

                        // update title and set not edited
                        primaryStage.setTitle(String.format(FILE_NAME_FORMAT, currentFile.getName(), ""));

                        syntaxManager.setLanguage(currentFile.getName());
                        setSyntaxHighlighting();
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
                            mainTextArea.replaceText(readFile(file.getPath()));

                            syntaxManager.setLanguage(file.getName());
                            setSyntaxHighlighting();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // update title and set not edited
                        primaryStage.setTitle(String.format(FILE_NAME_FORMAT, file.getName(), ""));
                    }
                }
        );
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