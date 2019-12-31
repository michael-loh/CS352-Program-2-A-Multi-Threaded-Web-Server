import java.io.*;
import java.net.*;
import java.util.*;

public class HTTP {

	private final String CLRF = "\r\n";
	private final int CHUNK_SIZE = 1024;
	private Socket sock;
	private BufferedReader readSock;
	private DataOutputStream os;

	public HTTP(Socket sock){
		this.sock = sock;
		try {
			readSock = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			os = new DataOutputStream(sock.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendResponse(){
		//tokenize the input
		String readLine = null;
		try {
			readLine = readSock.readLine();
			if(readLine == null){
				return;
			}
			System.out.println(readLine);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		StringTokenizer st = new StringTokenizer(readLine);


		//get the header lines
		String headerLine = null;

		try {
			while( (headerLine = readSock.readLine()).length() != 0){
				System.out.println(headerLine);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}





		//Save the elements of the http request
		String method = null;
		String fileName = null;
		FileInputStream fileStream = null;
		String version = null;

		boolean fileExists = true;

		int count = 0;
		while(st.hasMoreTokens()){
			switch(count)
			{
			case(0):
				method = st.nextToken();
			break;
			case(1):
				fileName = st.nextToken();
				fileName = "." + fileName;
			break;
			case(2):
				version = st.nextToken();
			break;
			}
			count++;
		}

		try {
			fileStream = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			fileExists = false;
			e.printStackTrace();
		}

		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if(fileExists){
			statusLine = "HTTP/1.0 200 OK" + CLRF;
			contentTypeLine = "Content-type: " + contentType( fileName ) + CLRF;
		}
		else{
			statusLine = "HTTP/1.0 404 Not Found" + CLRF;
			contentTypeLine = "";
			entityBody = "<HTML>" +
					"<HEAD><TITLE>Not Found</TITLE></HEAD>" +
					"<BODY>Not Found</BODY></HTML>";
		}

		//send to sockets output stream
		try {
			os.writeBytes(statusLine);
			if(fileExists)
				os.writeBytes(contentTypeLine);
			os.writeBytes(CLRF);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(fileExists){
			try {
				sendBytes(fileStream, os);
				fileStream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			try {
				os.writeBytes(entityBody);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//close
		try {
			os.close();
			readSock.close();
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private void sendBytes(FileInputStream fileStream, DataOutputStream os2) throws Exception{
		byte[] buffer = new byte[CHUNK_SIZE];
		int bytes = 0;
		while((bytes = fileStream.read(buffer)) != -1){
			os.write(buffer, 0, bytes);
		}
	}

	private String contentType(String fileName){
		if(fileName.endsWith(".htm") || fileName.endsWith(".html") ){
			return "text/html";
		}
		else if(fileName.endsWith(".gif") ){
			return "image/gif";
		}
		else if(fileName.endsWith(".bmp") ){
			return "image/bmp";
		}
		else if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg") ){
			return "image/jpeg";
		}
		else if(fileName.endsWith(".pdf")){
			return "application/pdf";
		}
		return "application/octet-stream";
	}

}
