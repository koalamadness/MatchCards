import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.*;

import javax.sound.sampled.*;
import javax.swing.border.Border;
import java.io.File;
import java.io.IOException;

public class MatchCards {

    static class Card {
        String cardName;
        ImageIcon cardImageIcon;

        public Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }

    String[] cardList = { //track cardNames
            "darkness",
            "double",
            "fairy",
            "fighting",
            "fire",
            "grass",
            "lightning",
            "metal",
            "psychic",
            "water"
    };

    int rows = 4;
    int columns = 5;
    int cardWidth = 90;
    int cardHeight = 128;

 // más negativo = más bajo

    boolean turnPlayerOne = true;
    int playerOneScore = 0;
    int playerTwoScore = 0;

    ArrayList<Card> cardSet;
    ImageIcon cardBackImageIcon;

    int boardWidth = columns * cardWidth; // 5 * 128
    int boardHeight = rows * cardHeight;

    JFrame frame = new JFrame("Pokemon Match Cards");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();

    int errorCount = 0;
    int pairCount = 0;

    ArrayList<JButton> board;
    Timer hideCardTimer;
    boolean gameReady = false;

    JButton card1Selected;
    JButton card2Selected;


    Timer turnTimer;
    int timeLeft = 15;

    MatchCards() {
        setupCards();
        shuffleCards();

        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        printTextLabel();

        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        board = new ArrayList<>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (Card card : cardSet) {
            JButton tile = getJButton(card);

            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(new Dimension(boardWidth, 30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> restartGame());
        restartGamePanel.add(restartButton);
        frame.add(restartGamePanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        turnTimer = new Timer(1000, e ->{
            timeLeft--;

            if (timeLeft <= 0){
                changeTurn();
            }

            printTextLabel();
        });

        //start game
        hideCardTimer = new Timer(1500, e -> hideCards());

        hideCardTimer.setRepeats(false);
        hideCardTimer.start();
    }

    private JButton getJButton(Card card) {
        JButton tile = new JButton();
        tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
        tile.setOpaque(true);
        tile.setIcon(card.cardImageIcon);
        tile.setFocusable(false);

        tile.addActionListener(e -> {
            if (!gameReady) {
                return;
            }
            JButton tile1 = (JButton) e.getSource();
            if (tile1.getIcon() == cardBackImageIcon) {
                if (card1Selected == null) {
                    card1Selected = tile1;
                    int index = board.indexOf(card1Selected);
                    card1Selected.setIcon(cardSet.get(index).cardImageIcon);

                }
                else if (card2Selected == null) {
                    card2Selected = tile1;
                    int index = board.indexOf(card2Selected);
                    card2Selected.setIcon(cardSet.get(index).cardImageIcon);


                    //WRONG
                    if(card1Selected.getIcon() != card2Selected.getIcon()) {

                        errorCount += 1;
                        animateError(card1Selected, card2Selected);
                        playSound("mixkit-wrong-answer-fail-notification-946.wav");
                        changeTurn();

                        // lost
                        if (errorCount == 10) {
                            playSound("mixkit-horror-lose-2028.wav");
                            textLabel.setText("U LOOOST");
                            return;
                        }

                        hideCardTimer.start();

                    }
                    else {
                        //pairCount += 1;

                        animatePair(card1Selected, card2Selected);
                        if (turnPlayerOne) {
                            playerOneScore += 1;
                        } else {
                            playerTwoScore += 1;
                        }

                        playSound("mixkit-quick-win-video-game-notification-269.wav");


                        card1Selected = null;
                        card2Selected = null;

                        if (playerOneScore == 6 || playerTwoScore == 6) {
                            playSound("mixkit-game-level-completed-2059.wav");
                            textLabel.setText("U WOOON");
                            turnTimer.stop();


                            setBoardEnabled(false); // 👈 BLOQUEA CARTAS
                            gameReady = false;
                            return;
                        }
                    }
                    printTextLabel();
                }
            }

        });
        return tile;
    }

    void setupCards() {
        cardSet = new ArrayList<>();
        for(String cardName : cardList) {
            Image cardImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/" + cardName + ".jpg"))).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));

            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }

        cardSet.addAll(cardSet);

        Image cardBackImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/back.jpg"))).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImage.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
    }


    void shuffleCards() {
        System.out.println(cardSet);
        //shuffle
        for (int i = 0; i < cardSet.size(); i++){
            int j = (int) (Math.random() * cardSet.size());
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);

        }
    }
    void hideCards() {

        if (gameReady && card1Selected != null && card2Selected != null) {
            card1Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected.setIcon(cardBackImageIcon);
            card2Selected = null;
        } else {

            for (JButton jButton : board) {
                jButton.setIcon(cardBackImageIcon);
            }
            gameReady = true;
            restartButton.setEnabled(true);
            timeLeft = 15;
            turnTimer.start();
        }
    }

    void restartGame(){

        setBoardEnabled(true);
        gameReady = false;
        restartButton.setEnabled(false);
        card1Selected = null;
        card2Selected = null;
        playSound("mixkit-failure-arcade-alert-notification-240.wav");
        shuffleCards();
        for (int i = 0; i < board.size(); i++) {
            board.get(i).setIcon(cardSet.get(i).cardImageIcon);
        }

        errorCount = 0;
        pairCount = 0;
        playerOneScore = 0;
        playerTwoScore = 0;
        turnPlayerOne = true;
        turnTimer.stop();
        timeLeft = 15;
        printTextLabel();
        hideCardTimer.start();
    }

    void printTextLabel() {
        String currentPlayer = turnPlayerOne ? "Player 1" : "Player 2";

        textLabel.setText(
                currentPlayer +
                        " | Time: " + timeLeft +
                        "s | Errors: " + errorCount +
                        " | P1: " + playerOneScore +
                        " P2: " + playerTwoScore
        );
    }

    void changeTurn() {

        // Solo una carta abierta → se tapa
        if (card1Selected != null && card2Selected == null) {
            card1Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
        }

        turnTimer.stop();
        timeLeft = 15;
        turnPlayerOne = !turnPlayerOne;
        turnTimer.start();
    }

    void playSound(String soundFileName) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getResource("./sounds/" + soundFileName))
            );
            Clip clip = AudioSystem.getClip();

            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setBoardEnabled(boolean enabled) {
        for (JButton button : board) {
            button.setEnabled(enabled);
        }
    }

    void animateError(JButton c1, JButton c2) {
        Border red = BorderFactory.createLineBorder(Color.RED, 3);
        Border normal = UIManager.getBorder("Button.border");

        c1.setBorder(red);
        c2.setBorder(red);

        new Timer(600, e -> {
            c1.setBorder(normal);
            c2.setBorder(normal);
        }).start();
    }

    void animatePair(JButton c1, JButton c2) {
        Border green = BorderFactory.createLineBorder(Color.GREEN, 3);
        Border normal = UIManager.getBorder("Button.border");

        c1.setBorder(green);
        c2.setBorder(green);

        new Timer(500, e -> {
            c1.setBorder(normal);
            c2.setBorder(normal);
        }).start();
    }

}
