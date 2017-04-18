import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class ControlPanel extends JPanel{
	
	Board board; //the board
	ChessModel model; //the model
	JLabel playerClock;
	JLabel opponentClock;
	Polarity currentPlayer; //polarity of the current player
	Polarity player;
	boolean running; //fix this
	
	JButton help; //always the help button
	JButton takeback; //always the help button
	JButton resign; //the resign or new game button
	JButton newgame; //creates a new game
	
	Timer playerTimer;
	Timer opponentTimer;
	
	String instructions = 
			"Welcome to Fleischer Chess! If you do not know how to play chess,s"
			+ "\n please familiarize yourself before playing Fleischer Chess.\n"
			+ "To move a piece, click on the piece you wish to move and then click"
			+ "\n on a valid space. If you wish to quit the "
			+ "game, click on the resign button.\n"
			+ "If you wish to play the a different opponent (a person or computer), \n"
			+ " click on the "
			+ "New Game button and navigate through the options \n"
			+ "Note that there is no way to change the clock time and that \n"
			+ "each player will only have 5 minutes to play, no more" 
			+ "no less. ";

	public ControlPanel(Board b, ChessModel m){
		board = b;
		model = m;
		player = Polarity.White;
		currentPlayer = Polarity.White; //first to move is white
		running = false;
		this.setPreferredSize(new Dimension(250,600));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setMaximumSize(new Dimension(250,600));
		
		createClocks();
		createTimers(5); //5 minutes per clock
		model.addMoveListener(new MoveHandler());
		
	}
	
	/**
	 * 
	 * @param minutes number of minutes to put on the clock
	 */
	public void createTimers(int mins){
		playerTimer = new Timer(1000, 
				new TimerHandler(player, playerClock, mins));
		opponentTimer = new Timer(1000,
				new TimerHandler(Polarity.opposite(player), opponentClock, mins));
	}
	
	public void createClocks(){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setMaximumSize(new Dimension(400, 60 + 120 + 10));
		
		Font timerfont = new Font("SansSerif", Font.PLAIN, 30);
		playerClock = new JLabel("05:00");
		playerClock.setFont(timerfont);
		playerClock.setBackground(Color.white);
		playerClock.setBorder(BorderFactory.createLineBorder(Color.black));
		playerClock.setAlignmentX(Component.CENTER_ALIGNMENT);
		playerClock.setOpaque(true);
		
		opponentClock = new JLabel("05:00");
		opponentClock.setFont(timerfont);
		opponentClock.setBackground(Color.white);
		opponentClock.setBorder(BorderFactory.createLineBorder(Color.black));
		opponentClock.setAlignmentX(Component.CENTER_ALIGNMENT);
		opponentClock.setOpaque(true);
		
		this.add(Box.createVerticalGlue());
		panel.add(opponentClock);
		addInGameButtons(panel);
		panel.add(playerClock);
		this.add(panel);
		this.add(Box.createVerticalGlue());
	}
	
	public void addInGameButtons(JPanel panel){
		int width = 140;
		int height = 40;
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.setPreferredSize(new Dimension(width*2, height*3 + 10));
		buttonPanel.setMaximumSize(new Dimension(400, height*4 + 10));
		
		help = new JButton("Help");
		help.setPreferredSize(new Dimension(width, height));
		help.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				JOptionPane.showMessageDialog(
						null,
						instructions);
			}
		});
		
		takeback = new JButton("Takeback");
		takeback.setPreferredSize(new Dimension(width, height));
		takeback.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				model.takeback();
				board.repaint();
			}
			
		});
		
		resign = new JButton("Resign");
		resign.setPreferredSize(new Dimension(width, height));
		resign.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				board.showCheckmate(Polarity.opposite(model.getCurrentSide()));
				playerTimer.stop();
				opponentTimer.stop();
			}
			
		});
		
		newgame = new JButton("New Game");
		newgame.setPreferredSize(new Dimension(width, height));
		newgame.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] options = {"Person", "Computer"};
				int n = JOptionPane.showOptionDialog(null,
				"Choose your opponent",
				"Choose player",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,     //do not use a custom Icon
				options,  //the titles of buttons
				null); //default button title
				
				
				Object[] options2 = {"White", "Black"};
				int m = JOptionPane.showOptionDialog(null,
				"Choose your side",
				"Choose side",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,     //do not use a custom Icon
				options2,  //the titles of buttons
				null); //default button title
				
				player = (m == 0) ? Polarity.White : Polarity.Black;
				currentPlayer = Polarity.White;
				if(n == 0){
					Player person = new Person(player);
					Player person2 = new Person(Polarity.opposite(player));
					ChessModel mod = new ChessModel(person, person2);
					mod.addMoveListener(new MoveHandler());
					person.setModel(mod);
					person2.setModel(mod);
					model = mod;
					board.setModel(model, player);
					board.repaint();
				} else if(n == 1){
					Player person = new Person(player);
					AI ai = new AI(Polarity.opposite(player));
					ChessModel mod = new ChessModel(person, ai);
					mod.addMoveListener(new MoveHandler());
					person.setModel(mod);
					ai.setModel(mod);
					ai.setBoard(board);
					model = mod;
					board.setModel(model, player);
					board.repaint();
					if(player == Polarity.Black)
						ai.move();
				}
				playerTimer.stop();
				opponentTimer.stop();
	
				playerClock.setText("05:00");
				opponentClock.setText("05:00");
				createTimers(5);
			}
		});
		 
		
		JPanel helpPanel = new JPanel();
		helpPanel.add(help);
		helpPanel.setPreferredSize(new Dimension(width, height));
		
		JPanel takePanel = new JPanel();
		takePanel.add(takeback);
		takePanel.setPreferredSize(new Dimension(width, height));
		
		JPanel resignPanel = new JPanel();
		resignPanel.add(resign);
		resignPanel.setPreferredSize(new Dimension(width, height));
		 
		JPanel drawPanel = new JPanel();
		drawPanel.add(newgame);
		drawPanel.setPreferredSize(new Dimension(width, height));
		
		buttonPanel.add(helpPanel);
		//buttonPanel.add(takePanel);
		buttonPanel.add(resignPanel);
		buttonPanel.add(drawPanel);
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(buttonPanel);
	}
	
	private class TimerHandler implements ActionListener{
		JLabel label;
		int minutes;
		int seconds;
		
		public TimerHandler(Polarity p, JLabel l, int mins){
			label = l;
			minutes = mins;
			seconds = 0;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(minutes == 0 && seconds == 0){
				board.showCheckmate(
						Polarity.opposite(currentPlayer)); //end of game
				playerTimer.stop(); //stop the clocks
				opponentTimer.stop();
			} else if(seconds == 0){
				minutes--;
				seconds = 59;
			} else {
				seconds--;
			}
			
			String mins = Integer.toString(minutes);
			String secs = Integer.toString(seconds);
			if(mins.length() == 1)
				mins = "0" + mins;
			if(secs.length() == 1)
				secs = "0" + secs;
			label.setText(mins + ":" + secs);
		}
		
	}
	
	private class MoveHandler implements MoveListener{

		@Override
		public void moved() {
			if(currentPlayer == player){
				playerTimer.stop();
				currentPlayer = Polarity.opposite(player);
				opponentTimer.start();
			} else {
				opponentTimer.stop();
				currentPlayer = player;
				playerTimer.start();
			}
			
		}
		
		@Override 
		public void pause(){
			playerTimer.stop();
			opponentTimer.stop();
		}
		
	}
	
}
