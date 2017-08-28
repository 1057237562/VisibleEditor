import java.awt.*;

public class GeneralPaint extends Event {
	
	@Override
	public boolean PaintBlock(VBlock b,Graphics g){
		switch(b.code){
			case "{":
				g.setColor(new Color(0,28,48)); // Draw rect
				g.fillRect(0, 0, b.getWidth(),b.getHeight());
				g.setColor(new Color(0,100,255)); // Fill rect
				g.fillRect(1, 1, b.getWidth() - 2,b.getHeight() - 2);
				return true;
			case "}":
				if(b.Addons != null){
					DrawOneDirection(b,g,new Color(0,100,255),new Color(0,28,48));
				}else{
					g.setColor(new Color(0,28,48)); // Draw rect
					g.fillRect(0, 0, b.getWidth(),b.getHeight());
					g.setColor(new Color(0,100,255)); // Fill rect
					g.fillRect(1, 1, b.getWidth() - 2,b.getHeight() - 2);
				}
				return true;
			case "@":
				DrawOneDirection(b,g,new Color(150,150,150),new Color(0,0,0));
				return true;
			case "class":
				DrawTwoDirection(b,g,new Color(255,150,0),new Color(48,28,0));
				return true;
			case "static":
				DrawTwoDirection(b,g,new Color(255,150,0),new Color(48,28,0));
				return true;
			case "void":
				DrawTwoDirection(b,g,new Color(255,150,0),new Color(48,28,0));
				return true;
			case "+":
				DrawTwoDirection(b,g,new Color(50,200,0),new Color(10,50,0));
				return true;
			case "-":
				DrawTwoDirection(b,g,new Color(50,200,0),new Color(10,50,0));
				return true;
			case "*":
				DrawTwoDirection(b,g,new Color(50,200,0),new Color(10,50,0));
				return true;
			case "/":
				DrawTwoDirection(b,g,new Color(50,200,0),new Color(10,50,0));
				return true;
			case "=":
				DrawTwoDirection(b,g,new Color(50,200,0),new Color(10,50,0));
				return true;
		}
		return false;
	}
	
	public void DrawOneDirection(VBlock b,Graphics g,Color cf,Color cb){
		g.setColor(cb); // Draw back rect
		g.fillRect(b.getWidth()-10, 0, 10,b.getHeight() / 3);
		g.fillRect(b.getWidth()-10, b.getHeight() - (b.getHeight() / 3) , 10,b.getHeight() / 3);
			
		g.setColor(cb); // Draw rect
		g.fillRect(0, 0, b.getWidth() - 9,b.getHeight());
		g.setColor(cf); // Fill rect
		g.fillRect(1, 1, b.getWidth() - 11,b.getHeight() - 2);
		g.fillRect(b.getWidth()-11, 1, 10,b.getHeight() / 3 - 2); // Fill the back rect
		g.fillRect(b.getWidth()-11, b.getHeight() - (b.getHeight() / 3) + 1, 10,b.getHeight() / 3 - 2);
	}
	
	public void DrawTwoDirection(VBlock b,Graphics g,Color cf,Color cb){
		g.setColor(cb); // Draw back rect
		g.fillRect(b.getWidth()-10, 0, 10,b.getHeight() / 3);
		g.fillRect(b.getWidth()-10, b.getHeight() - (b.getHeight() / 3) , 10,b.getHeight() / 3);
		
		g.setColor(cb); // Draw front rect
		g.fillRect(0,b.getHeight() / 3 , 10,b.getHeight() - (b.getHeight() / 3) - (b.getHeight() / 3));
		g.setColor(cb);  //Draw main rect
		g.fillRect(9,0,b.getWidth()-18,b.getHeight());
		g.setColor(cf); 
		g.fillRect(10,1,b.getWidth()-20,b.getHeight()-2);		
		
		g.setColor(cf); // Fill the front rect
		g.fillRect(1,b.getHeight() / 3 + 1 ,9,b.getHeight() - (b.getHeight() / 3) - (b.getHeight() / 3) - 2);	
		g.fillRect(b.getWidth()-11, 1, 10,b.getHeight() / 3 - 2); // Fill the back rect
		g.fillRect(b.getWidth()-11, b.getHeight() - (b.getHeight() / 3) + 1, 10,b.getHeight() / 3 - 2);
	}
	
	public void DrawAutomaticDirection(VBlock b,Graphics g,Color cf,Color cb){
		g.setColor(cb); // Draw back rect
		g.fillRect(b.getWidth()-10, 0, 10,b.getHeight() / 3);
		g.fillRect(b.getWidth()-10, b.getHeight() - (b.getHeight() / 3) , 10,b.getHeight() / 3);
		
		if(b.Parent != null && b.Attachment || !VFrame.isWordCharacter(b.code.charAt(0))){
			g.setColor(cb); // Draw front rect
			g.fillRect(0,b.getHeight() / 3 , 10,b.getHeight() - (b.getHeight() / 3) - (b.getHeight() / 3));
			g.setColor(cb);  //Draw main rect
			g.fillRect(9,0,b.getWidth()-18,b.getHeight());
			g.setColor(cf);  
			g.fillRect(10,1,b.getWidth()-20,b.getHeight()-2);
		}else{
			g.setColor(cb);  //Draw main rect
			g.fillRect(0,0,b.getWidth()-9,b.getHeight());
			g.setColor(cf);  
			g.fillRect(1,1,b.getWidth()-11,b.getHeight()-2);
		}
		
		g.setColor(cf); // Fill the front rect
		g.fillRect(1,b.getHeight() / 3 + 1 ,9,b.getHeight() - (b.getHeight() / 3) - (b.getHeight() / 3) - 2);	
		g.fillRect(b.getWidth()-11, 1, 10,b.getHeight() / 3 - 2); // Fill the back rect
		g.fillRect(b.getWidth()-11, b.getHeight() - (b.getHeight() / 3) + 1, 10,b.getHeight() / 3 - 2);
	}
}