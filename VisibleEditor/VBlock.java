import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.*;
import java.io.*;
import java.beans.*;
import java.net.*;

public class VBlock extends JButton implements Cloneable {

	public String code;

	public VBlock Parent;
	public VBlock Son;
	public VBlock Addons;

	public boolean Attachment = false;
	public int type;

	private int xOld;
	private int yOld;
	public boolean onDrag = false;
	public String escape;

	public boolean Model = false;
	private VBlock newp;
	public VAttacher pat;
	
	public boolean insame = false;

	public VFrame parentFrame;
	
	public int ModelID;
	public VGroup ModelGroup;
	
	//12:06 PM 7/27/2017 
	public ArrayList<AComponent> applyment = new ArrayList<AComponent>();
	
	// 8/2/2017 5:15 Added in v1.1.6
	public String refer; // When the escape has a Variable
	public Map<String,String> variable;
	public TranslateThread tt;
	
	String lastEscape = "";
	
	public boolean closing = false;

	public VBlock(VFrame frame,int types) {
		this.setSize(50,50);
		this.addMouseListener(ma);
		this.addMouseMotionListener(mma);
		this.setFocusPainted(false);
		this.setContentAreaFilled(false);
		
		parentFrame = frame;
		type = types; // temp
		
		this.addComponentListener(new ComponentListener(){
			
			@Override
			public void componentHidden(ComponentEvent e){
				
			}
			
			@Override
			public void componentMoved(ComponentEvent e){ // Refresh Applyment
				for(AComponent a:applyment.toArray(new AComponent[0])){
					a.Auto();
				}
			}
			
			@Override
			public void componentResized(ComponentEvent e){
				 
			}
			
			@Override
			public void componentShown(ComponentEvent e){
				 
			}
		});

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu jpm = new JPopupMenu();
					JMenuItem copy = new JMenuItem("Copy"); // Pop up Menu
					copy.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) { // Add New Block
							VBlock parent = VBlock.this;
							VBlock newparent = VBlock.this.clonez();
							newparent.setLocation(e.getLocationOnScreen().x - VisibleEditor.mainFrame.splitpanel.getLocationOnScreen().x, e.getLocationOnScreen().y - VisibleEditor.mainFrame.splitpanel.getLocationOnScreen().y); // Follow Mouse

							xOld = e.getX();
							yOld = e.getY();

							newp = newparent;

							VisibleEditor.mainFrame.registerBlock(newparent);
							VBlock tmpparent = newparent;

							VBlock addon = parent.Addons;
							while(addon != null) {
								//Clone addons
								VBlock newaddon = addon.clonez();
								newaddon.Parent = tmpparent;
								tmpparent.Addons = newaddon;
								tmpparent = newaddon;
								VisibleEditor.mainFrame.registerBlock(newaddon);

								parent = addon;
								addon = addon.Addons;
							}

							//Clone All Sons
							tmpparent = newparent;
							parent = VBlock.this;
							VBlock son = parent.Son;
							while(son != null) {
								VBlock newson = son.clonez();
								newson.Parent = tmpparent;
								tmpparent.Son = newson;
								tmpparent = newson;
								VisibleEditor.mainFrame.registerBlock(newson);

								//Clone Addons on Sons
								VBlock tmpp2 = newson;
								VBlock p2 = son;
								addon = son.Addons;
								while(addon != null) {
									VBlock newaddon = addon.clonez();
									newaddon.Parent = tmpp2;
									tmpp2.Addons = newaddon;
									tmpp2 = newaddon;
									VisibleEditor.mainFrame.registerBlock(newaddon);

									p2 = addon;
									addon = addon.Addons;
								}
								//Ends

								parent = son;
								son = son.Son;
							}

							newparent.Refresh();
							
						}
						
						@Override
						public void mouseReleased(MouseEvent e){
							newp.Detect();
							newp.DetectDeletion();
							jpm.setVisible(false);
						}

					});

					copy.addMouseMotionListener(new MouseMotionAdapter() {
						@Override
						public void mouseDragged(MouseEvent e) {

							newp.setLocation(e.getXOnScreen() - VisibleEditor.mainFrame.splitpanel.getLocationOnScreen().x - xOld
							                 , e.getYOnScreen() - VisibleEditor.mainFrame.splitpanel.getLocationOnScreen().y - yOld);
							newp.Refresh();

						}
					});
					jpm.add(copy);
					
					JMenuItem delet = new JMenuItem("Delete");
					
					delet.addMouseListener(new MouseAdapter(){
						@Override
						public void mousePressed(MouseEvent e) { // Remove Block
							if(Model){
								try {
									String result = "";
									BufferedReader br = new BufferedReader(new FileReader(VFrame.tspfile));
									String s = null;
									while((s = br.readLine())!=null) {
										result = result + s + System.getProperty("line.separator");
									}
									br.close();
									
									result = result.replace(VFrame.tspcode[ModelID] + System.getProperty("line.separator"),"");
									
									FileWriter out = new FileWriter(VFrame.tspfile); // Save the new TSP to local
									out.write(result);
									out.close();
								} catch(Exception e1) {
									e1.printStackTrace();
								}
								VisibleEditor.mainFrame.InstallTSP(VFrame.txt2String(VFrame.tspfile),VFrame.tspfile);
								VisibleEditor.mainFrame.RefreshTSP();
							}else{
								DeleteAll();
								parentFrame.repaint();
							}
						}
					});
					jpm.add(delet);
					
					jpm.show(VisibleEditor.mainFrame, e.getLocationOnScreen().x - VisibleEditor.mainFrame.getLocationOnScreen().x, e.getLocationOnScreen().y - VisibleEditor.mainFrame.getLocationOnScreen().y);
				}
			}
		});
	}
	
	@Override
	protected void paintComponent(Graphics g){
		//UIDesign
		int width = this.getWidth();  
        int height = this.getHeight(); 
		Graphics2D g2d = (Graphics2D) g;  
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);  
		
		for(int i = 0; i<VisibleEditor.cs.size(); i++) {
			if(VisibleEditor.cs.get(i).PaintBlock(this,g)){
				super.paintComponent(g);
				return;
			}
		}
		
		g2d.setColor(new Color(48,28,0)); // Draw back rect
		g2d.fillRect(width-10, 0, 10,height / 3);
		g2d.fillRect(width-10, height - (height / 3) , 10,height / 3);
		
		try{
			if(this.Parent != null && this.Attachment || !VFrame.isWordCharacter(this.code.charAt(0))){
				g2d.setColor(new Color(48,28,0)); // Draw front rect
				g2d.fillRect(0,height / 3 , 10,height - (height / 3) - (height / 3));
				g2d.setColor(new Color(48,28,0));  //Draw main rect
				g2d.fillRect(9,0,width-18,height);
				g2d.setColor(new Color(255,150,0));  
				g2d.fillRect(10,1,width-20,height-2);
			}else{
				g2d.setColor(new Color(48,28,0));  //Draw main rect
				g2d.fillRect(0,0,width-9,height);
				g2d.setColor(new Color(255,150,0));  
				g2d.fillRect(1,1,width-11,height-2);
			}
		}catch(Exception e){
			
		}
		g2d.setColor(new Color(255,150,0)); // Fill the front rect
		g2d.fillRect(1,height / 3 + 1 ,9,height - (height / 3) - (height / 3) - 2);	
		g2d.fillRect(width-11, 1, 10,height / 3 - 2); // Fill the back rect
		g2d.fillRect(width-11, height - (height / 3) + 1, 10,height / 3 - 2);
		
		super.paintComponent(g);
	}
	
	public void UpdateEscape(){
		XMLUtil.AutoEscape(this);
	}
	
	public void UpdateAllEscape(){ // Backward
		UpdateEscape();
		VBlock parent = VBlock.this.Parent;
		while(parent != null){ // Escape
			parent.UpdateEscape();
			try{
				if(parent.Parent.Addons == parent){
					parent = parent.Parent;
				}else{
					parent = null;
				}
			}catch(Exception e2){
				parent = null;
			}
		}
	}

	public VBlock clonez() {
		VBlock o = null;
		o = new VBlock(parentFrame,type);
		o.code = this.code;
		o.escape = this.escape;
		o.Attachment = this.Attachment;
		o.refer = this.refer;
		o.variable = this.variable;
		return o;
	}

	public void RefreshType() {
		if(type == 1) {
			FontMetrics fm = this.getFontMetrics(this.getFont());
			this.setSize(fm.stringWidth(this.getText()) + 40,fm.getHeight()+20);
		}
	}
	
	public void GenerateCode(){
		String gcode = refer.replaceAll("\" ","\"").replaceAll(" \"","\"");
		for(AComponent a:applyment.toArray(new AComponent[0])){
			String ID = a.ID;
			String Context = ((JTextComponent)a.jc).getText();
			gcode = gcode.replaceAll("%"+ID+"%",Context);
			gcode = gcode.replace("%"+ID+"%",Context);
		}
		code = gcode;
	}

	public void AutoSize() {
		if(closing){
			return;
		}
			
		String translated = code;
		if(escape != null){
			translated = escape;
		}
			
		if(VisibleEditor.isWordCharacter(translated.charAt(0)) && VFrame.TranslateEngine != 0) {
			if(VisibleEditor.dict.containsKey(translated)) {
				translated = VisibleEditor.dict.get(translated) + "";
			} else {
				if(tt != null){
					tt.aborted = true;
				}
				tt = new TranslateThread(translated,this);
			}
		}
		FontMetrics fm = getFontMetrics(getFont());
		
		if(escape != null){
			translated = GenerateApplyment(translated ,fm);
		}
			
		setText(translated);
		setSize(fm.stringWidth(translated) + 40,fm.getHeight() + 20);
			
		VBlock parent = VBlock.this;
		VBlock addon = Addons;
		while(addon != null) {
			addon.setLocation(parent.getX() + parent.getWidth() - 10,parent.getY());
			parent = addon;
			addon = addon.Addons;
		}
	}

	public String GenerateApplyment(String translate, FontMetrics fm){
		if(escape == null){
			return translate;
		}
		
		//Edit Box
		int nowX = 0;
		String translated = translate.replaceAll("%"," % ");
		
		if(refer != null && escape != lastEscape || applyment.size() == 0){
			lastEscape = escape;
			
			for(AComponent a:applyment.toArray(new AComponent[0])){
				if(Model){
					ModelGroup.remove(a.jc);
				}else{
					parentFrame.unregisterApply(a);
				}
			}
			applyment.clear();
			
			String[] varString = escape.split("%");
			String[] vts = translated.split("%");
			
			for(int i = 0; i<(int)(varString.length / 2); i++){
				int before = i * 2;
				int ID = i * 2 + 1;
				
				AComponent a = null;
				
				if(variable != null){
					a = new AComponent(VBlock.this, (JComponent)new JTextField(variable.get(varString[ID])), varString[ID]);
				}else{
					a = new AComponent(VBlock.this, (JComponent)new JTextField(""), varString[ID]);
				}

				
				if(i == 0){
					a.x = fm.stringWidth(vts[before]) + 20 - fm.stringWidth(" ");
				}else{
					try{
						a.x = nowX + fm.stringWidth(vts[before]) - fm.stringWidth(" ") * 2;
					}catch(Exception e){
						System.out.println(translated);
					}
				}
				
				a.width = fm.stringWidth(" % " +varString[ID] + " % ");
				a.height = fm.getHeight() + 20;
				a.y = 0;
				
				nowX = a.x + a.width;
				
				if(Model){
					ModelGroup.add(a.jc,1);
				}else{
					parentFrame.registerApply(a);
				}
				
				a.Auto();
				applyment.add(a);
			}
		}
		
		if(Model){
			ModelGroup.repaint();
		}else{
			parentFrame.repaint();
		}
		
		return translated;
	}
	
	public void Refresh() {
		int lastout = 0;
		ArrayList<VBlock> needmatch = new ArrayList<VBlock>();
		//Refresh All Addons
		VBlock parent = VBlock.this;

		parent.AutoSize();//parent.code);

		VBlock addon = VBlock.this.Addons;
		while(addon != null) {
			addon.setLocation(parent.getX() + parent.getWidth() - 10,parent.getY());

			addon.AutoSize();//addon.code);

			parent = addon;
			addon = addon.Addons;
		}

		//Refresh All Sons
		parent = VBlock.this;
		
		if(parent.pat != null && !onDrag){ // Check pat
			parent.pat.Test();
		}
		
		VBlock son = VBlock.this.Son;
		while(son != null) {
			int out = 0;
			if(parent.code.replace("{","").length() != parent.code.length() && parent.code.replace("{","").length() == 0) {
				out ++;
			}
			if(parent.code.replace("}","").length() != parent.code.length() && parent.code.replace("}","").length() == 0) {
				out --;
			}
			VBlock adz = parent.Addons;
			while(adz != null) {
				if(adz.code.replace("{","").length() != adz.code.length() && adz.code.replace("{","").length()==0) {
					if(out < 0){
						out = out + 2;
					}else{
						out ++;
					}
				}
				if(adz.code.replace("}","").length() != adz.code.length() && adz.code.replace("}","").length() == 0) {
					out--;
				}
				adz = adz.Addons;
			}
			
			if(out<0){ // What the Heck
				out = 0;
			}
			
			if(son.code.replace("}","").length() != son.code.length() && son.code.replace("}","").length() == 0) {
				out --;
			}
			
			son.setLocation(parent.getX() + out * 20,parent.getY() + parent.getHeight()); // Get {} In out
			
			if(!onDrag){
				if(out > 0){
					needmatch.add(son); // Throw an needmatch
				}
				if(out < 0){
					VBlock[] nmatches = needmatch.toArray(new VBlock[0]);
					for(int i = nmatches.length - 1; i > -1 ; i--){
						VBlock matching = nmatches[i];
						if(son.getX() == matching.getX()){
							if(son.pat != null && son.pat.son != son){
								son.pat.Remove();
								son.pat = null;
							}
							if(son.pat == null){
								try{
									son.pat = new VAttacher();
									matching.Parent.pat = son.pat;
									son.pat.parent = matching;
									son.pat.son = son;
								
									VisibleEditor.mainFrame.add(son.pat);
									VisibleEditor.mainFrame.jlp.setLayer(son.pat,3);
									VisibleEditor.mainFrame.attachers.add(son.pat);	
								}catch(Exception e){
									e.printStackTrace();
									System.out.println("Wrong: Wrong Code:3-2");
								}
							}
							
							son.pat.Refresh();
							son.pat.Locate();
							
							needmatch.remove(matching);
						}
					}
					
					if(son.pat == null){ //Attach not found
						VBlock up = son.Parent;
						while(up.getX() != son.getX()){
							up = up.Parent;
							if(up == null){
								break;
							}
						}
						if(up != null){
							try{
								son.pat = new VAttacher();
								up.pat = son.pat;
								son.pat.parent = up;
								son.pat.son = son;

								son.pat.Refresh();
							
								VisibleEditor.mainFrame.add(son.pat);
								VisibleEditor.mainFrame.jlp.setLayer(son.pat,3);
								VisibleEditor.mainFrame.attachers.add(son.pat);

								son.pat.Locate();
							}catch(Exception e){
								e.printStackTrace();
								System.out.println("Wrong: Wrong Code:3-2");
							}
						}
					}
				}
			}else{
				if(son.pat != null){
					son.pat.Refresh();
					son.pat.Locate();
				}
			}
			
			son.AutoSize();//son.code);

			//Refresh Addons on Sons
			VBlock p2 = son;
			addon = son.Addons;
			while(addon != null) {
				addon.setLocation(p2.getX() + p2.getWidth() - 10,p2.getY());

				addon.AutoSize();//addon.code);

				p2 = addon;
				addon = addon.Addons;
			}
			//Ends

			if(son.pat != null && !onDrag){ // Check pat
				son.pat.Test();
			}
			
			if(insame){
				if(son.getX() == VBlock.this.getX() && son.Parent.getX() != VBlock.this.getX() && VBlock.this.Parent != null && son.Son != null){ // Move down to up
					VBlock.this.Parent.Son = son.Son;
					son.Son.Parent = VBlock.this.Parent; // Clear Relationship
					son.Son = null;
					break;
				}
			}
			
			parent = son;
			son = son.Son;
		}
		
		if(insame){
			VBlock.this.Parent.Refresh(); // Move that
			insame = false;
		}
		
	}
	
	public void onDestroy(){
		for(AComponent a:applyment.toArray(new AComponent[0])){
			parentFrame.unregisterApply(a);
		}
		applyment.clear();
		closing = true;
	}

	public void Detect() {
		VBlock[] blocks = VBlock.this.parentFrame.blocks.toArray(new VBlock[0]);

		for(int i = 0; i<blocks.length; i++) {
			if(VBlock.this != blocks[i]) {
				// Relationship
				if(VBlock.this.getX() <= blocks[i].getX() + blocks[i].getWidth()
				        && VBlock.this.getX() >= blocks[i].getX()
				        && VBlock.this.getY() <= blocks[i].getY() + blocks[i].getHeight() //Detect Area
				        && VBlock.this.getY() >= blocks[i].getY() + (int)(blocks[i].getHeight() / 2)) {
					
					if(blocks[i].Son == null && !blocks[i].Attachment) {
						VBlock.this.Parent = blocks[i];
						blocks[i].Son = VBlock.this;
						VBlock.this.setLocation(blocks[i].getX(),blocks[i].getY() + blocks[i].getHeight());//Bound Blocks
						blocks[i].Refresh();
						break;
					}
					if(blocks[i].Son != null && !blocks[i].Attachment){
						VBlock lastson = VBlock.this.Son;
						while(lastson.Son != null){
							lastson = lastson.Son;
						}
						blocks[i].Son.Parent = lastson;
						lastson.Son = blocks[i].Son;
						blocks[i].Son = VBlock.this;
						VBlock.this.Parent = blocks[i];
						blocks[i].Refresh();
						break;
					}
				}
				//Attachment
				if(VBlock.this.getX() <= blocks[i].getX() + blocks[i].getWidth()
				        && VBlock.this.getX() >= blocks[i].getX()
				        && VBlock.this.getY() <= blocks[i].getY() + (int)(blocks[i].getHeight()/2) //Detect Area
				        && VBlock.this.getY() >= blocks[i].getY()) {
					if(blocks[i].Addons == null) {
						VBlock.this.Parent = blocks[i];
						blocks[i].Addons = VBlock.this;
						VBlock.this.setLocation(blocks[i].getX() + blocks[i].getWidth() - 10,blocks[i].getY());//Location
						VBlock.this.Attachment = true;
						VBlock.this.Son = null;
						VBlock.this.Refresh();
						break;
					}
				}
			}
		}
	}

	public void DetectDeletion() {
		//Testfor is the Blocks in tools box
		if(this.getLocationOnScreen().x >= VisibleEditor.mainFrame.tsp.getLocationOnScreen().x
		        && this.getLocationOnScreen().x <= VisibleEditor.mainFrame.tsp.getLocationOnScreen().x + VisibleEditor.mainFrame.tsp.getWidth()
		        && this.getLocationOnScreen().y >= VisibleEditor.mainFrame.tsp.getLocationOnScreen().y
		        && this.getLocationOnScreen().y <= VisibleEditor.mainFrame.tsp.getLocationOnScreen().y + VisibleEditor.mainFrame.tsp.getHeight()) { // In Dispose Area
			DeleteAll();
			parentFrame.repaint();
		}
	}
	
	public void DeleteAll(){
		VBlock parent = VBlock.this;
			
		if(parent.pat != null){
			VisibleEditor.mainFrame.remove(parent.pat);
			VisibleEditor.mainFrame.attachers.remove(parent.pat);
			parent.pat.son.pat = null;
			parent.pat.parent.pat = null;
		}
		VisibleEditor.mainFrame.unregisterBlock(parent);

		VBlock addon = VBlock.this.Addons;
		while(addon != null) {
			VisibleEditor.mainFrame.unregisterBlock(addon);

			parent = addon;
			addon = addon.Addons;
		}
		
		//Refresh All Sons
		parent = VBlock.this;
		VBlock son = VBlock.this.Son;
		while(son != null) {
			VisibleEditor.mainFrame.unregisterBlock(son);

			if(son.pat != null){
				VisibleEditor.mainFrame.remove(son.pat);
				VisibleEditor.mainFrame.attachers.remove(son.pat);
				son.pat.son.pat = null;
			}
			//Refresh Addons on Sons
			VBlock p2 = son;
			addon = son.Addons;
			while(addon != null) {
				VisibleEditor.mainFrame.unregisterBlock(addon);

				p2 = addon;
				addon = addon.Addons;
			}
			//Ends
			
			parent = son;
			son = son.Son;
		}
	}

	private MouseAdapter ma = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				JPopupMenu jpm = new JPopupMenu();
				return;
			}

			VisibleEditor.mainFrame.jlp.setLayer(VisibleEditor.mainFrame.tsp,2);
			VisibleEditor.mainFrame.jlp.setLayer(VisibleEditor.mainFrame.psp,2);
			VisibleEditor.mainFrame.jlp.setLayer(VisibleEditor.mainFrame.hscrollbar,2);//Put down
			VisibleEditor.mainFrame.jlp.setLayer(VisibleEditor.mainFrame.vscrollbar,2);

			if(!Model) {
				xOld = e.getXOnScreen();
				yOld = e.getYOnScreen();

				if(VBlock.this.Parent != null){
					insame = true;
				}
				VBlock.this.Refresh();
				insame = false;
				
				//Remove All Escape
				VBlock parent = VBlock.this;
				try{
					while(parent != null){
						try{
							if(parent.Parent.Addons == parent){
								if(parent.escape != null){
									parent.UpdateEscape();
									parent.AutoSize();//parent.code);
									parent.Refresh();
								}
								parent = parent.Parent;
							}else{
								if(parent.escape != null){
									parent.UpdateEscape();
									parent.AutoSize();//parent.code);
									parent.Refresh();
								}
								parent = null;
							}
						}catch(Exception ez){
							if(parent.escape != null){
								parent.UpdateEscape();
								parent.AutoSize();//parent.code);
								parent.Refresh();
							}
							parent = null;
						}
					}
				}catch(Exception e1){
					
				}
				//Ends 
				
				if(VBlock.this.Parent != null) {
					if(VBlock.this.Parent.Son == VBlock.this) {
						VBlock.this.Parent.Son = null; // Clear Relationship
					}
					
					if(VBlock.this.Parent.Addons == VBlock.this) {
						VBlock.this.Parent.Addons = null; // Clear Attachment
						VBlock.this.Attachment = false;
					}
					
					VBlock.this.Parent = null;
				}
				
				if(VBlock.this.pat != null){ // Remove attacher
					VisibleEditor.mainFrame.remove(VBlock.this.pat);
					VisibleEditor.mainFrame.attachers.remove(VBlock.this.pat);
					VAttacher tmpat = VBlock.this.pat;
					tmpat.son.pat = null;
					tmpat.parent.pat = null;
					tmpat = null;
				}

				RefreshType(); // Temp
			} else {

				//GetOrignalParent
				VBlock Originalparent = VBlock.this;
				while(Originalparent.Parent != null) {
					Originalparent = Originalparent.Parent;
				}
				VBlock parent = Originalparent;

				//Clone Parent
				VBlock newparent = parent.clonez();
				newparent.setLocation(e.getXOnScreen() - VisibleEditor.mainFrame.splitpanel.getLocationOnScreen().x - e.getX()
				                      , e.getYOnScreen() - VisibleEditor.mainFrame.splitpanel.getLocationOnScreen().y - e.getY()); // Follow Mouse

				xOld = e.getX();
				yOld = e.getY();

				newp = newparent;

				VisibleEditor.mainFrame.registerBlock(newparent);
				VBlock tmpparent = newparent;

				VBlock addon = parent.Addons;
				while(addon != null) {
					//Clone addons
					VBlock newaddon = addon.clonez();
					newaddon.Parent = tmpparent;
					tmpparent.Addons = newaddon;
					tmpparent = newaddon;
					VisibleEditor.mainFrame.registerBlock(newaddon);

					parent = addon;
					addon = addon.Addons;
				}

				//Clone All Sons
				tmpparent = newparent;
				parent = Originalparent;
				VBlock son = parent.Son;
				while(son != null) {
					VBlock newson = son.clonez();
					newson.Parent = tmpparent;
					tmpparent.Son = newson;
					tmpparent = newson;
					VisibleEditor.mainFrame.registerBlock(newson);

					//Clone Addons on Sons
					VBlock tmpp2 = newson;
					VBlock p2 = son;
					addon = son.Addons;
					while(addon != null) {
						VBlock newaddon = addon.clonez();
						newaddon.Parent = tmpp2;
						tmpp2.Addons = newaddon;
						tmpp2 = newaddon;
						VisibleEditor.mainFrame.registerBlock(newaddon);

						p2 = addon;
						addon = addon.Addons;
					}
					//Ends

					parent = son;
					son = son.Son;
				}

				newparent.Refresh();
			}

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {

				return;
			}
			
			// Auto Complete
			
			/*if(VisibleEditor.isWordCharacter(code.charAt(0)) || code.trim() == "\\." && Parent != null){
				if(Parent != null && Parent.code.trim() != "new"){
					File[] tmp = parentFrame.blockslist.toArray(new File[0]);
					
					VBlock target = VBlock.this;
					if(code.trim() == "\\."){
						target = Parent;
					}
					
					for(File file:tmp) {
						if(file.getName().trim() == target.code.trim()){
							
							break;
						}
					}
				}
			}*/
			
			onDrag = false;

			VisibleEditor.mainFrame.jlp.setLayer(VisibleEditor.mainFrame.tsp,5);
			VisibleEditor.mainFrame.jlp.setLayer(VisibleEditor.mainFrame.psp,6);
			VisibleEditor.mainFrame.jlp.setLayer(VisibleEditor.mainFrame.hscrollbar,5);
			VisibleEditor.mainFrame.jlp.setLayer(VisibleEditor.mainFrame.vscrollbar,5);//Put on Top

			if(!Model) {
				VBlock.this.Detect();
				VBlock.this.DetectDeletion();
				UpdateAllEscape(); // Escape
			} else {
				try{
					newp.Detect();
				}catch(Exception e1){}
				newp.DetectDeletion();
				newp.UpdateAllEscape();
			}
			
			VisibleEditor.mainFrame.ResetScrollBar();
			
			for(int i = 0; i<VisibleEditor.cs.size(); i++) {
				if(!Model){
					VisibleEditor.cs.get(i).OnBlockSet(VBlock.this);
				}else{
					VisibleEditor.cs.get(i).OnBlockSet(newp);
				}
			}
		}

	};

	private MouseMotionAdapter mma = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {

				return;
			}

			if(!Model) {
				VBlock.this.setLocation(VBlock.this.getX() + e.getXOnScreen() - xOld, VBlock.this.getY() +  e.getYOnScreen() - yOld);
				xOld = e.getXOnScreen();
				yOld = e.getYOnScreen();
				onDrag = true;
				VBlock.this.Refresh();
			} else {
				newp.setLocation(e.getXOnScreen() - VisibleEditor.mainFrame.splitpanel.getLocationOnScreen().x - xOld
				                 , e.getYOnScreen() - VisibleEditor.mainFrame.splitpanel.getLocationOnScreen().y - yOld);
				newp.Refresh();
			}
		}
	};
}

class TranslateThread {
	public Thread td = new Thread(new Runnable(){
		@Override
		public void run(){
			String translated = translating;
			
			translated = VisibleEditor.translate(translated);
			FontMetrics fm = target.getFontMetrics(target.getFont());
			
			if(aborted){
				return;
			}
			
			for(AComponent a:target.applyment.toArray(new AComponent[0])){
				if(target.Model){
					target.ModelGroup.remove(a.jc);
				}else{
					target.parentFrame.unregisterApply(a);
				}
			}
			target.applyment.clear();
			
			if(nowEscape != target.escape && nowEscape == translating || nowEscape == null){
				target.tt = new TranslateThread(target.escape,target);
				return;
			}
			
			if(target.escape != null){
				translated = target.GenerateApplyment(translated ,fm);
			}
			
			if(aborted){
				return;
			}
			
			target.setText(translated);
			target.setSize(fm.stringWidth(translated) + 40,fm.getHeight() + 20);
			
			VBlock parent = target;//Refresh All Addons
			VBlock addon = target.Addons;
			while(addon != null) {
				addon.setLocation(parent.getX() + parent.getWidth() - 10,parent.getY());
				parent = addon;
				addon = addon.Addons;
			}
		}
	});
	
	public boolean aborted = false;
	public VBlock target;
	public String translating;
	public String nowEscape = "";
	
	public TranslateThread(String translate,VBlock tb){
		nowEscape = tb.escape;
		
		target = tb;
		translating = translate;
		td.start();
	}
}
