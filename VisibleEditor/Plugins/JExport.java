import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class JExport extends Event {

	public JFrame jf = new JFrame();
	public JTextPane jtp = new JTextPane();
	
	public JFrame in = new JFrame();
	public JList<String> jl = new JList<String>();
	public JButton add = new JButton();
	public JButton remove = new JButton();
	public JPanel tpanel = new JPanel();
	public static JFrame homeframe = new JFrame();
	public static JTextPane inpuz = new JTextPane();
	public static JButton jbz = new JButton();
	
	public ArrayList<String> impo = new ArrayList<String>();

	public void OnLoad(VFrame mf) {
		jf.setSize(1200,250);
		jf.setTitle("Console");
		jf.setLayout(new BorderLayout());
		jf.setContentPane(new ScrollPane());
		jf.add(jtp);
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\javahome.cfg"));
			inpuz.setText(br.readLine());
		}catch(Exception e){
		
		}
		homeframe.setSize(600,70);
		homeframe.setTitle("Java Home:");
		homeframe.add(inpuz,BorderLayout.CENTER);
		homeframe.add(jbz,BorderLayout.EAST);
		jbz.setText("Browse");
		jbz.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.showOpenDialog(new JLabel());
				File file = jfc.getSelectedFile();
				inpuz.setText(file.getAbsolutePath()+"\\");
				try{
					FileOutputStream out = new FileOutputStream(new File(System.getProperty("user.dir") + "\\javahome.cfg"),false);
					out.write(inpuz.getText().getBytes());
					out.close();
				}catch(Exception e1){
			
				}
			}
		});
		
		inpuz.addKeyListener(new java.awt.event.KeyAdapter(){
		
			@Override
            public void keyReleased(KeyEvent e) {
			try{
				int ca = inpuz.getCaretPosition();
				System.out.println(inpuz.getText().substring(inpuz.getText().length()-1));
				if(inpuz.getText().substring(inpuz.getText().length()-1) != "\\"){
					inpuz.setText(inpuz.getText()+"\\");
					inpuz.setCaretPosition(ca);
				}
			}catch(Exception e2){
			
			}

			try{
				FileOutputStream out = new FileOutputStream(new File(System.getProperty("user.dir") + "\\javahome.cfg"),false);
				out.write(inpuz.getText().getBytes());
				out.close();
			}catch(Exception e1){
			
			}
                
            }
        });
		
		in.setSize(600,300);
		in.setTitle("Import List");
		in.setLayout(new BorderLayout());
		in.add(new JScrollPane(jl),BorderLayout.CENTER);
		add.setText("Import");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.showOpenDialog(new JLabel());
				File file = jfc.getSelectedFile();
				
				if(file != null){
					impo.add(file.getAbsolutePath());
					RefreshList();
					saveImportList();
				}
			}
		});
		remove.setText("Remove");
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				impo.remove(jl.getModel().getElementAt(jl.getSelectedIndex()));
				RefreshList();
				saveImportList();
			}
		});
		tpanel.add(add);
		tpanel.add(remove);
		in.add(tpanel,BorderLayout.SOUTH);
		
		JMenuBar jmb = mf.getJMenuBar();
		JMenu java = new JMenu("JExport");
		JMenuItem ex = new JMenuItem("Export");
		ex.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileOutputStream out = new FileOutputStream(new File(VisibleEditor.filename),false);
					out.write(VisibleEditor.mainFrame.export().getBytes());
					out.close();
				} catch(Exception e1) {

				}
				try {
					Thread th = new Thread() {
						public void run() {
							try {
								Process p = Runtime.getRuntime().exec(inpuz.getText() + "javac \""+VisibleEditor.filename+"\"");
								out(inpuz.getText() + "javac \""+VisibleEditor.filename+"\"");//export
								p.waitFor(); // Wait Export Complete
								Console(p.getErrorStream());
							} catch(Exception e2) {
								System.out.println(e2);
							}
						}
					};

					th.start();
				} catch(Exception e1) {

				}
			}
		});
		java.add(ex);
		JMenuItem run = new JMenuItem("Export & Run");
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileOutputStream out = new FileOutputStream(new File(VisibleEditor.filename),false);
					out.write(VisibleEditor.mainFrame.export().getBytes());
					out.close();
				} catch(Exception e1) {

				}
				try {
					File target = new File(VisibleEditor.filename);

					Thread th = new Thread() {
						public void run() {
							try {
							Process  p;
							
								if(impo.size() != 0){
									p = Runtime.getRuntime().exec(inpuz.getText() + "javac \""+VisibleEditor.filename+"\"");
									out(inpuz.getText() + "javac "+String.join(";",impo.toArray(new String[0]))+" \""+VisibleEditor.filename+"\"");//export
									p.waitFor(); // Wait Export Complete
									Console(p.getErrorStream());
								}else{
									p = Runtime.getRuntime().exec(inpuz.getText() + "javac \""+VisibleEditor.filename+"\"");
									out(inpuz.getText() + "javac \""+VisibleEditor.filename+"\"");//export
									p.waitFor(); // Wait Export Complete
									Console(p.getErrorStream());
								}
								
								out(inpuz.getText() + "java -cp \""+target.getParent()+"\" \"" + target.getName().split("\\.")[0]+"\"");
								p = Runtime.getRuntime().exec(inpuz.getText() + "java -cp \""+target.getParent()+"\" "+target.getName().split("\\.")[0]);//run
								Console(p.getInputStream());
							} catch(Exception e2) {
								System.out.println(e2);
							}
						}
					};

					th.start();


				} catch(Exception e1) {
					System.out.println(e1);
				}
			}
		});
		java.add(run);
		JMenuItem im = new JMenuItem("Import Settings");
		im.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				in.setVisible(true);
				try{
					BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\import.cfg"));
					String s = br.readLine();
					impo = new ArrayList<String>(Arrays.asList(s.split(",")));
					RefreshList();
				}catch(Exception er){
				
				}
			}
		});
		java.add(im);
		JMenuItem jh = new JMenuItem("Java Home");
		jh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				homeframe.setVisible(true);
			}
		});
		java.add(jh);
		jmb.add(java);
	}

	public void RefreshList(){
		ListModel<String> jListModel = new DefaultComboBoxModel<String>(impo.toArray(new String[0]));
		jl.setModel(jListModel);
	}
	
	public void saveImportList () {
		try {
			FileWriter out = new FileWriter(System.getProperty("user.dir") + "\\import.cfg"); // Save the dictionary to the Local
			out.write(String.join(",",impo.toArray(new String[0])));
			out.close();
		} catch(Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void Console(InputStream p) {
		jtp.setText(jtp.getText()+"\n-------------------------------------------------------");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(p));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			jf.setVisible(true);
			jtp.setText(jtp.getText()+"\n"+sb.toString());
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void out(String content){
		jtp.setText(jtp.getText()+"\n"+content);
	}
	
}