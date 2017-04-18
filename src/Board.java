import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Board extends JPanel{

	public static final int SPACE_X = 10; //the space buffer between the frame
	public static final int SPACE_Y = 10; //space buffer in the y direction
	public static final int PIECE_SPACE = 0; //space between the board line and the piece
	
	private ChessModel model; //the master model of the board (the one the user sees)
	private Polarity side; //the side to display on the bottom of screen
	
	int xunit; //the space of one square in the x direction
	int yunit; //the space of one square in the y direction
	int origin_x; //the origin of where to draw the board
	int origin_y; //the origin of where to draw the board
	int width;    //the width of the whole board
	int height;   //the height of the whole board
	
	Piece selected; //the piece that the player has selected
	Piece premove; //the piece that is given a premove option
	Location before;
	Location after; //shows the before and after moves a player made
	boolean running; //is the game being played? 
	
	
	public Board(ChessModel m, Polarity p){
		this.setPreferredSize(new Dimension(600,600));
		this.setMinimumSize(new Dimension(500,500));
		MouseHandler mh = new MouseHandler();
		this.addMouseListener(mh);
		this.addMouseMotionListener(mh);
		model = m;
		side = p;
		running = true;
	}
	
	/**
	 * Sets the board with a new model and repaints to show the model
	 * @param m The new chess model
	 */
	public void setModel(ChessModel m, Polarity bottom){
		model = m;
		selected = null;
		premove = null;
		running = true;
		side = bottom;
	}
	
	/*********************************************************************************
	 ------------------------------- PAINTING METHODS --------------------------------
	 ********************************************************************************/
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		drawBoard(g2);
		if(selected != null)
			drawLegalMoves(g2);
		if(premove != null)
			drawPremove(g2);
		if(before != null && after != null)
			drawBeforeAndAfter(g2);
		drawCheck(g2);
		drawPieces(g2);
		g2.dispose();
	}
	
	public void drawBoard(Graphics2D g2){
		//set the width and height of the board
		int size = Math.min(getWidth(), getHeight()) - 10;
		int offset = (side == Polarity.White) ? 0 : 1;
		
		
		//find the origin of the board in pixel coordinates
		origin_x = (getWidth() - size)/2;
		origin_y = (getHeight() - size)/2;
		xunit = (int) (1.0 / 8 * size);
		yunit = (int) (1.0 / 8 * size);
		
		g2.setStroke(new BasicStroke(3));
		g2.setColor(Color.black);
		g2.drawRect(origin_x, origin_y, xunit * 8, yunit * 8);
		g2.setStroke(new BasicStroke(1));
		
		//fill the board with paint
		Color light = new Color(240, 217, 185);
		Color dark = new Color(180, 135, 102);
		
		//draw the grid
		g2.setColor(Color.black);
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				int x = i * xunit;
 				int y = j * yunit;
				g2.setColor( (i + j + offset) % 2 == 0 ? light : dark);
				g2.fillRect(origin_x + x, origin_y + y, xunit, yunit); 
			}
		}
	}
	
	public void drawPieces(Graphics2D g2){
		int size = Math.min(getWidth(), getHeight()) - 10;
		size = (size - 16 * PIECE_SPACE)/8;
		for(int row = 0; row < 8; row++){
			for(int col = 0; col < 8; col++){
				Piece p = model.getPiece(row, col);
				if(p == null)
					continue;
				int x = (int) (col * xunit);
				double y = (side == Polarity.White) ? 
						(7 - row) * yunit 
						: row * yunit;
				p.draw(g2, origin_x + x + PIECE_SPACE, 
						(int) (origin_y + y + PIECE_SPACE), size, size);
			}
		}
	}
	
	/**
	 * Precondition: selected is not null
	 * @param g2
	 */
	public void drawLegalMoves(Graphics2D g2){
		Color highlight = new Color(0, 100, 0, 80);
		double radius = xunit / 6;
		Set<Location> moves = selected.getLegalMoves(model);
		moves.add(selected.getLocation());
		g2.setPaint(highlight);
		for(Location loc : moves){
			double x = origin_x + loc.col * xunit;
			double y = (side == Polarity.White) ? 
					origin_y + (7 - loc.row) * yunit 
					: origin_y + loc.row * yunit;
			if(model.doesPieceExistAt(loc)){
				g2.fillRect((int) x, (int) y, (int) xunit, (int) yunit);
			}
			else{
				g2.fill(new Ellipse2D.Double(
						x + (xunit/2) - radius, 
						y + (yunit/2) - radius, 
						radius*2, 
						radius*2));
			}
		}
	}
	/**
	 * Precondition: premove is not null
	 */
	public void drawPremove(Graphics2D g2){
		Color highlight = new Color(33, 33, 33, 80);
		double radius = xunit / 6;
		Set<Location> moves = premove.getPremoveLocations();
		moves.add(premove.getLocation());
		g2.setPaint(highlight);
		for(Location loc : moves){
			double x = origin_x + loc.col * xunit;
			double y = (side == Polarity.White) ? 
					origin_y + (7 - loc.row) * yunit 
					: origin_y + loc.row * yunit;
			if(model.doesPieceExistAt(loc)){
				g2.fillRect((int) x, (int) y, (int) xunit, (int) yunit);
			}
			else{
				g2.fill(new Ellipse2D.Double(
						x + (xunit/2) - radius, 
						y + (yunit/2) - radius, 
						radius*2, 
						radius*2));
			}
		}
	}
	
	public void drawCheck(Graphics2D g2){
		Location king = model.findLocationOfPlayerKing(model.getCurrentSide());
		if((selected == null || !selected.getLocation().equals(king))
				&& model.isPlayerInCheck(model.getCurrentSide()) ){
			int x = origin_x + king.col * xunit;
			int y = side == Polarity.White ? 
					(origin_y + (7 - king.row) * yunit) 
					: origin_y + king.row * yunit;
			Color highlight = new Color(200, 0, 0, 100);
			g2.setPaint(highlight);
			g2.fillRect(x, y, (int) xunit, (int) yunit);
		}
	}
	
	/**
	 * Precondition: global variables before and after are not null
	 * @param g2
	 */
	public void drawBeforeAndAfter(Graphics2D g2){
		Color shade = new Color(88, 152, 38, 120);
		Location back = model.getTakebackLocation();
		Location forward = model.getCurrentLocation();
		if(back == null || forward == null)
			return;
		int bx = origin_x + back.col * xunit;
		int by = side == Polarity.White ? 
				(origin_y + (7 - back.row) * yunit) 
				: origin_y + back.row * yunit;
		int ax = origin_x + forward.col * xunit;
		int ay = side == Polarity.White ? 
				(origin_y + (7 - forward.row) * yunit) 
				: origin_y + forward.row * yunit;
		g2.setPaint(shade);
		g2.fillRect(bx, by, xunit, yunit);
		g2.fillRect(ax, ay, xunit, yunit);
	}
	
	/*********************************************************************************
	 ------------------------------- MOVEMENT METHODS --------------------------------
	 ********************************************************************************/
	public boolean makeMove(Location loc){
		if(selected == null || selected.getLocation().equals(loc) 
				|| !model.canMove())
			return false;
		Set<Location> moves = selected.getLegalMoves(model);
		//check if the selected location is in the set of legal moves
		//if the move was successful, switch players and turn off all selected measures
		Location l = selected.getLocation();
		if(moves.contains(loc) && model.movePiece(l, loc) != -1){
			before = l;
			after = loc;
			selected = premove;
			premove = null;
			return true;
		} else
			return false;
	}
	
	/**
	 * If the piece was not selected initially, then select it
	 * @param loc The location of the event
	 */
	public void select(Location loc){
		Piece p = model.getPiece(loc);
		if(p == null){
			selected = null;
			premove = null;
		} else if(p == selected){
			selected = null;
		} else if(p == premove){
			premove = null;
		} else if(p.getPolarity() == model.getCurrentSide() && model.canMove()) {
			selected = p;
		} else if(p.getPolarity() != model.getCurrentSide() && !model.canMove()){
			premove = p;
		}
		repaint();
	}
	
	public void showCheckmate(Polarity side){
		JOptionPane.showMessageDialog
		(null, side + " wins!");
		running = false;
	}
	
	public void showStalemate(){
		JOptionPane.showMessageDialog
		(null, "Stalemate!");
		running = false;
	}
	
	private class MouseHandler extends MouseAdapter{
		
		public Location findLocation(MouseEvent e){
			int x = e.getX();
			int y = e.getY();
			int col = (int) ((x - 1.0 * origin_x) / xunit);
			int row = (int) ((y - 1.0 * origin_y) / yunit);
			row = side == Polarity.White ? 7 - row : row;
			return new Location(row, col);
		}
		
		@Override
		public void mousePressed(MouseEvent e){
			
			Location loc = findLocation(e);
			if(running && !makeMove(loc))
				select(loc);
			repaint();
			if(running && model.isPlayerCheckmated(model.getCurrentSide())){
				model.dispatchPause();
				showCheckmate(Polarity.opposite(model.getCurrentSide()));
			} else if(running && model.isPlayerStalemated(model.getCurrentSide())){
				model.dispatchPause();
				showStalemate();
			}
		}
	}
	
}
