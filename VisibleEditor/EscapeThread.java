import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class EscapeThread {

	public VBlock block;
	public Thread searchThread = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO: Implement this method
			getFiles(VisibleEditor.descriptionfolder);
			if(block.escape != null) {
				block.AutoSize();//block.escape);
				try{
					block.Refresh();
				}catch(Exception e){
					
				}
			} else {
				block.escape = block.code;
			}
		}

	});
	//Wrong #1
	public void getFiles(String filePath) {
		File root = new File(filePath);
		File[] files = root.listFiles();
		if(files != null) {
			file:for(File file:files) {
				if(file.isDirectory()) {
					getFiles(file.getAbsolutePath());
				} else {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					try {
						DocumentBuilder db = dbf.newDocumentBuilder();
						Document document = db.parse(file);
						NodeList list = document.getElementsByTagName("block");
						next:for (int i = 0; i < list.getLength(); ++i) {
							Element element = (Element) list.item(i);
							try {
								//Speciel Escape
								String ca = element.getElementsByTagName("case").item(0).getFirstChild().getNodeValue();
								String content = element.getAttribute("name");
								try {
									if (block.code.replace(content, "") != block.code) {
										String[] points = ca.split("\\+");
										VBlock Addons = block.Addons;
										for (int z = 0; z < points.length; z ++) {
											String point = points[z];
											boolean proceed = false;
											switch (point.trim()) {
											case "%word%":
												if (!isWordCharacter(Addons.code.charAt(0))) {
													continue next;
												}
												proceed = true;
												break;
											case "%class%":
												if (!isWordCharacter(Addons.code.charAt(0))) {
													continue next;
												}
												proceed = true;
												break;
											case "%word...%":
												if (!isWordCharacter(Addons.code.charAt(0))) {
													continue next;
												}
												try {
													while(isWordCharacter(Addons.Addons.code.charAt(0))) {
														Addons = Addons.Addons;
													}
												} catch(Exception e) {
													continue next;
												}
												proceed = true;
												break;
											}
											if (!proceed) {
												if (Addons.code.replace(point.trim(), "").length() == Addons.code.length()) {
													continue next;
												}
											}
											Addons = Addons.Addons;
										}
										// Succeed
										block.escape = element.getElementsByTagName("escaped").item(0) .getFirstChild().getNodeValue().trim();
									}
								} catch(Exception ez) {
									//ez.printStackTrace();
								}
							} catch (Exception e) {
								String content = element.getAttribute("name");
								if (XMLUtil.DetectMatches(block,content,true)) {
									block.escape = element.getElementsByTagName("escaped").item(0) .getFirstChild().getNodeValue().trim();
								} else {
									continue file;
								}
							}
						}
					} catch (Exception e) {}

				}
			}
		}
	}

	public static boolean isWordCharacter(char ch) {
		if (Character.isLetter(ch) || Character.isDigit(ch)) {
			return true;
		}
		return false;
	}

	public EscapeThread(VBlock target) {
		block = target;
		searchThread.start();
	}
}
