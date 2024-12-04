package application;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


public class Main extends Application {

	private GridPane grid;
	private GridPane gridcontainer;

	int rowmax = 6;
	int colmax = 7;
	int droplocrow = -1;
	int droploccol = -1;
	int winnerid = -1;
	int[][] integer2DArray;
	boolean blockneeded = false;
	int blockcol = -1;

	TextField rowtext;
	TextField coltext;
	Label winnerstatus;

	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Game.fxml"));
			Scene scene = new Scene(root,900,500);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			gridcontainer = (GridPane) scene.lookup("#gridcontainer");
			ButtonBar bb = (ButtonBar) scene.lookup("#bbar");
			// Get the row and column text.
			// Parse it to int
			// check to see that it is reasonable
			// set the values
			winnerstatus = (Label) bb.getButtons().get(0);
			rowtext = (TextField) bb.getButtons().get(2);
			coltext = (TextField) bb.getButtons().get(4);
			Button b = (Button) bb.getButtons().get(5);		
			b.setOnAction(fillGrid);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	EventHandler<ActionEvent> fillGrid = new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent arg0) {
			winnerid = -1;
			winnerstatus.setText("");
			grid = new GridPane();
			gridcontainer.getChildren().clear();
			gridcontainer.add(grid, 0, 0);
			String rowstring = rowtext.getText();
			try {
				rowmax = Integer.valueOf(rowstring);
				if ( rowmax > 9 ) {
					rowmax = 9;
					rowtext.setText("9");
				}
			} catch (Exception e) {
				rowmax = 6;
				rowtext.setText("6");
			}
			String colstring = coltext.getText();
			try {
				colmax = Integer.valueOf(colstring);
				if ( colmax > 18 ) {
					colmax = 18;
					coltext.setText("18");
				}
			} catch (Exception e) {
				colmax = 7;
				coltext.setText("7");
			}
			integer2DArray = new int[rowmax][colmax];
			for (int row = 0; row < rowmax; row++) {
				for (int col = 0; col < colmax; col++) {
					try {
						for (int i = 0; i < rowmax; i++) {
							for (int j = 0; j < colmax; j++) {
								integer2DArray[i][j] = 0;
							}
						}
						Pane cell = (Pane) FXMLLoader.load(getClass().getResource("Cell.fxml"));
						Circle c = (Circle) cell.getChildren().get(0);
						c.setId(row + "x" + col);
						c.setOnMouseClicked(cellClick);
						grid.add(cell, col, row);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	};
	EventHandler<MouseEvent> cellClick = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			blockneeded = false;
			boolean s = false;
			blockcol=-1;
			if(winnerid != -1)
				return;
			//System.out.println(event.getSource());
			Circle c = (Circle) event.getSource();
			String theid = c.getId();
			// Make what is below this a method (maybe called drop)
			// that take "theid", "color" and "player number"
			// as an argument along with color
			// of the token.
			// First player's token is the click
			String parts[] = theid.split("x");
			//System.out.println(parts[0] + " " + parts[1]);
			int clickcol = Integer.valueOf(parts[1]);

			boolean p1 = drop(1, clickcol, Paint.valueOf("#FCFC58"));
			WINNER(1);
			if(winnerid != -1)
			{
				return;
			}
			//almost(1);

			if (!p1) {
				return;
			}
			for(int i = 0; i<colmax; i++)
			{
				boolean space = hasspace(i);
				if(space)
				{
				blockneeded=false;
				almost(2,i);
				if(blockneeded)
				{
					break;
				}
				}
			}
			if(blockneeded == true)
			{
				drop(2, blockcol, Paint.valueOf("#DD4444"));
				WINNER(2);
				if(winnerid != -1)
				{
					return;
				}
			}
			
			blockneeded=false;
			boolean p2 = false;
			for(int i = 0; i<colmax; i++)
			{
				boolean space = hasspace(i);
				if(space)
				{
					blockneeded=false;
					almost(1,i);
					if(blockneeded)
					{
						break;
					}
				}
			}
			if(blockneeded == true)
			{
				drop(2, blockcol, Paint.valueOf("#DD4444"));
			}
			else {
			while (!p2) {
				int randcol = ThreadLocalRandom.current().nextInt(0, colmax);
				p2 = drop(2, randcol, Paint.valueOf("#DD4444"));
				//p2 = drop(2, 6, Paint.valueOf("#DD4444"));
			}
			}
			WINNER(2);

			// Check to see if the user that clicked won.

			// Now the computer has to drop a random token.
			// Generate a token id, check to see if that column is full.
			// if not drop it, if so pick another
			// 
			// playerID = 2
			// drop(playerID, getRandomColumnWithEmpty(), player2Color)

			// Check to see if the computer one.
		}

	};

	private boolean drop(int playerID, int col, Paint color) {
		boolean done = false;
		String goodid = "";
		int chipcount = 0;
		for (int i = 0; i < rowmax; i++) {
			if ( integer2DArray[i][col] > 0 ) {
				chipcount = chipcount + 1;
			}
		}
		if ( chipcount >= rowmax) {
			return false;
		}
		if (!done) {
			for (int i = 0; i < rowmax; i++) {
				if (integer2DArray[i][col] > 0) {
					integer2DArray[i - 1][col] = playerID;
					goodid = (i-1)+"x"+col;
					droploccol = col;
					droplocrow = i-1;
					done = true;
					break;
				}
			}
		}
		if (!done) {
			integer2DArray[rowmax - 1][col] = playerID;
			goodid = (rowmax-1)+"x"+col;
			droploccol = col;
			droplocrow = rowmax-1;
			done = true;
		}

		if(done){
			List children = grid.getChildren();
			for(int k = 0; k<rowmax*colmax; k++)
			{
				Pane toChange = (Pane) children.get(k);
				Circle circa = (Circle) toChange.getChildren().get(0);
				if(circa.getId().equals(goodid))
				{
					circa.setFill(color);
				}
			}
		}
		if(playerID == 1)
		{
		System.out.println("Log Connect 4 Game: Player played in column " + (col+1));
		}
		
		if(playerID == 2)
		{
			System.out.println("Log Connect 4 Game: Computer played in column " + (col+1));
		}
		return done;

	}
	private void WINNER(int playerid) 
	{		
		int connect = 0;
		int down = rowmax-droplocrow;
		int up = droplocrow;
		for(int i =1; i<down; i++)
		{
			if(integer2DArray[droplocrow+i][droploccol] == playerid)
			{
				connect++;
			}
			else {break;}
		}
		for(int i =1; i<up; i++)
		{
			if(integer2DArray[droplocrow-i][droploccol] == playerid)
			{
				connect++;
			}
			else {break;}
		}
		if(connect >= 3)
		{
			//System.out.println("WINNER UPDOWN");
			if(playerid == 1) {
				winnerstatus.setText("You win");}
			else {winnerstatus.setText("You lose");}
			
			winnerid = playerid;
			return;
		}
		
		connect = 0;
		int left = droploccol+1;
		int right = colmax-droploccol;
		for(int i =1; i<right; i++)
		{
			if(integer2DArray[droplocrow][droploccol+i] == playerid)
			{
				connect++;
			}
			else {break;}
		}
		for(int i =1; i<left; i++)
		{
			if(integer2DArray[droplocrow][droploccol-i] == playerid)
			{
				connect++;
			}
			else {break;}
		}
		if(connect >= 3)
		{
			//System.out.println("WINNER LEFTRIGHT");
			if(playerid == 1) {
				winnerstatus.setText("You win");}
			else {winnerstatus.setText("You lose");}
			winnerid = playerid;
			return;
		}
		
		connect = 0;
		for(int i =1; (i<right) && (i<up); i++)
		{
			if(integer2DArray[droplocrow-i][droploccol+i] == playerid)
			{
				connect++;
			}
			else {break;}
		}
		for(int i =1; (i<left && i<down); i++)
		{
			if(integer2DArray[droplocrow+i][droploccol-i] == playerid)
			{
				connect++;
			}
			else {break;}
		}
		if(connect >= 3)
		{
			//System.out.println("WINNER DIAGUP");
			if(playerid == 1) {
				winnerstatus.setText("You win");}
			else {winnerstatus.setText("You lose");}
			winnerid = playerid;
			return;
		}
		
		connect = 0;
		for(int i =1; (i<left) && (i<up); i++)
		{
			if(integer2DArray[droplocrow-i][droploccol-i] == playerid)
			{
				connect++;
			}
			else {break;}
		}
		for(int i =1; (i<right && i<down); i++)
		{
			if(integer2DArray[droplocrow+i][droploccol+i] == playerid)
			{
				connect++;
			}
			else {break;}
		}
		if(connect >= 3)
		{
			//System.out.println("WINNER DIAGDOWN");
			if(playerid == 1) {
				winnerstatus.setText("You win");}
			else {winnerstatus.setText("You lose");}
			winnerid = playerid;
			return;
		}
		
}
		private boolean hasspace(int thecol)
		{
			int chipcount = 0;
			for (int i = 0; i < rowmax; i++) {
				if ( integer2DArray[i][thecol] > 0 ) {
					chipcount = chipcount + 1;
				}
			}
			if ( chipcount >= rowmax) {
				return false;
			}
			return true;
		}
		private int chipcount(int thecol)
		{
			int chipcount = 0;
			for (int i = 0; i < rowmax; i++) {
				if ( integer2DArray[i][thecol] > 0 ) {
					chipcount = chipcount + 1;
				}
			}
			return chipcount;
		}
		private void almost(int playerid, int col) 
		{		
			int connect = 0;
			int row = rowmax-chipcount(col)-1;
			int down = chipcount(col);
			//int up = chipcount(col)-1;
			int empty = 0;
			for(int i =1; i<=down; i++)
			{
				if(integer2DArray[row+i][col] == playerid)
				{
					connect++;
				}
				else {break;}
			}
			
			if(connect >= 3)
			{
				//System.out.println("BLOCKING UPDOWN");
				blockcol = col;
				blockneeded = true;
				return;
			}
			connect = 0;
			int left = col;
			int right = colmax-col-1;
			
			for(int i = 1; i<=left; i++)
			{
				if(integer2DArray[row][col-i] == playerid)
				{
					connect++;
				}
				else {break;}
			}
			for(int i = 1; i<=right; i++)
			{
				if(integer2DArray[row][col+i] == playerid)
				{
					connect++;
				}
				else {break;}
			}
			if(connect >= 3)
			{
				//System.out.println("BLOCKING LEFTRIGHT");
				blockcol=col;
				blockneeded = true;
				return;
			}
			
			connect = 0;
			int up = rowmax-chipcount(col)-1;
			for(int i = 1; (i<=up && i<=right); i++)
			{
				if(integer2DArray[row-i][col+i] == playerid)
				{
					connect++;
				}
				else {break;}
			}
			for(int i = 1; (i<=down && i<=left); i++)
			{
				if(integer2DArray[row+i][col-i] == playerid)
				{
					connect++;
				}
				else {break;}
			}
			if(connect >= 3)
			{
				//System.out.println("BLOCKING DIAG Y=X");
				blockcol=col;
				blockneeded = true;
				return;
			}
			
			connect = 0;
			
			for(int i = 1; (i<=up && i<=left); i++)
			{
				if(integer2DArray[row-i][col-i] == playerid)
				{
					connect++;
				}
				else {break;}
			}
			for(int i = 1; (i<=down && i<=right); i++)
			{
				if(integer2DArray[row+i][col+i] == playerid)
				{
					connect++;
				}
				else {break;}
			}
			if(connect >= 3)
			{
				//System.out.println("BLOCKING DIAG down");
				blockcol=col;
				blockneeded = true;
				return;
			}

			
			/*connect = 0;
			empty=0;
			int left = droploccol+1;
			int right = colmax-droploccol;
			boolean blocker = false;
			for(int i =1; i<right; i++)
			{
				if(integer2DArray[droplocrow][droploccol+i] == playerid)
				{
					connect++;
				}
				else if(integer2DArray[droplocrow][droploccol+i] != 0)
				{break;}
				if(integer2DArray[droplocrow][droploccol+i] == 0)
				{
					if(!blocker)
					{blockcol = droploccol+i;blocker = true;}
					if(empty!=1)
					{
					empty++;
					}
				}
				else if(integer2DArray[droplocrow][droploccol+i] != playerid)
				{break;}
			}
			for(int i =1; i<left; i++)
			{
				if(integer2DArray[droplocrow][droploccol-i] == playerid)
				{
					connect++;
				}
				else if(integer2DArray[droplocrow][droploccol-i] != 0) 
				{break;}
				if(integer2DArray[droplocrow][droploccol-i] == 0)
				{
					if(!blocker)
					{blockcol = droploccol-i;blocker = true;}
					if(empty!=1) {
					empty++;}
				}
				else if(integer2DArray[droplocrow][droploccol-i] != playerid)
				{
					break;
				}
			}
			if(connect+empty >= 3)
			{
				System.out.println("BLOCKED LEFTRIGHT");
				blockneeded = true;
				
				return;
			}
			
			empty = 0;
			connect = 0;
			for(int i =1; (i<right) && (i<up-1); i++)
			{
				if(integer2DArray[droplocrow-i][droploccol+i] == playerid)
				{
					connect++;
				}
				else if(integer2DArray[droplocrow-i][droploccol+i] != 0) 
				{break;}
				if(integer2DArray[droplocrow-i][droploccol+i] == 0)
				{
					if(empty!=1)
					{
					blockcol = droploccol+i;
					empty++;
					}
				}
				else if(integer2DArray[droplocrow-i][droploccol+i] != playerid) 
				{break;}
			}
			for(int i =1; (i<left && i<down); i++)
			{
				if(integer2DArray[droplocrow+i][droploccol-i] == playerid)
				{
					connect++;
				}
				else if(integer2DArray[droplocrow+i][droploccol-i] != 0){break;}
				if(integer2DArray[droplocrow+i][droploccol-i] == 0)
				{
					if(empty!=1)
					{
						blockcol=droploccol-i;
						empty++;
					}
				}
			}
			if(connect +empty>= 3)
			{
				System.out.println("BLOCKED DIAGUP");
				
				blockneeded = true;
				return;
			}*//*
			empty = 0;
			connect = 0;
			for(int i =1; (i<left) && (i<up); i++)
			{
				if(integer2DArray[droplocrow-i][droploccol-i] == playerid)
				{
					connect++;
				}
				else {break;}
			}
			for(int i =1; (i<right && i<down); i++)
			{
				if(integer2DArray[droplocrow+i][droploccol+i] == playerid)
				{
					connect++;
				}
				else {break;}
			}
			if(connect >= 3)
			{
				System.out.println("WINNER DIAGDOWN");
				if(playerid == 1) {
					winnerstatus.setText("You win");}
				else {winnerstatus.setText("You lose");}
				winnerid = playerid;
				return;
			}
		
		connect = 0;
		*/
			
			
			
//		if(droplocrow+3<rowmax) {
//			if(integer2DArray[droplocrow+1][droploccol] == playerid)
//			{
//				connect++;
//			}
//			if(integer2DArray[droplocrow+2][droploccol] == playerid)
//			{
//				connect++;
//			}
//			if(integer2DArray[droplocrow+3][droploccol] == playerid)
//			{
//				connect++;
//			}
//			if(connect == 3)
//			{
//				System.out.println("winner");
//				winnerid = playerid;
//				return;
//			}}
//		connect = 0;
//		if(droplocrow-3>=0) {
//			if(integer2DArray[droplocrow-1][droploccol] == playerid)
//			{
//				connect++;
//			}
//			if(integer2DArray[droplocrow-2][droploccol] == playerid)
//			{
//				connect++;
//			}
//			if(integer2DArray[droplocrow-3][droploccol] == playerid)
//			{
//				connect++;
//			}
//			if(connect == 3)
//			{
//				System.out.println("winner");
//				winnerid = playerid;
//				return;
//			}
//		}
//		connect = 0;
//		if(droploccol+3<colmax) {
//			if(integer2DArray[droplocrow][droploccol+1] == playerid)
//			{
//				connect++;
//			}
//			if(integer2DArray[droplocrow][droploccol+2] == playerid)
//			{
//				connect++;
//			}
//			if(integer2DArray[droplocrow][droploccol+3] == playerid)
//			{
//				connect++;
//			}
//			if(connect == 3)
//			{
//				System.out.println("winner");
//				winnerid = playerid;
//				return;
//			}
//		}
//			connect = 0;
//			if(droploccol-3>=0) {
//				if(integer2DArray[droplocrow][droploccol-1] == playerid)
//				{
//					connect++;
//				}
//				if(integer2DArray[droplocrow][droploccol-2] == playerid)
//				{
//					connect++;
//				}
//				if(integer2DArray[droplocrow][droploccol-3] == playerid)
//				{
//					connect++;
//				}
//				if(connect == 3)
//				{
//					System.out.println("winner");
//					winnerid = playerid;
//					return;
//				}
//
//			}
//			connect = 0;
//			if(droploccol-3>=0 && droplocrow-3>=0) {
//				if(integer2DArray[droplocrow-1][droploccol-1] == playerid)
//				{
//					connect++;
//				}
//				if(integer2DArray[droplocrow-2][droploccol-2] == playerid)
//				{
//					connect++;
//				}
//				if(integer2DArray[droplocrow-3][droploccol-3] == playerid)
//				{
//					connect++;
//				}
//				if(connect == 3)
//				{
//					System.out.println("winner");
//					winnerid = playerid;
//					return;
//				}
//
//			}
//			connect = 0;
//			if(droploccol+3<colmax && droplocrow-3>=0) {
//				if(integer2DArray[droplocrow-1][droploccol+1] == playerid)
//				{
//					connect++;
//				}
//				if(integer2DArray[droplocrow-2][droploccol+2] == playerid)
//				{
//					connect++;
//				}
//				if(integer2DArray[droplocrow-3][droploccol+3] == playerid)
//				{
//					connect++;
//				}
//				if(connect == 3)
//				{
//					System.out.println("winner");
//					winnerid = playerid;
//					return;
//				}
//
//			}
//			connect = 0;
//			if(droploccol-3>=0 && droplocrow+3<rowmax) {
//				if(integer2DArray[droplocrow+1][droploccol-1] == playerid)
//				{
//					connect++;
//				}
//				if(integer2DArray[droplocrow+2][droploccol-2] == playerid)
//				{
//					connect++;
//				}
//				if(integer2DArray[droplocrow+3][droploccol-3] == playerid)
//				{
//					connect++;
//				}
//				if(connect == 3)
//				{
//					System.out.println("winner");
//					winnerid = playerid;
//					return;
//				}
//
//			}
//			connect = 0;
//			if(droploccol+3<colmax && droplocrow+3<rowmax) {
//				if(integer2DArray[droplocrow+1][droploccol+1] == playerid)
//				{
//					connect++;
//				}
//				if(integer2DArray[droplocrow+2][droploccol+2] == playerid)
//				{
//					connect++;
//				}
//				if(integer2DArray[droplocrow+3][droploccol+3] == playerid)
//				{
//					connect++;
//				}
//				if(connect == 3)
//				{
//					System.out.println("winner");
//					winnerid = playerid;
//					return;
//				}
//
//			}
	}


}
