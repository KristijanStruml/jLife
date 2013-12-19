package custom.swing;

public interface IGridObserver {

	public void cellTurnedOn(int row, int column);

	public void cellTurnedOff(int row, int column);
}
