//Riley Marzka
//CS1501
//Project4 (Graphs)
//Due: 4/3/17 (Mon)

public class SpanEdge{
	private int parent;
	private double late;

	public SpanEdge(int v, double l){
		parent = v;
		late = l;
	}

	public int getParent(){
		return parent;
	}

	public void setParent(int v){
		parent = v;
	}

	public double getLatency(){
		return late;
	}

	public void setLatency(double l){
		late = l;
	}
}