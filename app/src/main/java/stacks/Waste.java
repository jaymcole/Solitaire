package stacks;

import android.graphics.Canvas;

import jasoncole.solitaire.Card;

/**
 * Created by Jason Cole on 7/15/2018.
 */

public class Waste extends CardStack {

    public final String TAG = "Waste";
    private static int CARDS_PER_HAND = 3;
    private Waste waste;

    public Waste (int x, int y, int offsetX, int offsetY) {
        super(x, y, offsetX, offsetY);
    }



    public void checkHand() {

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
        if (cards.isEmpty()) {
            if (card.getValue() == 13)
                return true;
            else
                return false;
        }
        if (!validStack(card))
            return false;
        return tail().getValue() == card.getValue() + 1 && tail().getSuit().getColor() != card.getSuit().getColor();
    }

    @Override
    public void renderStack(Canvas canvas) {
        int start = Math.max(0, cards.size()-1 - CARDS_PER_HAND);
        for (int i = start; i < cards.size(); i++) {
            cards.get(i).render(canvas);;
        }

    }

    @Override
    public void updateStack() {
        if (cards.isEmpty())
            return;

        int start = Math.max(0, cards.size()- CARDS_PER_HAND);
        int offX = 0, offY = 0;
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
                card.setTarget(x + offX,y + offY);
            card.setStack(this);
            if (i >= start) {
                if (card.isRevealed()) {
                    offX += childOffsetX;
                    offY += childOffsetY;
                } else {
                    offX += childOffsetX * 0.5f;
                    offY += childOffsetY * 0.5f;
                }
            }

        }


    }

    @Override
    protected boolean validPickup(Card card) {
        return validStack(card);
    }

    public int getMaxCards () {
        return CARDS_PER_HAND;
    }

    public void setWaste(Waste waste) {
        this.waste = waste;
    }

}
