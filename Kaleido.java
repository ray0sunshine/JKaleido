import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Kaleido extends JComponent{
	private int centerRad = 7;
	private commonOps ops = new commonOps();
	
	public int mouseX;
	public int mouseY;
	
	private int lx[] = new int[100];
	private int ly[] = new int[100];
	
	private ColorControl cc = new ColorControl();
	private boolean colorSelecting = false;
	private boolean transforming = false;
	private boolean drawing = false;
	
	private int brCur;
	private int brMax = 20;
	private int brMin = 1;
	
	private static JFrame frame = new JFrame("Kaleido");
	
	private ArrayList<cLine> lines = new ArrayList<cLine>();
	private ArrayList<Integer> lineX = new ArrayList<Integer>();
	private ArrayList<Integer> lineY = new ArrayList<Integer>();
	
	private boolean fade = false;
	private boolean pressure = true;
	private boolean arbStart = false;
	
	private int selectedIdx = -1;
	
	MouseMotionListener mlisten = new MouseMotionListener(){
		public void mouseDragged(MouseEvent e){
			mouseX = e.getX();
			mouseY = e.getY();
			if(!colorSelecting){
				cc.updateMouse(mouseX, mouseY);
				if(!transforming){
					if(drawing){
						lineX.add(mouseX);
						lineY.add(mouseY);
					}
				}else{
					lines.get(selectedIdx).update(getCenterX(), getCenterY(), mouseX, mouseY);
				}
			}
			repaint();
		}
		
		public void mouseMoved(MouseEvent e){
			mouseX = e.getX();
			mouseY = e.getY();
			if(!colorSelecting){
				cc.updateMouse(mouseX, mouseY);
			}
			transforming = false;
			repaint();
		}
	};
	
	MouseWheelListener mwlisten = new MouseWheelListener(){
		public void mouseWheelMoved(MouseWheelEvent e){
			if(e.getWheelRotation() < 0){
				if(brCur < brMax){
					brCur++;
				}
			}else{
				if(brCur > brMin){
					brCur--;
				}
			}
			repaint();
		}
	};
	
	MouseListener melisten = new MouseListener(){
		public void mouseClicked(MouseEvent e){
			int mx = e.getX();
			int my = e.getY();
			if(colorSelecting){
				cc.selectColor(mx, my);
			}else{
				if(ops.dist(mx, my, getCenterX(), getCenterY()) <= centerRad){
					if(e.getClickCount()==1){
						cc.selectRandColor();
					}else{
						lines.clear();
					}
				}else{
					int i = lines.size()-1;
					boolean selected = false;
					while(i>=0){
						if(!selected){
							if(lines.get(i).select(mx-getCenterX(), my-getCenterY())){
								selected = true;
								selectedIdx = i;
							}
						}else{
							lines.get(i).deselect();
						}
						i--;
					}
					
					if(!selected){
						selectedIdx = -1;
					}
				}
			}
			repaint();
		}

		public void mouseEntered(MouseEvent arg0){}

		public void mouseExited(MouseEvent arg0){}

		public void mousePressed(MouseEvent e){
			if(!colorSelecting){
				if(selectedIdx < 0 && ((ops.dist(e.getX(), e.getY(), getCenterX(), getCenterY()) <= centerRad)||arbStart)){
					drawing = true;
				}else if(selectedIdx >= 0 && lines.size() > selectedIdx){
					if(lines.get(selectedIdx).select(mouseX-getCenterX(), mouseY-getCenterY())){
						transforming = true;
						lines.get(selectedIdx).setTrans(getCenterX(), getCenterY(), mouseX, mouseY);
					}else{
						selectedIdx = -1;
					}
				}
			}
		}

		public void mouseReleased(MouseEvent arg0){
			if(!lineX.isEmpty()){
				lines.add(new cLine(ops.intL2A(ops.optimizeX(lineX, lineY, 5, true)), ops.intL2A(ops.optimizeY(lineX, lineY, 5, true)), fade, pressure, cc.curColor, brCur, getCenterX(), getCenterY()));
				lineX = new ArrayList<Integer>();
				lineY = new ArrayList<Integer>();
			}
			if(selectedIdx >= 0 && lines.size() > selectedIdx){
				lines.get(selectedIdx).endTrans();
				transforming = false;
			}
			drawing = false;
			repaint();
		}
	};
	
	KeyAdapter klisten = new KeyAdapter(){
		public void keyReleased(KeyEvent e){}
		
		public void keyPressed(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_SPACE){
				cc.updateMouse(mouseX, mouseY);
				colorSelecting = !colorSelecting;
			}else if(e.getKeyCode() == KeyEvent.VK_F){
				fade = !fade;
			}else if(e.getKeyCode() == KeyEvent.VK_P){
				pressure = !pressure;
			}else if(e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown()){
				if(!lines.isEmpty()){
					lines.remove(lines.size()-1);
					if(selectedIdx == lines.size()){
						selectedIdx = -1;
					}
				}
			}else if(e.getKeyCode() == KeyEvent.VK_DELETE){
				if(selectedIdx >= 0){
					lines.remove(selectedIdx);
					selectedIdx = -1;
				}
			}else if(e.getKeyCode() == KeyEvent.VK_A){
				arbStart = !arbStart;
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
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image img = toolkit.getImage("cross.gif");
		Point hts = new Point(5,5);
		Cursor cCursor = toolkit.createCustomCursor(img, hts, "cc");
		frame.setCursor(cCursor);
	}
	
	public Kaleido(){
		addMouseMotionListener(mlisten);
		addMouseListener(melisten);
		addMouseWheelListener(mwlisten);
		frame.addKeyListener(klisten);
		brCur = 3;
	}
	
	public void paintComponent(Graphics g2d){
		for(int i=0; i<50; i++){
			lx[i] = (i*8)+100;
			ly[i] = (int)(Math.sqrt(i*200)+100);
		}
		Graphics2D g = (Graphics2D)g2d;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(Color.RED);
		g.drawString("Red: " + cc.curColor.getRed(), 10, 20);
		g.setColor(Color.GREEN);
		g.drawString("Green: " + cc.curColor.getGreen(), 10, 32);
		g.setColor(Color.BLUE);
		g.drawString("Blue: " + cc.curColor.getBlue(), 10, 44);
		g.setColor(Color.WHITE);
		g.drawString("Stroke Width: " + brCur, 10, 56);
		if(fade){
			g.drawString("alpha fade [ON]", 10, 68);
		}else{
			g.drawString("alpha fade [OFF]", 10, 68);
		}
		if(pressure){
			g.drawString("simulate pressure [ON]", 10, 80);
		}else{
			g.drawString("simulate pressure [OFF]", 10, 80);
		}
		if(arbStart){
			g.drawString("Arbitrary line start [ON]", 10, 92);
		}else{
			g.drawString("Arbitrary line start [OFF]", 10, 92);
		}
		
		for(cLine l : lines){
			AffineTransform at = g.getTransform();
			l.drawAll(g, getCenterX(), getCenterY());
			g.setTransform(at);
		}
		
		if(!lineX.isEmpty()){
			g.setColor(cc.curColor);
			g.setStroke(new BasicStroke(brCur, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.drawPolyline(ops.intL2A(lineX), ops.intL2A(lineY), lineX.size());
		}
		
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.setColor(cc.curColor);
		g.fillOval(getCenterX()-centerRad, getCenterY()-centerRad, 2*centerRad+1, 2*centerRad+1);
		g.setColor(Color.WHITE);
		g.drawOval(getCenterX()-centerRad, getCenterY()-centerRad, 2*centerRad+1, 2*centerRad+1);
		
		if(colorSelecting){
			cc.drawPicker(g, mouseX, mouseY);
		}else{
			g.drawOval(mouseX-brCur/2, mouseY-brCur/2, brCur, brCur);
		}
	}
	
	private int getCenterX(){
		return getWidth()/2;
	}
	
	private int getCenterY(){
		return getHeight()/2;
	}
}
