import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
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
	
	public String escape;

	public boolean Model = false;
	private VBlock newp;
	public VAttacher pat;
	
	public boolean insame = false;

	private VFrame parentFrame;

	public VBlock(VFrame frame,int types) {
		this.setSize(50,50);
		this.addMouseListener(ma);
		this.addMouseMotionListener(mma);

		parentFrame = frame;
		type = types; // temp

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu jpm = new JPopupMenu();
					JMenuItem copy = new JMenuItem("Copy");
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
					jpm.show(VisibleEditor.mainFrame, e.getLocationOnScreen().x - VisibleEditor.mainFrame.getLocationOnScreen().x, e.getLocationOnScreen().y - VisibleEditor.mainFrame.getLocationOnScreen().y);
				}
			}
		});
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		//UIDesign
		
	}
	
	public void UpdateEscape(){
		XMLUtil.AutoEscape(this);
	}
	
	public void UpdateAllEscape(){
		UpdateEscape();
		VBlock parent = VBlock.this.Parent;
		while(parent != null){ // Escape
			XMLUtil.AutoEscape(parent);
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
		return o;
	}

	public void RefreshType() {
		if(type == 1) {
			FontMetrics fm = this.getFontMetrics(this.getFont());
			this.setSize(fm.stringWidth(this.getText()) + 40,fm.getHeight()+20);
		}
	}

	public void AutoSize(String text) {
		String translated = text;
		if(translated == this.code){
			if(this.escape != null && this.escape != this.code){
				translated = this.escape;
			}
		}
		
		if(VisibleEditor.isWordCharacter(translated.charAt(0))) {
			if(VisibleEditor.dict.containsKey(translated)) {
				translated = VisibleEditor.dict.get(translated) + "";
			} else {
				translated = VisibleEditor.translate(translated);
			}
		}
		this.setText(translated);
		FontMetrics fm = this.getFontMetrics(this.getFont());
		this.setSize(fm.stringWidth(translated) + 40,fm.getHeight() + 20);
	}

	public void Refresh() {
		int lastout = 0;
		ArrayList<VBlock> needmatch = new ArrayList<VBlock>();
		//Refresh All Addons
		VBlock parent = VBlock.this;

		parent.AutoSize(parent.code);

		VBlock addon = VBlock.this.Addons;
		while(addon != null) {
			addon.setLocation(parent.getX() + parent.getWidth(),parent.getY());

			addon.AutoSize(addon.code);

			parent = addon;
			addon = addon.Addons;
		}

		//Refresh All Sons
		parent = VBlock.this;
		
		if(parent.pat != null){ // Check pat
			parent.pat.Test();
		}
		
		VBlock son = VBlock.this.Son;
		while(son != null) {
			int out = 0;
			if(parent.code.replace("{","").length() != parent.code.length()) {
				out ++;
			}
			if(parent.code.replace("}","").length() != parent.code.length()) {
				out --;
			}
			VBlock adz = parent.Addons;
			while(adz != null) {
				if(adz.code.replace("{","").length() != adz.code.length()) {
					if(out < 0){
						out = out + 2;
					}else{
						out ++;
					}
				}
				if(adz.code.replace("}","").length() != adz.code.length()) {
					out--;
				}
				adz = adz.Addons;
			}
			
			if(out<0){ // What the Heck
				out = 0;
			}
			
			if(son.code.replace("}","").length() != son.code.length()) {
				out --;
			}
			
			son.setLocation(parent.getX() + out * 20,parent.getY() + parent.getHeight()); // Get {} In out
			
			if(out > 0){
				needmatch.add(son);
			}
			if(out < 0){
				for(VBlock matching:needmatch.toArray(new VBlock[0])){
					if(son.getX() == (matching.getX() - 20)){
						if(son.pat == null){
							son.pat = new VAttacher();
							matching.Parent.pat = son.pat;
							son.pat.parent = matching;
							son.pat.son = son;
						
							VisibleEditor.mainFrame.add(son.pat);
							VisibleEditor.mainFrame.jlp.setLayer(son.pat,3);
							VisibleEditor.mainFrame.attachers.add(son.pat);				
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
						son.pat = new VAttacher();
						up.pat = son.pat;
						son.pat.parent = up.Son;
						son.pat.son = son;

						son.pat.Refresh();
					
						VisibleEditor.mainFrame.add(son.pat);
						VisibleEditor.mainFrame.jlp.setLayer(son.pat,3);
						VisibleEditor.mainFrame.attachers.add(son.pat);

						son.pat.Locate();
					}
				}
			}
			
			son.AutoSize(son.code);

			//Refresh Addons on Sons
			VBlock p2 = son;
			addon = son.Addons;
			while(addon != null) {
				addon.setLocation(p2.getX() + p2.getWidth(),p2.getY());

				addon.AutoSize(addon.code);

				p2 = addon;
				addon = addon.Addons;
			}
			//Ends

			if(son.pat != null){ // Check pat
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
						VBlock.this.setLocation(blocks[i].getX() + blocks[i].getWidth(),blocks[i].getY());//Location
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
			VBlock parent = VBlock.this;

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
									parent.escape = null;
									parent.AutoSize(parent.code);
									parent.Refresh();
								}
								parent = parent.Parent;
							}else{
								if(parent.escape != null){
									parent.escape = null;
									parent.AutoSize(parent.code);
									parent.Refresh();
								}
								parent = null;
							}
						}catch(Exception ez){
							if(parent.escape != null){
								parent.escape = null;
								parent.AutoSize(parent.code);
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

			VisibleEditor.mainFrame.jlp.setLayer(VisibleEditor.mainFrame.tsp,4);
			VisibleEditor.mainFrame.jlp.setLayer(VisibleEditor.mainFrame.psp,5);
			VisibleEditor.mainFrame.jlp.setLayer(VisibleEditor.mainFrame.hscrollbar,4);
			VisibleEditor.mainFrame.jlp.setLayer(VisibleEditor.mainFrame.vscrollbar,4);//Put on Top

			if(!Model) {
				VBlock.this.Detect();
				VBlock.this.DetectDeletion();
				UpdateAllEscape(); // Escape
			} else {
				newp.Detect();
				newp.DetectDeletion();
				newp.UpdateAllEscape();
			}
			
			VisibleEditor.mainFrame.ResetScrollBar();
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

				VBlock.this.Refresh();
			} else {
				newp.setLocation(e.getXOnScreen() - VisibleEditor.mainFrame.splitpanel.getLocationOnScreen().x - xOld
				                 , e.getYOnScreen() - VisibleEditor.mainFrame.splitpanel.getLocationOnScreen().y - yOld);
				newp.Refresh();
			}
		}
	};
}
