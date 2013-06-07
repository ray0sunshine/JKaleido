import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class commonOps {
	private Random rand;
	
	public commonOps(){
		rand = new Random();
	}
	
	double dist(int x1, int y1, int x2, int y2){
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
	
	public int rInt(int min, int max){
		return min + rand.nextInt(max-min+1);
	}
	
	public float rFloat(float min, float max){
		return min + rand.nextFloat()*(max-min);
	}

	public double rDouble(double min, double max) {
		return min + rand.nextDouble()*(max-min);
	}
	
	public double arcDeg(int xBase, int yBase, int x, int y){
		double t = Math.toDegrees(Math.atan2(y-yBase, x-xBase));
		if(t<0){
			return t+360;
		}
		return t;
	}
	
	public int arcDegInt(int xBase, int yBase, int x, int y){
		int t = (int)Math.toDegrees(Math.atan2(y-yBase, x-xBase));
		if(t<0){
			return t+360;
		}
		return t;
	}
	
	public int[] intL2A(ArrayList<Integer> list){
		int[] arr = new int[list.size()];
		Iterator<Integer> it = list.iterator();
		for(int i=0; i<arr.length; i++){
			arr[i] = it.next().intValue();
		}
		return arr;
	}
	
	//gotta optimize lines so we push better performance
	//also, wanna smooth lines cuz drawing with a mouse sux....hmmmm how to interpolate....
	//nope, not doing splines...too expensive, opt for the trivial method, weighted sampling, elastic relaxation...w/e I call it what I want
	public ArrayList<Integer> optimizeX(ArrayList<Integer> x, ArrayList<Integer> y, double pruneDist, boolean smooth){
		ArrayList<Integer> returner = new ArrayList<Integer>();
		returner.add(x.get(0));
		int prevX = x.get(0);
		int prevY = x.get(0);
		int secondLast = x.size()-1;
		for(int i=1; i<secondLast; i++){
			if(dist(prevX, prevY, x.get(i), y.get(i)) > pruneDist){
				prevX = x.get(i);
				prevY = y.get(i);
				returner.add(x.get(i));
			}
		}
		returner.add(x.get(secondLast));
		if(smooth && returner.size()>3){
			ArrayList<Integer> sm = new ArrayList<Integer>();
			sm.add(returner.get(0));
			for(int i=1; i<returner.size()-1; i++){
				sm.add((2*returner.get(i)+returner.get(i-1)+returner.get(i+1))/4);
			}
			sm.add(returner.get(returner.size()-1));
			return sm;
		}else{
			return returner;
		}
	}
	
	public ArrayList<Integer> optimizeY(ArrayList<Integer> x, ArrayList<Integer> y, double pruneDist, boolean smooth){
		ArrayList<Integer> returner = new ArrayList<Integer>();
		returner.add(y.get(0));
		int prevX = x.get(0);
		int prevY = x.get(0);
		int secondLast = x.size()-1;
		for(int i=1; i<secondLast; i++){
			if(dist(prevX, prevY, x.get(i), y.get(i)) > pruneDist){
				prevX = x.get(i);
				prevY = y.get(i);
				returner.add(y.get(i));
			}
		}
		returner.add(y.get(secondLast));
		if(smooth && returner.size()>3){
			ArrayList<Integer> sm = new ArrayList<Integer>();
			sm.add(returner.get(0));
			for(int i=1; i<returner.size()-1; i++){
				sm.add((2*returner.get(i)+returner.get(i-1)+returner.get(i+1))/4);
			}
			sm.add(returner.get(returner.size()-1));
			return sm;
		}else{
			return returner;
		}
	}
	
	public double p2seg(int px, int py, int sx1, int sy1, int sx2, int sy2){
		double vx = sx1 - px;
		double vy = sy1 - py;
		double ux = sx2 - sx1;
		double uy = sy2 - sy1;
		double len = ux*ux+uy*uy;
		double det = (-vx*ux)+(-vy*uy);
		if(det < 0){
			return dist(px,py,sx1,sy1);
		}else if(det > len){
			return dist(px,py,sx2,sy2);
		}
		det = ux*vy-uy*vx;
		return Math.sqrt((det*det)/len);
	}
}
