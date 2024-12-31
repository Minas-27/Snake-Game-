import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int TILE_SIZE = 25;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int GAME_UNITS = (WIDTH * HEIGHT) / (TILE_SIZE * TILE_SIZE);
    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyParts = 3;
    private int foodX, foodY;
    private char direction = 'R'; // Snake starts moving right
    private boolean running = false;
    private Timer timer;
    private Random random;

    // New: Difficulty levels and score
    private int score = 0;
    private int delay = 125; // Default speed for Intermediate level

    // State for menu
    private boolean isMenu = true;
    private JFrame frame;

    private Font titleFont = new Font("Serif", Font.BOLD, 50);
    private Font buttonFont = new Font("Arial", Font.PLAIN, 30);

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        random = new Random();
    }

    public void startGame() {
        spawnFood();
        running = true;
        isMenu = false;
        timer = new Timer(delay, this);
        timer.start();
    }

    public void spawnFood() {
        foodX = random.nextInt(WIDTH / TILE_SIZE) * TILE_SIZE;
        foodY = random.nextInt(HEIGHT / TILE_SIZE) * TILE_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] -= TILE_SIZE;
            case 'D' -> y[0] += TILE_SIZE;
            case 'L' -> x[0] -= TILE_SIZE;
            case 'R' -> x[0] += TILE_SIZE;
        }
    }

    public void checkCollision() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            bodyParts++;
            score++; // Increment score
            spawnFood();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isMenu) {
            drawMenu(g);
        } else if (running) {
            drawGame(g);
        } else {
            gameOver(g);
        }
    }

    public void drawMenu(Graphics g) {
        g.setColor(new Color(20, 20, 20)); // Almost black
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(new Color(0, 204, 255)); // Light blue color for the title
        g.setFont(titleFont);
        String title = "Snake Game";
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(title, (WIDTH - metrics.stringWidth(title)) / 2, HEIGHT / 4);

        String[] levels = {"Easy", "Intermediate", "Hard", "Very Hard"};
        int yOffset = HEIGHT / 2 - 50;
        for (String level : levels) {
            g.setColor(new Color(0, 102, 204)); // Dark blue for buttons
            g.setFont(buttonFont);
            g.drawString(level, (WIDTH - metrics.stringWidth(level)) / 2, yOffset);
            yOffset += 50; // Space between options
        }

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        g.drawString("Press 1, 2, 3, or 4 to select difficulty", (WIDTH - metrics.stringWidth("Press 1, 2, 3, or 4 to select difficulty")) / 2, HEIGHT - 50);
    }
    public void drawGame(Graphics g) {
        g.setColor(new Color(0, 0, 50)); // Bluish black color
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.RED);
        g.fillOval(foodX, foodY, TILE_SIZE, TILE_SIZE);

        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g.setColor(new Color(0, 204, 255)); // Light blue for head
            } else {
                g.setColor(new Color(0, 102, 204)); // Dark blue for body
            }
            g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 20);
    }

    public void gameOver(Graphics g) {
        g.setColor(new Color(0, 0, 50)); // Bluish black color
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Game Over text
        g.setColor(new Color(255, 51, 51)); // Red color for Game Over text
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2);

        // Display final score
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Final Score: " + score, (WIDTH - metrics.stringWidth("Final Score: " + score)) / 2, HEIGHT / 2 + 40);

        g.drawString("Press R to Restart", (WIDTH - metrics.stringWidth("Press R to Restart")) / 2, HEIGHT / 2 + 80);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollision();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isMenu) {
            if (e.getKeyCode() == KeyEvent.VK_1) {
                delay = 200; // Easy
                startGame();
            } else if (e.getKeyCode() == KeyEvent.VK_2) {
                delay = 125; // Intermediate
                startGame();
            } else if (e.getKeyCode() == KeyEvent.VK_3) {
                delay = 75; // Hard
                startGame();
            } else if (e.getKeyCode() == KeyEvent.VK_4) {
                delay = 50; // Very Hard
                startGame();
            }
        } else if (running) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') direction = 'L';
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') direction = 'R';
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') direction = 'U';
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') direction = 'D';
                }
            }
        } else if (!running && e.getKeyCode() == KeyEvent.VK_R) {
            resetGame();
        }
    }

    public void resetGame() {
        bodyParts = 3;
        score = 0;
        direction = 'R';
        running = false;
        isMenu = true;
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame gamePanel = new SnakeGame();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
