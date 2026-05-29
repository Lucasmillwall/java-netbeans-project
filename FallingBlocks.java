package fallingblocksgame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FallingBlocks extends JPanel implements ActionListener, KeyListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int FPS = 60;

    private final Timer timer = new Timer(1000 / FPS, this);

    private Rectangle player;
    private int playerSpeed = 7;

    private final List<Rectangle> blocks = new ArrayList<>();
    private final Random rand = new Random();

    private int spawnCooldown = 0;
    private int blockSpeed = 4;

    private boolean leftPressed, rightPressed;
    private boolean gameOver = false;

    private long startTime;
    private int score = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Falling Blocks");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            FallingBlocks game = new FallingBlocks();
            frame.setContentPane(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            game.start();
        });
    }

    
    public FallingBlocks() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        resetGame();
    }

    private void start() {
        requestFocusInWindow();
        timer.start();
    }

    private void resetGame() {
        player = new Rectangle(WIDTH / 2 - 30, HEIGHT - 80, 60, 20);
        blocks.clear();
        leftPressed = rightPressed = false;
        gameOver = false;

        startTime = System.currentTimeMillis();
        score = 0;
        blockSpeed = 4;
        spawnCooldown = 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            updatePlayer();
            updateBlocks();
            updateDifficultyAndScore();
            checkCollisions();
        }
        repaint();
    }

    private void updatePlayer() {
        if (leftPressed) player.x -= playerSpeed;
        if (rightPressed) player.x += playerSpeed;

        if (player.x < 0) player.x = 0;
        if (player.x + player.width > WIDTH) player.x = WIDTH - player.width;
    }

    private void updateBlocks() {
        spawnCooldown--;
        if (spawnCooldown <= 0) {
            spawnBlock();
            int elapsedSec = (int) ((System.currentTimeMillis() - startTime) / 1000);
            spawnCooldown = Math.max(10, 35 - elapsedSec);
        }

        for (Rectangle b : blocks) {
            b.y += blockSpeed;
        }

        blocks.removeIf(b -> b.y > HEIGHT + 50);
    }

    private void spawnBlock() {
        int w = 30 + rand.nextInt(60);
        int h = 20 + rand.nextInt(40);
        int x = rand.nextInt(WIDTH - w);
        int y = -h;
        blocks.add(new Rectangle(x, y, w, h));
    }

    private void updateDifficultyAndScore() {
        int elapsedSec = (int) ((System.currentTimeMillis() - startTime) / 1000);
        score = elapsedSec * 10;

        blockSpeed = 4 + elapsedSec / 8;
        if (blockSpeed > 14) blockSpeed = 14;
    }

    private void checkCollisions() {
        for (Rectangle b : blocks) {
            if (b.intersects(player)) {
                gameOver = true;
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Player
        g2.setColor(Color.CYAN);
        g2.fillRect(player.x, player.y, player.width, player.height);

        // Blocks
        g2.setColor(Color.RED);
        for (Rectangle b : blocks) {
            g2.fillRect(b.x, b.y, b.width, b.height);
        }

        // HUD
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("Score: " + score, 15, 25);
        g2.drawString("Move: \u2190 \u2192 (or A/D)   Restart: R", 15, 50);

        // Game over text (no grey overlay)
        if (gameOver) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 48));
            String msg = "GAME OVER";
            int msgW = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (WIDTH - msgW) / 2, HEIGHT / 2 - 20);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 22));
            String msg2 = "Final Score: " + score + "   (Press R to Restart)";
            int msg2W = g2.getFontMetrics().stringWidth(msg2);
            g2.drawString(msg2, (WIDTH - msg2W) / 2, HEIGHT / 2 + 25);
        }

        g2.dispose();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) leftPressed = true;
        if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) rightPressed = true;
        if (k == KeyEvent.VK_R) resetGame();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) leftPressed = false;
        if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // not used
    }
}


