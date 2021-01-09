import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class APIexplorer extends Thread{
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
	String date = null;
	DBmanager db = new DBmanager(); // �뜲�씠�꽣 ���옣�븯湲� �쐞�빐�꽌asd
	StringBuilder url = new StringBuilder();
	String str= null;
	

	private void getURLparam(String URL,String tag) throws Exception{ //肄붾줈�굹19 �젙蹂대�� �뙆�떛�븯�뒗 硫붿냼�뱶, tag - 肄붾줈�굹 �젙蹂대�� �떆�룄紐�/�쟾泥�/援�媛�濡� �굹�닠�꽌 �뙆�떛

			str = "";
			date = simpleDateFormat.format(new Date());
			
			try {
				url = new StringBuilder(URL);
				url.append("?" + URLEncoder.encode("ServiceKey","UTF-8") +"="+"api service key"); //Service Key
				url.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //�럹�씠吏�踰덊샇
				url.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); //�븳 �럹�씠吏� 寃곌낵 �닔
				if(tag.equals("kreg")) {
					url.append("&" + URLEncoder.encode("startCreateDt","UTF-8") + "=" + URLEncoder.encode("20201201", "UTF-8")); //寃��깋�븷 �깮�꽦�씪 踰붿쐞�쓽 �떆�옉
				}else {
					url.append("&" + URLEncoder.encode("startCreateDt","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); //寃��깋�븷 �깮�꽦�씪 踰붿쐞�쓽 �떆�옉
				}
				url.append("&" + URLEncoder.encode("endCreateDt","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); //寃��깋�븷 �깮�꽦�씪 踰붿쐞�쓽 醫낅즺
				System.out.println(url.toString());
				DocumentBuilderFactory docfac = DocumentBuilderFactory.newInstance(); // xml�뙆�씪濡쒕��꽣 dom �삤釉뚯젥�듃 �듃由щ�� �깮�꽦�븯�뒗 parser瑜� �뼸�쓣 �닔 �엳�룄濡� �븯�뒗 api�젣怨�
				DocumentBuilder docbuild = docfac.newDocumentBuilder(); // url�뿉�꽌 �젙蹂대�� �뙆�떛 /xml�씠 議댁옱�븯�뒗 url
				Document doc = docbuild.parse(url.toString()); // 臾몄옄�뿴 url ���엯
				doc.getDocumentElement().normalize(); // dom tree媛� xml�쓽 援ъ“��濡� �셿�꽦
	        
				NodeList nlist = doc.getElementsByTagName("item"); //�끂�뱶由ъ뒪�듃�뿉 itemtag�븞�뿉 �엳�뒗 媛믩뱾�쓣 ���옣 �삁) <careCnt> 3000 , careCnt�씪�뒗 �깭洹� �씠由꾩쓣 �끂�뱶 �씠由꾩쑝濡� �븯怨� 媛믪� 3000�쑝濡� ���옣
				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				System.out.println("�뙆�떛�븷 由ъ뒪�듃 �닔 : "+ nlist.getLength());
				
				
				if(tag.equals("kreg")) { //吏��뿭肄붾줈�굹 �쁽�솴
					for(int i = 0; i < nlist.getLength();i++) {
						Node nNode = nlist.item(i);
						Element eElement = (Element) nNode;
						if(nNode.getNodeType() == Node.ELEMENT_NODE){
							str += Integer.parseInt(getTagValue("defCnt", eElement).toString());
							str += "/"+getTagValue("deathCnt", eElement).toString();
							str += "/"+getTagValue("isolClearCnt", eElement).toString();
							String[] dt = getTagValue("createDt", eElement).toString().split(" ");
							str += "/"+dt[0];
							str += "/"+getTagValue("gubun", eElement).toString();
							
							// �뜲�씠�꽣 踰좎씠�뒪 �젙蹂대꽆湲곌린
							db.insertData(str, "name");
							db.insertData(str, "reg");
//							System.out.println(str);
							str ="";
							}
						}
		
				}else if(tag.equals("other")) { //援�媛�蹂꾩퐫濡쒕굹 �쁽�솴
					for(int i = 0; i < nlist.getLength();i++) {
						Node nNode = nlist.item(i);
						Element eElement = (Element) nNode;
						if(nNode.getNodeType() == Node.ELEMENT_NODE){
							str += getTagValue("natDefCnt", eElement).toString();
							str += "/"+getTagValue("stdDay", eElement).toString();
							str += "/"+getTagValue("nationNm", eElement).toString();
							str += "/"+getTagValue("natDeathCnt", eElement).toString();
							str += "/"+getTagValue("natDeathRate", eElement).toString();
							str += "/"+getTagValue("areaNm",eElement).toString();
							// �뜲�씠�꽣 踰좎씠�뒪 �젙蹂대꽆湲곌린
							db.insertData(str, "con");
//							System.out.println(str);
							str ="";
				}
			
		}	
	        	
	        }// for end
			}catch(Exception e) {
				e.printStackTrace();
			}	
		
	}
	
	private void hosgetURLparam() throws Exception{ // 蹂묒썝 �젙蹂대�� �뙆�떛�븯�뒗 硫붿냼�뱶
		String str = "";
		
		try {
			int page = 1;
			
			while(true) {
				String page1 = Integer.toString(page);
				StringBuilder url = new StringBuilder("http://apis.data.go.kr/B551182/pubReliefHospService/getpubReliefHospList");
				url.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "api service key"); /*Service Key*/
		        url.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(page1, "UTF-8")); /*�럹�씠吏�踰덊샇*/
		        url.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*�븳 �럹�씠吏� 寃곌낵 �닔*/
		        url.append("&" + URLEncoder.encode("spclAdmTyCd","UTF-8") + "=" + URLEncoder.encode("97", "UTF-8")); /*A0: 援�誘쇱븞�떖蹂묒썝/97: 肄붾줈�굹寃��궗 �떎�떆湲곌�/99: 肄붾줈�굹 �꽑蹂�*/
				DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
				Document doc = dBuilder.parse(url.toString());
				doc.getDocumentElement().normalize();
//				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        
				NodeList nlist = doc.getElementsByTagName("item"); 
        
				for(int i = 0; i < nlist.getLength();i++) {
					Node nNode = nlist.item(i);
					if(nNode.getNodeType() == Node.ELEMENT_NODE){
				
						Element eElement = (Element) nNode;
						str += getTagValue("sidoNm", eElement).toString();
						str += "/"+getTagValue("sgguNm", eElement).toString();
						str += "/"+getTagValue("yadmNm", eElement).toString();
						str += "/"+getTagValue("telno", eElement).toString();
						str += "/"+getTagValue("spclAdmTyCd",eElement).toString();
							// �뜲�씠�꽣 踰좎씠�뒪 �젙蹂대꽆湲곌린
						db.insertData(str, "hos");
//						System.out.println(str);
						str ="";
					   }
					}
				page += 1;
				if(page > 11){	
					break;
				}
			
			}
				
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void uploadingData(String URL,String tag,String date) throws Exception{ // �뜲�씠�꽣瑜� �뾽�뜲�씠�듃�븯湲� �쐞�븳 硫붿냼�뱶 , tag -> reg(吏��뿭), con(援�媛�)
		str = "";
		
		try {
			url = new StringBuilder(URL);
			url.append("?" + URLEncoder.encode("ServiceKey","UTF-8") +"="+"api service key"); //Service Key
			url.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //�럹�씠吏�踰덊샇
			url.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); //�븳 �럹�씠吏� 寃곌낵 �닔
			url.append("&" + URLEncoder.encode("startCreateDt","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); //寃��깋�븷 �깮�꽦�씪 踰붿쐞�쓽 �떆�옉
			url.append("&" + URLEncoder.encode("endCreateDt","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); //寃��깋�븷 �깮�꽦�씪 踰붿쐞�쓽 醫낅즺
			System.out.println(url.toString());
			DocumentBuilderFactory docfac = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder docbuild = docfac.newDocumentBuilder();
			Document doc = docbuild.parse(url.toString()); 
			doc.getDocumentElement().normalize(); // dom tree媛� xml�쓽 援ъ“��濡� �셿�꽦

        
			NodeList nlist = doc.getElementsByTagName("item");
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			System.out.println("�뙆�떛�븷 由ъ뒪�듃 �닔 : "+ nlist.getLength());
        
			if(tag.equals("kreg")) { //吏��뿭肄붾줈�굹 �쁽�솴
				for(int i = 0; i < nlist.getLength();i++) {
					Node nNode = nlist.item(i);
					Element eElement = (Element) nNode;
					if(nNode.getNodeType() == Node.ELEMENT_NODE){
						str += Integer.parseInt(getTagValue("decideCnt", eElement).toString());
						str += "/"+getTagValue("deathCnt", eElement).toString();
						str += "/"+getTagValue("clearCnt", eElement).toString();
						str += "/"+getTagValue("createDt", eElement).toString();
						str += "/"+getTagValue("gubun", eElement).toString();
						str += "/"+getTagValue("accExamCnt",eElement).toString();
						str += "/"+getTagValue("careCnt", eElement).toString();
						// �뜲�씠�꽣 踰좎씠�뒪 �젙蹂대꽆湲곌린
						db.insertData(str, "reg");
						System.out.println(str);
						str ="";
						}
					}
	
			}else if(tag.equals("other")) { //援�媛�蹂꾩퐫濡쒕굹 �쁽�솴
				for(int i = 0; i < nlist.getLength();i++) {
					Node nNode = nlist.item(i);
					Element eElement = (Element) nNode;
					if(nNode.getNodeType() == Node.ELEMENT_NODE){
						str += getTagValue("natDefCnt", eElement).toString();
						str += "/"+getTagValue("stdDay", eElement).toString();
						str += "/"+getTagValue("nationNm", eElement).toString();
						str += "/"+getTagValue("natDeathCnt", eElement).toString();
						str += "/"+getTagValue("natDeathRate", eElement).toString();
						str += "/"+getTagValue("areaNm",eElement).toString();
						// �뜲�씠�꽣 踰좎씠�뒪 �젙蹂대꽆湲곌린
						db.insertData(str, "con");
						System.out.println(str);
						str ="";
			}
		
	}	
        	
        }// for end
		}catch(Exception e) {
			e.printStackTrace();
		}	
	
	}
	
	private String getTagValue(String tag, Element element){ //�깭洹멸컪�쓣 寃��깋�븯�뒗 硫붿냼�뱶
		NodeList nlist = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node nvalue = (Node) nlist.item(0);
		if(nvalue == null) 
	        return null;
	    return nvalue.getNodeValue();
	}
	
	public void run() {
		int count = 0;
		String date = null;
		long today,tomorrow;
		
		try {
			getURLparam("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19SidoInfStateJson","kreg"); //�떆�룄蹂� 肄붾줈�굹19�젙蹂� api
	    	getURLparam("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19NatInfStateJson","other");// 援�媛�蹂� 肄붾줈�굹 19�젙蹂� api
			hosgetURLparam(); //蹂묒썝�젙蹂�
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		while(true) {//�뾽�뜲�씠�듃 �븘�뱶�뿉 �꽑�뼵�맂 �궇吏쒖� �떆媛꾩씠 �떎瑜대떎硫� �뜲�씠�꽣瑜� sql�뿉 ���옣
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
			String date2 = simpleDateFormat.format(new Date());
			if(count == 0) {
				date = simpleDateFormat.format(new Date());
				count++;
			}
			
			tomorrow = Long.parseLong(date2); //臾몄옄�뿴�쓣 �닽�옄濡� 蹂��솚 -> 鍮꾧탳�븯湲� �쐞�빐�꽌
			today = Long.parseLong(date);
			
			if(today < tomorrow){ //留뚯빟�뿉 date2媛� date蹂대떎 而ㅼ쭏�븣 �뾽�뜲�씠�듃瑜� �떆耳쒖쨲
				try {
					uploadingData("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19SidoInfStateJson","kreg",date2);
					uploadingData("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19NatInfStateJson","other",date2);
				}catch(Exception e) {
					e.getStackTrace();
				}
				count=0;
			}
		}
		
	}
}
		
