import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.*;



public class Checker {
	private int height;
	private int width;
	private ArrayList<LinkedList<Point>> listPoints;
	
	public void fillListFromFile (String filename) throws FormattingException, FileNotFoundException {

		File f = new File(filename);

		Scanner s = new Scanner(f);
		int i = 0;
		String line;
		if (!s.hasNextLine()) {
			throw new FormattingException("Empty file!");
		}
		else {
			line = s.nextLine();
			String[] parts = line.split(" ");
			if(parts.length == 2){
				height = Integer.parseInt(parts[0]);
				width = Integer.parseInt(parts[1]);
			}
			else{
				throw new FormattingException("Improper formatting of input file!");
			}

		}
		listPoints = new ArrayList<LinkedList<Point>>();
		
		if(!s.hasNextLine()){
			throw new FormattingException("Improper formatting of input file!");
		}

		while (s.hasNextLine()) {
			line = s.nextLine();
			String[] parts = line.split(" ");
			
			if (parts.length == 4
					&& Integer.parseInt(parts[0]) < height && Integer.parseInt(parts[2]) < height
					&& Integer.parseInt(parts[1]) < width && Integer.parseInt(parts[3]) < width 
					&& Integer.parseInt(parts[0]) >= 0 && Integer.parseInt(parts[1]) >= 0
					&& Integer.parseInt(parts[2]) >= 0 && Integer.parseInt(parts[3]) >= 0) {
				Point topLeft = new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
				Point bottRight = new Point(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
				for (int j = 0; j < listPoints.size(); j++) {
					if (isOverlap(topLeft, bottRight, listPoints.get(j).get(0), listPoints.get(j).get(1))) {
						throw new FormattingException("Improper formatting of init file!");
					}
				}
				listPoints.add(new LinkedList<Point>());
				listPoints.get(i).add(topLeft);
				listPoints.get(i).add(bottRight);
				i++;
			}
			else {
				throw new FormattingException("Improper formatting of input file!");
			}
			
		}
	}
	
	
	private int findTopL(int x, int y){
		Point test = new Point(x,y);
		for(int i = 0; i < listPoints.size(); i++){
			if(listPoints.get(i).get(0).equals(test)){
				return i;
			}
		}
		return -1;
	}
	
	public boolean checkGoal(String goalFile) throws FileNotFoundException, FormattingException{
		File f = new File(goalFile);
		Scanner s = new Scanner(f);
		if(!s.hasNextLine()) {
			throw new FormattingException("Improper formatting of goal file!");
		}
		while (s.hasNextLine()) {
			String line = s.nextLine();
			String[] parts = line.split(" ");
			if (parts.length == 4 
					&& Integer.parseInt(parts[0]) < height && Integer.parseInt(parts[2]) < height &&
					Integer.parseInt(parts[1]) < width && Integer.parseInt(parts[3]) < width 
					&& Integer.parseInt(parts[0]) >= 0 && Integer.parseInt(parts[1]) >= 0
					&& Integer.parseInt(parts[2]) >= 0 && Integer.parseInt(parts[3]) >= 0) {
				int index = findTopL(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
				if(index == -1 || !listPoints.get(index).get(1).equals(new Point(Integer.parseInt(parts[2]), Integer.parseInt(parts[3])))){
					return false;
				}
			}
			else {
				throw new FormattingException("Improper formatting of goal file!");
			}
		}
		return true;
	}
	
	private boolean isOverlap(Point newL, Point newR, Point origL, Point origR){
		if((origL.x <= newR.x) 
				&& (origR.x >= newL.x)
				&& (origL.y <= newR.y)
				 && (origR.y >= newL.y)){
			return true;
		}
		return false;
	}
	
	public void assertMoves(String moves) throws StdinFormattingException, ImpossibleMoveException{		
		Point botRight = null;
		int index = -1;
		String[] parts = moves.split(" ");
		if (parts.length != 4) {
			throw new StdinFormattingException("Must input 4 numbers!");
		}
		if(Integer.parseInt(parts[0]) != Integer.parseInt(parts[2])
				&& Integer.parseInt(parts[1]) != Integer.parseInt(parts[3])){
			throw new ImpossibleMoveException("Move can only travel along singular axis!");
		}
		if(Integer.parseInt(parts[0]) < 0
				|| Integer.parseInt(parts[2]) < 0
				|| Integer.parseInt(parts[1]) < 0
				|| Integer.parseInt(parts[3]) < 0){
			throw new ImpossibleMoveException("Cannot have negative index");
		}
		
		Point topLeft = new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
		Point toMoveL = new Point(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
		for(int i = 0; i < listPoints.size(); i++){
			if(listPoints.get(i).get(0).equals(topLeft)){
				botRight = listPoints.get(i).get(1);
				index = i; //saving position of the i for later
			}
		}
		if (index == -1) {
			throw new StdinFormattingException("Block to move not found!");
		}
		//calculate the new bottom right coordinates
		int distX = toMoveL.x - topLeft.x;
		int distY = toMoveL.y - topLeft.y;
		Point toMoveR = new Point(botRight.x + distX, botRight.y + distY);
		if(toMoveR.x < 0 || toMoveR.y < 0){
			throw new ImpossibleMoveException("Cannot have negative index for block.");
		}
		if(toMoveL.x < height && toMoveL.y < width && toMoveR.x < height && toMoveR.y< width){

			for(int i = 0; i < listPoints.size(); i++){
				Point left = listPoints.get(i).get(0);
				Point right = listPoints.get(i).get(1);
				if(!left.equals(topLeft)){ //not at the original block
					if(this.isOverlap(toMoveL, toMoveR, left, right)){
						throw new ImpossibleMoveException("Impossible move! There will be an overlap of blocks!");

					}
				}
			}
			listPoints.get(index).get(0).move(toMoveL.x, toMoveL.y);
			listPoints.get(index).get(1).move(toMoveR.x, toMoveR.y);
			//System.out.println(myAdjList.get(index).get(0).x + " + " + myAdjList.get(index).get(0).y + " + " + myAdjList.get(index).get(1).x + " + " + myAdjList.get(index).get(1).y);
		}
		else { //if given move is not within height and width
			throw new ImpossibleMoveException("Impossible move! Blocks out of range of tray");
//			System.err.println("Impossible move! Blocks out of range of tray!");
//			System.exit(6);
		}
	}
	
	public void getMoves(String goalFile) throws ImpossibleMoveException, StdinFormattingException {
		Scanner s = new Scanner(System.in);
		while (s.hasNextLine()) {
			String line = s.nextLine();
			this.assertMoves(line);
		}
	}


	public static void main(String[] args){
		try{
			Checker check = new Checker();
			if (args.length != 2) {
				System.err.print("Should input 2 files!");
				System.exit(2);
//				throw new NumberOfFilesException("Should input 2 files");
//				System.err.println("Should input 2 files!");
//				System.exit(2);
			}
			check.fillListFromFile(args[0]);
			check.getMoves(args[1]);
			if (check.checkGoal(args[1])) {
				System.err.print("Successful moves!");
				System.exit(0);
			}
			else {
				System.err.print("Input moves do not solve puzzle!");
				System.exit(1);
				//throw new DoesNotSolveException("Input moves do not solve puzzle!");
//				System.err.println("Input moves do not solve puzzle!");
//				System.exit(1);
			}
		}
//		catch(SuccessException e){
//			System.err.println(e);
//			System.exit(0);
//		}
//		catch(DoesNotSolveException e){
//			System.err.println(e);
//			System.exit(1);
//		}
//		catch(NumberOfFilesException e){
//			System.err.println(e);
//			System.exit(2);
//		}
		catch (FileNotFoundException e) {
			System.err.println("File does not exist!");
			System.exit(3);
		}
		catch(StdinFormattingException e){
			System.err.println(e.getMessage());
			System.exit(4);
		}
		catch(FormattingException e){
			System.err.println(e.getMessage());
			System.exit(5);
		}
		catch (ImpossibleMoveException e){
			System.err.println(e.getMessage());
			System.exit(6);
		}
	}
	
	
	public class FormattingException extends Exception {
		//when caught should do System.exit(5) + error message
	    public FormattingException(){
	        super();
	    }

	    public FormattingException(String message){
	        super(message);
	    }
	}
	
	public static class SuccessException extends Exception{
		//when caught should do system.exit(0)
		public SuccessException(){
			super();
		}

		public SuccessException(String message){
			super(message);
		}
		
	}
	
	public static class DoesNotSolveException extends Exception{
		// should system.exit(1)
		public DoesNotSolveException(){
			super();
		}
		
		public DoesNotSolveException(String message){
			super(message);
		}
	}
	
	public static class NumberOfFilesException extends Exception{
		//should system.exit(2)
		public NumberOfFilesException(){
			super();
		}
		
		public NumberOfFilesException(String message){
			super(message);
		}
	}
	
//	public class FileDNEException extends Exception{
//		//system.exit(3)
//		public FileDNEException(){
//			super();
//		}
//		
//		public FileDNEException(String message){
//			super(message);
//		}
//	}
	
	public class StdinFormattingException extends Exception{
		//system.exit(4)
		public StdinFormattingException(){
			super();
		}
		
		public StdinFormattingException(String message){
			super(message);
		}
	}
	
	public class ImpossibleMoveException extends Exception{
		//system.exit(6)
		public ImpossibleMoveException(){
			super();
		}
		
		public ImpossibleMoveException(String message){
			super(message);
		}
	}
	
	
}