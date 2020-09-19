# SolvingTantrix
Solving Tantrix is an open-source program for solving _Tantrix Discovery_ puzzles, written in Java.

The Solving Tantrix source code is available under the MIT license.

Enjoy -- Lorenz

## Table of Contents

* [Introduction](#introduction)
* [Solution](#solution)
* [Results](#results)
* [Build Instructions](#build-instructions)

## Introduction

I recently received a _Tantrix Discovery_ puzzle, which consists of 10 numbered, hexagonal tiles:

![Tantrix Discovery Tiles](images/readme/Tiles_strip.png "Tantrix Discovery Tiles")

The goal is to arrange a subset of the tiles by applying the following rules:
1. All line segments at the edges of neighboring tiles must match their colors.
1. Some line segments of the tiles must form a closed loop in a given color.

You start with tiles 1-3, then work your way up to the full set of 10 tiles.

For example, tiles 1-4 can be arranged into the following solutions, with the given loop color being red:

![Tantrix Discovery Tiles 1-4](images/readme/Tiles_1_4.png "Tantrix Discovery Tiles 1-4")

This made me curious: How many solutions are possible for a given number of tiles and color? Would mean more tiles more (or fewer?) solutions?

## Solution

I wrote a computer program in Java (the source code is available in this repo) that computes all solutions. It uses a backtracking algorithm and eliminates duplicate solutions by computing a hash value, which it compares with those of previous solutions. It also rejects solutions containing holes (apparently an unofficial Tantrix rule). For each actual solution the program renders an image and saves it to a file.

## Results

The game’s instructions suggest a set of puzzles. Here are **some sample solutions**:
Tiles 1-... | Loop Color | Sample Solution
-------:|------------|:--------------:
3	      | Yellow     | ![Tantrix Solution 3 Yellow](images/readme/Solution_03_2_0001.png "Tantrix Solution 3 Yellow")
4	      | Red        | ![Tantrix Solution 4 Red](images/readme/Solution_04_0_0003.png "Tantrix Solution 4 Red")
5	      | Red        | ![Tantrix Solution 5 Red](images/readme/Solution_05_0_0007.png "Tantrix Solution 5 Red")
6	      | Blue       | ![Tantrix Solution 6 Blue](images/readme/Solution_06_1_0011.png "Tantrix Solution 6 Blue")
7	      | Red        | ![Tantrix Solution 7 Red](images/readme/Solution_07_0_0029.png "Tantrix Solution 7 Red")
8	      | Blue       | ![Tantrix Solution 8 Blue](images/readme/Solution_08_1_0085.png "Tantrix Solution 8 Blue")
9	      | Yellow     | ![Tantrix Solution 9 Yellow](images/readme/Solution_09_2_0182.png "Tantrix Solution 9 Yellow")
10      | Red        | ![Tantrix Solution 10 Red](images/readme/Solution_10_0_0289.png "Tantrix Solution 10 Red")
10      | Blue       | ![Tantrix Solution 10 Blue](images/readme/Solution_10_1_1639.png "Tantrix Solution 10 Blue")
10      | Yellow     | ![Tantrix Solution 10 Yellow](images/readme/Solution_10_2_2560.png "Tantrix Solution 10 Yellow")

Out of all solutions, one of **my favorite solutions** is this double-loop:

![Tantrix Solution Double Loop](images/readme/Solution_10_1_2134.png "Tantrix Solution Double Loop")

Find a **“poster-like” image** of all 2902 solutions [here](images/poster/poster.png).

Find **individual images** of all 2902 solutions [here](images/solutions).

If you like statistics, here’s a breakdown of the numbers of solutions by tile set and loop color:

Tiles 1-... | Yellow | Red    | Blue
-----------:|-------:|-------:|-------:
3           | 2      | -      | -
4           | -      | 2      | -
5           | -      | 4      | -
6           |	-      | -      | 8
7           | -      | 32     | 36
8           | -      | -      | 84
9           | 114    | -      | -
10          | 388    | 1280   | 952
**Total**   | **504** | **1318** | **1080**

They add up to a grand total of	2902 solutions.

## Build Instructions

Build the program with the Eclipse IDE by using the provided Eclipse project files. I used Eclipse 2019-12 (4.10.0) running Java 1.8, but older versions might do as well. After running the program, find the image files of each solution in the `solutions` subfolder of the project.

Source of Tantrix tile images: [Wikipedia Tantrix](https://en.wikipedia.org/wiki/Tantrix "Wikipedia Tantrix") with own modifications
