import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EtchedBorder; 

public class JAutoImport extends Event {
	public VFrame mainFrame;
	public ArrayList<VBlock> TmpBlock = new ArrayList<VBlock>();

	@Override
	public void OnLoad(VFrame mf) {
		mainFrame = mf;
	}

	@Override
	public void OnLoadTSP(String[] codes ,File targetFile) {

		if(codes[0].indexOf("(") == -1 && codes[0].indexOf(")") == -1 && codes[0].indexOf(".") != -1) {
			//Find the OriginalParent
			if(mainFrame.blocks.size() > 0) {
				VBlock parent = mainFrame.blocks.get(0);
				while(parent.Parent != null) {
					parent = parent.Parent;
				}

				VBlock son = parent;
				check:while(son.code.indexOf("import") != -1) {
					//Checking duplicated
					VBlock addon = son.Addons;
					String[] packages = codes[0].split("\\.");
					for(String packagename:packages) {
						if(addon != null) {
							if(addon.code.trim() == "\\." && addon.Addons != null) {
								addon = addon.Addons;
							}
							if(addon.code != packagename && addon.code.trim() != "*") {
								son = son.Son;
								continue check;
							}
						}
					}

					return;
				}
				System.out.println(codes[0]); // Need show a msgbox to ask
				VBlock lastblock = new VBlock(mainFrame,1);
				lastblock.code = "import";
				if((parent.getY() - parent.getHeight()) >=0){
					lastblock.setLocation(parent.getX(),parent.getY() - parent.getHeight());
				}else{
					lastblock.setLocation(parent.getX(),0);
				}
				mainFrame.registerBlock(lastblock);
				TmpBlock.add(lastblock);
				parent.Parent = lastblock;
				lastblock.Son = parent;
				VBlock startBlock = lastblock;

				String[] packages = codes[0].split("\\.");
				for(int i = 0; i < packages.length; i++) {
					String packagename = packages[i];
					VBlock block = new VBlock(mainFrame,1);
					block.code = packagename;
					block.Parent = lastblock;
					lastblock.Addons = block;
					block.Attachment = true;
					lastblock = block;
					mainFrame.registerBlock(block);
					TmpBlock.add(lastblock);

					if(i == packages.length - 1) {
						block = new VBlock(mainFrame,1);
						block.code = ";";
						block.Parent = lastblock;
						lastblock.Addons = block;
						block.Attachment = true;
						lastblock = block;
						mainFrame.registerBlock(block);
					} else {
						block = new VBlock(mainFrame,1);
						block.code = ".";
						block.Parent = lastblock;
						lastblock.Addons = block;
						block.Attachment = true;
						lastblock = block;
						mainFrame.registerBlock(block);
					}
					TmpBlock.add(lastblock);
				}

				//Refresh things
				startBlock.Refresh();
				parent.Refresh();

				//JOptionPane.showInternalMessageDialog(mainFrame.jlp, "information","information", JOptionPane.INFORMATION_MESSAGE);
				setToolTip("<html><body><font size=\"3\">Imported Package : "+codes[0]+"</font><p><font size=\"3\"><a href=\'http://www.cancel.com\'>Cancel</a> <a href=\'http://www.close.com\'>Close</a></font></p></body></html>");
				mainFrame.getContentPane().requestFocus();
			}

		}
	}

	public void setToolTip(Icon icon, String msg) {
		ToolTipSingle single = new ToolTipSingle(mainFrame,TmpBlock);
		if (icon != null) {
			single._iconLabel.setIcon(icon);
		}
		single._message.setText(msg);
		single.setLocation(1,1);
	}
	
	public void setToolTip(String msg) {
		setToolTip(null, msg);
	}
}

class ToolTipSingle extends JWindow {
	private static final long serialVersionUID = 1L;
	public JLabel _iconLabel = new JLabel();
	public JEditorPane _message = new JEditorPane();
	public int Gap = 1;
	public Color _bgColor = new Color(255, 255, 225);
	public VFrame mainFrame;
	public ArrayList<VBlock> TmpBlock;
	public ToolTipSingle(VFrame mf,ArrayList<VBlock> tb) {
		initComponents();
		mainFrame = mf;
		TmpBlock = tb;
		setSize(200,85);
		setAlwaysOnTop(true);
		_message.setEditable(false);
		_message.setContentType("text/html");
		_message.addHyperlinkListener(new HyperlinkListener() { 
			public void hyperlinkUpdate(HyperlinkEvent e) { 
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					String ez = e.getURL().toString();
					if(ez.indexOf("http://www.cancel.com") != -1){
						for(VBlock b:TmpBlock.toArray(new VBlock[0])){
							mainFrame.unregisterBlock(b);
							if(b.Son != null){
								//b.Son.setLocation(b.Son.getX(),b.Son.getY() - b.Son.getHeight());
								b.Son.Parent = null;
							}
						}
						mainFrame.repaint();
					}
					setVisible(false);
				} 
			} 
		});
		Thread td = new Thread(new Runnable(){
			@Override
			public void run(){
				try{
					Thread.sleep(2000);
				}catch(Exception e){
					
				}
				setVisible(false);
			}
		});
		td.start();
	}

	private void initComponents() {
		//setSize(250,150);
		//_message.setFont(getMessageFont());
		setVisible(true);
		JPanel externalPanel=new JPanel(new BorderLayout(1,1));
		externalPanel.setBackground(_bgColor);
		JPanel innerPanel=new JPanel(new BorderLayout(Gap,Gap));
		innerPanel.setBackground(_bgColor);
		_message.setBackground(_bgColor);
		//_message.setMargin(new Insets(4,4,4,4));
		EtchedBorder etchedBorder=(EtchedBorder)BorderFactory.createEtchedBorder();
		externalPanel.setBorder(etchedBorder);
		externalPanel.add(innerPanel);
		_message.setForeground(Color.BLACK);
		innerPanel.add(_iconLabel,BorderLayout.WEST);
		innerPanel.add(_message,BorderLayout.CENTER);
		getContentPane().add(externalPanel);
	}
	
}