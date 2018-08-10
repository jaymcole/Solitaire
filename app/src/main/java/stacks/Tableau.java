package stacks;

import android.graphics.RectF;

import gameViews.Klondike;
import jasoncole.solitaire.Card;

/**
 * Created by SYSTEM on 7/15/2018.
 */

public class Tableau extends CardStack {

    public final String TAG = "Tableau";

    public Tableau (int x, int y, int offsetX, int offsetY) {
        super(x, y, offsetX, offsetY);
    }

    /**
     *
     * @return true if all cards in stack have been revealed.
     */
    public boolean isStackRevealed() {
        if (head() == null)
            return true;
        return head().isRevealed();
    }

    @Override
    protected void initBounds() {
        bounds = new RectF(x, y, x + (int) Card.width, Integer.MAX_VALUE);
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
        if (card == null) {
            return false;
        }
        if (tail() == null) {
            if (card.getValue() == 13)
                return true;
            else
                return false;
        }
//        if (!validStack(card)
//                || !tail().isRevealed())
//            return false;
        return tail().getValue() == card.getValue() + 1 && tail().getSuit().getColor() != card.getSuit().getColor();
    }

    @Override
    protected boolean validPickup(Card card) {
        if (card == null)
            return false;

        Klondike.addScore(card.poke());
        if (!card.isRevealed())
            return false;
        return validStack(card);
    }
}
