import javax.swing.JFrame;
import java.awt.Graphics;
import java.io.File;

public class Event{
	public int TranslateID = -1;
	public void OnLoad(VFrame mf){}
	public String Translate(String text){ return text; }
	public void OnCreatBlock(VBlock block){}
	public void OnCreatApplyment(AComponent applyment){}
	public void OnApplymentChanged(AComponent applyment){}
	public void OnDestroyBlock(VBlock block){}
	public void OnDestroyApplyment(AComponent applyment){}
	public void OnBlockSet(VBlock component){}
	public String OnExport(String translatedcode){ return translatedcode; }
	public boolean PaintBlock(VBlock block,Graphics g){ return false; }
	public void OnLoadTSP(String[] content,File tspfile){}
	public void NewFile(){}
	public void OpenFile(){}
}