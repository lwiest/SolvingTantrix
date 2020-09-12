/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Lorenz Wiest
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import com.lorenzwiest.solvingtantrix.SolvingTantrix.Tile;

public class SolvingTantrixGraphics {
	private final static String FILENAME_TILE_STRIP = "resources/TantrixTiles.png";
	private final static String FOLDERNAME_SOLUTIONS = "solutions/";

	private final static int NUM_TILES = 14;
	private final static int TILE_WIDTH = 126;
	private final static int TILE_HEIGHT = 144;

	public static void render(String filename, List<Tile> solution, Tile[][] board, int minRow, int maxRow, int minCol, int maxCol) {
		final int OFFSET_X = 4;
		final int OFFSET_Y = (int) (OFFSET_X * Math.cos(Math.toRadians(60 / 2)));
		final int GAP_H = (int) ((TILE_WIDTH / 2) * Math.tan(Math.toRadians(60 / 2)));

		final int TILE_WIDTH_PLUS_OFFSET_X = TILE_WIDTH + OFFSET_X;
		final int HALF_TILE_WIDTH_PLUS_OFFSET_X = TILE_WIDTH_PLUS_OFFSET_X / 2;
		final int TILE_HEIGHT_PLUS_OFFSET_Y = TILE_HEIGHT + OFFSET_Y;

		int minX = Integer.MAX_VALUE;
		int maxX = -1;

		int numBoardRows = board.length;
		int numBoardCols = board[0].length;

		for (int r = 0; r < numBoardRows; r++) {
			int x = isEven(r) ? HALF_TILE_WIDTH_PLUS_OFFSET_X : 0;
			for (int c = 0; c < numBoardCols; c++) {
				if (board[r][c] != SolvingTantrix.EMPTY) {
					if (x < minX) {
						minX = x;
					}
					if ((x + TILE_WIDTH) > maxX) {
						maxX = x + TILE_WIDTH;
					}
				}
				x += TILE_WIDTH_PLUS_OFFSET_X;
			}
		}

		int offsetX = 0;
		if (isEven(minRow)) {
			for (int r = minRow + 1; r <= maxRow; r += 2) {
				if (board[r][minCol] != SolvingTantrix.EMPTY) {
					offsetX = HALF_TILE_WIDTH_PLUS_OFFSET_X;
					break;
				}
			}
		}

		if (isOdd(minRow)) {
			for (int r = minRow; r <= maxRow; r += 2) {
				if (board[r][minCol] != SolvingTantrix.EMPTY) {
					offsetX = HALF_TILE_WIDTH_PLUS_OFFSET_X;
					break;
				}
			}
		}

		int numSubBoardRows = (maxRow - minRow) + 1;
		int numSubBoardCols = (maxCol - minCol) + 1;

		int imgHeight = (((numSubBoardRows * (TILE_HEIGHT_PLUS_OFFSET_Y - GAP_H)) + ((numSubBoardRows > 1) ? (GAP_H - OFFSET_Y) : 0)));
		int imgWidth = maxX - minX;

		BufferedImage imgRaster = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = imgRaster.createGraphics();

		for (int r = 0; r < numSubBoardRows; r++) {
			for (int c = 0; c < numSubBoardCols; c++) {
				if (board[minRow + r][minCol + c] != SolvingTantrix.EMPTY) {
					BufferedImage imgTile = getTile(board[minRow + r][minCol + c]);

					int x = offsetX + ((c * TILE_WIDTH_PLUS_OFFSET_X) - (isOdd(minRow + r) ? HALF_TILE_WIDTH_PLUS_OFFSET_X : 0));
					int y = r * (TILE_HEIGHT_PLUS_OFFSET_Y - GAP_H);

					g2d.drawImage(imgTile, x, y, null);
				}
			}
		}

		g2d.dispose();
		saveImage(filename, imgRaster);
	}

	private static boolean isEven(int number) {
		return ((number % 2) == 0);
	}

	private static boolean isOdd(int number) {
		return ((number % 2) == 1);
	}

	private static void saveImage(String filename, BufferedImage img) {
		try {
			File outputFolder = new File(FOLDERNAME_SOLUTIONS);
			if (outputFolder.exists() == false) {
				outputFolder.mkdir();
			}
			File outputFile = new File(FOLDERNAME_SOLUTIONS + filename);
			ImageIO.write(img, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static BufferedImage getTile(Tile t) {
		final int TILE_ID_TO_STRIP_TILE[] = { 11, 4, 1, 7, 2, 6, 13, 12, 5, 10 };

		if (IMG_TILES == null) {
			IMG_TILES = createImageTiles();
		}

		int tileNr = t.getId();
		int indexStripTile = TILE_ID_TO_STRIP_TILE[tileNr - 1];
		int indexRot = t.getAngle() / 60;

		return IMG_TILES[indexStripTile][indexRot];
	}

	private static BufferedImage[][] IMG_TILES;

	private static BufferedImage[][] createImageTiles() {
		BufferedImage imgStrip = null;
		try {
			imgStrip = ImageIO.read(new File(FILENAME_TILE_STRIP));
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedImage[][] imgTiles = new BufferedImage[NUM_TILES][Tile.NUM_DIRECTIONS];

		for (int numTile = 0; numTile < NUM_TILES; numTile++) {
			for (int direction = 0; direction < Tile.NUM_DIRECTIONS; direction++) {
				int offsetX = numTile * TILE_WIDTH;
				int offsetY = 0;

				BufferedImage imgTile = new BufferedImage(TILE_WIDTH, TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d1 = imgTile.createGraphics();

				int srcX = offsetX;
				int srcY = offsetY;
				int dstX = offsetX + TILE_WIDTH;
				int dstY = offsetY + TILE_HEIGHT;

				g2d1.drawImage(imgStrip, 0, 0, TILE_WIDTH, TILE_HEIGHT, srcX, srcY, dstX, dstY, null);
				g2d1.dispose();

				BufferedImage imgRotTile = new BufferedImage(TILE_WIDTH, TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d2 = imgRotTile.createGraphics();

				g2d2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				AffineTransform at = new AffineTransform();
				double rads = Math.toRadians(direction * 60);
				int x = TILE_WIDTH / 2;
				int y = TILE_HEIGHT / 2;

				at.rotate(rads, x, y);
				g2d2.setTransform(at);
				g2d2.drawImage(imgTile, 0, 0, null);
				g2d2.dispose();

				imgTiles[numTile][direction] = imgRotTile;
			}
		}
		return imgTiles;
	}
}
