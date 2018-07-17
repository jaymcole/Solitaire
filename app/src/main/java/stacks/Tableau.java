package stacks;

import android.graphics.Rect;
import android.graphics.RectF;

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
        bounds = new RectF(x, y, x + (int)Card.width, Integer.MAX_VALUE);
    }

    @Override
    public void addCardToBottom (Card card) {
        super.addCardToBottom(card);
//        bounds = new RectF(x, y, x + (int)Card.width, tail.getY() + Card.height);
    }

    @Override
    public void addCardToTop (Card card) {
        super.addCardToTop(card);
//        bounds = new RectF(x, y, x + (int)Card.width, tail.getY() + Card.height);
    }

    @Override
    public void remove(Card card) {
        super.remove(card);
//        if (tail == null) {
//            bounds = new RectF(x, y, x + (int)Card.width, Card.height);
//        } else {
//            bounds = new RectF(x, y, x + (int)Card.width, tail.getY() + Card.height);
//        }
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
        if (!validStack(card) || !tail.isRevealed())
            return false;
        return tail.getValue() == card.getValue() + 1 && tail.getSuit().getColor() != card.getSuit().getColor();
    }

    @Override
    protected boolean validPickup(Card card) {
        if (card == null)
            return false;

        card.poke();
        if (!card.isRevealed())
            return false;
        return validStack(card);
    }
}
