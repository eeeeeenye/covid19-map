import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class additionalFrame extends JFrame implements ActionListener{
	JPanel panel = new JPanel();
	Vector<String> otherRegion = new Vector<String>();
	
	JLabel img = null;
	JComboBox con;
	JTextField def = new JTextField();
	JTextField death = new JTextField();
	JTextField deathRate = new JTextField();
	
	JTable table; //보여주기용
	DefaultTableModel tableModel; 
	String columNames[] = {"지역","상세","병원이름","전화번호"};
	String[][] hosinfo = null;
	
	DBmanager db = new DBmanager();
	private String[] contry = db.Query("국가").split(" ");
	
	int count = 0;
			
	void hospital(String sido) { //각 도마다 안심병원을 테이블을 통해 보여줌
		panel = new JPanel(new BorderLayout());
		
		String[] hosInfo = db.Query(sido).split("\n");
		hosinfo = new String[hosInfo.length][];
		int r = 0;
		for (String row : hosInfo) {
		hosinfo[r++] = row.split("/");
		}
		
		setTitle("코로나 검사 병원 목록");
		setSize(1000,400);
		tableModel = new DefaultTableModel(hosinfo, columNames);
		table = new JTable(tableModel);
		JScrollPane sp = new JScrollPane(table);
		panel.add(sp,BorderLayout.CENTER);
		setContentPane(panel);
		setVisible(true);
		
		Dimension size1 = this.getSize();
		Dimension size2 = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((size2.width - size1.width) / 2 , (size2.height - size1.height) / 2);
		
	}
	
   void ContryFrame() {//메뉴에서 
		panel = new JPanel(new BorderLayout());
		JPanel eastPanel = new JPanel(new FlowLayout());
		JLabel nation = new JLabel("국가명 : ");
		JLabel def = new JLabel("총 확진자 : ");
		JLabel death = new JLabel("총 사망자 : ");
		JLabel deathRate = new JLabel("사망률 :");
		
		
		try {
			BufferedImage bi = ImageIO.read(new File("./세계지도.png"));
			img = new JLabel(new ImageIcon(bi));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		this.def.setPreferredSize(new Dimension(70,30));
		this.death.setPreferredSize(new Dimension(70,30));
		this.deathRate.setPreferredSize(new Dimension(90,30));
		
		setSize(1100,500);
		setTitle("해외 코로나 현황");
		
		for(int i = 0; i < contry.length;i++) {
			otherRegion.add(contry[i]);
		}
	
		con = new JComboBox(otherRegion);
		eastPanel.setPreferredSize(new Dimension(160,panel.getHeight()));
		eastPanel.setBackground(Color.LIGHT_GRAY);
		eastPanel.add(nation);
		eastPanel.add(con);
		eastPanel.add(def);
		eastPanel.add(this.def);
		eastPanel.add(death);
		eastPanel.add(this.death);
		eastPanel.add(deathRate);
		eastPanel.add(this.deathRate);
		panel.add(img,BorderLayout.CENTER);
		panel.add(eastPanel,BorderLayout.EAST);
		con.addActionListener(this);
		
		setContentPane(panel);
		
		setVisible(true);
		
		Dimension size = this.getSize();
		Dimension size2 = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((size2.width - size.width)/2,(size2.height - size.height)/2);
	}

	@Override
	public void actionPerformed(ActionEvent e) { //콤보박스이벤트
		JComboBox jbox = (JComboBox)e.getSource();
		String name = (String)jbox.getSelectedItem();
		
		String info[] = db.defInfoQuery("/"+name, "해외").split(" ");
		def.setText(info[0]);	
		death.setText(info[1]);
		deathRate.setText(info[2]);
		
	}

}
