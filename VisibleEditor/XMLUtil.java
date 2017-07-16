import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class XMLUtil{
	public static ArrayList<Element> cases= new ArrayList<Element>();
	public static final int MAX_LENGTH = 500;
	public static NodeList list;
	
	public static void loadXML(File file) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(file);
		list = document.getElementsByTagName("block");
		System.out.println("list length: " + list.getLength());
		outputElement(list); //Load XML
	}
	
	public static void AutoEscape(VBlock target){
		String escape = target.code;
		String escape1 = null;
		try{
			escape1 = getNormalEscape(target);
		}catch(Exception e){
			
		}
		if(escape1 != null){
			escape = escape1;
		}
		
		//Speciel Escape
		
		String escape2 = null;
		try{
			escape2 = getSpecielEscape(target);
		}catch(Exception e){
			
		}
		if(escape2 != null){
			escape = escape2;
		}
		if(escape == target.code){
			EscapeThread et = new EscapeThread(target);
		}else{
			target.escape = escape;
			target.AutoSize(escape);
			target.Refresh();
		}
	}
	
	public static String getSpecielEscape(VBlock target){ // Get Escaped
		casing:for (int i = 0; i < cases.size(); i++)
		{
			if (target.code.replace(cases.get(i).getAttribute("name"), "") != target.code)
			{
				String[] points = cases.get(i).getElementsByTagName("case").item(0) .getFirstChild().getNodeValue().split("\\+");
				VBlock Addons = target.Addons;
				for (int z = 0;z < points.length; z ++)
				{
					String point = points[z];
					boolean proceed = false;
					switch (point.trim())  // The Requirements checking
					{
						case "%word%":
							if (!isWordCharacter(Addons.code.charAt(0)))
							{
								continue casing;
							}
							proceed = true;
							break;
						case "%class%":
							if (!isWordCharacter(Addons.code.charAt(0)))
							{
								continue casing;
							}
							proceed = true;
							break;
						case "%word...%":
							if (!isWordCharacter(Addons.code.charAt(0)))
							{
								continue casing;
							}
							try{
								while(isWordCharacter(Addons.Addons.code.charAt(0))){
									Addons = Addons.Addons;
								}
							}catch(Exception e){
								continue casing;
							}
							proceed = true;
							break;
					}
					if (!proceed)
					{
						if (Addons.code.replace(point.trim(), "").length() == Addons.code.length())
						{
							continue casing;
						}
					}
					Addons = Addons.Addons;
				}
				return cases.get(i).getElementsByTagName("escaped").item(0) .getFirstChild().getNodeValue().trim();
			}
		}
		return null;
	}
	
	public static String getNormalEscape(VBlock target){
		for (int i = 0; i < list.getLength(); ++i)
		{ 
			Element element = (Element) list.item(i); // Normal Escaped
			try
			{
				String content = element.getAttribute("name");
				try
				{
					String casez = element.getElementsByTagName("case").item(0) .getFirstChild().getNodeValue();
				}
				catch (Exception e)
				{
					if (target.code.replace(content, "") != target.code)
					{
						return element.getElementsByTagName("escaped").item(0) .getFirstChild().getNodeValue().trim();
					}
				}
				
			}
			catch (Exception e)
			{

			}
		}
		return null;
	}
	
	public static boolean isWordCharacter(char ch)
	{
		if (Character.isLetter(ch) || Character.isDigit(ch))
		{
			return true;
		}
		return false;
	}

	public static void outputElement(NodeList list){
		for (int i = 0; i < list.getLength(); ++i){ 
			System.out.println("----------------------"); 
			Element element = (Element) list.item(i); 
			try{
				String content = element.getAttribute("name");
				System.out.println("name: " + content);
			}
			catch (Exception e){

			}
			try
			{
				String content = element.getElementsByTagName("escaped").item(0) .getFirstChild().getNodeValue();
				System.out.println("escaped: " + content);
			}
			catch (Exception e)
			{

			}
			try
			{
				String content = element.getElementsByTagName("type").item(0) .getFirstChild().getNodeValue();
				System.out.println("type: " + content);
			}
			catch (Exception e)
			{

			}
			try
			{
				String content = element.getElementsByTagName("case").item(0) .getFirstChild().getNodeValue();
				cases.add(element);
				System.out.println("case: " + content);
			}
			catch (Exception e)
			{

			}
		}
	}
}
