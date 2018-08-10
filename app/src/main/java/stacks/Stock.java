package stacks;

import android.graphics.Canvas;

import jasoncole.solitaire.Card;

/**
 * Created by Jason Cole on 7/15/2018.
 */

public class Stock extends CardStack {

    public final String TAG = "Stock";
    private Waste waste;
    private Waste discard;

    public Stock(int x, int y, CardStack waste) {

        super(x, y);
        this.waste = (Waste)waste;
//        discard = new Waste(-1000, -1000, 0, 0);
    }



    @Override
    public Card pickup(int x, int y) {
        drawHand();
        return null;
    }

    @Override
    protected boolean validPickup(Card card) {
        return false;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawCircle(x + (Card.width * 0.5f), y + (Card.height * 0.5f), (Card.width * 0.3f), placeholderPaint);
    }

    public void drawHand() {
        if (head() == null) {
            waste.emptyStackToBottom(this);
            this.setRevealedStatus(false);

        } else {
            for(int i = 0; i < waste.getMaxCards(); i++) {
                Card card = tail();
                if (card == null)
                    break;
                remove(card);
                card.setRevealed(true);
                waste.addCardToTop(card);
            }
        }
    }

    @Override
    protected boolean validDrop(Card card) {
        return false;
    }

    public Waste getDiscard() {
        return discard;
    }
}
