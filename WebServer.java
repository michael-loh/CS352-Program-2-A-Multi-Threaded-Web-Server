import java.io.IOException;
import java.net.*;

public class WebServer {
	
	public static void main(String[]args){
		WebServer server = new WebServer();
		server.run();
	}

	private void run() {
		
		int portnum = 5520;
		
		ServerSocket servSock = null;
		try {
			servSock = new ServerSocket(portnum);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(true){
			Socket sock = null;
			try {
				sock = servSock.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			WebRequest servThread = new WebRequest(sock);
			servThread.start();
		}
		
	}

}