package jasoncole.solitaire;

/**
 * Created by SYSTEM on 7/13/2018.
 */

public enum Suit {
    Spades      ("♠", Card.BLACK),
    Diamonds    ("♦", Card.RED),
    Hearts      ("♥", Card.RED),
    Clubs       ("♣", Card.BLACK);


    private final String character;
    private final int color;
    Suit (String character, int color) {
        this.character = character;
        this.color = color;
    }

    public String getCharacter() {
        return character;
    }

    public int getColor() {return color;}
}
