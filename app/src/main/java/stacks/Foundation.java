package stacks;

import jasoncole.solitaire.Card;
import jasoncole.solitaire.Suit;

/**
 * Created by SYSTEM on 7/15/2018.
 */

public class Foundation extends CardStack{

    public final String TAG = "Foundation";

    private Suit suit;

    public Foundation (int x, int y, Suit suit) {
        super(x, y);
        this.suit = suit;
    }

    @Override
    public boolean validStack(Card card) {
        if (card == null)
            return false;
        if (!card.hasNext())
            return true;
        if (card.getNext().getValue() == card.getValue() + 1 && card.getSuit() == suit)
            return validStack(card.getNext());
        return false;
    }

    @Override
    protected boolean validDrop(Card card) {
        if (!validStack(card))
            return false;
        if (isEmpty()) {
            if (card.getValue() == 1 ) {
                suit = card.getSuit();
                return true;
            }
            return false;
        }
        if (card.getSuit() != suit)
            return false;



        if (tail.getValue() == card.getValue() - 1)
            return true;

        return false;
    }

    @Override
    protected boolean validPickup(Card card) {
        return validStack(card);
    }


}
