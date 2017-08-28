import java.util.*;
import javax.swing.text.JTextComponent;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class JAutoFill extends Event{
	public VFrame mainFrame;
	public Map<AComponent,AComponent> vlist = new HashMap<AComponent,AComponent>();
	public AComponent last = null;
	public JWindow fill;
	public static JList<String> rlist = new JList<String>();
	public static JScrollPane rt = new JScrollPane(rlist);
	public VBlock nowb;
	//Settings
	public boolean AutoShow = true;
	public boolean AutoInsertEnd = true;
	public Map<String,String> dict = null;
	
	@Override
	public void OnLoad(VFrame mf){
		mainFrame = mf;
		fill = new JWindow(mf);
		fill.setContentPane(rt);
		fill.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				fill.setVisible(false);
			}
			@Override
			public void focusGained(FocusEvent e) {
				
			}
		});
		
		rlist.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				fill.setVisible(false);
			}
			@Override
			public void focusGained(FocusEvent e) {
				
			}
		});

		rlist.addMouseListener(new MouseAdapter() { // Open the TSP from Search
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					JList myList = (JList) e.getSource();
					int index = myList.getSelectedIndex();
					String obj = "";
					if(AutoInsertEnd){
						obj = dict.get(myList.getModel().getElementAt(index).toString()) + ";";
					}else{
						obj = dict.get(myList.getModel().getElementAt(index).toString());
					}
					
					if(nowb.Addons != null && nowb.Addons.code.trim().equals(".")){
						toBlock(obj,nowb.Addons);
					}else{
						VBlock block = new VBlock(mainFrame,1);
						block.code = ".";
						block.Attachment = true;
						block.Parent = nowb;
						nowb.Addons = block;
						mainFrame.registerBlock(block);
						toBlock(obj,nowb.Addons);
					}
				}
			}
		});
	}
	
	public void toBlock(String code,VBlock p){
		String[] codes = XMLUtil.fromCodeStack(code);
		
		VBlock parent = p;
		for(String s:codes){
			VBlock block = new VBlock(mainFrame,1);
			block.code = s;
			block.Attachment = true;
			block.Parent = parent;
			parent.Addons = block;
			mainFrame.registerBlock(block);
			parent = block;
		}
	}
	
	@Override
	public void NewFile(){
		vlist = new HashMap<AComponent,AComponent>();
	}
	
	@Override
	public void OpenFile(){
		vlist = new HashMap<AComponent,AComponent>();
	}
	
	@Override
	public void OnCreatApplyment(AComponent c){
		if(c.getBase().refer.trim().equals("%1% %+2% = new %1%")){
			if(c.getID().equals("1")){
				last = c;
			}
			if(c.getID().equals("+2")){
				if(last != null){
					vlist.put(c,last);
					last = null;
				}
			}
			
		}
	}
	
	@Override
	public void OnBlockSet(VBlock b){
		if(AutoShow && b.Addons != null){ // Decided to Show up
			return;
		}
		
		VBlock target = b;
		if(b.Parent != null && b.code.trim().equals(".")){
			target = b.Parent;
		}
		nowb = target;
		
		for (Map.Entry<AComponent, AComponent> entry: vlist.entrySet()) {
			JComponent key = ((AComponent)entry.getKey()).jc;
			if(target.code.trim().equals(((JTextComponent)key).getText().trim())){
				JComponent value = ((AComponent)entry.getValue()).jc;
				ShowWindow(((JTextComponent)value).getText());
				break;
			}
			
		}
	}
	
	public void ShowWindow(String classname){
		fill.setSize(mainFrame.psp.getWidth()-50,mainFrame.psp.getHeight()+50);
		dict = new HashMap<String,String>();
		File[] tmp = mainFrame.blockslist.toArray(new File[0]);
		for(File file:tmp){
			if(file == null){
				continue;
			}
			String filename = file.getName();
			if(filename.replace(classname.trim(),"").length() != filename.length()){
				if(filename.split("\\.")[0].trim().equals(classname.trim())){
					// Show the Window
					String[] codes = VFrame.txt2String(file);
					ArrayList<String> translated = new ArrayList<String>();
					for(int i = 1; i < codes.length; i++){
						String td = translate(codes[i].split("\\(")[0]) + "(" + codes[i].split("\\(")[1];
						translated.add(td);
						dict.put(td,codes[i]);
					}
					ListModel<String> jlm = new DefaultComboBoxModel<String>(translated.toArray(new String[0]));
					rlist.setModel(jlm);
					fill.setVisible(true);
					fill.setLocation(nowb.getLocationOnScreen().x + nowb.getWidth(),nowb.getLocationOnScreen().y + nowb.getHeight());
					fill.setFocusable(true);
					rlist.setFocusable(true);
					rlist.requestFocus();
					break;
				}
				
			}
			
		}
	}
	
	public String translate(String s){
		if(mainFrame.TranslateEngine == 0){
			return s;
		}
		if(VisibleEditor.dict.containsKey(s)) {
				return VisibleEditor.dict.get(s) + "";
			} else {
				return VisibleEditor.translate(s);
			}
	}
}