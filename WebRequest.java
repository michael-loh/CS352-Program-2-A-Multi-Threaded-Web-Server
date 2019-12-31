import java.net.*;
import java.io.*;

public class WebRequest extends Thread {

	private Socket sock;

	public WebRequest(Socket sock) {
		this.sock = sock;
	}

	public void run(){

		HTTP request = new HTTP(sock);
		request.sendResponse();

	}

}
