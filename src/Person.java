import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

public class Person extends Player{

	public Person(Polarity p) {
		super(p);
		canMove = true;
	}

	@Override
	public void move() {
		//this method does nothing
		
	}
	

	@Override
	public void promote() {
		//ask the user which piece they want to promote to
		Object[] options = {"Knight", "Bishop", "Rook" , "Queen"};
		int n = JOptionPane.showOptionDialog(null,
		"Pawn Promoted!",
		"Please choose your piece:",
		JOptionPane.YES_NO_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,     //do not use a custom Icon
		options,  //the titles of buttons
		null); //default button title
		
		int id = ChessModel.QUEEN;
		switch(n){
		case 0: id = ChessModel.KNIGHT; break;
		case 1: id = ChessModel.BISHOP; break;
		case 2: id = ChessModel.ROOK; break;
		case 3: id = ChessModel.QUEEN; break;
		}
		
		model.promotePawn(id);
		
	}


}
