import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;


public class APIexplorer extends Thread{
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
	String date = null;
	DBmanager db = new DBmanager(); // 데이터 저장하기 위해서
	StringBuilder url = new StringBuilder();
	String str= null;
	

	private void getURLparam(String URL,String tag) throws Exception{ //코로나19 정보를 파싱하는 메소드, tag - 코로나 정보를 시도명/전체/국가로 나눠서 파싱

			str = "";
			date = simpleDateFormat.format(new Date());
			
			try {
				url = new StringBuilder(URL);
				url.append("?" + URLEncoder.encode("ServiceKey","UTF-8") +"="+"service key"); //Service Key
				url.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //페이지번호
				url.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); //한 페이지 결과 수
				if(tag.equals("kreg")) {
					url.append("&" + URLEncoder.encode("startCreateDt","UTF-8") + "=" + URLEncoder.encode("20201201", "UTF-8")); //검색할 생성일 범위의 시작
				}else {
					url.append("&" + URLEncoder.encode("startCreateDt","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); //검색할 생성일 범위의 시작
				}
				url.append("&" + URLEncoder.encode("endCreateDt","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); //검색할 생성일 범위의 종료
				System.out.println(url.toString());
				DocumentBuilderFactory docfac = DocumentBuilderFactory.newInstance(); // xml파일로부터 dom 오브젝트 트리를 생성하는 parser를 얻을 수 있도록 하는 api제공
				DocumentBuilder docbuild = docfac.newDocumentBuilder(); // url에서 정보를 파싱 /xml이 존재하는 url
				Document doc = docbuild.parse(url.toString()); // 문자열 url 대입
				doc.getDocumentElement().normalize(); // dom tree가 xml의 구조대로 완성
	        
				NodeList nlist = doc.getElementsByTagName("item"); //노드리스트에 itemtag안에 있는 값들을 저장 예) <careCnt> 3000 , careCnt라는 태그 이름을 노드 이름으로 하고 값은 3000으로 저장
				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				System.out.println("파싱할 리스트 수 : "+ nlist.getLength());
				
				
				if(tag.equals("kreg")) { //지역코로나 현황
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
							
							// 데이터 베이스 정보넘기기
							db.insertData(str, "name");
							db.insertData(str, "reg");
//							System.out.println(str);
							str ="";
							}
						}
		
				}else if(tag.equals("other")) { //국가별코로나 현황
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
							// 데이터 베이스 정보넘기기
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
	
	private void hosgetURLparam() throws Exception{ // 병원 정보를 파싱하는 메소드
		String str = "";
		
		try {
			int page = 1;
			
			while(true) {
				String page1 = Integer.toString(page);
				StringBuilder url = new StringBuilder("http://apis.data.go.kr/B551182/pubReliefHospService/getpubReliefHospList");
				url.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "service key"); /*Service Key*/
		        url.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(page1, "UTF-8")); /*페이지번호*/
		        url.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
		        url.append("&" + URLEncoder.encode("spclAdmTyCd","UTF-8") + "=" + URLEncoder.encode("97", "UTF-8")); /*A0: 국민안심병원/97: 코로나검사 실시기관/99: 코로나 선별*/
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
							// 데이터 베이스 정보넘기기
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
	
	private void uploadingData(String URL,String tag,String date) throws Exception{ // 데이터를 업데이트하기 위한 메소드 , tag -> reg(지역), con(국가)
		str = "";
		
		try {
			url = new StringBuilder(URL);
			url.append("?" + URLEncoder.encode("ServiceKey","UTF-8") +"="+"service key"); //Service Key
			url.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //페이지번호
			url.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); //한 페이지 결과 수
			url.append("&" + URLEncoder.encode("startCreateDt","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); //검색할 생성일 범위의 시작
			url.append("&" + URLEncoder.encode("endCreateDt","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); //검색할 생성일 범위의 종료
			System.out.println(url.toString());
			DocumentBuilderFactory docfac = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder docbuild = docfac.newDocumentBuilder();
			Document doc = docbuild.parse(url.toString()); 
			doc.getDocumentElement().normalize(); // dom tree가 xml의 구조대로 완성

        
			NodeList nlist = doc.getElementsByTagName("item");
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			System.out.println("파싱할 리스트 수 : "+ nlist.getLength());
        
			if(tag.equals("kreg")) { //지역코로나 현황
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
						// 데이터 베이스 정보넘기기
						db.insertData(str, "reg");
						System.out.println(str);
						str ="";
						}
					}
	
			}else if(tag.equals("other")) { //국가별코로나 현황
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
						// 데이터 베이스 정보넘기기
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
	
	private String getTagValue(String tag, Element element){ //태그값을 검색하는 메소드
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
			getURLparam("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19SidoInfStateJson","kreg"); //시도별 코로나19정보 api
	    	getURLparam("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19NatInfStateJson","other");// 국가별 코로나 19정보 api
			hosgetURLparam(); //병원정보
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		while(true) {//업데이트 필드에 선언된 날짜와 시간이 다르다면 데이터를 sql에 저장
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
			String date2 = simpleDateFormat.format(new Date());
			if(count == 0) {
				date = simpleDateFormat.format(new Date());
				count++;
			}
			
			tomorrow = Long.parseLong(date2); //문자열을 숫자로 변환 -> 비교하기 위해서
			today = Long.parseLong(date);
			
			if(today < tomorrow){ //만약에 date2가 date보다 커질때 업데이트를 시켜줌
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
		
