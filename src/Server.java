import java.net.*;
import java.io.*;

public class Server {
	ServerSocket svs = null;
	
	public static void main(String[] args) {

		Server ss = new Server();
		APIexplorer api = new APIexplorer();
		DBmanager db = new DBmanager();
		
		
		db.createTable();
		
		api.start();
		
		try { 
			ss.svs = new ServerSocket(7777);

			while(true) {
				Socket socket = ss.svs.accept();
				TagReceiver tc = new TagReceiver(ss,socket);
				tc.start();

			}
			
		} catch (Exception e1) { e1.printStackTrace(); }


	}

}

class TagReceiver extends Thread{ // 검색프레임을 받는 메소드
	Socket socket;
	Server server;
	DataInputStream datainstream;
	InputStream instream;
	MyFrame my;
	
	TagReceiver(Server ss, Socket s){
		this.socket = s;
		this.server = ss;
	}
	
	public void run() {
			
		try {
			instream = this.socket.getInputStream();
			datainstream = new DataInputStream(instream);
			my = new MyFrame(socket);
			
			String data = datainstream.readUTF();
			System.out.println(data+"----------------------------");

			if(data.equals("start")) {
				my.searchRegin();
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
}

