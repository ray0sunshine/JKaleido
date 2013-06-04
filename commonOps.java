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
}
