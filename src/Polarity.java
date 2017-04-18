
public enum Polarity {
	Black,
	White;
	
	public static Polarity opposite(Polarity p){
		return p == White ? Black : White; 
	}
}
