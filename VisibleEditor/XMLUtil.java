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
		if(escape != target.code){
			target.escape = escape;
			target.AutoSize();//escape);
			target.Refresh();
		}
		
		// Wrong #2
		EscapeThread et = new EscapeThread(target);
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
					if (DetectMatches(target,content,true))
					{
						return element.getElementsByTagName("escaped").item(0).getFirstChild().getNodeValue().trim();
					}
				}
				
			}
			catch (Exception e)
			{
				
			}
		}
		return null;
	}

	public static boolean DetectMatches(VBlock b,String content,boolean remove){
		VBlock son = b;
		
		if(b.code.replace(content,"") != b.code){
			return true;
		}
		
		String[] matches = content.split(" ");
		
		if(matches.length == 1){ // Skip the Speciel Detection
			if(son.code.replace(content, "") != son.code){
				return true;
			}else{
				return false;
			}
		}
		
		ArrayList<VBlock> nd = new ArrayList<VBlock>();
		
		casing:for(int i = 0; i<matches.length; i++){
			String match = matches[i];
			// Override at 7/31/2017 10:09 PM
			
			boolean done = false;
			
			while(!done){
				done = true;
				
				String[] codes = son.code.split(" ");
			
				if(codes.length > 1){
					// Wrong caused in here. Those code don't split the () out of the word
					codes = fromCodeStack(son.code);
				}
				
				for(int j = 0; j<codes.length; j++){
					String code = codes[j];
					
					boolean proceed = false;
					
					switch(match){
						case "%word%":
							if (!isWordCharacter(code.charAt(0))){
								return false;
							}
							proceed = true;
							break;
						case "%class%":
							if (!isWordCharacter(code.charAt(0))){
								return false;
							}
							proceed = true;
							break;
						case "%word...%":
						if (!isWordCharacter(code.charAt(0))){
							return false;
						}
						try{
							while(isWordCharacter(codes[j].charAt(0))){
								if(j<codes.length){
									j++;
								}else{
									done = false;
								}
								
							}
						}catch(Exception e){
							return false;
						}
						proceed = true;
						break;
					}
					
					// Normal Matches
					
					if(!proceed){
						if(code.replace(match,"") == code){
							return false;
						}
					}
					
					// End
					
					if(j == codes.length - 1 && done){
						if(son != b){
							nd.add(son);
						}
						if(son.Addons != null){
							son = son.Addons;
						}else{
							return false;
						}
						
					}
					
				}
			}
		}
		
		if(remove){
			String s = "";
			for(VBlock needdelete:nd.toArray(new VBlock[0])){
				//Combind the Code
				s = s + " " + needdelete.code;
				
				//Remove the Code
				if(!b.Model){
					b.Addons = needdelete.Addons;
					if(needdelete.Addons != null){
						needdelete.Addons.Parent = b;
					}
					VisibleEditor.mainFrame.unregisterBlock(needdelete);
				}else{
					b.Addons = needdelete.Addons;
					if(needdelete.Addons != null){
						needdelete.Addons.Parent = b;
					}
					needdelete.ModelGroup.remove(needdelete);
				}
			}
			if(!b.Model){
				VisibleEditor.mainFrame.repaint();
			}else{
				b.ModelGroup.repaint();
			}
			 // Wrong #4 Didn't fixed yet
			if(s.trim() == ""){
				return false;
			}
			b.code = s.trim(); // Replace in 8/1/2017 5:12PM
			b.Refresh();
		}
		
		return true;
	}
	
	public static boolean isWordCharacter(char ch)
	{
		if (Character.isLetter(ch) || Character.isDigit(ch))
		{
			return true;
		}
		return false;
	}
	
	public static String[] fromCodeStack(String code){
		String[] codef = code.split("|");
		ArrayList<String> codechunk = new ArrayList<String>();
		String combind = "";
		
		for(String flake:codef){
			if(flake.charAt(0) != ' '){
				if(VFrame.isWordCharacter(flake.charAt(0))){
					combind = combind+flake;
				}else{
					if(combind != ""){
						// This happened like this : XX();
						codechunk.add(combind);
						combind = "";
					}
					
					codechunk.add(flake);
					
				}
			}else{
				codechunk.add(combind);
				combind = "";
			}
		}
		
		return codechunk.toArray(new String[0]);
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
