import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class MatchCards {

    class Card {
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

    ArrayList<Card> cardSet;
    ImageIcon cardBackImageIcon;

    int boardWidth = columns * cardWidth; // 5 * 128
    int boardHeight = rows * cardHeight;

    JFrame frame = new JFrame("Pokemon Match Cards");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();



    int errorCount = 0;
    ArrayList<JButton> board;

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
        textLabel.setText("Errors: " + Integer.toString(errorCount));

        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        board = new ArrayList<JButton>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (Card card : cardSet) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(card.cardImageIcon);
            tile.setFocusable(false);
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        frame.pack();
        frame.setVisible(true);


    }

    void setupCards() {
        cardSet = new ArrayList<Card>();
        for(String cardName : cardList) {
            Image cardImg = new ImageIcon(getClass().getResource("./img/" + cardName + ".jpg")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));

            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }

        cardSet.addAll(cardSet);

        Image cardBackImage = new ImageIcon(getClass().getResource("./img/back.jpg")).getImage();
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
}
