import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

/*
-test out horizontal optimized inside polygon check
-optimize polylines by removing vertices that are within a threshold distance of each other
-use arrow controls for rotate and resize
*/

public class Kaleido extends JComponent{
	private int centerRad = 7;
	private commonOps ops = new commonOps();
	
	public int mouseX;
	public int mouseY;
	
	private int lx[] = new int[100];
	private int ly[] = new int[100];
	
	private ColorControl cc = new ColorControl();
	private boolean colorSelecting = false;
	
	private static JFrame frame = new JFrame("Kaleido");
	
	MouseMotionListener mlisten = new MouseMotionListener(){
		public void mouseDragged(MouseEvent e){
			mouseX = e.getX();
			mouseY = e.getY();
			if(!colorSelecting){
				cc.updateMouse(mouseX, mouseY);
			}
			repaint();
		}
		
		public void mouseMoved(MouseEvent e){
			mouseX = e.getX();
			mouseY = e.getY();
			if(!colorSelecting){
				cc.updateMouse(mouseX, mouseY);
			}
			repaint();
		}
	};
	
	MouseAdapter melisten = new MouseAdapter(){
		public void mouseClicked(MouseEvent e){
			int mx = e.getX();
			int my = e.getY();
			if(colorSelecting){
				cc.selectColor(mx, my);
			}else{
				if(ops.dist(mx, my, getCenterX(), getCenterY()) <= centerRad){
					cc.selectRandColor();
				}
			}
			repaint();
		}
	};
	
	KeyAdapter klisten = new KeyAdapter(){
		public void keyReleased(KeyEvent e){
		}
		
		public void keyPressed(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_SPACE){
				cc.updateMouse(mouseX, mouseY);
				colorSelecting = !colorSelecting;
			}
			repaint();
		}
	};
	
	public static void main(String[] args){
		Kaleido canvas = new Kaleido();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.BLACK);
		frame.setSize(900, 650);
		frame.setContentPane(canvas);
		frame.setVisible(true);
	}
	
	public Kaleido(){
		addMouseMotionListener(mlisten);
		addMouseListener(melisten);
		frame.addKeyListener(klisten);
	}
	
	public void paintComponent(Graphics g2d){
		for(int i=0; i<50; i++){
			lx[i] = (i*8)+100;
			ly[i] = (int)(Math.sqrt(i*200)+100);
		}
		Graphics2D g = (Graphics2D)g2d;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		float fwidth = 10;
		
		for(int i=1; i<50; i++){
			g.setColor(new Color(cc.curColor.getRed(),cc.curColor.getGreen(),cc.curColor.getBlue()));
			float nwidth = (float)(Math.sqrt((float)((25.0-Math.abs(i-25))/25.0))*fwidth);
			g.setStroke(new BasicStroke(nwidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.drawLine(lx[i-1], ly[i-1], lx[i], ly[i]);
		}
		
		g.setColor(Color.RED);
		g.drawString("Red: " + cc.curColor.getRed(), 10, 20);
		g.setColor(Color.GREEN);
		g.drawString("Green: " + cc.curColor.getGreen(), 10, 32);
		g.setColor(Color.BLUE);
		g.drawString("Blue: " + cc.curColor.getBlue(), 10, 44);
		
		g.setColor(cc.curColor);
		g.fillOval(getCenterX()-centerRad, getCenterY()-centerRad, 2*centerRad+1, 2*centerRad+1);
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.setColor(Color.WHITE);
		g.drawOval(getCenterX()-centerRad, getCenterY()-centerRad, 2*centerRad+1, 2*centerRad+1);
		
		if(colorSelecting){
			cc.drawPicker(g, mouseX, mouseY);
		}
	}
	
	private int getCenterX(){
		return getWidth()/2;
	}
	
	private int getCenterY(){
		return getHeight()/2;
	}
}
