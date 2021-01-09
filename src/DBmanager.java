import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DBmanager {
	Connection cnt = null;
	Statement stmt = null;
	private String user = "root";
	private String pw = "pw";
	private String url = "jdbc:mysql://localhost/Covid19?serverTimezone=Asia/Seoul";
	
	
	void createTable() { // 吏��뿭, 蹂묒썝, 援�媛� �뀒�씠釉� �깮�꽦 硫붿냼�뱶
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			cnt = DriverManager.getConnection(url,user,pw);
			stmt = cnt.createStatement();
			
			String str1 = "CREATE TABLE region ("
					+ "decideCnt varchar(10) unique,"
					+ "deathCnt varchar(10),"
					+ "ClearCnt varchar(10),"
					+ "createDt varchar(30) not null,"
					+ "gubun varchar(2) not null,"
					+ "primary key (createDt,gubun))";
			
			String str4 = "CREATE TABLE regionName ("
					+ "Name varchar(10) unique primary key);";
			
			String str2 = "CREATE TABLE hospital ("
					+ "sidoNm varchar(2),"
					+ "sgguNm varchar(10) not null,"
					+ "yadmNm varchar(50) not null,"
					+ "telNo varchar(20),"
					+ "spciAdmTyCd varchar(10),"
					+ "primary key(sgguNm,yadmNm))";
			
			String str3 = "CREATE TABLE contry ("
					+ "natDefCnt varchar(10),"
					+ "stdDay varchar(20),"
					+ "nationNm varchar(30),"
					+ "natDeathCnt varchar(100),"
					+ "natDeathRate varchar(100),"
					+ "areaNm varchar(10),"
					+ "primary key(nationNm,stdDay))";
			stmt.executeUpdate(str3);
			stmt.executeUpdate(str4);
			stmt.executeUpdate(str1);
			stmt.executeUpdate(str2);
			
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	String Query(String tag) { // 援��궡 吏��뿭�씠由�,援�媛��씠由�,�궇吏�,蹂묒썝�젙蹂� 由ы꽩�븯�뒗 硫붿냼�뱶
		String str = "";
		String date = null;
		
		try {
			cnt = DriverManager.getConnection(url,user,pw);
			stmt = cnt.createStatement();
			if(tag.equals("�궇吏�")) {
				String query = "SELECT createDt FROM region";
			
				ResultSet res = stmt.executeQuery(query);
				int i = 0;
				
				while(res.next()) {
					if(i==0) {
						date = res.getString("createDt");
						System.out.println(date+"!!!!!!!!");
					}
                    String dd = res.getString("createDt");
					
					if(!date.equals(dd)) {
						date = dd;
						str += res.getString("createDt")+" ";
					}
					i++;
				}
				
			}else if(tag.equals("吏��뿭")){
				String query = "SELECT * FROM regionName";
				
				ResultSet res = stmt.executeQuery(query);
			
				while(res.next()) {
					str += res.getString("Name")+" ";
				}
			}else if(tag.equals("援�媛�")){
				String query = "SELECT nationNm FROM contry";
				
				ResultSet res = stmt.executeQuery(query);
			
				while(res.next()) {
					str += res.getString("nationNm")+" ";
				}
			}
			else {
				String query = "SELECT * FROM hospital WHERE sidoNm='"
						+ tag+"'";
				
				ResultSet res = stmt.executeQuery(query);
				
				while(res.next()) {
					str += res.getString("sidoNm")+"/";
					str += res.getString("sgguNm")+"/";
					str += res.getString("yadmNm")+"/";
					str += res.getString("telNo")+"\n";
				}
			}
			
		}catch(Exception e) {
			e.getStackTrace();
		}
		
		return str;
	}
	
	void insertData(String s,String tag) { // 30�씪媛꾩쓽 肄붾줈�굹 �젙蹂대�� �궫�엯�빐二쇰뒗 硫붿냼�뱶
		String str = null;
		String[] reginfo = s.split("/"); // �븳以꾨줈 諛쏆� 媛믩뱾�쓣 �걡�뼱以�
		String[] hosinfo = s.split("/");
		String[] coninfo = s.split("/");
		
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			cnt = DriverManager.getConnection(url,user,pw);
			stmt = cnt.createStatement();
			
			if(tag.equals("reg")) { //�깭洹멸컪�뿉 �뵲�씪 ���옣�릺�뒗 媛믪씠 �떎由�
				str = "INSERT INTO region VALUES ("
						+ "'"+reginfo[0]+"','"+ reginfo[1] +"','"+reginfo[2]+"','"
						+reginfo[3]+"','"+reginfo[4]+"')";
			}else if(tag.equals("hos")) {
				str = "INSERT INTO hospital VALUES ("
						+ "'"+hosinfo[0]+"','"+ hosinfo[1] +"','"+hosinfo[2]+"','"
						+hosinfo[3]+"','"+hosinfo[4]+"')";
			}else if(tag.equals("con")) {
				str = "INSERT INTO contry VALUES ("
						+ "'"+coninfo[0]+"','"+ coninfo[1] +"','"+coninfo[2]+"','"
						+coninfo[3]+"','"+coninfo[4]+"','"+coninfo[5]+"')";
			}else {
				str = "INSERT INTO regionName VALUES ("
						+ "'"+reginfo[4]+"')";
			}
			stmt.executeUpdate(str);
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("insert硫붿냼�뱶");
		}
		
		
	}

	String defInfoQuery(String s,String tag) {//肄붾줈�굹�젙蹂대�� 由ы꽩�븯�뒗 硫붿냼�뱶 援��궡/�쇅
		String data[] = s.split("/");
		String query = null;
		String info = "";
		SimpleDateFormat simple = new SimpleDateFormat("yyyy�뀈 MM�썡 dd�씪 09�떆");
		String date = simple.format(new Date());
		System.out.println(data[1]);
		
		try {
			cnt = DriverManager.getConnection(url,user,pw);
			stmt = cnt.createStatement();
			if(tag.equals("援��궡")) {
				query = "SELECT * FROM region WHERE createDt='"
						+ data[2]+"' and gubun='"+data[1]+"'";
				ResultSet res = stmt.executeQuery(query);
				while(res.next()) {
					info += res.getString("decideCnt")+" ";
					info += res.getString("deathCnt")+" ";
					info += res.getString("ClearCnt")+" ";
		    	}
			}else {
				query = "SELECT * FROM contry WHERE nationNm='"+data[1]+"' and stdDay='"+date+"'";
				ResultSet res = stmt.executeQuery(query);
				while(res.next()) {
					info += res.getString("natDefCnt")+" ";
					info += res.getString("natDeathCnt")+" ";
					info += res.getString("natDeathRate")+" ";
		    	}
			}
			
		}catch(Exception e) {
			e.getStackTrace();
			System.out.println("defInfoQuery硫붿냼�뱶 臾몄젣");
		}
		
		return info;
	}
	
}
