package stacks;

import android.graphics.Rect;

import jasoncole.solitaire.Card;

/**
 * Created by Jason Cole on 7/15/2018.
 */

public class Waste extends CardStack {

    public final String TAG = "Waste";
    private static int CARDS_PER_HAND = 3;

    public Waste (int x, int y, int offsetX, int offsetY) {
        super(x, y, offsetX, offsetY);
    }


    @Override
    public boolean validStack(Card card) {
        if (!card.hasNext())
            return true;
        if (card.getNext().getValue() == card.getValue() - 1)
            return validStack(card.getNext());
        return false;
    }

    @Override
    protected boolean validDrop(Card card) {
        if (tail == null) {
            if (card.getValue() == 13)
                return true;
            else
                return false;
        }
        if (!validStack(card))
            return false;
        return tail.getValue() == card.getValue() + 1 && tail.getSuit().getColor() != card.getSuit().getColor();
    }

    @Override
    protected boolean validPickup(Card card) {
        return validStack(card);
    }

    public int getMaxCards () {
        return CARDS_PER_HAND;
    }

}
