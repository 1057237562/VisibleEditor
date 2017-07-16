import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.URLConnection;

public class BingEngine{
	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.setLayout(null);
		jf.setSize(500,500);
		jf.setVisible(true);
		JTextPane jtp = new JTextPane();
		jtp.setBounds(0,0,500,400);
		JButton jb = new JButton("Translate");
		jb.setBounds(0,400,500,50);
		jf.add(jtp);
		jf.add(jb);
		jf.repaint();
		jb.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				String appid = sendGet("http://www.microsofttranslator.com/ajax/v3/WidgetV3.ashx?siteData=ueOIGRSKkd965FeEGM5JtQ**&ctf=False&ui=true&settings=Manual&from=").split("appId:\'")[1].split("x2a\',")[0];
				
				appid = appid.replaceAll("\\\\","");
				//System.out.println("http://api.microsofttranslator.com/v2/ajax.svc/TranslateArray?appId=\""+appid+"*\"&texts=[%22"+jtp.getText().replaceAll(" ","+")+"%22]&from=%22en%22&to=%22zh-CHS%22&oncomplete=_mstc4&onerror=_mste4&loc=en&ctr=&ref=WidgetV3&rgp=2e5a2f57");
				String sr = sendGet("http://api.microsofttranslator.com/v2/ajax.svc/TranslateArray?appId=\""+appid+"*\"&texts=[%22"+jtp.getText().replaceAll(" ","+")+"%22]&from=%22en%22&to=%22zh-CHS%22&oncomplete=_mstc4&onerror=_mste4&loc=en&ctr=&ref=WidgetV3&rgp=2e5a2f57");
				try{
				sr = sr.split("\"TranslatedText\":\"")[1].split("\"")[0];
				System.out.println(sr);
				
				}catch(Exception z){
					z.printStackTrace();
				}
			}
		});
	}
	
	public static String BingTranslate(String text,String target){
		String appid = sendGet("http://www.microsofttranslator.com/ajax/v3/WidgetV3.ashx?siteData=ueOIGRSKkd965FeEGM5JtQ**&ctf=False&ui=true&settings=Manual&from=").split("appId:\'")[1].split("x2a\',")[0];
				
		appid = appid.replaceAll("\\\\","");
		String sr = sendGet("http://api.microsofttranslator.com/v2/ajax.svc/TranslateArray?appId=\""+appid+"*\"&texts=[%22"+text.replaceAll(" ","+")+"%22]&from=%22en%22&to=%22"+target+"%22&oncomplete=_mstc4&onerror=_mste4&loc=en&ctr=&ref=WidgetV3&rgp=2e5a2f57");
		sr = sr.split("\"TranslatedText\":\"")[1].split("\"")[0];
		return sr;
	}
	
	private static char[] getChars (byte[] bytes) {
      Charset cs = Charset.forName ("UTF-8");
      ByteBuffer bb = ByteBuffer.allocate (bytes.length);
      bb.put (bytes);
                 bb.flip ();
       CharBuffer cb = cs.decode (bb);
  
	return cb.array();
	}

	public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"utf-8"));
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
	
	public static byte[] gbk2utf8(String chenese) {
        char c[] = chenese.toCharArray();
        byte[] fullByte = new byte[3 * c.length];
        for (int i = 0; i < c.length; i++) {
            int m = (int) c[i];
            String word = Integer.toBinaryString(m);
            StringBuffer sb = new StringBuffer();
            int len = 16 - word.length();
            for (int j = 0; j < len; j++) {
                sb.append("0");
            }
            sb.append(word);
            sb.insert(0, "1110");
            sb.insert(8, "10");
            sb.insert(16, "10");
            System.out.println(sb.toString());
            String s1 = sb.substring(0, 8);
            String s2 = sb.substring(8, 16);
            String s3 = sb.substring(16);
            byte b0 = Integer.valueOf(s1, 2).byteValue();
            byte b1 = Integer.valueOf(s2, 2).byteValue();
            byte b2 = Integer.valueOf(s3, 2).byteValue();
            byte[] bf = new byte[3];
            bf[0] = b0;
            bf[1] = b1;
            bf[2] = b2;
            
            fullByte[i * 3] = bf[0];            
            fullByte[i * 3 + 1] = bf[1];            
            fullByte[i * 3 + 2] = bf[2];
        }
        return fullByte;
    }
}