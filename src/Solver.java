import java.util.*;
import java.util.List;
import java.awt.*;
import java.io.*;

public class Solver {
	private static boolean[][] matrix;
	public static void main(String[] args){ 	
		Solver s = new Solver();
		TrayConfigurations t = new TrayConfigurations(args[0]);
		int height = t.height;
		int width = t.width;
		Solver.matrix = new boolean[height][width];
		TrayConfigurations goal = new TrayConfigurations(t.blockDimensions, t.height, t.width, args[1]);
		ArrayList<String> returnArray = s.graphTraversal(t, goal);
		if(returnArray == null){ 
			System.exit(1);
		}
		Collections.reverse(returnArray);
		for (int i = 1; i < returnArray.size(); i++){ 
			System.out.println(returnArray.get(i));
		}
		System.exit(0);
	}
	
	public boolean isValidMove(TrayConfigurations t, boolean[][] matrix, Point blockToMove, Point blockDimension, String direction){ 
		int leftX = blockToMove.x;
		int leftY = blockToMove.y;
		int blockH = blockDimension.x;
		int blockW = blockDimension.y;
		
		if (direction.equals("up")){ 
			//the first if case checks if we indexed out of the board (meaning if moving in that direction gets us off 
			// of the tray)
			if (leftX - 1 < 0){ 
				return false;
			}
			//the for loop checks that no blocks occupy the spaces we want to move to. We check this using the 
			//matrix array that contains T/F values denoting whether or not a block occupies that index 
			for (int w = blockW; w >= 0; w--){ 
				if(matrix[leftX - 1][leftY + w]){ 
					return false;
				}
			}
		} else if (direction.equals("down")){
			if(leftX + blockH + 1 >= t.height){ 
				return false;
			}
			for (int w = blockW; w >= 0; w--){ 
				if(matrix[leftX + blockH + 1][leftY + w]){ 
					return false;
				}
			}
		}else if (direction.equals("left")){ 
			if(leftY - 1 < 0){ 
				return false;
			}
			
			for (int h = blockH; h >= 0; h--){ 
				
				if(matrix[leftX + h][leftY - 1]){ 
					return false;
				}
			}
		}else if (direction.equals("right")){ 
			if(leftY + blockW + 1 >= t.width){ 
				return false;
			}
			for (int h = blockH; h >= 0; h--){ 
				if(matrix[leftX + h][leftY + blockW + 1]){
					return false;
				}
			}
		}
		return true;
	}
	
	public ArrayList<TrayConfigurations> possibleMoves(TrayConfigurations t){ 
		ArrayList<TrayConfigurations> listOfMoves = new ArrayList<TrayConfigurations>();
		
		//we construct a 2D boolean array to check if we can 
		for(int i = 0; i < matrix.length; i++){ 
			Arrays.fill(matrix[i], false);
		}
		// adding the true values where blocks occupy on the board 
		for (int j = 0; j < t.blocksArray.length; j++){ 
			int[] subArray = t.blocksArray[j];
			for(int l = 0; l < subArray.length; l += 2){
				int xPt = subArray[l];
				int yPt = subArray[l + 1];
				Point toMoveDim = t.blockDimensions.get(j);
				for(int i = toMoveDim.x; i >= 0; i--){ 
					for (int k = toMoveDim.y; k >= 0; k--){ 
						matrix[xPt + i][yPt + k] = true;
					}
				}
			}
		}
		
		
		// for every block on the board, is it valid to move it u, d, l, r
		for(int i = 0; i < t.blocksArray.length; i++){ 
			for(int k = 0; k < t.blocksArray[i].length; k += 2){ 
				Point blockToMove = new Point(t.blocksArray[i][k], t.blocksArray[i][k + 1]);
				Point blockDimension = t.blockDimensions.get(i);
				
				if(isValidMove(t, matrix, blockToMove, blockDimension, "up")){
					TrayConfigurations moveUp = new TrayConfigurations(t);
					makeMove(moveUp, blockToMove, k, blockDimension, "up");
					listOfMoves.add(moveUp);
				}
				if(isValidMove(t, matrix, blockToMove, blockDimension, "down")){ 
					TrayConfigurations moveDown = new TrayConfigurations(t);
					makeMove(moveDown, blockToMove, k, blockDimension, "down");
					listOfMoves.add(moveDown);
				}
				if(isValidMove(t, matrix, blockToMove, blockDimension, "left")){ 
					TrayConfigurations moveLeft = new TrayConfigurations(t);
					makeMove(moveLeft, blockToMove, k, blockDimension, "left");
					listOfMoves.add(moveLeft);
					
				}
				if(isValidMove(t, matrix, blockToMove, blockDimension, "right")){ 
					TrayConfigurations moveRight = new TrayConfigurations(t);
					makeMove(moveRight, blockToMove, k, blockDimension, "right");
					listOfMoves.add(moveRight);
				}
			}
		}
		return listOfMoves;
	}
	
	
	
	public void makeMove(TrayConfigurations t, Point blockToMove, int indexOfPt, Point blockDim, String direction){
		// given a tray, a block on that tray to move, the block's starting index, the block's dimension, and a direction, this method will make changes to the 
		//data structures to reflect that move 
		int leftX = blockToMove.x;
		int leftY = blockToMove.y;
		int index = t.blockDimensions.indexOf(blockDim);
		int[] list = t.blocksArray[index];
		
		if(direction.equals("up")){
			list[indexOfPt] = list[indexOfPt] - 1;
			t.moves = Integer.toString(leftX) + " " + Integer.toString(leftY) + " "
					+ Integer.toString(leftX - 1) + " " + Integer.toString(leftY);
				
		} else if (direction.equals("down")){ 
			list[indexOfPt] = list[indexOfPt] + 1;
			t.moves = Integer.toString(leftX) + " " + Integer.toString(leftY) + " "
					+ Integer.toString(leftX + 1) + " " + Integer.toString(leftY);
			
		} else if (direction.equals("right")){ 
			list[indexOfPt + 1] = list[indexOfPt + 1] + 1;
			t.moves = Integer.toString(leftX) + " " + Integer.toString(leftY) + " "
					+ Integer.toString(leftX) + " " + Integer.toString(leftY + 1);
		} else { 
			list[indexOfPt + 1] = list[indexOfPt + 1] - 1;
			t.moves = Integer.toString(leftX) + " " + Integer.toString(leftY) + " "
			+ Integer.toString(leftX) + " " + Integer.toString(leftY - 1);
			
		}
	}
	
	
	// should return the set of moves of a winning configuration represented as an ArrayList of strings, or null if unsolvable 
	public ArrayList<String> graphTraversal(TrayConfigurations t, TrayConfigurations goal){ 
		ArrayList<String> toReturn = new ArrayList<String>();
		HashSet<TrayConfigurations> alreadySeen = new HashSet<TrayConfigurations>();
		Stack<TrayConfigurations> stack = new Stack<TrayConfigurations>();
		stack.add(t);
		while(!stack.isEmpty()){ 
			TrayConfigurations nextT = stack.pop();
			if(nextT.equals(goal)){
				TrayConfigurations traverse = nextT;
				while(traverse != null){ 
					toReturn.add(traverse.moves);
					traverse = traverse.parent;
				}
				return toReturn;
			} else { 
				if(!alreadySeen.contains(nextT)){ 
					alreadySeen.add(nextT);
					ArrayList<TrayConfigurations> moves = possibleMoves(nextT);
					for(TrayConfigurations configuration : moves){ 
						stack.push(configuration);
					}
				}
			}
		}
		return null;
		
	}
	
	public static int distanceEq(int p1X, int p1Y, int p2X, int p2Y){ 
		return Math.abs(p1X - p2X) + Math.abs(p1Y - p2Y);
	}
	
		
	private static class TrayConfigurations {
		
		private int height; // this is the height of the tray
		private int width; // this is the width of the tray
		private ArrayList<Point> blockDimensions; // this is an arrayList storing 
		//all of the dimensions of the different blocks 
		//private ArrayList<ArrayList<Point>> blocksArray; // this is an array where
		//the indices correspond to the blockDimensions array and it at a certain 
		//index, all of the blocks of that dimension are stored in an array of ints where the every two 
		//indices corresponds to a point
		private int[][] blocksArray;
		private TrayConfigurations parent;
		private String moves;
		
		public TrayConfigurations(String filename){ 
			try { 
				Scanner sc = new Scanner (new File (filename));
				if(!sc.hasNextLine()) {
					System.exit(4);
				}
				String boardDimensions = sc.nextLine();
				String[] parts = boardDimensions.split(" ");
				if(parts.length == 2){
					this.height = Integer.parseInt(parts[0]);
					this.width = Integer.parseInt(parts[1]);
				} else { 
					System.exit(4);
				}
				if (!sc.hasNext()){ 
					System.exit(4);
				} else { 
					this.moves = null;
					this.parent = null;
					blockDimensions = new ArrayList<Point>();
					ArrayList<ArrayList<Point>> tempBlocks= new ArrayList<ArrayList<Point>>();
					HashSet<Point> pointsOccupied = new HashSet<Point>(); 
					while (sc.hasNext()){ 
						String s = sc.nextLine();						
						String[] blocks = s.split(" ");
						if (!isValidBlock(blocks)){ 
							System.exit(4);
						}
						Point p = blockDim(blocks);
						Point topLeft = new Point(Integer.parseInt(blocks[0]), Integer.parseInt(blocks[1]));
						ArrayList<Point> occupied = getPoints(topLeft, p);
						for(Point pt : occupied){ 
							if(pointsOccupied.contains(pt)){ 
								System.exit(4);
							}
							pointsOccupied.add(pt);
						}
						if(this.blockDimensions.contains(p)){ 
							int index = blockDimensions.indexOf(p);
							tempBlocks.get(index).add(topLeft);
						} else { 
							blockDimensions.add(p);
							int index = blockDimensions.size() - 1;
							ArrayList<Point> l = new ArrayList<Point>();
							l.add(topLeft);
							tempBlocks.add(index, l);
						}
						
					}
					blocksArray = new int[blockDimensions.size()][];
					for(int i = 0; i < blockDimensions.size(); i++){ 
						ArrayList<Point> list = tempBlocks.get(i);
						blocksArray[i] = new int[list.size() * 2];
						for(int k = 0, j = 0; k < list.size(); k += 1, j+=2){ 
							Point p = list.get(k);
							blocksArray[i][j] = p.x;
							blocksArray[i][j + 1] = p.y;
						}
					}
				}
			}
			catch (FileNotFoundException e){ 
				System.exit(3);
			}
			
		}
		public ArrayList<Point> getPoints(Point left, Point dim) {
			ArrayList<Point> toReturn = new ArrayList<Point>();
			for(int i = dim.x; i >= 0; i--){ 
				for (int k = dim.y; k >= 0; k--){ 
					toReturn.add(new Point((left.x + i), (left.y + k)));
				}
			}
			return toReturn;
		}
		
		public TrayConfigurations(ArrayList<Point> dimensions, int ht, int wdth, String filename){ 
			this.height = ht;  
			this.width = wdth; 
			this.moves = null;
			this.parent = null;
			this.blockDimensions = dimensions;
			HashSet<Point> pointsOccupied = new HashSet<Point>();
			ArrayList<ArrayList<Point>>tempBlocks = new ArrayList<ArrayList<Point>>();
			for (int i = 0; i < blockDimensions.size(); i++){ 
				tempBlocks.add(i, new ArrayList<Point>());
			}
			try { 
				Scanner sc = new Scanner (new File (filename));
				if(!sc.hasNextLine()) {
					System.exit(4);
				}
				while(sc.hasNext()){ 
					String s = sc.nextLine();
					String[] blocks = s.split(" ");
					if (!isValidBlock(blocks)){ 
						System.exit(4);
					}
					Point p = blockDim(blocks);
					Point topLeft = new Point(Integer.parseInt(blocks[0]), Integer.parseInt(blocks[1]));
					
					int indexOfPoint = this.blockDimensions.indexOf(p);
					if(indexOfPoint == -1){ 
						System.exit(4);
					}
					ArrayList<Point> occupied = getPoints(topLeft, p);
					for(Point pt : occupied){ 
						if(pointsOccupied.contains(pt)){ 
							System.exit(4);
						}
						pointsOccupied.add(pt);
					}
					
					tempBlocks.get(indexOfPoint).add(topLeft);
				}
			}catch (FileNotFoundException e){ 
				System.exit(3);
			}
			this.blocksArray = new int[this.blockDimensions.size()][];
			for(int i = 0; i < blockDimensions.size(); i++){ 
				ArrayList<Point> list = tempBlocks.get(i);
				blocksArray[i] = new int[list.size() * 2];
				for(int k = 0, j = 0; k < list.size(); k += 1, j+= 2){ 
					Point p = list.get(k);
					blocksArray[i][j] = p.x;
					blocksArray[i][j + 1] = p.y;
				}
			}
		}
		
		public TrayConfigurations(TrayConfigurations t){ 
			int newHeight = t.height;
			int newWidth = t.width;
			this.parent = t;
			this.moves = "";
			int[][] newBlocksArray = new int[t.blockDimensions.size()][];
			for(int i = 0; i < t.blockDimensions.size(); i++){ 
				int[] tempArr = t.blocksArray[i];
				int tempArrL = tempArr.length;
				newBlocksArray[i] = new int[tempArrL];
				System.arraycopy(tempArr, 0, newBlocksArray[i], 0, tempArrL);
			}
			this.height = newHeight;
			this.width = newWidth;
			this.blockDimensions = t.blockDimensions;
			this.blocksArray = newBlocksArray;
		}
		
		private boolean isValidBlock(String[] blocks){ 
			if (blocks.length != 4){ 
				return false;
			} else { 
				int first = Integer.parseInt(blocks[0]);
				int second = Integer.parseInt(blocks[1]);
				int third = Integer.parseInt(blocks[2]);
				int fourth = Integer.parseInt(blocks[3]);
				return (first < height && first >= 0
						&& second < width && second >= 0
						&& third < height && third >= 0
						&& fourth < width && fourth >= 0);
			}
		}
		
		private Point blockDim(String[] block){ 
			int xCoord = Integer.parseInt(block[2]) - Integer.parseInt(block[0]);
			int yCoord = Integer.parseInt(block[3]) - Integer.parseInt(block[1]);
			return new Point(xCoord, yCoord);
		}
		
		public boolean equals(Object obj){ 
			TrayConfigurations t = (TrayConfigurations) obj;
			for (int index = 0; index < t.blockDimensions.size(); index++){ 
				int[] thisBlocks = this.blocksArray[index];
				int[] tBlocks = t.blocksArray[index];
				
				int i = 0;
				while(i < tBlocks.length){
					int xPt = tBlocks[i];
					int yPt = tBlocks[i + 1];
					int j = 0; 
					
					while (j < thisBlocks.length){ 
						if(thisBlocks[j] == xPt){ 
							if(thisBlocks[j + 1] == yPt){
								break;
							}
						} if (j == (thisBlocks.length - 2)){ 
							return false;
						}
						j+=2;
					}
					i += 2;
					
				}	
			}
			return true;
		}
		
		public int hashCode(){ 
			int toReturn = 0;
			for (int index = 0; index < this.blockDimensions.size(); index++){ 
				Point p = this.blockDimensions.get(index);
				int x = p.x;
				int y = p.y;
				int j = (1511 * x) + (1039 * y);
				int[] blockList = this.blocksArray[index];
				int listSums = 0;
				int loop = 0;
				while (loop < blockList.length){ 
					int xHash = 3559 * blockList[loop];
					int yHash = 5953 * blockList[loop + 1];
					listSums += xHash + yHash;
					loop += 2;
				}
				toReturn = toReturn * (j * listSums);
			}
			return toReturn;
		}
	}

}