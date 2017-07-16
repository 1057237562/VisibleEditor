import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JBlockImport extends Event {

	public static File folder;
	public static File file;
	public static File targetfolder;
	public static VFrame maf;

	public void OnLoad(VFrame mf) {
		maf = mf;

		JMenuBar jmb = mf.getJMenuBar();
		JMenu java = new JMenu("JBlockImport");
		JMenuItem ij = new JMenuItem("Import Jar");
		ij.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.showOpenDialog(new JLabel());
				file = jfc.getSelectedFile();

				folder = new File(System.getProperty("user.dir") + "\\Temp");
				folder.mkdir();

				JFileChooser jfz = new JFileChooser();
				jfz.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfz.showDialog(new JLabel(),"Select");
				targetfolder = jfz.getSelectedFile();

				Thread td = new Thread() {
					public void run() {
						try {
							uncompress(file,folder);
						} catch(Exception e) {
							e.printStackTrace();
						}
						maf.Refreshpkgl();
					}
				};

				td.start();
				folder.delete();
			}
		});
		java.add(ij);
		jmb.add(java);
	}

	public static void uncompress(File jarFile, File tarDir) throws IOException {
		JarFile jfInst = new JarFile(jarFile);
		Enumeration<JarEntry> enumEntry = jfInst.entries();
		while (enumEntry.hasMoreElements()) {
			JarEntry jarEntry = enumEntry.nextElement();
			File tarFile = new File(tarDir, jarEntry.getName());
			makeFile(jarEntry, tarFile);
			if (jarEntry.isDirectory()) {
				continue;
			}
		}
	}

	public static void makeFile(JarEntry jarEntry, File fileInst) throws IOException {
		if (!fileInst.exists()) {
			if (!jarEntry.isDirectory()) {
				URL url = file.toURI().toURL();
				StringBuilder sb = new StringBuilder();
				if(url != null) {
					URLClassLoader loader = new URLClassLoader(new URL[] {url});
					Class c = null;
					try {
						if(fileInst.getParent().split("Temp\\\\").length == 1) {
							System.out.println("Process:"+fileInst.getName().substring(0,fileInst.getName().lastIndexOf(".")));
							c = loader.loadClass(fileInst.getName().substring(0,fileInst.getName().lastIndexOf(".")));
						} else {
							System.out.println("Process:"+fileInst.getParent().split("Temp\\\\")[1].replace("\\",".")+"."+fileInst.getName().substring(0,fileInst.getName().lastIndexOf(".")));
							c = loader.loadClass(fileInst.getParent().split("Temp\\\\")[1].replace("\\",".") +"."+fileInst.getName().substring(0,fileInst.getName().lastIndexOf(".")));
						}

					} catch(Exception e) {
						e.printStackTrace();
					}
					if(c != null) {
						Method[] m = c.getMethods();
						for(int i = 0; i<m.length; i++) {
							StringBuilder sp = new StringBuilder();
							Class<?>[] para = m[i].getParameterTypes();
							for(int j = 0; j<para.length; j++) {
								sp.append(para[j].getName());
								if(j<para.length - 1) {
									sp.append(",");
								}
							}
							sb.append(m[i].getName()+"("+sp.toString()+")"+System.getProperty("line.separator"));
						}
					}
				}
				try {
					File output = new File(targetfolder.getAbsolutePath() + fileInst.getAbsolutePath().split("Temp")[1]); // Get Path in Target Folder
					output.getParentFile().mkdirs();
					FileWriter out = new FileWriter(output); // Save the dictionary to the Local
					out.write(fileInst.getAbsolutePath().split("Temp\\\\")[1].replaceAll("\\\\","\\.").replace(".class","")+System.getProperty("line.separator")+sb.toString()+"");
					out.close();
				} catch(Exception e) {
					e.printStackTrace();
				}

			}
		}
	}
}