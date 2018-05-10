package hacklympics.teacher.pages;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import com.jfoenix.controls.JFXTextArea;
import com.hacklympics.api.event.EventType;
import com.hacklympics.api.event.EventManager;
import com.hacklympics.api.event.EventListener;
import com.hacklympics.api.event.NewMessageEvent;
import com.hacklympics.api.message.Message;
import com.hacklympics.api.session.Session;

public class MessagesController implements Initializable {

    @FXML
    private TextArea messageBoard;
    @FXML
    private JFXTextArea inputArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setOnNewMessage((NewMessageEvent event) -> {
            messageBoard.appendText(event.getMessage() + "\n");
        });
    }
    
    public void setOnNewMessage(EventListener<NewMessageEvent> listener) {
        EventManager.getInstance().addEventListener(EventType.NEW_MESSAGE, listener);
    }
    
    
    public void send(ActionEvent event) {
        String message = inputArea.getText();
        
        if (!message.isEmpty()) {
            Message.create(Session.getInstance().getCurrentUser().getUsername(), message);
            inputArea.clear();
        }
    }
    
}