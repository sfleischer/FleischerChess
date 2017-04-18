import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Game implements Runnable{

	public static void main(String[] args){
		SwingUtilities.invokeLater(new Game());
	}

	@Override
	public void run() {
		JFrame frame = new JFrame("Fleischer Chess");
	
		
		Person white = new Person(Polarity.White);
		AI black = new AI(Polarity.Black);
		
		//this is a default model
		ChessModel model = new ChessModel(white, black);
		white.setModel(model);
		black.setModel(model);
		
		Board board = new Board(model, Polarity.White);
		ControlPanel cp = new ControlPanel(board, model);
		
		black.setBoard(board);
		//white.setBoard(board);
		white.move();
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(cp, BorderLayout.CENTER);
		panel.add(board, BorderLayout.WEST);
		
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
		
	}
	
	
}
