/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.uvt.info.dsa2;


import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author Ricardo Belinha
 */
public class GraphicUserInterface extends Application {

    PublicKeyCryptography program;

    @Override
    public void start(Stage primaryStage) {
        int width = 600, height = 900;
        UseFileAsMessage tempFile = new UseFileAsMessage();
        program = new PublicKeyCryptography();
        AudioClip bSound = new AudioClip(this.getClass().getResource("sounds/button_click.mp3").toExternalForm());
        AudioClip backgroundSound = new AudioClip(this.getClass().getResource("sounds/background.mp3").toExternalForm());
        backgroundSound.setVolume(0.15);
        backgroundSound.play();

        VBox finalVBox = new VBox(), vbox = new VBox(), globalMessage = new VBox(), message = new VBox(), message2 = new VBox(), answer = new VBox(), copyright = new VBox(), pdf = new VBox();
        HBox radioBox = new HBox(), finalHBox = new HBox();

        Label messageLabel = new Label("Type a message:");
        messageLabel.setFont(new Font(25));
        messageLabel.setStyle("-fx-font-weight: bold;");
        messageLabel.setTextFill(Color.WHITE);

        final TextField messageTextField = new TextField();
        messageTextField.setPrefWidth(width * 0.75);
        messageTextField.setMinWidth(width * 0.75);
        messageTextField.setMaxWidth(width * 0.75);

        Label messageLabel3 = new Label("Or choose a PDF as message:");
        messageLabel3.setFont(new Font(25));
        messageLabel3.setStyle("-fx-font-weight: bold;");
        messageLabel3.setTextFill(Color.WHITE);

        Button btn2 = new Button();
        btn2.setPrefSize(width * 0.35, height * 0.085);
        btn2.setMinSize(width * 0.35, height * 0.085);
        btn2.setMaxSize(width * 0.35, height * 0.085);
        btn2.setFont(new Font(25));
        btn2.setBackground(new Background(new BackgroundImage(new Image(Resources.getResourceFile("images/button2.png")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true))));

        Button sound_on = new Button();
        sound_on.setPrefSize(32, 32);
        sound_on.setMinSize(32, 32);
        sound_on.setMaxSize(32, 32);
        sound_on.setFont(new Font(25));
        sound_on.setBackground(new Background(new BackgroundImage(new Image(Resources.getResourceFile("images/sound_on.png")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true))));

        Button sound_off = new Button();
        sound_off.setPrefSize(32, 32);
        sound_off.setMinSize(32, 32);
        sound_off.setMaxSize(32, 32);
        sound_off.setFont(new Font(25));
        sound_off.setBackground(new Background(new BackgroundImage(new Image(Resources.getResourceFile("images/sound_off.png")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true))));

        Label messageLabel2 = new Label("Signature:");
        messageLabel2.setFont(new Font(25));
        messageLabel2.setStyle("-fx-font-weight: bold;");
        messageLabel2.setTextFill(Color.WHITE);

        ToggleGroup group = new ToggleGroup();
        RadioButton rbutton1 = new RadioButton("SHA1withRSA");
        rbutton1.setTextFill(Color.WHITE);
        rbutton1.setToggleGroup(group);
        rbutton1.setSelected(true);
        RadioButton rbutton2 = new RadioButton("SHA256withRSA");
        rbutton2.setTextFill(Color.WHITE);
        rbutton2.setToggleGroup(group);

        CheckBox cb = new CheckBox("Show only the decrypted message in HEXA");
        cb.setTextFill(Color.WHITE);
        cb.setSelected(true);

        Button btn = new Button();
        btn.setPrefSize(width * 0.55, height * 0.135);
        btn.setMinSize(width * 0.55, height * 0.135);
        btn.setMaxSize(width * 0.55, height * 0.135);
        btn.setFont(new Font(25));
        btn.setBackground(new Background(new BackgroundImage(new Image(Resources.getResourceFile("images/button.png")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true))));

        Label answerLabel = new Label("Answer:");
        answerLabel.setFont(new Font(25));
        answerLabel.setStyle("-fx-font-weight: bold;");
        answerLabel.setTextFill(Color.WHITE);

        final TextArea answerTextArea = new TextArea();
        answerTextArea.setFont(new Font(15));
        answerTextArea.setPrefSize(width * 0.75, height * 0.30);
        answerTextArea.setMinSize(width * 0.75, height * 0.30);
        answerTextArea.setMaxSize(width * 0.75, height * 0.30);
        answerTextArea.setWrapText(true);
        answerTextArea.setEditable(false);

        StackPane root = new StackPane();

        btn.setOnMouseEntered((MouseEvent event) -> {
            btn.setScaleX(1.25);
            btn.setScaleY(1.25);
            root.getScene().setCursor(Cursor.HAND);
        });
        btn.setOnMouseExited((MouseEvent event) -> {
            btn.setScaleX(1);
            btn.setScaleY(1);
            root.getScene().setCursor(Cursor.DEFAULT);
        });

        btn.setOnMousePressed((MouseEvent event) -> {
            bSound.play();
            RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
            String toogleGroupValue = selectedRadioButton.getText();
            FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(answerTextArea.getFont());
            double widthPerChar = fontMetrics.computeStringWidth(" -");
            double maxCharsPerLine = answerTextArea.getWidth() / widthPerChar;
            for (int i = 0; i < maxCharsPerLine - 5; i++) {
                answerTextArea.setText(answerTextArea.getText() + " -");
            }
            answerTextArea.setText(answerTextArea.getText() + "\n");
            if (!tempFile.isUse()) {
                if (!messageTextField.getText().isEmpty()) {
                    answerTextArea.setText(answerTextArea.getText() + "\n Using normal message as input message...\n");
                    answerTextArea.setText(answerTextArea.getText() + program.run(messageTextField.getText().getBytes(), toogleGroupValue, !cb.isSelected()) + "\n");
                } else {
                    answerTextArea.setText(answerTextArea.getText() + "\n The input message is empty!\n Type something there!\n\n");
                }
            } else {
                answerTextArea.setText(answerTextArea.getText() + "\n Using PDF file as input message...\n");
                answerTextArea.setText(answerTextArea.getText() + " File start in Hexa: " + program.bytesToHex(tempFile.getDataInBytes(), 40) + "\n");
                answerTextArea.setText(answerTextArea.getText() + program.run(tempFile.getDataInBytes(), toogleGroupValue, !cb.isSelected()) + "\n");
            }
            answerTextArea.selectPositionCaret(answerTextArea.getLength());
            answerTextArea.deselect();
            answerTextArea.setScrollTop(Double.MAX_VALUE);
            tempFile.reset();
        });

        btn2.setOnMouseEntered((MouseEvent event) -> {
            btn2.setScaleX(1.25);
            btn2.setScaleY(1.25);
            root.getScene().setCursor(Cursor.HAND);
        });
        btn2.setOnMouseExited((MouseEvent event) -> {
            btn2.setScaleX(1);
            btn2.setScaleY(1);
            root.getScene().setCursor(Cursor.DEFAULT);
        });

        btn2.setOnMousePressed((MouseEvent event) -> {
            bSound.play();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open an PDF file");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.dir") + "\\src\\dsa2\\ro\\uvt\\info\\dsa2\\files")
            );
            File file = fileChooser.showOpenDialog(root.getScene().getWindow());
            if (file != null) {
                try {
                    //System.out.println(Paths.get(file.getPath()));
                    PDDocument doc = PDDocument.load(new File(Paths.get(file.getPath()).toString()));
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    byte[] tempSaveBytes = pdfStripper.getText(doc).getBytes();
                    doc.close();
                    if (tempSaveBytes.length > 0) {
                        tempFile.setUse(true);
                        tempFile.setDataInBytes(tempSaveBytes);
                    } else {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Error Dialog - Open PDF file");
                        alert.setHeaderText("Open PDF file");
                        alert.setContentText("Ooops, there was an error! We could not read the information inside that PDF.");

                        alert.showAndWait();
                    }
                } catch (IOException ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Dialog - Open PDF file");
                    alert.setHeaderText("Open PDF file");
                    alert.setContentText("Ooops, there was an error! That PDF file does not exist!");

                    alert.showAndWait();
                }
            }
        });

        sound_on.setOnMouseEntered((MouseEvent event) -> {
            root.getScene().setCursor(Cursor.HAND);
        });
        sound_on.setOnMouseExited((MouseEvent event) -> {
            root.getScene().setCursor(Cursor.DEFAULT);
        });

        sound_on.setOnMousePressed((MouseEvent event) -> {
            backgroundSound.play();
        });

        sound_off.setOnMouseEntered((MouseEvent event) -> {
            root.getScene().setCursor(Cursor.HAND);
        });
        sound_off.setOnMouseExited((MouseEvent event) -> {
            root.getScene().setCursor(Cursor.DEFAULT);
        });

        sound_off.setOnMousePressed((MouseEvent event) -> {
            backgroundSound.stop();
        });

        Label copyrightLabel = new Label("Created by Ricardo Belinha");
        copyrightLabel.setFont(new Font(12));
        copyrightLabel.setStyle("-fx-font-weight: bold;");
        copyrightLabel.setTextFill(Color.WHITE);
        Label copyrightLabel2 = new Label("All rights reserved 2020");
        copyrightLabel2.setFont(new Font(10));
        copyrightLabel2.setStyle("-fx-font-weight: bold;");
        copyrightLabel2.setTextFill(Color.WHITE);

        pdf.getChildren().addAll(messageLabel3, btn2);
        pdf.setAlignment(Pos.CENTER);
        message.setSpacing(height * 0.025);
        message.getChildren().addAll(messageLabel, messageTextField, pdf);
        message.setAlignment(Pos.CENTER);
        message.setSpacing(height * 0.0125);
        radioBox.getChildren().addAll(rbutton1, rbutton2);
        radioBox.setAlignment(Pos.CENTER);
        radioBox.setSpacing(width * 0.05);
        message2.getChildren().addAll(messageLabel2, radioBox);
        message2.setAlignment(Pos.CENTER);
        message.setSpacing(height * 0.0125);
        globalMessage.getChildren().addAll(message, message2);
        globalMessage.setAlignment(Pos.CENTER);
        globalMessage.setSpacing(height * 0.05);
        answer.getChildren().addAll(answerLabel, answerTextArea);
        answer.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(globalMessage, btn, cb, answer);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(height * 0.025);
        copyright.getChildren().addAll(copyrightLabel, copyrightLabel2);
        copyright.setAlignment(Pos.CENTER);
        finalHBox.getChildren().addAll(sound_on, copyright, sound_off);
        finalHBox.setAlignment(Pos.CENTER);
        finalHBox.setSpacing(width * 0.025);
        finalVBox.getChildren().addAll(vbox, finalHBox);
        finalVBox.setSpacing(height * 0.025);
        finalVBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(finalVBox);
        root.setBackground(new Background(new BackgroundImage(new Image(Resources.getResourceFile("images/background.png")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true))));

        Scene scene = new Scene(root, width, height);

        primaryStage.setTitle("Security and Cryptography - Project 3 by Ricardo Belinha");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(Resources.getResourceFile("images/icon.png")));
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    class UseFileAsMessage {

        boolean use = false;
        byte[] dataInBytes = null;

        public boolean isUse() {
            return use;
        }

        public void setUse(boolean use) {
            this.use = use;
        }

        public byte[] getDataInBytes() {
            return dataInBytes;
        }

        public void setDataInBytes(byte[] dataInBytes) {
            this.dataInBytes = dataInBytes;
        }

        public void reset() {
            this.setUse(false);
            this.setDataInBytes(null);
        }
    }
}
