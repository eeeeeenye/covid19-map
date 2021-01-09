import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.*;


public class Client {
	Socket socket = null;
	
	public static void main(String[] args) {
		Client c = new Client();
		
		try {
			c.socket = new Socket("localhost",7777);
			StartFrame sf= new StartFrame(c.socket); 
			ClientListener cl = new ClientListener(c.socket);
			
			cl.start();
		}catch(Exception e) {
			e.getStackTrace();
			System.exit(0);
			System.out.println("Client");
		}
		
	}
}



class StartFrame extends JFrame{ // 클라이언트가 서버에 접근했을 때 나오는 프레임
	int count = 0;
	Socket socket;
	DataOutputStream dataoutstream;
	OutputStream outstream;
	
	StartFrame(Socket socket){
		this.socket = socket;
		JPanel panel = new JPanel(new BorderLayout());
		JPanel center = new JPanel(new FlowLayout());
		JPanel south = new JPanel(new FlowLayout());
		
		JLabel img = null;
		JLabel covid =new JLabel("<html><br><br>C O V I D 1 9<br><br>P R O G R A M<br><br><html>"); // 행을 다르게 하기 위해 <html>언어 사용
		JLabel loading = new JLabel("m a d e  b y  I n h y e.");
		
		setSize(800,600);
		setTitle("Loading...");
		
		loading.setFont(new Font("Yu Gothic UI Light", Font.ITALIC, 15));
		
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(new File("covid19.png"));
		}catch(Exception e) {
			e.getStackTrace();
		}
		img = new JLabel(new ImageIcon(bi));
		covid.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 25));
		covid.setForeground(Color.WHITE);
		covid.setHorizontalAlignment(SwingConstants.CENTER);
		
		center.add(covid);
		center.add(img);
		
		south.add(loading);
		panel.add(center,BorderLayout.CENTER);
		panel.add(south,BorderLayout.SOUTH);
		center.setBackground(Color.DARK_GRAY);
		south.setBackground(Color.LIGHT_GRAY);
		setContentPane(panel);
		
		Dimension size1 = this.getSize();
		Dimension size2 = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((size2.width - size1.width) / 2 , (size2.height - size1.height) / 2);

		setVisible(true);
		
		Timer t = new Timer(4000,new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				try {
					outstream = socket.getOutputStream(); 
					dataoutstream = new DataOutputStream(outstream);
					dataoutstream.writeUTF("start");
				} catch (IOException e) {e.printStackTrace();}
			}
		});
		
		t.start();
		
		try {
			Thread.sleep(5000);
		}catch(Exception e) {
			
		}
		t.stop();
		
	}




	
}

class ClientListener extends Thread{ //서버에서 보내는 문자를 받아 출력
	Socket socket;
	DataInputStream datainstream;
	InputStream instream;
	
	ClientListener(Socket s){
		this.socket = s;
	}
	
	public void run() {
		while(true) {
			try {
				instream = socket.getInputStream();
				datainstream = new DataInputStream(instream);
				
				if(datainstream.readUTF().equals("종료")) {
					System.exit(0);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
			
		}
	}
}