package dk.ninjabear.talk;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class TalkApp extends Application {
	private int port;
	private Thread server;
	private ServerSocket serverSocket;
	
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		initSettings();
		primaryStage.setTitle("Talk");
		
		Button but = new Button("Talk!");
		but.setOnAction(e -> {
			try {
				Socket socket = new Socket("localhost", port);
				new ChatWindow(socket);
			} catch (IOException ex) {ex.printStackTrace();}
		});
		primaryStage.setScene(new Scene(but));
		
		primaryStage.show();
		primaryStage.setOnCloseRequest(a -> {
			try {serverSocket.close();}
			catch (IOException e) {e.printStackTrace();}
		});
		server.start();
	}
	
	private void initSettings() {
		port = 7000;
		
		server = new Thread(() -> {
			try (ServerSocket welcomeSocket = new ServerSocket(port)) {
				serverSocket = welcomeSocket;
				while(true) {
					Socket socket = welcomeSocket.accept();
					Platform.runLater(() ->	new ChatWindow(socket));
				}
			} catch (SocketException e) {
			} catch (IOException e) {e.printStackTrace();}
			
		});
	}
}
