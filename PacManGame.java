package pacman_game;

import java.util.Scanner;

class PacMan {
	private static final int MAZE_WIDTH = 10;
	private static final int MAZE_HEIGHT = 10;
	private static final char PACMAN = 'P';
	private static final char GHOST = 'G';
	private static final char DOT = '.';
	private static final char POWER_UP = '*';
	private static final char EMPTY = ' ';
	private static char[][] maze = new char[MAZE_HEIGHT][MAZE_WIDTH];
	static boolean invencible = false;
	private static final int INVINCIBILITY_DURATION = 10;
	private static int pacmanX, pacmanY;
	static int score = 0;
	private static int remainingGhosts;
	private static int remainingInvincibilityTime = 10;

	public static void initializeGame() {
		score = 0;
		remainingGhosts = 10;
		for (int i = 0; i < MAZE_HEIGHT; i++) {
			for (int j = 0; j < MAZE_WIDTH; j++) {
				maze[i][j] = DOT; // initialize the maze with dots
			}
		}

		placeGhost(10);
		placePowerUps(15);

		// place pacman in the middle of the maze
		pacmanX = MAZE_HEIGHT / 2;
		pacmanY = MAZE_WIDTH / 2;
		maze[pacmanX][pacmanY] = PACMAN;
	}

	private static void placeGhost(int numberGhosts) { // place ghost random
		int count = 0;
		while (count < numberGhosts) {
			int x = (int) (Math.random() * MAZE_HEIGHT);
			int y = (int) (Math.random() * MAZE_WIDTH);
			if (maze[x][y] == DOT) {
				maze[x][y] = GHOST;
				count++;
			}
		}
		remainingGhosts = numberGhosts;
	}

	private static void placePowerUps(int numPowerUps) { // place power ups random
		int count = 0;
		while (count < numPowerUps) {
			int x = (int) (Math.random() * MAZE_HEIGHT);
			int y = (int) (Math.random() * MAZE_WIDTH);
			if (maze[x][y] == DOT) {
				maze[x][y] = POWER_UP;
				count++;
			}
		}
	}

	public static void displayMaze() { // print the maze
		for (char[] row : maze) {
			for (char column : row) {
				System.out.print(column + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	private static void moveGhosts() {

		for (int i = 0; i < MAZE_HEIGHT; i++) {
			for (int j = 0; j < MAZE_WIDTH; j++) {
				if (maze[i][j] == GHOST) {

					int newX = i, newY = j;
					while (true) {
						int direction = (int) (Math.random() * 4);

						if (direction == 0 && newX > 0 && maze[newX - 1][newY] == DOT) {
							newX--; // Up
							break;
						} else if (direction == 1 && newX < MAZE_HEIGHT - 1 && maze[newX + 1][newY] == DOT) {
							newX++; // Down
							break;
						} else if (direction == 2 && newY > 0 && maze[newX][newY - 1] == DOT) {
							newY--; // Left
							break;
						} else if (direction == 3 && newY < MAZE_WIDTH - 1 && maze[newX][newY + 1] == DOT) {
							newY++; // Right
							break;
						}
					}
					if (newX == pacmanX && newY == pacmanY) {
						System.out.println("Oh no! Pacman has beed eaten by a Ghost!");
						return;
					}
					maze[i][j] = DOT;
					maze[newX][newY] = GHOST;
				}
			}
		}
	}

	public synchronized static boolean movePacman(String direction) {
		int newX = pacmanX, newY = pacmanY;
		switch (direction) {
		case "W" -> {
			if (pacmanX > 0)
				newX--;
		} // If Pacman is not in the border top
		case "S" -> {
			if (pacmanX < MAZE_HEIGHT - 1)
				newX++;
		} // If Pacman is not in the border down
		case "A" -> {
			if (pacmanY > 0)
				newY--;
		} // if Pacman is not in the left border
		case "D" -> {
			if (pacmanY < MAZE_WIDTH - 1)
				newY++;
		} // if Pacman is not in the right boder
		default -> System.out.println("Wrong choice. Please try again.");
		}

		if (invencible) {
			if (maze[newX][newY] == GHOST) {
				maze[newX][newY] = EMPTY; // empty the space when pacman eats a gosht
				score += 20;
				remainingGhosts = getRemainingGhosts() - 1;
				System.out.println("Pacman ate a ghost! +20 points!");
			}
		} else if (maze[newX][newY] == GHOST) {
			System.out.println("A Ghost ate you! Game Over.\nYour score: " + score);
			return false;
		}

		if (maze[newX][newY] == DOT) {
			score += 10;
			System.out.println("Pacman collected a dot! Current Score: " + score);
		}

		if (maze[newX][newY] == POWER_UP) { // if pacman eats a powerup is invencible
			if (!invencible) {
				invencible = true;
				System.out.println("You ate a Power Up. Pacman now is Invencible for 10 seconds!");
				java.util.Timer newTimer = new java.util.Timer();

				newTimer.schedule(new java.util.TimerTask() { // and a timer works for 10 seconds
					@Override
					public void run() {
						invencible = false; // when the timer is over pacman is not invencible anymore
						System.out.println("Time is over. Pacman is not longer invencible.");
					}
				}, INVINCIBILITY_DURATION * 1000

				);
			}
		}

		if (getRemainingGhosts() == 0) {
			System.out.println("Congratulations! Pacman ate all the ghosts! You win! Your score: " + score);
			return false;
		}

		maze[pacmanX][pacmanY] = EMPTY; // empty the espace where pacman was
		pacmanX = newX;
		pacmanY = newY;
		maze[pacmanX][pacmanY] = PACMAN; // move pacman to the new place
		moveGhosts();
		return true;
	}

	public synchronized static int getRemainingGhosts() {
		return remainingGhosts;
	}

}

public class PacManGame {

	public static void main(String[] args) {

		PacMan pacman = new PacMan();
		Scanner scanner = new Scanner(System.in);
		PacMan.initializeGame();
		PacMan.displayMaze();

		while (true) {
			System.out.print("Enter your direction (W/S/A/D): ");
			String input = scanner.nextLine().toUpperCase();
			if (input.matches("[WSAD]")) {
				boolean moved = PacMan.movePacman(input);
				if (!moved) { // if it is not posible to move, ends the loop
					break;
				}
				PacMan.displayMaze();
				if (PacMan.getRemainingGhosts() == 0) {
					System.out.println(
							"Congratulations! Pacman ate all the ghosts! You win! Your score: " + pacman.score);
					break;
				}
			} else {
				System.out.println("Invalid input.");
			}

		}
		scanner.close();
	}

}
