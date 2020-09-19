/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Lorenz Wiest
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sub-license,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.lorenzwiest.solvingtantrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SolvingTantrix {

	public enum Color {
		RED, //
		BLUE, //
		YELLOW
	}

	//////////////////////////////////////////////////////////////////////////////

	// Colors are stored by index in "colors" like so:
	//
	//   5 0
	//  4   1
	//   3 2

	public static class Tile {
		public static final int NUM_DIRECTIONS = 6;

		private int id;
		private Color[] colors;
		private int angle;
		private int col;
		private int row;

		private Tile(int id, Color[] colors) {
			this.id = id;
			this.colors = new Color[NUM_DIRECTIONS];
			System.arraycopy(colors, 0, this.colors, 0, NUM_DIRECTIONS);

			this.col = -1;
			this.row = -1;
			this.angle = 0;
		}

		public int getId() {
			return this.id;
		}

		public Color getColor(int direction) {
			return this.colors[direction];
		}

		public int getCol() {
			return this.col;
		}

		public int getRow() {
			return this.row;
		}

		public int getAngle() {
			return this.angle;
		}

		public void setColor(int direction, Color color) {
			this.colors[direction] = color;
		}

		public void setAngle(int angle) {
			this.angle = angle;
		}

		public void setCol(int col) {
			this.col = col;
		}

		public void setRow(int row) {
			this.row = row;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Tile) {
				Tile toCompare = (Tile) o;
				return this.id == toCompare.id;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.id;
		}

		@Override
		public String toString() {
			return "Tile #" + getId() + " @ " + getAngle() + " deg";
		}
	}

	//////////////////////////////////////////////////////////////////////////////

	private static int solutionCount = 1;

	private static Tile[] TILES = {
			new Tile(1, new Color[] { Color.BLUE, Color.RED, Color.YELLOW, Color.YELLOW, Color.BLUE, Color.RED }),
			new Tile(2, new Color[] { Color.RED, Color.BLUE, Color.YELLOW, Color.YELLOW, Color.BLUE, Color.RED }),
			new Tile(3, new Color[] { Color.YELLOW, Color.RED, Color.RED, Color.BLUE, Color.BLUE, Color.YELLOW }),
			new Tile(4, new Color[] { Color.RED, Color.BLUE, Color.RED, Color.YELLOW, Color.BLUE, Color.YELLOW }),
			new Tile(5, new Color[] { Color.YELLOW, Color.RED, Color.BLUE, Color.BLUE, Color.RED, Color.YELLOW }),
			new Tile(6, new Color[] { Color.BLUE, Color.YELLOW, Color.BLUE, Color.RED, Color.YELLOW, Color.RED }),
			new Tile(7, new Color[] { Color.YELLOW, Color.RED, Color.BLUE, Color.BLUE, Color.YELLOW, Color.RED }),
			new Tile(8, new Color[] { Color.RED, Color.YELLOW, Color.BLUE, Color.BLUE, Color.RED, Color.YELLOW }),
			new Tile(9, new Color[] { Color.BLUE, Color.RED, Color.BLUE, Color.YELLOW, Color.RED, Color.YELLOW }),
			new Tile(10, new Color[] { Color.RED, Color.BLUE, Color.YELLOW, Color.YELLOW, Color.RED, Color.BLUE })
	};

	// The hexgrid
	//
	//   A   B   C   D
	// E   F   G   H
	//   I   J   K   L
	// M   N   O   P
	//
	// is stored in "BOARD" as follows:
	//
	// Tile[][] BOARD = {
	//     { A, B, C, D },
	//     { E, F, G, H },
	//     { I, J, K, L },
	//     { M, N, O, P }
	// }

	private static final int MAX_TILES = 10;
	private static final int BOARD_SIZE = (MAX_TILES * 2) + 1;
	private static final Tile BOARD[][] = new Tile[BOARD_SIZE][BOARD_SIZE];
	public static final Tile EMPTY = null;

	private static final int CENTER_COL = MAX_TILES;
	private static final int CENTER_ROW = MAX_TILES;

	private static final Set<String> HASHED_SOLUTIONS = new HashSet<String>();

	public static void main(String[] args) {
		for (int numTiles = 3; numTiles <= TILES.length; numTiles++) {
			for (Color loopColor : Color.values()) {
				clearBoard();
				clearHashedSolutions();

				List<Tile> solution = new ArrayList<Tile>();
				List<Tile> supply = createSupply(numTiles);

				Tile firstTile = supply.get(0);
				solution.add(firstTile);
				supply.remove(firstTile);
				putTileOnBoard(firstTile, CENTER_COL, CENTER_ROW);

				step(solution, supply, loopColor);
			}
		}
	}

	public static void clearHashedSolutions() {
		HASHED_SOLUTIONS.clear();
	}

	private static void clearBoard() {
		for (int c = 0; c < BOARD_SIZE; c++) {
			for (int r = 0; r < BOARD_SIZE; r++) {
				BOARD[c][r] = EMPTY;
			}
		}
	}

	private static List<Tile> createSupply(int numTiles) {
		List<Tile> supply = new ArrayList<Tile>();
		for (int i = 0; i < numTiles; i++) {
			supply.add(TILES[i]);
		}
		return supply;
	}

	private static void step(List<Tile> solution, List<Tile> supply, Color loopColor) {
		if (supply.size() == 0) {

			// calculate subboard that contains the tiles of the solution

			int minRow = BOARD_SIZE;
			int maxRow = -1;
			int minCol = BOARD_SIZE;
			int maxCol = -1;

			for (int r = 0; r < BOARD_SIZE; r++) {
				for (int c = 0; c < BOARD_SIZE; c++) {
					if (BOARD[r][c] != EMPTY) {
						if (r < minRow) {
							minRow = r;
						}
						if (r > maxRow) {
							maxRow = r;
						}
						if (c < minCol) {
							minCol = c;
						}
						if (c > maxCol) {
							maxCol = c;
						}
					}
				}
			}

			// discard duplicate solutions

			StringBuffer sb = new StringBuffer();
			for (int r = minRow; r <= maxRow; r++) {
				for (int c = minCol; c <= maxCol; c++) {
					Tile t = BOARD[r][c];
					if (t != EMPTY) {
						int id = t.getId();
						int angle = t.getAngle();
						int relC = c - minCol;
						int relR = r - minRow;
						String strTile = String.format("[%d,%d,%d,%d]", relC, relR, id, angle);
						sb.append(strTile);
					}
				}
			}

			String hashedSolution = sb.toString();
			if (HASHED_SOLUTIONS.contains(hashedSolution)) {
				System.out.println("Solution is a duplicate, skipped.");
				return;
			}
			HASHED_SOLUTIONS.add(hashedSolution);

			// discard solutions with hole

			int numSubBoardRows = (maxRow - minRow) + 1;
			int numSubBoardCols = (maxCol - minCol) + 1;

			Tile[][] subBoard = new Tile[numSubBoardRows][numSubBoardCols];

			for (int r = minRow; r <= maxRow; r++) {
				for (int c = minCol; c <= maxCol; c++) {
					subBoard[r - minRow][c - minCol] = BOARD[r][c];
				}
			}

			Tile aTile = solution.get(0);
			for (int c = 0; c < numSubBoardCols; c++) {
				floodFill(subBoard, c, 0, aTile);
				floodFill(subBoard, c, numSubBoardRows - 1, aTile);
			}
			for (int r = 0; r < numSubBoardRows; r++) {
				floodFill(subBoard, 0, r, aTile);
				floodFill(subBoard, numSubBoardCols - 1, r, aTile);
			}

			boolean hasHole = hasHole(subBoard);
			if (hasHole) {
				System.out.println("Solution has holes, skipped.");
				return;
			}

			// solution found

			printSolution(solution);

			String filename = String.format("Solution_%02d_%d_%04d.png", solution.size(), loopColor.ordinal(), solutionCount);
			SolvingTantrixGraphics.render(filename, solution, BOARD, minRow, maxRow, minCol, maxCol);

			solutionCount++;
			return;
		}

		Tile lastTile = solution.get(solution.size() - 1);

		int lastTileCol = lastTile.getCol();
		int lastTileRow = lastTile.getRow();

		for (int direction = 0; direction < Tile.NUM_DIRECTIONS; direction++) {
			int c = lastTileCol + getColOffset(lastTile, direction);
			int r = lastTileRow + getRowOffset(lastTile, direction);

			if (BOARD[r][c] == EMPTY) {
				for (Tile t : supply) {
					for (int rotate = 0; rotate < Tile.NUM_DIRECTIONS; rotate++) {
						putTileOnBoard(t, c, r);
						solution.add(t);
						if (doTilesFitEachOther(solution, supply, loopColor)) {
							step(solution, cloneAllTilesBut(supply, t), loopColor);
						}
						solution.remove(t);
						pullTileOffBoard(t, c, r);
						rotateTile60DegreesClockwise(t);
					}
				}
			}
		}
	}

	private static void printSolution(List<Tile> solution) {
		System.out.println("Solution #" + solutionCount);

		System.out.print("Tiles: ");
		for (Tile t : solution) {
			System.out.print(String.format("(#%s @ %d deg) ", t.getId(), t.getAngle()));
		}
		System.out.println();

		for (int r = 0; r < BOARD_SIZE; r++) {
			if (isEven(r)) {
				System.out.print("  ");
			}
			for (int c = 0; c < BOARD_SIZE; c++) {
				Tile t = BOARD[r][c];
				if (t != EMPTY) {
					int id = t.getId();
					System.out.print(id);
					if (id < 10) {
						System.out.print(".");
					}
				} else {
					System.out.print("..");
				}
				System.out.print("  ");
			}
			System.out.println();
		}
	}

	private static void putTileOnBoard(Tile t, int col, int row) {
		BOARD[row][col] = t;
		t.setCol(col);
		t.setRow(row);
	}

	private static void pullTileOffBoard(Tile t, int col, int row) {
		BOARD[row][col] = EMPTY;
		t.setCol(-1);
		t.setRow(-1);
	}

	private final static int[] EVEN_OFFSET_COLUMN = { 1, 1, 1, 0, -1, 0 };
	private final static int[] EVEN_OFFSET_ROW = { -1, 0, 1, 1, 0, -1 };

	private final static int[] ODD_OFFSET_COLUMN = { 0, 1, 0, -1, -1, -1 };
	private final static int[] ODD_OFFSET_ROW = { -1, 0, 1, 1, 0, -1 };

	private static int getColOffset(Tile t, int direction) {
		return (isEven(t.getRow())) ? EVEN_OFFSET_COLUMN[direction] : ODD_OFFSET_COLUMN[direction];
	}

	private static int getRowOffset(Tile t, int direction) {
		return (isEven(t.getRow())) ? EVEN_OFFSET_ROW[direction] : ODD_OFFSET_ROW[direction];
	}

	private static boolean isEven(int number) {
		return ((number % 2) == 0);
	}

	private static List<Tile> cloneAllTilesBut(List<Tile> list, Tile t) {
		List<Tile> newList = new ArrayList<Tile>(list);
		newList.remove(t);
		return newList;
	}

	private static void rotateTile60DegreesClockwise(Tile t) {
		//   5 0      4 5
		//  4   1 -> 3   0
		//   3 2      2 1

		Color color0 = t.getColor(0);
		Color color1 = t.getColor(1);
		Color color2 = t.getColor(2);
		Color color3 = t.getColor(3);
		Color color4 = t.getColor(4);
		Color color5 = t.getColor(5);
		int angle = t.getAngle();

		t.setColor(0, color5);
		t.setColor(1, color0);
		t.setColor(2, color1);
		t.setColor(3, color2);
		t.setColor(4, color3);
		t.setColor(5, color4);
		t.setAngle((angle + 60) % 360);
	}

	private static boolean doTilesFitEachOther(List<Tile> solution, List<Tile> supply, Color loopColor) {

		// check if adjacent tiles match

		boolean isFirstTileTouched = false;

		Tile firstTile = solution.get(0);
		Tile lastTile = solution.get(solution.size() - 1);
		Tile butLastTile = solution.get(solution.size() - 2);

		int lastTileCol = lastTile.getCol();
		int lastTileRow = lastTile.getRow();

		for (int direction = 0; direction < Tile.NUM_DIRECTIONS; direction++) {
			int neighborCol = lastTileCol + getColOffset(lastTile, direction);
			int neighborRow = lastTileRow + getRowOffset(lastTile, direction);
			Tile neighborTile = BOARD[neighborRow][neighborCol];
			if (neighborTile != EMPTY) {
				Color tileColor = lastTile.getColor(direction);

				int neighborDirection = (direction + (Tile.NUM_DIRECTIONS / 2)) % Tile.NUM_DIRECTIONS;
				Color neighborColor = neighborTile.getColor(neighborDirection);
				if (tileColor != neighborColor) {
					return false;
				}

				if ((neighborTile == butLastTile) && (neighborColor != loopColor)) {
					return false;
				}

				if ((neighborTile == firstTile) && (neighborColor == loopColor)) {
					isFirstTileTouched = true;
				}
			}
		}

		if ((supply.size() == 1) && (isFirstTileTouched == false)) {
			return false;
		}

		return true;
	}

	private static boolean hasHole(Tile[][] subBoard) {
		int numRows = subBoard.length;
		int numCols = subBoard[0].length;

		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				if (subBoard[r][c] == EMPTY) {
					return true;
				}
			}
		}
		return false;
	}

	private static void floodFill(Tile[][] subBoard, int col, int row, Tile aTile) {
		int numRows = subBoard.length;
		int numCols = subBoard[0].length;

		if ((col < 0) || (row < 0) || (col >= numCols) || (row >= numRows)) {
			return;
		}

		if (subBoard[row][col] != EMPTY) {
			return;
		}

		subBoard[row][col] = aTile;
		floodFill(subBoard, col - 1, row, aTile);
		floodFill(subBoard, col + 1, row, aTile);
		floodFill(subBoard, col, row + 1, aTile);
		floodFill(subBoard, col, row - 1, aTile);
	}
}
