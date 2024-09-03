package ca.utm.utoronto.assignment2.ThreeMusketeers;

import java.awt.event.MouseEvent;


import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class BoardPanel extends GridPane implements EventHandler<ActionEvent> {

    private final View view;
    private final Board board;
    private boolean selected = false; // true if a cell has been clicked in consideration for moving in the board
    private int cellCol = 0, cellRow = 0; // stored the row and column of the selected piece that's about to be moved
    
    /**
     * Constructs a new GridPane that contains a Cell for each position in the board
     *
     * Contains default alignment and styles which can be modified
     * @param view
     * @param board
     */
    public BoardPanel(View view, Board board) {
        this.view = view;
        this.board = board;

        // Can modify styling
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #181a1b;");
        int size = 550;
        this.setPrefSize(size, size);
        this.setMinSize(size, size);
        this.setMaxSize(size, size);

        setupBoard();
        updateCells();
    }


    /**
     * Setup the BoardPanel with Cells
     */
    private void setupBoard(){ // TODO
    	// gets a list of all the cells in the baords array of the current board
    	List<Cell> cells = board.getAllCells();
    	// adds all the cell button into locations in the grid based on their coordinate positions
    	for (Cell cell: cells) {
    		this.add(cell, cell.getCoordinate().row, cell.getCoordinate().col);
    	}
    }

    /**
     * Updates the BoardPanel to represent the board with the latest information
     *
     * If it's a computer move: disable all cells and disable all game controls in view
     *
     * If it's a human player turn and they are picking a piece to move:
     *      - disable all cells
     *      - enable cells containing valid pieces that the player can move
     * If it's a human player turn and they have picked a piece to move:
     *      - disable all cells
     *      - enable cells containing other valid pieces the player can move
     *      - enable cells containing the possible destinations for the currently selected piece
     *		
     * If the game is over:
     *      - update view.messageLabel with the winner ('MUSKETEER' or 'GUARD')
     *      - disable all cells
     */
    protected void updateCells(){ // TODO   	
    	// disables and sets to default color all the cells in the board if either the musketeers or the guard won
    	if (board.isGameOver()) { 
    		List<Cell> cells = board.getAllCells();
    		for (Cell cell: cells) {
        		cell.setDisable(true);
        		cell.setDefaultColor();
        	}
    		// sets the message label to say weather Musketeer or Guard WON. 
    		view.messageLabel.setText(board.getWinner().toString() + " WON");
    	}
    	// if its the computer's turn then all the cells and buttons in the board are disabled  
    	else if (!view.model.isHumanTurn()) {
    		// disable buttons
    		view.restartButton.setDisable(true);
    		view.saveButton.setDisable(true);
    		view.undoButton.setDisable(true);
    		
    		// disable cells and set to default color
    		List<Cell> cells = board.getAllCells();
    		for (Cell cell: cells) {
        		cell.setDisable(true);
        		cell.setDefaultColor();
        	}
    	}
    	// if it's the human players turn & they haven't picked anything then disable all cells expect the ones have >= 0 possible destinations this turn    
    	else if (view.model.isHumanTurn() && selected == false) {
    		List<Cell> cells = board.getAllCells();
    		for (Cell cell: cells) {
    			// Disable all cells
        		cell.setDisable(true);
        		cell.setDefaultColor();
        		
        		// if cell isn't null and still has possible moves this turn then enable it and set the handle() to activate when clicked
        		if (cell.getPiece() != null && cell.getPiece().getType().equals(board.getTurn())) {	        		
		        	if (board.getPossibleDestinations(cell).size() > 0) {
		        		cell.setDisable(false);
		        		cell.setOptionsColor();
		        		cell.setOnAction(e -> handle(e));
		        	}
        		}
        	}
    	}
    	// if it's the human players turn & they have picked a piece then disable all cells expect the selected piece and it's destination pieces 
    	else if (view.model.isHumanTurn() && selected) {    				
    		List<Cell> cells = board.getAllCells();
    		for (Cell cell: cells) {
        		cell.setDisable(true);
    		}
    		
    		// enable selected piece and set the handle() to activate when it's clicked
    		board.board[cellRow][cellCol].setDisable(false);
    		board.board[cellRow][cellCol].setOnAction(e -> handle(e));
    		
    		// if within range of the board & a valid destination piece then enable right destination cell and set the handle() to activate when it's clicked
    		if (0 <= cellCol + 1 && cellCol + 1 <= 4) {
    			Move move = new Move(board.board[cellRow][cellCol], board.board[cellRow][cellCol + 1]);
    			if (board.isValidMove(move)) {
					board.board[cellRow][cellCol + 1].setDisable(false);
					board.board[cellRow][cellCol + 1].setOnAction(e -> handle(e));
    			}
			}
    		// if within range of the board & a valid destination piece then enable top destination cell and set the handle() to activate when it's clicked
			if (0 <= cellRow + 1 && cellRow + 1 <= 4) {
				Move move = new Move(board.board[cellRow][cellCol], board.board[cellRow + 1][cellCol]);
    			if (board.isValidMove(move)) {
					board.board[cellRow + 1][cellCol].setDisable(false);
					board.board[cellRow + 1][cellCol].setOnAction(e -> handle(e));
    			}
			}
			// if within range of the board & a valid destination piece then enable left destination cell and set the handle() to activate when it's clicked
			if (0 <= cellCol - 1 && cellCol - 1 <= 4) {
				Move move = new Move(board.board[cellRow][cellCol], board.board[cellRow][cellCol - 1]);
    			if (board.isValidMove(move)) {
					board.board[cellRow][cellCol - 1].setDisable(false);
					board.board[cellRow][cellCol - 1].setOnAction(e -> handle(e));
    			}
			}
			// if within range of the board & a valid destination piece then enable bottom destination cell and set the handle() to activate when it's clicked
			if (0 <= cellRow - 1 && cellRow - 1 <= 4) {
				Move move = new Move(board.board[cellRow][cellCol], board.board[cellRow - 1][cellCol]);
    			if (board.isValidMove(move)) {
					board.board[cellRow - 1][cellCol].setDisable(false);
					board.board[cellRow - 1][cellCol].setOnAction(e -> handle(e));
    			}
			}
    	}
    	 
    	
    }

    /**
     * Handles Cell clicks and updates the board accordingly
     * When a Cell gets clicked the following must be handled:
     *  - If it's a valid piece that the player can move, select the piece and update the board
     *  - If it's a destination for a selected piece to move, perform the move and update the board
     * @param actionEvent
     */
    @Override    
    public void handle(ActionEvent actionEvent) { // TODO    	
    	Button sourceButton = (Button) actionEvent.getSource();
        
    	List<Cell> cells = board.getAllCells();
    	// sets all cells to a default color
		for (Cell cell: cells) {
			cell.setDefaultColor();
			// checks if the cell is the one that was clicked
			if (sourceButton.equals(cell)) {
				// if a piece hasn't been selected then highlight the clicked piece in dark red as the selected agent while highlighting its valid destination pieces with bright red 
				if (!selected) {
					// gets the row & column of the clicked cell and highlight the clicked piece in dark red to indicate that it as the selected agent
					cellCol = cell.getCoordinate().col;
					cellRow = cell.getCoordinate().row;	
					cell.setAgentFromColor();            	
					
					/* if within range of the board & a valid destination piece then highlight the right cell as bright red using setAgentToColor() to 
					 * indicate that its a valid destination piece of the selected piece */
					if (0 <= cellCol + 1 && cellCol + 1 <= 4) {
						Move move = new Move(board.board[cellRow][cellCol], board.board[cellRow][cellCol + 1]);
		    			if (board.isValidMove(move)) {
		    				board.board[cellRow][cellCol + 1].setAgentToColor();
		    			}
					}
					/* if within range of the board & a valid destination piece then highlight the top cell as bright red using setAgentToColor() to 
					 * indicate that its a valid destination piece of the selected piece */
					if (0 <= cellRow + 1 && cellRow + 1 <= 4) {
						Move move = new Move(board.board[cellRow][cellCol], board.board[cellRow + 1][cellCol]);
		    			if (board.isValidMove(move)) {
		    				board.board[cellRow + 1][cellCol].setAgentToColor();
		    			}
					}
					/* if within range of the board & a valid destination piece then highlight the left cell as bright red using setAgentToColor() to 
					 * indicate that its a valid destination piece of the selected piece */
					if (0 <= cellCol - 1 && cellCol - 1 <= 4) {
						Move move = new Move(board.board[cellRow][cellCol], board.board[cellRow][cellCol - 1]);
		    			if (board.isValidMove(move)) {
		    				board.board[cellRow][cellCol - 1].setAgentToColor();
		    			}
					}
					/* if within range of the board & a valid destination piece then highlight the bottom cell as bright red using setAgentToColor() to 
					 * indicate that its a valid destination piece of the selected piece */
					if (0 <= cellRow - 1 && cellRow - 1 <= 4) {
						Move move = new Move(board.board[cellRow][cellCol], board.board[cellRow - 1][cellCol]);
		    			if (board.isValidMove(move)) {
		    				board.board[cellRow - 1][cellCol].setAgentToColor();
		    			}
					}
					
					// set selected to true to show that a cell has been clicked in consideration for moving
					selected = true;
					break;
				}
				// if a piece has already been selected for moving then another click to a enabled cell indicate that a destination cell has been clicked
				else {
					// gets the row and column information of the destination cell clicked
					int moveToCol = cell.getCoordinate().col;
					int moveToRow = cell.getCoordinate().row;	
					
					// if the destination cell is the same cell as the cell that been selected to be moved, no moves are made and the board resets to the previous state
					if (cellCol == moveToCol && cellRow == moveToRow) {
						view.setShowBaord();
						view.setUndoButton();
						selected = false;
					}
					/* if the destination cell clicked is one of the possible destinations of the move selected cell thats being moved, the piece in the 
					 * selected cell is moved to the destination cell to indicate that a move has been made in the board */
					else if ((cellCol == moveToCol + 1 && cellRow == moveToRow) || (cellCol == moveToCol - 1 && cellRow == moveToRow) || (cellCol == moveToCol && cellRow == moveToRow + 1) || (cellCol == moveToCol && cellRow == moveToRow - 1)) {
						Move move = new Move(board.board[cellRow][cellCol], board.board[moveToRow][moveToCol]);
						view.model.move(move);
						view.setShowBaord();
						view.setUndoButton();
						selected = false;				
					}
					
				}
			}
    	}
    	// updates the board after peices have been clicked   	
    	updateCells();
    }
}
