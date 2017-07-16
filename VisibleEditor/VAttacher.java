import javax.swing.*;

public class VAttacher extends JButton{
	public VBlock parent;
	public VBlock son;
	
	public void Refresh(){
		this.setSize(20,son.getY() - parent.getY());
	}
	
	public void Locate(){
		this.setLocation(parent.getX()-20,parent.getY());
	}
	
	public void Test(){
		if(parent != null && son != null){
			if((parent.getX()-20) != son.getX()){
				VisibleEditor.mainFrame.remove(this);
				VisibleEditor.mainFrame.attachers.remove(this);
				this.son.pat = null;
				this.parent.pat = null;
			}
		}else{
			VisibleEditor.mainFrame.remove(this);
			VisibleEditor.mainFrame.attachers.remove(this);
			if(this.son != null){
				this.son.pat = null;
			}
			if(this.parent != null){
				this.parent.pat = null;
			}
		}
	}
}