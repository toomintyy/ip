package minty.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import minty.Minty;

/**
 * Controls Minty's main chat window.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Minty minty;

    /**
     * Configures behavior that depends only on the loaded interface controls.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Connects the window to the chatbot and displays its welcome message.
     *
     * @param minty chatbot that processes user commands.
     */
    public void setMinty(Minty minty) {
        this.minty = minty;
        dialogContainer.getChildren().add(DialogBox.getMintyDialog(
                "Heyyy! I'm Feeling Minty.\nWhat can I do for you today?"));
        userInput.requestFocus();
    }

    /**
     * Sends the current command to Minty and displays both sides of the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = minty.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getMintyDialog(response));
        userInput.clear();

        if (input.equals("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
