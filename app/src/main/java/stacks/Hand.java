package stacks;

import android.graphics.Canvas;
import android.util.Log;

import jasoncole.solitaire.Card;

/**
 * Created by Jason Cole on 8/5/2018.
 */

public class Hand extends CardStack {
    private CardStack previousStack;

    private int offX, offY;


    @Override
    public void render(Canvas canvas) {}

    public Hand(int x, int y) {
        super(x, y, 0, (int)(Card.width / 4));
        offX = 0;
        offY = 0;
    }

    public void setPositionOffsets(int x, int y) {
        offX = x;
        offY = y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        forceStackPosition();
    }

    public void setOffsetPosition(int x, int y) {
        this.x = x + offX;
        this.y = y + offY;
        forceStackPosition();
    }

    public void setChildOffsets(int x, int y) {
        this.childOffsetX = x;
        this.childOffsetY = y;
    }



    public void setPreviousStack(CardStack prev) {
        previousStack = prev;
    }

    public void returnCards() {
        if (previousStack != null) {
            this.emptyStackToTop(previousStack);
            previousStack.updateStack();
        }
    }

    public void dropStack(CardStack dropOn) {
        Card card = head();
        if (dropOn == null || card == null || dropOn == this) {
            returnCards();
            return;
        }

        if (dropOn.validDrop(card))
            this.emptyStackToTop(dropOn);
        else
            returnCards();
    }



    @Override
    protected boolean validDrop(Card card) {return false;}

    @Override
    protected boolean validPickup(Card card) {
        return false;
    }
}
