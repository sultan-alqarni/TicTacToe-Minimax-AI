import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
 
/**
 * Tic-Tac-Toe Game with AI using Minimax Algorithm
 * CSC 361 - Artificial Intelligence Project
 */
public class TicTacToe extends JFrame {
 
    // ─── Constants ───────────────────────────────────────────────
    static final char PLAYER = 'X';
    static final char AI     = 'O';
    static final char EMPTY  = ' ';
 
    // ─── Game State ───────────────────────────────────────────────
    char[] board = new char[9];   // 9 cells: index 0-8
    boolean gameOver = false;
 
    // ─── GUI Components ───────────────────────────────────────────
    JButton[] cells = new JButton[9];
    JLabel statusLabel;
    JButton restartBtn;
 
    // ─── Colors ───────────────────────────────────────────────────
    Color BG         = new Color(30, 30, 46);
    Color CELL_BG    = new Color(49, 50, 68);
    Color X_COLOR    = new Color(137, 180, 250);  // blue
    Color O_COLOR    = new Color(243, 139, 168);  // pink
    Color WIN_COLOR  = new Color(166, 227, 161);  // green
    Color TEXT_COLOR = new Color(205, 214, 244);
 
    // ══════════════════════════════════════════════════════════════
    public TicTacToe() {
        initBoard();
        buildUI();
    }
 
    // ─── Initialize board to empty ────────────────────────────────
    void initBoard() {
        for (int i = 0; i < 9; i++) board[i] = EMPTY;
        gameOver = false;
    }
 
    // ─── Build the Swing UI ───────────────────────────────────────
    void buildUI() {
        setTitle("Tic-Tac-Toe  |  AI vs Player");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(10, 10));
 
        // ── Status label ──
        statusLabel = new JLabel("Your turn! (X)", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(statusLabel, BorderLayout.NORTH);
 
        // ── Grid panel ──
        JPanel grid = new JPanel(new GridLayout(3, 3, 8, 8));
        grid.setBackground(BG);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
 
        Font cellFont = new Font("Segoe UI", Font.BOLD, 56);
        for (int i = 0; i < 9; i++) {
            final int idx = i;
            JButton btn = new JButton("");
            btn.setFont(cellFont);
            btn.setBackground(CELL_BG);
            btn.setForeground(TEXT_COLOR);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(BG, 3, true));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> playerMove(idx));
            cells[i] = btn;
            grid.add(btn);
        }
        add(grid, BorderLayout.CENTER);
 
        // ── Restart button ──
        restartBtn = new JButton("↺  Restart");
        restartBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        restartBtn.setBackground(new Color(69, 71, 90));
        restartBtn.setForeground(TEXT_COLOR);
        restartBtn.setFocusPainted(false);
        restartBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        restartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        restartBtn.addActionListener(e -> restartGame());
        JPanel south = new JPanel();
        south.setBackground(BG);
        south.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        south.add(restartBtn);
        add(south, BorderLayout.SOUTH);
 
        pack();
        setSize(420, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }
 
    // ══════════════════════════════════════════════════════════════
    //  GAME LOGIC
    // ══════════════════════════════════════════════════════════════
 
    // ─── Handle player click ──────────────────────────────────────
    void playerMove(int idx) {
        if (gameOver || board[idx] != EMPTY) return;
 
        board[idx] = PLAYER;
        cells[idx].setText("X");
        cells[idx].setForeground(X_COLOR);
 
        if (checkEnd()) return;
 
        statusLabel.setText("AI is thinking...");
        // Small delay so the player sees their move first
        Timer timer = new Timer(300, e -> {
            aiMove();
            checkEnd();
        });
        timer.setRepeats(false);
        timer.start();
    }
 
    // ─── AI picks best move using Minimax ─────────────────────────
    void aiMove() {
        int best = -1, bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < 9; i++) {
            if (board[i] == EMPTY) {
                board[i] = AI;
                int score = minimax(board, 0, false);
                board[i] = EMPTY;
                if (score > bestScore) { bestScore = score; best = i; }
            }
        }
        if (best != -1) {
            board[best] = AI;
            cells[best].setText("O");
            cells[best].setForeground(O_COLOR);
        }
    }
 
    // ══════════════════════════════════════════════════════════════
    //  MINIMAX ALGORITHM
    // ══════════════════════════════════════════════════════════════
    /**
     * Minimax: recursively evaluates all possible game states.
     * AI (maximizer) tries to maximize score; Player (minimizer) tries to minimize.
     *
     * @param b        current board state
     * @param depth    how many moves deep we are
     * @param isMax    true = AI's turn, false = Player's turn
     * @return         score of this board state
     */
    int minimax(char[] b, int depth, boolean isMax) {
        int result = evaluate(b);
        if (result != 0) return result;          // someone won
        if (isBoardFull(b)) return 0;            // draw
 
        if (isMax) {
            // AI wants the highest score
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (b[i] == EMPTY) {
                    b[i] = AI;
                    best = Math.max(best, minimax(b, depth + 1, false));
                    b[i] = EMPTY;
                }
            }
            return best;
        } else {
            // Player wants the lowest score
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (b[i] == EMPTY) {
                    b[i] = PLAYER;
                    best = Math.min(best, minimax(b, depth + 1, true));
                    b[i] = EMPTY;
                }
            }
            return best;
        }
    }
 
    /**
     * Evaluate board: +10 if AI wins, -10 if Player wins, 0 otherwise.
     */
    int evaluate(char[] b) {
        int[][] lines = {
            {0,1,2}, {3,4,5}, {6,7,8},   // rows
            {0,3,6}, {1,4,7}, {2,5,8},   // cols
            {0,4,8}, {2,4,6}              // diagonals
        };
        for (int[] line : lines) {
            if (b[line[0]] != EMPTY && b[line[0]] == b[line[1]] && b[line[1]] == b[line[2]]) {
                return (b[line[0]] == AI) ? +10 : -10;
            }
        }
        return 0;
    }
 
    // ══════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════
 
    boolean isBoardFull(char[] b) {
        for (char c : b) if (c == EMPTY) return false;
        return true;
    }
 
    /** Returns winning line indices, or null if no winner yet. */
    int[] winningLine() {
        int[][] lines = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
        };
        for (int[] line : lines) {
            if (board[line[0]] != EMPTY
                && board[line[0]] == board[line[1]]
                && board[line[1]] == board[line[2]]) {
                return line;
            }
        }
        return null;
    }
 
    /** Check if game ended; update UI accordingly. Returns true if over. */
    boolean checkEnd() {
        int[] line = winningLine();
        if (line != null) {
            gameOver = true;
            char winner = board[line[0]];
            // Highlight winning cells
            for (int idx : line) cells[idx].setBackground(WIN_COLOR);
            statusLabel.setText(winner == PLAYER ? "🎉 You win!" : "🤖 AI wins!");
            return true;
        }
        if (isBoardFull(board)) {
            gameOver = true;
            statusLabel.setText("🤝 It's a draw!");
            return true;
        }
        statusLabel.setText("Your turn! (X)");
        return false;
    }
 
    void restartGame() {
        initBoard();
        for (JButton btn : cells) {
            btn.setText("");
            btn.setBackground(CELL_BG);
            btn.setForeground(TEXT_COLOR);
        }
        statusLabel.setText("Your turn! (X)");
    }
 
    // ─── Entry Point ──────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToe::new);
    }
}
 