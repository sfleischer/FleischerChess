
public interface MoveListener {

	//tell the timers to switch clocks
	public void moved();
	
	//tell the timers to stop
	public void pause();
}
