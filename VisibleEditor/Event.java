import javax.swing.JFrame;
import java.awt.Graphics;

public class Event{
	public int TranslateID = -1;
	public void OnLoad(VFrame mf){}
	public String Translate(String text){ return text; }
	public void OnCreatBlock(VBlock block){}
	public String OnExport(String translatedcode){ return translatedcode; }
	public boolean PaintBlock(VBlock block,Graphics g){ return false; }
}