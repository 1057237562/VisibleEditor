import javax.swing.*;
import java.awt.event.*;

public class AComponent{
	
	public VBlock Base;
	public JComponent jc;
	public String ID;
	
	public int x; // as Parent
	public int y;
	
	public int height;
	public int width;
	
	public AComponent(VBlock Parent,JComponent c, String cID){
		Base = Parent;
		jc = c;
		ID = cID;
		if(cID.indexOf("+") != -1 && !Base.Model){
			Base.parentFrame.newBlock.add(AComponent.this);
			Base.parentFrame.GetNewBlocks();
		}
		jc.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
				
			}
 
			public void keyReleased(KeyEvent e) {
				for(int i = 0; i<VisibleEditor.cs.size(); i++) {
					VisibleEditor.cs.get(i).OnApplymentChanged(AComponent.this);
				}
				Base.GenerateCode();
				if(cID.indexOf("+") != -1 && !Base.Model){
					Base.parentFrame.GetNewBlocks();
				}
			}
 
			public void keyTyped(KeyEvent e) {
				
			}
		});
	}
	
	public void Auto(){
		jc.setBounds(Base.getX() + x, Base.getY() + y, width, height);
	}
	
	public VBlock getBase(){
		return Base;
	}
	
	public String getID(){
		return ID;
	}
}