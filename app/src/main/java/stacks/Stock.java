package stacks;

import jasoncole.solitaire.Card;

/**
 * Created by SYSTEM on 7/15/2018.
 */

public class Stock extends CardStack {

    public final String TAG = "Stock";
    private Waste waste;
    private Waste discard;

    public Stock(int x, int y, CardStack waste) {

        super(x, y);
        this.waste = (Waste)waste;
        discard = new Waste(-1000, -1000, 0, 0);
    }


    @Override
    public Card pickup(int x, int y) {
        drawHand();
        return null;
    }

    public void drawHand() {
        discard.addCardToTop(waste.emptyStack());
        if (head == null) {
            this.addCardToBottom(discard.emptyStack());
            setRevealed(head, false);

        } else {
            for(int i = 0; i < waste.getMaxCards(); i++) {
                Card card = tail;
                if (card == null)
                    break;
                remove(tail);
                card.setRevealed(true);
                waste.addCardToTop(card);
            }
        }
    }

    @Override
    protected boolean validDrop(Card card) {
        return false;
    }
}
