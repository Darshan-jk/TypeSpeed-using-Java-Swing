/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
/**
 *
 * @author JKD
 */
public class TypeSpeed extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TypeSpeed.class.getName());
    
    private Timer timer;
    private int timeLeft = 60;
    private String currentWord = "";
    private int currentIndex = 0;

    private int score = 0;
    private int totalWords = 0;

    private JLabel statusLabel, timeLabel, accuracyLabel;
    private JTextField showwords, enterwords;
    private JButton nextBtn, startBtn;
    private JProgressBar progressBar;

    String[] words = {

        // ===== EASY (lowercase) =====
        "cat", "dog", "sun", "pen", "cup",
        "book", "tree", "milk", "fish", "door",

        // ===== EASY (uppercase) =====
        "CAT", "DOG", "SUN", "PEN", "CUP",

        // ===== EASY (numbers) =====
        "123", "456", "789", "101", "202",

        // ===== MEDIUM (normal words) =====
        "apple", "banana", "keyboard", "screen", "mouse",
        "window", "school", "market", "garden", "internet",

        // ===== MEDIUM (mixed case) =====
        "Java", "Python", "Laptop", "Mobile", "Google",
        "YouTube", "GitHub", "StackOverflow",

        // ===== MEDIUM (alpha-numeric) =====
        "java123", "code2024", "test01", "user99", "data100",

        // ===== HARD (long words) =====
        "developer", "programming", "application", "technology",
        "synchronization", "implementation", "architecture",
        "communication", "optimization", "configuration",

        // ===== HARD (mixed complex) =====
        "Java_Dev2024", "Code@123", "Test#Case1", "User_Name99",

       
    };

    /**
     * Creates new form TypeSpeed
     */
    public TypeSpeed() {
        
        initUI();
        setupActions();
    }
    
    private void initUI() {

        setTitle("Typing Speed Test");
        setSize(420, 300);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(Color.WHITE);

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        topPanel.setBackground(Color.WHITE);

        statusLabel = new JLabel("Ready", JLabel.CENTER);
        timeLabel = new JLabel("60s", JLabel.CENTER);
        accuracyLabel = new JLabel("0%", JLabel.CENTER);

        topPanel.add(statusLabel);
        topPanel.add(timeLabel);
        topPanel.add(accuracyLabel);

        // ===== PROGRESS BAR =====
        progressBar = new JProgressBar(0, 60);
        progressBar.setValue(60);
        progressBar.setPreferredSize(new Dimension(300, 8));
        progressBar.setMaximumSize(new Dimension(300, 8));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setBorderPainted(false);
        progressBar.setForeground(new Color(0, 180, 0));

        // ===== WORD DISPLAY =====
        showwords = new JTextField();
        showwords.setFont(new Font("Arial", Font.BOLD, 18));
        showwords.setMaximumSize(new Dimension(300, 40));
        showwords.setHorizontalAlignment(JTextField.CENTER);
        showwords.setEditable(false);

        // ===== INPUT FIELD =====
        enterwords = new JTextField();
        enterwords.setFont(new Font("Arial", Font.PLAIN, 16));
        enterwords.setMaximumSize(new Dimension(300, 35));
        enterwords.setHorizontalAlignment(JTextField.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        nextBtn = new JButton("Next");
        startBtn = new JButton("Start");

        nextBtn.setEnabled(false);

        buttonPanel.add(nextBtn);
        buttonPanel.add(startBtn);

        // ===== ADD COMPONENTS =====
        mainPanel.add(topPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(progressBar);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(showwords);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(enterwords);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private void setupActions() {

        nextBtn.addActionListener(e -> nextWord());

        startBtn.addActionListener(e -> {

            if (startBtn.getText().equals("Start")) {

                score = 0;
                totalWords = 0;

                nextBtn.setEnabled(true);
                nextWord();

                enterwords.requestFocusInWindow(); // ✅ MOVE CURSOR HERE

                startBtn.setText("Restart");
                statusLabel.setText("Started");

            } else {

                showResult();
                resetGame();

                startBtn.setText("Start");
                nextBtn.setEnabled(false);
            }
        });

        enterwords.addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent e) {

                String typed = enterwords.getText();

                if (typed.equals(currentWord)) {

                    timer.stop(); // ✅ STOP TIMER IMMEDIATELY
                    statusLabel.setText("✅ Correct");

                } else {
                    statusLabel.setText("❌ Typing...");
                }

                updateAccuracy();
            }

            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    String typed = enterwords.getText();

                    if (typed.equals(currentWord)) {
                        score++;
                        nextWord(); // move to next
                    } else {
                        statusLabel.setText("❌ Wrong word!");
                    }
                }
            }
        });

        timer = new Timer(1000, e -> {
            timeLeft--;
            progressBar.setValue(timeLeft);
            timeLabel.setText(timeLeft + "s");

            if (timeLeft > 30) {
                progressBar.setForeground(new Color(0, 180, 0));
            } else if (timeLeft > 10) {
                progressBar.setForeground(Color.ORANGE);
            } else {
                progressBar.setForeground(Color.RED);
            }

            if (timeLeft <= 0) {
                timer.stop();
                statusLabel.setText("⛔ Time Up");
                enterwords.setEditable(false);
                showResult();
            }
        });
    }

    private void nextWord() {

        if (currentIndex >= words.length) {
            // ✅ ALL WORDS COMPLETED
            timer.stop();
            enterwords.setEditable(false);
            showResult();
            statusLabel.setText("🎉 Test Completed");
            return;
        }

        currentWord = words[currentIndex];
        currentIndex++;

        showwords.setText(currentWord);
        enterwords.setText("");
        enterwords.setEditable(true);

        enterwords.requestFocusInWindow();

        totalWords++;
        startTimer();
    }

    private void startTimer() {
        timer.stop();
        timeLeft = 60;

        progressBar.setValue(60);
        timeLabel.setText("60s");

        timer.start();
    }

    private void updateAccuracy() {
        double accuracy = totalWords == 0 ? 0 : ((double) score / totalWords) * 100;
        accuracyLabel.setText(String.format("%.1f%%", accuracy));
    }

    private void showResult() {
        double accuracy = totalWords == 0 ? 0 : (double) score / totalWords * 100;

        statusLabel.setText(
            "Score: " + score + "/" + totalWords +
            " | Accuracy: " + String.format("%.1f", accuracy) + "%"
        );
    }

    private void resetGame() {
        timer.stop();
        currentIndex = 0;

        timeLeft = 60;
        progressBar.setValue(60);
        timeLabel.setText("60s");

        showwords.setText("");
        enterwords.setText("");
        enterwords.setEditable(true);

        score = 0;
        totalWords = 0;

        accuracyLabel.setText("0%");
        statusLabel.setText("Ready");
    }
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Type Speed Testing");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(168, 168, 168)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addGap(144, 144, 144))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(340, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new TypeSpeed().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
