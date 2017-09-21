package dk.ninjabear.talk;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ChatWindow extends Stage {
	private Socket socket;
	private DataOutputStream outStream;
	private TextArea textArea;
	private TextField input;
	
	public ChatWindow(Socket socket) {
		this.socket = socket;
		try {
			outStream = new DataOutputStream(socket.getOutputStream());
			BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			new Thread(() -> {
				try {
					while(true) {
						String inMessage = inStream.readLine();
						Platform.runLater(() ->	textArea.setText(textArea.getText() + "you>" + inMessage + "\n"));
					}
				} catch (IOException e) {e.printStackTrace();}
			}).start();
		} catch (IOException e) {e.printStackTrace();}
		
		this.setTitle("chat");
		
		textArea = new TextArea();
		input = new TextField();
		input.setOnAction(e -> send());
		BorderPane root = new BorderPane(textArea);
		root.setBottom(input);
		this.setScene(new Scene(root));
		
		this.setOnCloseRequest(a -> {
			try {socket.close();}
			catch (IOException e) {e.printStackTrace();}
		});
		
		this.show();
	}
	
	private void send() {
		String outMessage = input.getText();
		input.clear();
		try {
			outStream.writeBytes(outMessage + "\n");
			textArea.setText(textArea.getText() + "me>" + outMessage + "\n");
		} catch (IOException e) {e.printStackTrace();}
	}
}
