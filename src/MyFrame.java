import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MyFrame extends JFrame implements ActionListener{
	JPanel panel = new JPanel(); //맨 뒤에 있는 프레임
	JPanel center = new JPanel();
	JComboBox kreg;
	JComboBox date;
	DBmanager db = new DBmanager();
	Vector<String> region = new Vector<String>();
	Vector<String> DT = new Vector<String>();
	JPanel westPanel = new JPanel(new FlowLayout());
	JPanel northPanel = new JPanel(new FlowLayout());
	Socket socket;
	
	// 텍스트 필드
	JTextField defcnt = new JTextField();
	JTextField clearcnt = new JTextField();
	JTextField death = new JTextField();
	
	//컴포넌트에 담을  변수를 가져옴
	private String r = db.Query("지역");
	private String[] d = db.Query("날짜").split(" ");
	int count;
	
	JLabel img;
	JLabel img2;
	JMenuBar mb = new JMenuBar();
	JMenu homeMenu = new JMenu("메뉴");
	JMenuItem aboard = new JMenuItem("국외");
	JMenuItem hos = new JMenuItem("검사 병원");
	JMenuItem exit = new JMenuItem("종료");
	
	//search 메소드
	String sidoNdate = "";
	int count2 = 0;
	String sido;
	String Dt;
	
	OutputStream outstream;
	DataOutputStream dataoutstream;
	additionalFrame add = new additionalFrame();
	
	MyFrame(Socket s){
		socket = s;
	}
	
	
	void searchRegin() {
		try {
			outstream = socket.getOutputStream();
			dataoutstream = new DataOutputStream(outstream);
		} catch (IOException e1) {e1.printStackTrace();}
		
		StringTokenizer st = new StringTokenizer(r);
		count = st.countTokens();
		int i = 0;
		
		while(i < count) { 
			region.add(st.nextToken());
			i++;
		}
		
		
		TreeSet<String> tSet = new TreeSet<String>(); //배열에 저장한 값들을 트리형태로 만들어준다. ->중복값을 없애기 위함
		
		for(int z = 0; z < d.length;z++) {
			tSet.add(d[z]); //값들을 하나씩 대입
		}
		
		Iterator it = tSet.iterator(); //tree의 요소들을 읽어옴
		
		while(it.hasNext()) {//읽어올 요소가 남았는지 확인하는 메소드
			String stst =  it.next().toString();
			DT.add(stst);
		}

		////라벨
		panel = new JPanel(new BorderLayout());
		JLabel select = new JLabel("지역선택 :   ");
		JLabel date = new JLabel("날짜 : ");
		JLabel def = new JLabel("확진자수 : ");
		JLabel death = new JLabel("사망자수 : ");
		JLabel clear= new JLabel("격리해제 수: ");
		JLabel reg2 = new JLabel("지 역 : ");
				
		//textfield의 크기 조절
		defcnt.setPreferredSize(new Dimension(60,30));
		clearcnt.setPreferredSize(new Dimension(60,30));
		this.death.setPreferredSize(new Dimension(60,30));
		
		try {
			BufferedImage bi = ImageIO.read(new File("./지도.png"));
			BufferedImage bi2 = ImageIO.read(new File("./마스크.png"));	
			img = new JLabel(new ImageIcon(bi)); //지도
			img2 = new JLabel(new ImageIcon(bi2));
		}catch(IOException e){
			e.getStackTrace();
		}
		
		setSize(600,600);
		
		
		def.setPreferredSize(new Dimension(60,30));
		death.setPreferredSize(new Dimension(60,30));
		
		aboard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("국외")) {
					try {dataoutstream.writeUTF("국외");
					} catch (IOException e1) {e1.printStackTrace();}
					add.ContryFrame();
					}
			}});
		
		hos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("검사 병원")) {
					try {dataoutstream.writeUTF("검사 병원");
					} catch (IOException e1) {e1.printStackTrace();}
					add.hospital(sido);
					}
			}});
		
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(e.getActionCommand().equals("종료")) { 
					try {dataoutstream.writeUTF("종료");
					} catch (IOException e1) {e1.printStackTrace();}
					dispose();}
			}});
		
		
		homeMenu.add(aboard);
		homeMenu.addSeparator();
		homeMenu.add(hos);
		homeMenu.addSeparator();
		homeMenu.add(exit);
		mb.add(homeMenu);
		kreg = new JComboBox(region);
		this.date = new JComboBox(DT);
		
		kreg.addActionListener(this);
		this.date.addActionListener(this);
		westPanel.setPreferredSize(new Dimension(160,panel.getHeight()));
		westPanel.setBackground(Color.LIGHT_GRAY);
		panel.add(mb,BorderLayout.NORTH);
		
		westPanel.add(select);
		westPanel.add(kreg);
		westPanel.add(date);
		westPanel.add(this.date);
		westPanel.add(def);
		westPanel.add(defcnt);
		westPanel.add(death);
		westPanel.add(this.death);
		westPanel.add(clear);
		westPanel.add(this.clearcnt);
		westPanel.add(img2);

		
		panel.add(img,BorderLayout.CENTER);
		panel.add(westPanel,BorderLayout.WEST);
		
		setContentPane(panel);
		setVisible(true);
		setTitle("코로나 현황 확인 프로그램");
		
		Dimension size1 = this.getSize();
		Dimension size2 = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((size2.width - size1.width) / 2 , (size2.height - size1.height) / 2);
	}
	


	@Override
	public void actionPerformed(ActionEvent e) { //콤보박스 이벤트 넣기
		JComboBox cb = (JComboBox) e.getSource();
		String select = (String) cb.getSelectedItem();
		
		count2++;
		if(select.length() > 2) {
			Dt = select;
		
		}else {
			sido = select;
		}
		
		if(count2 == 2) {
			sidoNdate = "/"+sido+"/"+Dt;
			String[] info = db.defInfoQuery(sidoNdate, "국내").split(" ");
			defcnt.setText(info[0]);
			clearcnt.setText(info[2]);
			death.setText(info[1]);
			sidoNdate = "";
		}else if(count2 > 2) {
			sidoNdate = "/"+sido+"/"+Dt;
			String[] info = db.defInfoQuery(sidoNdate, "국내").split(" ");
			defcnt.setText(info[0]);
			clearcnt.setText(info[2]);
			death.setText(info[1]);
			sidoNdate = "";
		}
		
	}
}

