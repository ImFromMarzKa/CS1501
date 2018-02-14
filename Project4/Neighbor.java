//Riley Marzka
//CS1501
//Project4 (Graphs)
//Due: 4/3/17 (Mon)


//Stores vertex and information about
// 	the edge connected to the first node 
//	in the list
public class Neighbor{

	private static final int COPP_SPEED = 230000000; // (m/s)
	private static final int OPTI_SPEED = 200000000; // (m/s)

	private int vert;
	private int bandWidth;
	private String cableType;
	private int cableLength;
	private double latency;
	public Neighbor next;

	public Neighbor(int num){
		vert = num;
		bandWidth = -1;
		cableType = null;
		cableLength = -1;
		latency = -1;
		next = null;
	}

	public Neighbor(int num, String type, int wid, int len){
		vert = num;
		bandWidth = wid;
		cableType = type;
		cableLength = len;
		
		int speed = 0;
		if(cableType.equals("copper")){
			speed = COPP_SPEED;
		}
		else{
			speed = OPTI_SPEED;
		}

		latency = (double)cableLength / (double)speed;

		next = null;
	}

	public int getVert(){
		return vert;
	}

	public int getBandWidth(){
		return bandWidth;
	}

	public double getLatency(){
		return latency;
	}

	public String getCableType(){
		return cableType;
	}
}