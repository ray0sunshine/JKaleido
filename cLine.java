import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class cLine {
	private static commonOps ops = new commonOps();
	private int x[];
	private int y[];
	private boolean press;
	private boolean fade;
	private boolean selected;
	private Color color;
	private float width;
	private double selectThresh;
	private float angle, sa, na;
	private float scale, sl, nl;
	
	int tx=0;
	int ty=0;
	
	public cLine(int lx[], int ly[], boolean f, boolean p, Color c, float w, int cx, int cy){
		
		x = new int[lx.length];
		y = new int[lx.length];
		for(int i=0; i<x.length; i++){
			x[i] = lx[i]-cx;
			y[i] = ly[i]-cy;
		}
		
		fade = f;
		press = p;
		color = c;
		width = w;
		if(w > 10){
			selectThresh = w/2;
		}else{
			selectThresh = 5;
		}
		scale = 1;
		nl = 1;
		angle = 0;
	}
	
	public void update(int cx, int cy, int dx, int dy){
		nl = (float)(ops.dist(cx,cy,dx,dy)/sl)*scale;
		float nna = (float)(ops.arcDeg(cx, cy, dx, dy)-sa+Math.toDegrees(angle));
		if(nna < -360){
			nna+=360;
		}else if(nna > 360){
			nna-=360;
		}
		if(!((nna < 0 && nna > -180)||(nna > 180))){
			na = nna;
		}
	}
	
	public void setTrans(int cx, int cy, int mx, int my){
		sl = (float)ops.dist(cx,cy,mx,my);
		sa = (float)ops.arcDeg(cx, cy, mx, my);
	}
	
	public void endTrans(){
		scale = nl;
		angle = (float) Math.toRadians(na);
	}
	
	public boolean select(int mx, int my){
		int bmx = (int)((Math.cos(-angle)*mx-Math.sin(-angle)*my)/scale);
		int bmy = (int)((Math.sin(-angle)*mx+Math.cos(-angle)*my)/scale);
		
		if(iselect(bmx, bmy)){
			return true;
		}
		
		float a = (float) Math.toDegrees(angle);
		if(a < 0){
			a += 360;
		}
		
		if(a < 5){
			return false;
		}
		
		for(float ca=a; ca<365; ca+=a){
			float ax = (float)Math.toRadians(ca);
			bmx = (int)((Math.cos(-ax)*mx-Math.sin(-ax)*my)/scale);
			bmy = (int)((Math.sin(-ax)*mx+Math.cos(-ax)*my)/scale);
			if(iselect(bmx, bmy)){
				return true;
			}
		}
		return false;
	}
	
	public boolean iselect(int mx, int my){
		int minX = x[0];
		int maxX = x[0];
		int minY = y[0];
		int maxY = y[0];
		for(int i=1; i<x.length; i++){
			if(x[i] > maxX){
				maxX = x[i];
			}else if(x[i] < minX){
				minX = x[i];
			}
			
			if(y[i] > maxY){
				maxY = y[i];
			}else if(y[i] < minY){
				minY = y[i];
			}
		}
		
		minX -= selectThresh;
		maxX += selectThresh;
		minY -= selectThresh;
		maxY += selectThresh;
		
		//check bounding box first
		if(mx >= minX && mx <= maxX && my >= minY && my <= maxY){
			for(int i=1; i<x.length; i++){
				if(ops.p2seg(mx, my, x[i-1], y[i-1], x[i], y[i]) < selectThresh){
					selected = true;
					return true;
				}
			}
			selected = false;
		}else{
			selected = false;
		}
		return false;
	}
	
	public void deselect(){
		selected = false;
	}
	
	//Thankyou affine transforms, you keep me from jumping off the roof of MC
	private AffineTransform makeTrans(float angleDelta, float scale, int dx, int dy){
		AffineTransform t = new AffineTransform();
		t.translate(dx, dy);
		t.rotate(angleDelta);
		t.scale(scale, scale);
		return t;
	}
	
	public void drawAll(Graphics2D g, int cx, int cy){
		g.setTransform(makeTrans((float)Math.toRadians(na), nl, cx, cy));
		
		float hi = x.length/2;
		float pressure;
		int alpha;
		int rd = color.getRed();
		int gr = color.getGreen();
		int bl = color.getBlue();
		
		if(selected){
			g.setStroke(new BasicStroke(width+4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.setColor(new Color(255-(rd/2), 255-(gr/2), 255-(bl/2), 192));
			g.drawPolyline(x, y, x.length);
		}
		
		if(press || fade){
			if(press && fade){
				for(int i=1; i<x.length; i++){
					pressure = ((hi-Math.abs(i-hi))/hi)*width;
					alpha = (int)(((hi-Math.abs(i-hi))/hi)*255);
					g.setColor(new Color(rd, gr, bl, alpha));
					g.setStroke(new BasicStroke(pressure, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
					g.drawLine(x[i-1], y[i-1], x[i], y[i]);
				}
			}else if(press){
				g.setColor(color);
				for(int i=1; i<x.length; i++){
					pressure = ((hi-Math.abs(i-hi))/hi)*width;
					g.setStroke(new BasicStroke(pressure, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
					g.drawLine(x[i-1], y[i-1], x[i], y[i]);
				}
			}else{
				g.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
				for(int i=1; i<x.length; i++){
					alpha = (int)(((hi-Math.abs(i-hi))/hi)*255);
					g.setColor(new Color(rd, gr, bl, alpha));
					g.drawLine(x[i-1], y[i-1], x[i], y[i]);
				}
			}
			
		}else{
			g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.setColor(color);
			g.drawPolyline(x, y, x.length);
		}
		
		drawInstance(g, cx, cy);
	}
	
	public void drawInstance(Graphics2D g, int cx, int cy){
		float a = na;
		if(a < 0){
			a += 360;
		}
		
		if(a < 5){
			return;
		}
		
		float hi = x.length/2;
		float pressure;
		int alpha;
		int rd = color.getRed();
		int gr = color.getGreen();
		int bl = color.getBlue();
		
		//set to 365 since it's easier to have a bit more room for adjusting line angles
		for(float ca=a; ca<365; ca+=a){
			g.setTransform(makeTrans((float)Math.toRadians(ca), nl, cx, cy));
			
			if(selected){
				g.setStroke(new BasicStroke(width+4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g.setColor(new Color(255-(rd/2), 255-(gr/2), 255-(bl/2), 192));
				g.drawPolyline(x, y, x.length);
			}
			
			if(press || fade){
				if(press && fade){
					for(int i=1; i<x.length; i++){
						pressure = ((hi-Math.abs(i-hi))/hi)*width;
						alpha = (int)(((hi-Math.abs(i-hi))/hi)*255);
						g.setColor(new Color(rd, gr, bl, alpha));
						g.setStroke(new BasicStroke(pressure, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
						g.drawLine(x[i-1], y[i-1], x[i], y[i]);
					}
				}else if(press){
					g.setColor(color);
					for(int i=1; i<x.length; i++){
						pressure = ((hi-Math.abs(i-hi))/hi)*width;
						g.setStroke(new BasicStroke(pressure, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
						g.drawLine(x[i-1], y[i-1], x[i], y[i]);
					}
				}else{
					g.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
					for(int i=1; i<x.length; i++){
						alpha = (int)(((hi-Math.abs(i-hi))/hi)*255);
						g.setColor(new Color(rd, gr, bl, alpha));
						g.drawLine(x[i-1], y[i-1], x[i], y[i]);
					}
				}
				
			}else{
				g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g.setColor(color);
				g.drawPolyline(x, y, x.length);
			}
		}
	}
}
