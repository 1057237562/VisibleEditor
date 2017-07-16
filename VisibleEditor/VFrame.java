import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.beans.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class VFrame extends JFrame {

	public static ArrayList<VBlock> blocks;
	public static ArrayList<VAttacher> attachers = new ArrayList<VAttacher>();
	public static DefaultMutableTreeNode top = new DefaultMutableTreeNode("Block Package");
	public static JScrollBar hscrollbar;
	public static JScrollBar vscrollbar;

	public static JPanel panel;
	public static JPanel tpanel;
	public static JPanel ppanel;
	public static JScrollPane usp;
	public static JScrollPane psp;
	public static JPanel tmppanel;
	public static JPanel bppanel;

	public static int lasth;
	public static int lastw;
	public static int TranslateEngine = 0;

	public static JLayeredPane jlp = new JLayeredPane();

	public static JSplitPane splitpanel;
	public static JSplitPane tsp;

	public static JTree pkgl;
	public static JTextField search = new JTextField("Search");
	public static JTabbedPane jtp = new JTabbedPane();
	public static ArrayList<File> blockslist = new ArrayList<File>();
	
	public static JList<String> resultlist = new JList<String>();
	public static JScrollPane result = new JScrollPane(resultlist);
	
	public static boolean focused = false;
	public static Thread td;
	public static boolean Breaknow = false;
	public static Map<String,File> resultmap;
	
	public static JFrame az = new JFrame();
	public static JTextPane inpua = new JTextPane();
	public static JButton jba = new JButton();
	
	public static File nowOpenBlock;
	
	public VFrame() {
		panel = new JPanel();

		tpanel = new JPanel(new GridLayout(0,2));
		
		tpanel.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3){
					JPopupMenu jpm = new JPopupMenu();
					JMenuItem add = new JMenuItem("Add");
					add.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) { // Add New Block
							az.setVisible(true);
							inpua.setText("");
						}
					});
					jpm.add(add);
					jpm.show(VFrame.this, e.getLocationOnScreen().x - VFrame.this.getLocationOnScreen().x, e.getLocationOnScreen().y - VFrame.this.getLocationOnScreen().y);
				}
			}
		});
		
		usp = new JScrollPane();
		usp.setViewportView(tpanel);

		tmppanel = new JPanel(); // The Position of ScrollPane

		File b = new File(System.getProperty("user.dir") + "\\Blocks");
		if(!b.exists() && !b.isDirectory()) {
			b.mkdir(); // Generate the Language File
		}

		getFiles(System.getProperty("user.dir") + "\\Blocks",top);
		
		ppanel = new JPanel();
		ppanel.setLayout(new BorderLayout());
		
		pkgl = new JTree(top); //Blocks Folder tree

		pkgl.addTreeSelectionListener(new TreeSelectionListener() { // Select File
			public void valueChanged(TreeSelectionEvent evt) {
				TreeNode node=(TreeNode)evt.getPath().getLastPathComponent();
				DefaultMutableTreeNode d = (DefaultMutableTreeNode) pkgl.getLastSelectedPathComponent();
				if(node.isLeaf()) {
					if(!((File) d.getUserObject()).isDirectory()) {
						InstallTSP(txt2String((File) d.getUserObject()),(File) d.getUserObject());
						nowOpenBlock = (File) d.getUserObject();
						VFrame.this.repaint();
						VFrame.this.validate();
					}
				}
			}
		});

		psp = new JScrollPane(pkgl);
		
		ppanel.add(psp,BorderLayout.CENTER);
		ppanel.add(search,BorderLayout.SOUTH);
		
		//Search TextField
		Document document = search.getDocument();  
        document.addDocumentListener(new DocumentListener(){ // UpdateList
			@Override  
			public void changedUpdate(DocumentEvent e) {
				Search();
			}
			@Override  
			public void removeUpdate(DocumentEvent e) {
				Search();
			}
			@Override  
			public void insertUpdate(DocumentEvent e) {
				Search();
			}  
		}); 
		search.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (search.getText().isEmpty()){
                    search.setText("Search");
                }
				if(!focused){
					result.setVisible(false);
				}
            }
			@Override
            public void focusGained(FocusEvent e) {
                if (search.getText().replace("Search","").length() != search.getText().length()){
                    search.setText("");
                }else{
					result.setVisible(true);
					result.setSize(psp.getWidth()-50,psp.getHeight()+50);
					result.setLocation(search.getLocationOnScreen().x - VFrame.this.getContentPane().getLocationOnScreen().x,search.getLocationOnScreen().y - result.getHeight() - VFrame.this.getContentPane().getLocationOnScreen().y);

				}
            }
        });//Hint 
		
		result.addFocusListener(new FocusAdapter(){ // Hide Result list
			@Override
            public void focusLost(FocusEvent e) {
                result.setVisible(false);
				focused = false;
            }
			@Override
            public void focusGained(FocusEvent e) {
                focused = true;
            }
		});
		
		resultlist.addMouseListener(new MouseAdapter() { // Open the TSP from Search
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
					JList myList = (JList) e.getSource();
                    int index = myList.getSelectedIndex();
                    String obj = myList.getModel().getElementAt(index).toString();
					System.out.println(obj.trim());
					nowOpenBlock = (File) resultmap.get(obj.trim());
					InstallTSP(txt2String(resultmap.get(obj.trim())),(File) resultmap.get(obj.trim()));
					VFrame.this.repaint();
					VFrame.this.validate();
                }
            }
        });
		
		bppanel = new JPanel();

		tsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,false,usp,bppanel);
		tsp.setDividerLocation(0.7);
		tsp.setResizeWeight(0.7);

		tsp.setDividerSize(3);

		splitpanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,false,panel,tmppanel);

		//System.out.println("setSplitSize");
		splitpanel.setDividerLocation(0.8);
		splitpanel.setResizeWeight(0.8);

		splitpanel.setDividerSize(3);

		this.setLayout(null);
		this.setContentPane(jlp);

		splitpanel.setBounds(0,0,this.getWidth(),this.getHeight());

		this.add(result);
		this.jlp.setLayer(result,6); // Outfit result
		result.setSize(175,300);
		result.setVisible(false);
		
		this.add(tsp);
		this.jlp.setLayer(tsp,4);
		this.add(splitpanel);
		this.jlp.setLayer(ppanel,5);
		this.add(ppanel);
		this.jlp.setLayer(splitpanel,1);

		//ScrollBarUI
		hscrollbar = new JScrollBar(JScrollBar.HORIZONTAL);
		vscrollbar = new JScrollBar(JScrollBar.VERTICAL);
		this.add(hscrollbar);
		this.jlp.setLayer(hscrollbar,4);
		this.add(vscrollbar);
		this.jlp.setLayer(vscrollbar,4);

		hscrollbar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				VBlock[] block = blocks.toArray(new VBlock[0]);
				for(int i = 0; i<block.length; i++) {
					block[i].setLocation(block[i].getX()-hscrollbar.getValue()+lastw,block[i].getY());
				}
				for(VAttacher attacher:attachers.toArray(new VAttacher[0])){
					attacher.Locate();
					attacher.Test();
				}
				lastw = hscrollbar.getValue();
			}

		});

		vscrollbar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				VBlock[] block = blocks.toArray(new VBlock[0]);
				for(int i = 0; i<block.length; i++) {
					block[i].setLocation(block[i].getX(),block[i].getY()-vscrollbar.getValue()+lasth);
				}
				for(VAttacher attacher:attachers.toArray(new VAttacher[0])){
					attacher.Locate();
					attacher.Test();
				}
				lasth = vscrollbar.getValue();
			}

		});
		//End UI

		splitpanel.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				try {
					RefreshTSP();
				} catch(Exception e1) {

				}
				VFrame.this.repaint();
			}

		});

		tsp.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				try {
					Refreshpkgpanel();
				} catch(Exception e1) {

				}
				VFrame.this.repaint();
			}

		});

		blocks = new ArrayList<VBlock>();

		this.addComponentListener(ca);
		this.addWindowStateListener(wsl);

		//Tmp Area
		//InstallTSP("if(){}");
		//End

		try {
			RefreshTSP();
			Refreshpkgpanel();
		} catch(Exception e) {

		}
		
		az.setSize(600,70); // Set ...
		az.setTitle("Add Blocks:");
		az.add(inpua,BorderLayout.CENTER);
		az.add(jba,BorderLayout.EAST);
		jba.setText("Add");
		jba.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				az.setVisible(false);
				StringBuilder sb = new StringBuilder();
				for(String s:txt2String(nowOpenBlock)){
					sb.append(s + System.getProperty("line.separator")); // Merge the String
				}
				System.out.println(sb.toString());
				String mixed = sb.toString() + inpua.getText(); // Add the New Block to file end
				try{
					FileOutputStream out = new FileOutputStream(nowOpenBlock,false); // Output the file
					out.write(mixed.getBytes());
					out.close();
				}catch(Exception e1){
			
				}
				InstallTSP(mixed.split(System.getProperty("line.separator")),nowOpenBlock); // Refresh TSP
				VFrame.this.repaint();
				VFrame.this.validate();
			}
		});
	}

	public void Search(){
		try{
			td.interrupt();
			Breaknow = true;
			td.join();
		}catch(Exception e){
			
		}
		try{
			resultmap = new HashMap<String,File>();
			result.setVisible(true);
			result.setSize(psp.getWidth()-50,psp.getHeight()+50);
			result.setLocation(search.getLocationOnScreen().x - VFrame.this.getContentPane().getLocationOnScreen().x,search.getLocationOnScreen().y - result.getHeight() - VFrame.this.getContentPane().getLocationOnScreen().y);
			td = new Thread(new Runnable(){
				@Override
				public void run(){ // Search the file contain the target
					try{
						ArrayList<String> matches = new ArrayList<String>();
						File[] tmp = blockslist.toArray(new File[0]);
						for(File file:tmp) {
							if(Breaknow){
								Breaknow = false;
								return;
							}
							if(file.getName().replace(search.getText(),"").length() != file.getName().length()){
								matches.add(file.getName());
								resultmap.put(file.getName().trim(),file);
								ListModel<String> jlm = new DefaultComboBoxModel<String>(matches.toArray(new String[0]));
								resultlist.setModel(jlm); // Refresh Instantly
								continue;
							}
							BufferedReader br = new BufferedReader(new FileReader(file));
							String s = br.readLine();
							whileloop : while(s != null){
								if(s.replace(search.getText(),"").length() != s.length()){
									matches.add(file.getName());
									resultmap.put(file.getName().trim(),file);
									ListModel<String> jlm = new DefaultComboBoxModel<String>(matches.toArray(new String[0]));
									resultlist.setModel(jlm); // Refresh Instantly
									break whileloop;
								}
								String[] keys = getAllkey(search.getText());
								for(String key:keys){
									if(s.replace(key,"").length() != s.length()){
										matches.add(file.getName());
										resultmap.put(file.getName().trim(),file);
										ListModel<String> jlm = new DefaultComboBoxModel<String>(matches.toArray(new String[0]));
										resultlist.setModel(jlm); // Refresh Instantly
										break whileloop; // Break All
									}
								}
								s = br.readLine();
							}
						}
						ListModel<String> jlm = new DefaultComboBoxModel<String>(matches.toArray(new String[0]));
						resultlist.setModel(jlm);
					}catch(Exception e){
				
					}
				}
			});
			td.start();
		}catch(Exception e){
			
		}
	}
	
	public static String[] getAllkey(String value){
		ArrayList<String> rs = new ArrayList<String>();
		for(Map.Entry entry:VisibleEditor.dict.entrySet()){
			if(entry.getValue().toString().replace(value,"").length() != entry.getValue().toString().length()){
				rs.add(entry.getKey().toString());
			}
		}
		return rs.toArray(new String[0]);
	}
	
	public static void Refreshpkgl() { // Refresh Tree
		top = new DefaultMutableTreeNode("Block Package");
		getFiles(System.getProperty("user.dir") + "\\Blocks",top);
		DefaultTreeModel model = (DefaultTreeModel) pkgl.getModel();
		model.setRoot(top);
	}
	
	public void Refreshpkgpanel () { // Refresh Tree
		ppanel.setBounds(bppanel.getLocationOnScreen().x - this.getContentPane().getLocationOnScreen().x,bppanel.getLocationOnScreen().y - this.getContentPane().getLocationOnScreen().y,this.getContentPane().getWidth() - bppanel.getLocationOnScreen().x + this.getContentPane().getLocationOnScreen().x,this.getContentPane().getHeight() - bppanel.getLocationOnScreen().y + this.getContentPane().getLocationOnScreen().y);
		ppanel.repaint();
		ppanel.validate();
	}

	public static String[] txt2String(File file) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s = null;
			while((s = br.readLine())!=null) {
				result.add(s);
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result.toArray(new String[0]);
	}

	public static void getFiles(String filePath,DefaultMutableTreeNode node) {
		File root = new File(filePath);
		File[] files = root.listFiles();
		if(files != null){
			for(File file:files) {
				DefaultMutableTreeNode t = new DefaultMutableTreeNode(file);
				node.add(t);
				if(file.isDirectory()) {
					getFiles(file.getAbsolutePath(),t);
				}else{
					blockslist.add(file);
				}
			}
		}
	}

	public void InstallTSP(String[] codes,File tfile) {
		tpanel.removeAll();
		tpanel.repaint();
		
		File describe = new File(tfile.getAbsolutePath().replace("Blocks","Description").replace(".txt",".xml").replace(".class",".xml")); 
		if(describe.exists()){
			try{
				XMLUtil.loadXML(describe);
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			System.out.println("File Not Founded:" + describe.getAbsolutePath());
		}
		
		code:for(int i = 0; i<codes.length; i++) {
			ArrayList<VBlock> tmpList = new ArrayList<VBlock>();
			if(codes[i].replaceAll(" ","").replaceAll("	","").replaceAll("\r","").replaceAll("\n","").replaceAll("\t","").length() != 0) { //including things
				String[] letter = codes[i].split("|");
				String word = "";
				VBlock parent = null;  // The Addons parent

				boolean inline = false;
				boolean incast = false;
				boolean changer = false;

				for(int j = 0; j<letter.length; j++) {
					
					if(letter[j].replace("\"","") != letter[j] && !changer) {
						if(incast) {
							incast = false;

						} else {
							incast = true; // In the ""'s String
						}
					}

					changer = false;

					if(letter[j].replace("\\","") != letter[j]) { // Next the changer symbol
						changer = true;
					}

					if(isWordCharacter(letter[j].charAt(0)) && j<letter.length-1 || incast && j<letter.length-1) {
						word = word + letter[j];
					} else {
						//Comband word
						if(isWordCharacter(letter[j].charAt(0)) || incast) {
							word = word + letter[j];
						}
						if(word != "") {
							VBlock block = new VBlock(this,1);
							block.code = word;
							word = "";
							if(parent == null) {
								parent = block;
							} else {
								block.Parent = parent;
								parent.Addons = block;
								block.Attachment = true;
								parent = block;
							}
							tmpList.add(block);
						}
						//Add Symbol
						if(!isWordCharacter(letter[j].charAt(0))) {
							if(letter[j].replace(" ","") == letter[j] && letter[j].replace("	","") == letter[j]) { // Ignore the Placeholder in the middle but split
								VBlock block = new VBlock(this,1);
								block.code = letter[j];
								if(parent == null) {
									parent = block;
								} else {
									block.Parent = parent;
									parent.Addons = block;
									block.Attachment = true;
									parent = block;
								}
								tmpList.add(block);
							}
						}
					}
				}
			}
			tpanel.add(new VGroup(tmpList.toArray(new VBlock[0])));
		}
		RepaintTSP();
	}

	public void RepaintTSP() {
		int height = 0;
		Component[] c = tpanel.getComponents();
		for(int i = 0; i<c.length; i++) {
			FontMetrics fm = c[i].getFontMetrics(c[i].getFont());
			height = height + fm.getHeight() + (int)fm.getHeight()/2;
		}
		tpanel.setPreferredSize(new Dimension(tsp.getWidth(), height));
	}

	public void RefreshTSP() {
		tsp.setBounds(tmppanel.getLocationOnScreen().x - this.getContentPane().getLocationOnScreen().x,0,this.getContentPane().getLocationOnScreen().x + this.getContentPane().getWidth() - tmppanel.getLocationOnScreen().x,tmppanel.getHeight());
		tpanel.setSize(tsp.getWidth(),tsp.getHeight());
		vscrollbar.setBounds(tmppanel.getLocationOnScreen().x - this.getContentPane().getLocationOnScreen().x-23,0,20,this.jlp.getHeight()-20);
		hscrollbar.setBounds(0,this.jlp.getHeight()-20,tmppanel.getLocationOnScreen().x - this.getContentPane().getLocationOnScreen().x-23,20);
		vscrollbar.repaint();
		hscrollbar.repaint();
		vscrollbar.validate();
		hscrollbar.validate();
		this.jlp.repaint();
		this.jlp.validate();
	}

	private ComponentAdapter ca = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			splitpanel.setBounds(0,0,VFrame.this.getWidth(),VFrame.this.getHeight());
			try {
				RefreshTSP();
			} catch(Exception e1) {

			}
		}
	};

	private WindowStateListener wsl = new WindowStateListener() {
		@Override
		public void windowStateChanged(WindowEvent state) {
			splitpanel.setBounds(0,0,VFrame.this.getWidth(),VFrame.this.getHeight());
			try {
				RefreshTSP();
			} catch(Exception e1) {

			}
		}
	};

	public void registerBlock(VBlock block) {
		for(int i = 0; i<VisibleEditor.cs.size(); i++) {
			VisibleEditor.cs.get(i).OnCreatBlock(block);
		}
		this.add(block); // Add In Panel
		this.jlp.setLayer(block,3);
		blocks.add(block); // Add In List
	}

	public void unregisterBlock(VBlock block) {
		this.remove(block);
		blocks.remove(block);
	}

	public String export() {
		VBlock[] block = blocks.toArray(new VBlock[0]);
		String s = "";
		int out = 0;
		int cnt = 0;  // Auto tabs out
		int offset = 0;
		ArrayList<String> codeStack = new ArrayList<String>();

		for(int i = 0; i<block.length; i++) {
			if(block[i].Parent == null) { // The Original Root (The Codes next are all based from this block)
				String z = "";
				VBlock parent = block[i];

				z = z + parent.code;

				VBlock addon = block[i].Addons;
				while(addon != null) {
					/*if(addon.Parent.code.replace(".","") != addon.Parent.code || addon.code.replace(".","") != addon.code ||
						addon.Parent.code.replace("*","") != addon.Parent.code || addon.code.replace("*","") != addon.code ||
						addon.Parent.code.replace("=","") != addon.Parent.code || addon.code.replace("=","") != addon.code ||
						addon.Parent.code.replace("\\|","") != addon.Parent.code || addon.code.replace("\\|","") != addon.code ||
						addon.Parent.code.replace("\"","") != addon.Parent.code || addon.code.replace("\\|","") != addon.code ||
						addon.Parent.code.replace("/","") != addon.Parent.code || addon.code.replace("/","") != addon.code
						){*/
					
					cnt = 0;  // Auto tabs out
					offset = 0;
					while((offset = addon.code.indexOf("{", offset)) != -1){
						offset = offset + "{".length();
						cnt++;
					}
					
					out += cnt;
					
					cnt = 0;
					offset = 0;
					while((offset = addon.code.indexOf("}", offset)) != -1){
						offset = offset + "}".length();
						cnt++;
					}
					
					out -= cnt;
					if(addon.code.charAt(0) != '('){
						if(!isWordCharacter(addon.Parent.code.charAt(0)) || !isWordCharacter(addon.code.charAt(0))) {
							z = z + addon.code;
						} else {
							z = z + " " + addon.code;
						}
					}else{
						z = z + " " + addon.code;
					}

					parent = addon;
					addon = addon.Addons;
				}

				//Refresh All Sons
				parent = block[i];
				VBlock son = block[i].Son;
				while(son != null) {
					String tabs = "";
					for(int ot = 0;ot < out;ot ++){
						tabs += "	";
					}
					z = z + System.getProperty("line.separator") + tabs + son.code;
					
					cnt = 0;  // Auto tabs out
					offset = 0;
					while((offset = son.code.indexOf("{", offset)) != -1){ // Out one
						offset = offset + "{".length();
						cnt++;
					}
					
					out += cnt;
					
					cnt = 0;
					offset = 0;
					while((offset = son.code.indexOf("}", offset)) != -1){ // In one
						offset = offset + "}".length();
						cnt++;
					}
					
					out -= cnt;
					
					//Refresh Addons on Sons
					VBlock p2 = son;
					addon = son.Addons;
					while(addon != null) {
						/*if(addon.Parent.code.replace(".","") != addon.Parent.code || addon.code.replace(".","") != addon.code ||
						addon.Parent.code.replace("*","") != addon.Parent.code || addon.code.replace("*","") != addon.code ||
						addon.Parent.code.replace("=","") != addon.Parent.code || addon.code.replace("=","") != addon.code ||
						addon.Parent.code.replace("\\|","") != addon.Parent.code || addon.code.replace("\\|","") != addon.code ||
						addon.Parent.code.replace("\"","") != addon.Parent.code || addon.code.replace("\\|","") != addon.code ||
						addon.Parent.code.replace("/","") != addon.Parent.code || addon.code.replace("/","") != addon.code
						){*/
						
						cnt = 0;  // Auto tabs out
						offset = 0;
						while((offset = addon.code.indexOf("{", offset)) != -1){ // Out one
							offset = offset + "{".length();
							cnt++;
						}
					
						out += cnt;
					
						cnt = 0;
						offset = 0;
						while((offset = addon.code.indexOf("}", offset)) != -1){ // In one
							offset = offset + "}".length();
							cnt++;
						}
					
						out -= cnt;
						
						if(addon.code.charAt(0) != '('){
							if(!isWordCharacter(addon.Parent.code.charAt(0)) || !isWordCharacter(addon.code.charAt(0))) {
								z = z + addon.code;
							} else {
								z = z + " " + addon.code;
							}
						}else{
							z = z + " " + addon.code;
						}

						p2 = addon;
						addon = addon.Addons;
					}
					//Ends

					parent = son;
					son = son.Son;
				}
				codeStack.add(z);
			}
		}

		String first = "";
		String[] codes = codeStack.toArray(new String[0]);
		for(int i = 0; i<codes.length; i++) {
			if(codes[i].replace("import","") != codes[i]) {
				if(first == "") {
					first = codes[i];
				}

			}
		}

		s = first;
		for(int i = 0; i<codes.length; i++) {
			if(codes[i] != first) {
				s = s + codes[i];
			}
		}
		for(int i = 0; i<VisibleEditor.cs.size(); i++) {
			s = VisibleEditor.cs.get(i).OnExport(s);
		}
		
		s = s.replaceAll("	" + "}","}");
		
		return s;
	}

	public void loadBlocks(String[] codes) {
		VBlock lastparent = null; // The Block in next lines base on this block;
		VBlock originalparent = null; // Refresh all Blocks at last;

		for(int i = 0; i<codes.length; i++) {
			if(codes[i].replaceAll(" ","").replaceAll("	","").replaceAll("\r","").replaceAll("\n","").replaceAll("\t","").length() != 0) { //including things
				String[] letter = codes[i].split("|");
				String word = "";
				VBlock parent = null;  // The Addons parent

				boolean inline = false;
				boolean incast = false;
				boolean changer = false;

				for(int j = 0; j<letter.length; j++) {

					if(letter[j].replace("\"","") != letter[j] && !changer) {
						if(incast) {
							incast = false;

						} else {
							incast = true; // In the ""'s String
						}
					}

					changer = false;

					if(letter[j].replace("\\","") != letter[j]) { // Next the changer symbol
						changer = true;
					}
					if(isWordCharacter(letter[j].charAt(0)) && j<letter.length-1 || incast && j<letter.length-1) {
						word = word + letter[j];
					} else {
						//Comband word
						if(isWordCharacter(letter[j].charAt(0)) || incast) {
							word = word + letter[j];
						}
						if(word != "") {
							VBlock block = new VBlock(this,1);
							block.code = word;
							word = "";
							if(parent == null) {
								parent = block;
								if(lastparent == null) {
									lastparent = block;
									if(originalparent == null) {
										block.setLocation(0,0); // Set To the top of window
										originalparent = block; // Set OParent
									}
								} else {
									lastparent.Son = block;
									block.Parent = lastparent;
									lastparent = block;
								}
							} else {
								block.Parent = parent;
								parent.Addons = block;
								block.Attachment = true;
								parent = block;
							}
							VisibleEditor.mainFrame.registerBlock(block);
						}
						//Add Symbol
						if(!isWordCharacter(letter[j].charAt(0))) {
							if(letter[j].replace(" ","") == letter[j] && letter[j].replace("	","") == letter[j]) { // Ignore the Placeholder in the middle but split
								VBlock block = new VBlock(this,1);
								block.code = letter[j];
								if(parent == null) {
									parent = block;
									if(lastparent == null) {
										lastparent = block;
									} else {
										lastparent.Son = block;
										block.Parent = lastparent;
										lastparent = block;
									}
								} else {
									block.Parent = parent;
									parent.Addons = block;
									block.Attachment = true;
									parent = block;
								}
								VisibleEditor.mainFrame.registerBlock(block);
							}
						}
					}
				}
				parent.UpdateAllEscape();
			}
		}

		originalparent.Refresh(); // Refresh All Blocks has been import
		ResetScrollBar();
	}

	public void ResetScrollBar() {
		VBlock[] block = blocks.toArray(new VBlock[0]);
		int mheight = 0;
		int mwidth = 0;

		int mh = 0;
		int mw = 0;
		int lh = 0;
		int lw = 0;

		for(int i = 0; i<block.length; i++) {
			if(mh == 0 || block[i].getY() > mh) {
				mh = block[i].getY();
				mheight = block[i].getHeight(); // Add Bottom of the Button
			}
			if(lh == 0 || block[i].getY() < lh) {
				lh = block[i].getY();
			}
			if(mw == 0 || block[i].getX() > mw) {
				mw = block[i].getX();
				mwidth = block[i].getWidth(); // Same as Height
			}
			if(lw == 0 || block[i].getX() < lw) {
				lw = block[i].getX();
			}
		}
		//Get List of the height and width
		vscrollbar.setMaximum(mh + mheight + vscrollbar.getValue());
		//vscrollbar.setMaximum(mh + mheight - lh);
		hscrollbar.setMaximum(mw + mwidth + hscrollbar.getValue());
		//hscrollbar.setMaximum(mw + mwidth + lw);
	}

	public boolean isWordCharacter(char ch) {
		if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_') {
			return true;
		}
		return false;
	}
}
