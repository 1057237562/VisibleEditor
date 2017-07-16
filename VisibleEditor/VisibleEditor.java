import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.*;
import java.io.*;
import java.beans.*;
import java.net.*;

public class VisibleEditor {

	private static JPanel panel;
	public static VFrame mainFrame;
	public static Map<String,String> dict = new HashMap<String,String>();
	public static ArrayList<Event> cs = new ArrayList<Event>();
	public static VisibleEditor t;
	public static JMenuBar jmb;
	public static String filename = "";
	public static String targetLanguage = "";
	public static JTextPane inpuz = new JTextPane();
	public static JButton jbz = new JButton();
	public static JFrame sf = new JFrame();
	
	public final static String descriptionfolder = System.getProperty("user.dir") + "\\Description";

	public static void main(String[] arg0) {

		File language = new File(System.getProperty("user.dir") + "\\Language");
		if(!language.exists() && !language.isDirectory()) {
			language.mkdir(); // Generate the Language File
		}
		
		File description = new File(System.getProperty("user.dir") + "\\Description");
		if(!description.exists() && !description.isDirectory()) {
			description.mkdir(); // Generate the Description File
		}

		File dic = new File(System.getProperty("user.dir") + "\\Language\\dict.cfg");
		if(dic.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\Language\\dict.cfg"));
				String s = br.readLine();
				String[] words = s.replaceAll("\\{","").replaceAll("\\}","").split(", ");

				for(int i = 0; i<words.length; i++) {
					dict.put(words[i].split("=")[0].replaceAll("\\s*", ""),words[i].split("=")[1].replaceAll("\\s*", "")); // Remove THE HECK in the File
				}
			} catch(Exception e1) {
				System.out.println(e1);
			}
		}

		File eg = new File(System.getProperty("user.dir") + "\\Language\\engine.cfg");
		if(eg.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\Language\\engine.cfg"));
				String s = br.readLine();
				mainFrame.TranslateEngine = Integer.parseInt(s);
			} catch(Exception e1) {
				System.out.println(e1);
			}
		}

		System.out.println("Loading Dictionary");

		File et = new File(System.getProperty("user.dir") + "\\Language\\targetLanguage.cfg"); // Get Target Language
		if(et.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\Language\\targetLanguage.cfg"));
				String s = br.readLine();
				targetLanguage = s;
			} catch(Exception e1) {
				System.out.println(e1);
			}
		} else {
			dic.delete();
			dict = new HashMap<String,String>();
		}

		System.out.println("Dictionary Loaded");

		VFrame jf = new VFrame();

		mainFrame = jf;

		jf.setTitle("VisibleEditor");

		jf.setSize(1500,700);
		jf.setVisible(true);

		/*VBlock vb = new VBlock(jf,1);
		VBlock vbp = new VBlock(jf,1);
		VBlock vbs = new VBlock(jf,1);

		jf.registerBlock(vb);
		jf.registerBlock(vbp);
		jf.registerBlock(vbs);*/ // add on test

		jmb = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem n = new JMenuItem("New");
		n.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VBlock[] vbs = jf.blocks.toArray(new VBlock[0]);
				for(int i = 0; i < vbs.length; i++) {
					jf.remove(vbs[i]);
					jf.blocks.remove(vbs[i]);
				}
				VAttacher[] vas = jf.attachers.toArray(new VAttacher[0]);
				for(int i = 0; i < vas.length; i++) {
					jf.remove(vas[i]);
					jf.attachers.remove(vas[i]);
				}
				jf.hscrollbar.setValue(0);
				jf.vscrollbar.setValue(0);
				filename = null;
				try {
					VisibleEditor.mainFrame.RefreshTSP();
				} catch(Exception e1) {

				}
			}
		});
		file.add(n);
		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				jfc.showDialog(new JLabel(),"Select");
				File file = jfc.getSelectedFile();
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
					String line = "";
					ArrayList<String> s = new ArrayList<String>();
					while((line = br.readLine()) != null) {
						s.add(line);
					}
					br.close();
					VBlock[] vbs = jf.blocks.toArray(new VBlock[0]);
					for(int i = 0; i < vbs.length; i++) {
						jf.remove(vbs[i]);
						jf.blocks.remove(vbs[i]);
					}
					VAttacher[] vas = jf.attachers.toArray(new VAttacher[0]);
					for(int i = 0; i < vas.length; i++) {
						jf.remove(vas[i]);
						jf.attachers.remove(vas[i]);
					}
					jf.hscrollbar.setValue(0);
					jf.vscrollbar.setValue(0);
					jf.loadBlocks(s.toArray(new String[0]));
					filename = file.getAbsolutePath();
				} catch(Exception e1) {

				}
				try {
					VisibleEditor.mainFrame.RefreshTSP();
				} catch(Exception e1) {

				}
			}
		});
		file.add(open);
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file;
				if(filename == "") {
					JFileChooser jfc = new JFileChooser();
					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					jfc.showSaveDialog(new JLabel());
					file = jfc.getSelectedFile();
				} else {
					file = new File(filename);
				}

				try {
					FileOutputStream out = new FileOutputStream(file,false);
					out.write(VisibleEditor.mainFrame.export().getBytes());
					out.close();
				} catch(Exception e1) {

				}
			}
		});
		file.add(save);
		JMenuItem saveas = new JMenuItem("Save As");
		saveas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file;
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				jfc.showSaveDialog(new JLabel());
				file = jfc.getSelectedFile();
				filename = file.getAbsolutePath();

				try {
					FileOutputStream out = new FileOutputStream(file,false);
					out.write(VisibleEditor.mainFrame.export().getBytes());
					out.close();
				} catch(Exception e1) {

				}
			}
		});
		file.add(saveas);
		JMenu tool = new JMenu("Tools");
		JMenuItem pc = new JMenuItem("Coordinate Calculator");
		pc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame jz = new PointCalculator();
				jz.setVisible(true);
			}
		});
		tool.add(pc);

		JMenu translate = new JMenu("Translator");
		JMenu engine = new JMenu("Translate Engine");
		JMenu tolanguage = new JMenu("Translate Language");
		JMenuItem deldict = new JMenu("Delete Temp file");
		deldict.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File dic = new File(System.getProperty("user.dir") + "\\Language\\dict.cfg");
				dic.delete();
				dict = new HashMap<String,String>();
			}
		});
		translate.add(engine);
		translate.add(tolanguage);
		translate.add(deldict);

		JMenuItem none = new JMenuItem("None");
		none.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.TranslateEngine = 0;
				saveTranslateEngine();
			}
		});
		JMenuItem bing = new JMenuItem("Bing");
		bing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.TranslateEngine = 1;
				saveTranslateEngine();
			}
		});
		JMenuItem baidu = new JMenuItem("Baidu");
		baidu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.TranslateEngine = 2;
				saveTranslateEngine();
			}
		});

		JMenuItem tg = new JMenuItem("Set Target Language");
		tg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sf.setSize(600,70);
				sf.setTitle("Set Target Language Code:");
				sf.add(inpuz,BorderLayout.CENTER);
				sf.add(jbz,BorderLayout.EAST);
				sf.setVisible(true);
				jbz.setText("Done");
				jbz.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						sf.setVisible(false);
						targetLanguage = inpuz.getText();
						try {
							FileOutputStream out = new FileOutputStream(new File(System.getProperty("user.dir") + "\\Language\\targetLanguage.cfg"),false);
							out.write(inpuz.getText().getBytes());
							out.close();
						} catch(Exception e1) {

						}
					}
				});
			}
		});
		
		System.out.println("Regist Component");

		tolanguage.add(tg);

		engine.add(none);
		engine.add(bing);
		engine.add(baidu);

		jmb.add(file);
		jmb.add(tool);
		jmb.add(translate);

		jf.setJMenuBar(jmb);

		System.out.println("Main Program Load Finished");
		System.out.println("Loading Plugins...");

		Thread td = new Thread(new Runnable() {
			@Override
			public void run() {
				File z = new File(System.getProperty("user.dir") + "\\Plugins");
				if(z.exists()) {
					URL url = null;
					try {
						url = z.toURI().toURL();
					} catch(Exception e) {
						System.out.println("Plugins not found");
					}
					if(url != null) {
						URLClassLoader loader = new URLClassLoader(new URL[] {url});
						File[] subFile = z.listFiles();

						for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) { // Load Plugins
							if (!subFile[iFileLength].isDirectory()) {
								String fileName = subFile[iFileLength].getName();
								System.out.println("Loading:" + fileName.substring(0,fileName.lastIndexOf(".")));
								if (fileName.trim().toLowerCase().endsWith(".class") && fileName.replaceAll("\\u0024","") == fileName) { //Split $
									try {
										Class c = loader.loadClass(fileName.substring(0,fileName.lastIndexOf(".")));
										Event e = (Event) c.newInstance();
										cs.add(e);
										System.out.println("Completed");
										e.OnLoad(mainFrame);
									} catch(Exception e) {
										System.out.println("Class cannot load:" + e + " on class" + fileName.substring(0,fileName.lastIndexOf(".")));
									}

								}
							}
						}
					}
				} else {
					z.mkdir();
				}
				
				System.out.println("Plugins Load Finished");
				
				System.out.println("Update UI");

				Thread ui = new Thread(new Runnable(){
					@Override
					public void run(){
						jf.validate();
					}
				});
				
				ui.start();
			}
		});

		td.start();
		
	}

	public static String translate(String text) {
		String s = text;
		if(isWordCharacter(s.charAt(0)) && s.replaceAll("[a-zA-Z]","") != s && targetLanguage != "") {
			System.out.println("Translating...");
			if(mainFrame.TranslateEngine == 1) {
				s = BingEngine.BingTranslate(text.replaceAll("([A-Z])"," $1"),targetLanguage).replaceAll(" ",""); // Add Space on the each HigherCase
			}
			if(mainFrame.TranslateEngine == 2) {
				s = BaiduEngine.BaiduTranslate(text.replaceAll("([A-Z])"," $1"),targetLanguage).replaceAll(" ","");
			}

			for(int i = 0; i<cs.size(); i++) {
				if(cs.get(i).TranslateID == mainFrame.TranslateEngine) {
					s = cs.get(i).Translate(text);
				}
			}

			dict.put(text,s);
			saveDict();
		}
		return s;
	}

	public static boolean isWordCharacter(char ch) {
		if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_') {
			return true;
		}
		return false;
	}

	public static void saveDict() {
		try {
			FileWriter out = new FileWriter(System.getProperty("user.dir") + "\\Language\\dict.cfg"); // Save the dictionary to the Local
			out.write(dict+"");
			out.close();
		} catch(Exception e1) {

		}
	}

	public static void saveTranslateEngine () {
		try {
			FileWriter out = new FileWriter(System.getProperty("user.dir") + "\\Language\\engine.cfg"); // Save the dictionary to the Local
			out.write(mainFrame.TranslateEngine + "");
			out.close();
		} catch(Exception e1) {

		}
	}
}

class PointCalculator extends JFrame {

	public static JButton component;
	public int fx;
	public int fy;
	public int oldx;
	public int oldy;

	public int direction = 0;

	public PointCalculator() {
		component = new JButton();

		JMenuBar jmb = new JMenuBar();
		JMenuItem imz = new JMenuItem("Insert to Editor");
		imz.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VBlock p = new VBlock(VisibleEditor.mainFrame,1);
				p.code = component.getX()+"";
				VisibleEditor.mainFrame.registerBlock(p);
				VBlock a1 = new VBlock(VisibleEditor.mainFrame,1);
				a1.code = ",";
				a1.Parent = p;
				p.Addons = a1;
				VisibleEditor.mainFrame.registerBlock(a1);
				VBlock a2 = new VBlock(VisibleEditor.mainFrame,1);
				a2.code = component.getY()+"";
				a2.Parent = a1;
				a1.Addons = a2;
				VisibleEditor.mainFrame.registerBlock(a2);
				VBlock a3 = new VBlock(VisibleEditor.mainFrame,1);
				a3.code = ",";
				a3.Parent = a2;
				a2.Addons = a3;
				VisibleEditor.mainFrame.registerBlock(a3);
				VBlock a4 = new VBlock(VisibleEditor.mainFrame,1);
				a4.code = component.getWidth()+"";
				a4.Parent = a3;
				a3.Addons = a4;
				VisibleEditor.mainFrame.registerBlock(a4);
				VBlock a5 = new VBlock(VisibleEditor.mainFrame,1);
				a5.code = ",";
				a5.Parent = a4;
				a4.Addons = a5;
				VisibleEditor.mainFrame.registerBlock(a5);
				VBlock a6 = new VBlock(VisibleEditor.mainFrame,1);
				a6.code = component.getHeight()+"";
				a6.Parent = a5;
				a5.Addons = a6;
				VisibleEditor.mainFrame.registerBlock(a6);
				p.Refresh();
			}
		});
		jmb.add(imz);
		this.setJMenuBar(jmb);

		this.setSize(500,700);
		this.setLayout(null);
		this.getContentPane().add(component);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				component.setLocation(e.getXOnScreen() - PointCalculator.this.getContentPane().getLocationOnScreen().x,e.getYOnScreen() - PointCalculator.this.getContentPane().getLocationOnScreen().y);
				component.setSize(0,0);
				fx = e.getXOnScreen();
				fy = e.getYOnScreen();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				PointCalculator.this.setTitle("x:"+component.getLocation().x +" y:"+component.getLocation().y + " Width:"+component.getWidth()+" Height:"+component.getHeight());
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				component.setSize(e.getXOnScreen()-fx,e.getYOnScreen()-fy);
				PointCalculator.this.setTitle("x:"+component.getLocation().x +" y:"+component.getLocation().y + " Width:"+component.getWidth()+" Height:"+component.getHeight());
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if(component.getHeight() == 0 && component.getWidth() == 0) {
					PointCalculator.this.setTitle("x:"+( e.getXOnScreen() - PointCalculator.this.getContentPane().getLocationOnScreen().x )+" y:"+ ( e.getY() - PointCalculator.this.getContentPane().getLocationOnScreen().y));
				}
			}
		});

		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				oldx = e.getXOnScreen();
				oldy = e.getYOnScreen();
				if(component.getLocationOnScreen().x < oldx && component.getLocationOnScreen().x+8 > oldx) { //Left
					if(component.getLocationOnScreen().y < oldy && component.getLocationOnScreen().y+8 > oldy) { //Top
						direction = 1;
					}
					if(component.getLocationOnScreen().y + component.getHeight() > oldy && component.getLocationOnScreen().y+component.getHeight() - 8 < oldy) { //Bottom
						direction = 2;
					}
				}
				if(component.getLocationOnScreen().x + component.getWidth() > oldx && component.getLocationOnScreen().x + component.getHeight() - 8 < oldx) { //Right
					if(component.getLocationOnScreen().y < oldy && component.getLocationOnScreen().y+8 > oldy) { //Top
						direction = 3;
					}
					if(component.getLocationOnScreen().y + component.getHeight() > oldy && component.getLocationOnScreen().y+component.getHeight() - 8 < oldy) { //Bottom
						direction = 4;
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				direction = 0;
			}
		});

		component.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				switch(direction) {
				case 0:
					component.setLocation(component.getLocation().x + e.getXOnScreen()-oldx,component.getLocation().y + e.getYOnScreen()-oldy);
					oldx = e.getXOnScreen();
					oldy = e.getYOnScreen();
					break;
				case 1:
					component.setLocation(component.getLocation().x + e.getXOnScreen()-oldx,component.getLocation().y + e.getYOnScreen()-oldy);
					component.setSize(component.getWidth() - e.getXOnScreen()+oldx,component.getHeight() - e.getYOnScreen()+oldy);
					oldx = e.getXOnScreen();
					oldy = e.getYOnScreen();
					break;
				case 2:
					component.setLocation(component.getLocation().x + e.getXOnScreen()-oldx,component.getLocation().y);
					component.setSize(component.getWidth() - e.getXOnScreen()+oldx,component.getHeight() + e.getYOnScreen()-oldy);
					oldx = e.getXOnScreen();
					oldy = e.getYOnScreen();
					break;
				case 3:
					component.setLocation(component.getLocation().x,component.getLocation().y + e.getYOnScreen()-oldy);
					component.setSize(component.getWidth() + e.getXOnScreen()-oldx,component.getHeight() - e.getYOnScreen()+oldy);
					oldx = e.getXOnScreen();
					oldy = e.getYOnScreen();
					break;
				case 4:
					component.setSize(component.getWidth() + e.getXOnScreen()-oldx,component.getHeight() + e.getYOnScreen()-oldy);
					oldx = e.getXOnScreen();
					oldy = e.getYOnScreen();
					break;
				}
				PointCalculator.this.setTitle("x:"+component.getLocation().x +" y:"+component.getLocation().y + " Width:"+component.getWidth()+" Height:"+component.getHeight());
			}
		});
	}
}

class VGroup extends JPanel {

	public VGroup(VBlock[] Model) {
		this.setLayout(null);
		for(int i = 0; i<Model.length; i++) {
			this.add(Model[i]);
			Model[i].Model = true;
			if(Model[i].Parent == null) {
				Model[i].Refresh();
			}
		}
	}
}