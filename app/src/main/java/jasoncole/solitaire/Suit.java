package jasoncole.solitaire;

/**
 * Created by SYSTEM on 7/13/2018.
 */

public enum Suit {
    Clubs       ("♣"),
    Diamonds    ("♦"),
    Hearts      ("♥"),
    Spades      ("♠");

    private final String character;
    Suit (String character) {
        this.character = character;
    }

    public String getCharacter() {
        return character;
    }


}
