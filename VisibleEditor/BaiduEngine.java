import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class BaiduEngine{
	
	public static String BaiduTranslate(String text,String target){
		String sr = sendGet("http://fanyi.baidu.com/v2transapi","from=en&to="+target+"&query="+text.replaceAll(" ","+")+"&transtype=translang&simple_means_flag=3");
		String result = sr.split("\"dst\":\"")[1].split("\"")[0];
		if(convert(result).length() < 1){
			return text;
		}
		return convert(result);
	}
	
	public static String convert(String utfString){  
	   StringBuilder sb = new StringBuilder();  
	   int i = -1;  
	   int pos = 0;  
	     
	   while((i=utfString.indexOf("\\u", pos)) != -1){  
	   
	       sb.append(utfString.substring(pos, i));  
	       if(i+5 < utfString.length()){  
	           pos = i+6;  
	           sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));  
	       }  
	   }  
	     
	   return sb.toString();  
	} 
	
	 public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
             
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
	
}