import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class ColorControl {
	public Color curColor;
	
	private int curHue, mx, my;
	private double hRad, hCos, hSin, hMin, hMax, iMin, iMax;
	private commonOps ops = new commonOps();
	
	public ColorControl(){
		curColor = new Color(255,0,0);
		curHue = 90;
		mx = 100;
		my = 100;
		hMin = 45;
		hMax = 60;
		iMin = 20;
		iMax = 45;
	}
	
	public void drawPicker(Graphics2D g, int mouseX, int mouseY){
		g.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		g.setColor(curColor);
		g.fillOval(mx-62, my-62, 125, 125);
		for(int i=0; i<360; i++){
			hRad = Math.toRadians(i);
			hCos = Math.cos(hRad);
			hSin = Math.sin(hRad);
			g.setColor(Hue(i));
			g.drawLine(mx+(int)(hMin*hCos), my+(int)(hMin*hSin), mx+(int)(hMax*hCos), my+(int)(hMax*hSin));
			if(i<180){
				g.setColor(Bright(i));
				g.drawLine(mx+(int)(iMin*hCos), my+(int)(iMin*hSin), mx+(int)(iMax*hCos), my+(int)(iMax*hSin));
			}else{
				g.setColor(Saturate(i-180));
				g.drawLine(mx+(int)(iMin*hCos), my+(int)(iMin*hSin), mx+(int)(iMax*hCos), my+(int)(iMax*hSin));
			}
		}
		g.setColor(getColor(mouseX,mouseY));
		g.fillOval(mx-12, my-12, 25, 25);
	}
	
	public void updateMouse(int mouseX, int mouseY){
		mx = mouseX;
		my = mouseY;
	}
	
	public void selectColor(int mouseX, int mouseY){
		int angle = getAngle(mouseX, mouseY);
		double dist = ops.dist(mouseX, mouseY, mx, my);
		if(dist >= hMin && dist <= hMax){
			curColor = Hue(angle);
			curHue = angle;
		}else if(dist >= iMin && dist <= iMax){
			if(angle < 180){
				curColor = Bright(angle);
			}else{
				curColor = Saturate(angle-180);
			}
		}
	}
	
	//too lazy to calculate hue from rgb so I'm just gonna simulate a random selection on the color wheel
	public void selectRandColor(){
		int angle = ops.rInt(0,359);
		double dist = ops.rDouble(iMin+1, hMax-1);
		if(dist >= hMin && dist <= hMax){
			curColor = Hue(angle);
			curHue = angle;
		}else if(dist >= iMin && dist <= iMax){
			if(angle < 180){
				curColor = Bright(angle);
			}else{
				curColor = Saturate(angle-180);
			}
		}
	}
	
	private int getAngle(int mouseX, int mouseY){
		return ops.arcDegInt(mx, my, mouseX, mouseY);
	}
	
	public Color getColor(int mouseX, int mouseY){
		int angle = getAngle(mouseX, mouseY);
		double dist = ops.dist(mouseX, mouseY, mx, my);
		if(dist >= hMin && dist <= hMax){
			return Hue(angle);
		}else if(dist >= iMin && dist <= iMax){
			if(angle < 180){
				return Bright(angle);
			}else{
				return Saturate(angle-180);
			}
		}
		return curColor;
	}
	
	private int brMap(int mChannel, int cChannel, int i){
		if(mChannel > 0){
			return (i*cChannel*255)/(mChannel*180);
		}
		return 0;
	}
	
	private int satMap(int mChannel, int cChannel, int i){
		if(mChannel > 0){
			if(cChannel == 0){
				int c = ((hRed(curHue)*mChannel)/255);
				return c + ((mChannel-c)*i)/180;
			}else if(cChannel == 1){
				int c = ((hGreen(curHue)*mChannel)/255);
				return c + ((mChannel-c)*i)/180;
			}else if(cChannel == 2){
				int c = ((hBlue(curHue)*mChannel)/255);
				return c + ((mChannel-c)*i)/180;
			}
		}
		return 0;
	}
	
	private Color Saturate(int i){
		int cr = curColor.getRed();
		int cg = curColor.getGreen();
		int cb = curColor.getBlue();
		if(hRed(curHue) == 255){
			return new Color(cr, satMap(cr,1,i), satMap(cr,2,i));
		}else if(hGreen(curHue) == 255){
			return new Color(satMap(cg,0,i), cg, satMap(cg,2,i));
		}else{
			return new Color(satMap(cb,0,i), satMap(cb,1,i), cb);
		}
	}
	
	private Color Bright(int i){
		int cr = curColor.getRed();
		int cg = curColor.getGreen();
		int cb = curColor.getBlue();
		if(hRed(curHue) == 255){
			return new Color((i*255)/180, brMap(cr,cg,i), brMap(cr,cb,i));
		}else if(hGreen(curHue) == 255){
			return new Color(brMap(cg,cr,i), (i*255)/180, brMap(cg,cb,i));
		}else{
			return new Color(brMap(cb,cr,i), brMap(cb,cg,i), (i*255)/180);
		}
	}
	
	private Color Hue(int t){
		return new Color(hRed(t),hGreen(t),hBlue(t));
	}
	
	private int hRed(int t){
		if(t >= 210 && t <= 330){
			return 0;
		}else if(t >= 30 && t <= 150){
			return 255;
		}else if(t > 150 && t < 210){
			return ((210-t)*255)/60;
		}else{
			if(t-330 < 0){
				return ((t+30)*255)/60;
			}
			return ((t-330)*255)/60;
		}
	}
	
	private int hGreen(int t){
		if(t >= 90 && t <= 210){
			return 0;
		}else if(t > 30 && t < 90){
			return ((90-t)*255)/60;
		}else if(t > 210 && t < 270){
			return ((t-210)*255)/60;
		}else{
			return 255;
		}
	}
	
	private int hBlue(int t){
		if(t >= 150 && t <= 270){
			return 255;
		}else if(t > 90 && t < 150){
			return ((t-90)*255)/60;
		}else if(t > 270 && t < 330){
			return ((330-t)*255)/60;
		}else{
			return 0;
		}
	}
}
