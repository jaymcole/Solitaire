package stacks;

import android.graphics.Rect;

import jasoncole.solitaire.Card;

/**
 * Created by SYSTEM on 7/15/2018.
 */

public class Tableau extends CardStack {

    public final String TAG = "Tableau";

    public Tableau (int x, int y, int offsetX, int offsetY) {
        super(x, y, offsetX, offsetY);
    }

    @Override
    protected void initBounds() {
        bounds = new Rect(x, y, x + (int)Card.width, Integer.MAX_VALUE);
    }

    @Override
    public boolean validStack(Card card) {
        if (card == null)
            return false;
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
}
