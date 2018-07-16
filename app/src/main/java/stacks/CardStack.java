package stacks;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import jasoncole.solitaire.Card;

/**
 * Created by Jason Cole on 7/15/2018.
 */

public abstract class CardStack {

    public final String TAG = "Empty";

    protected Card head = null, tail = null;
    protected int childOffsetX, childOffsetY;
    protected int x, y;
    protected Rect bounds;


    public CardStack(int x, int y) {
        this.x = x;
        this.y = y;
        this.childOffsetX = 0;
        this.childOffsetY = 0;
        head = null;
        tail = null;
        initBounds();
    }

    public CardStack(int x, int y, int offsetX, int offsetY) {
        this.x = x;
        this.y = y;
        head = null;
        tail = null;
        this.childOffsetX = offsetX;
        this.childOffsetY = offsetY;
        initBounds();
    }

    public boolean drop (Card card) {
        return validDrop(card);
    }

    public Card pickup(int x, int y) {
        if (head == null)
            return null;

        Card temp = head;
        Card card = head.inBounds(x, y) ? head : null;
        while (temp != null) {
            if (temp.inBounds(x, y)) {
                card = temp;
            }
            temp = temp.getNext();
        }

        if (validPickup(card)) {
            card.poke();
            return card;
        } else
            return null;
    }

    protected boolean validDrop(Card card) {
        return false;
    }

    protected boolean validPickup(Card card) {
        return false;
    }

    public boolean validStack (Card card) {
        if (card == null)
            return false;
        if (!card.hasNext())
            return true;
        if (!card.isRevealed())
            return false;
        if (card.getNext().getValue() == card.getValue() - 1)
            return validStack(card.getNext());
        return false;
    }

    protected void initBounds() {
        bounds = new Rect(x, y, x + (int)Card.width, y + (int)Card.height);
    }

    public boolean contains(int x, int y) {
        if (bounds == null)
            return false;
        return bounds.contains(x, y);
    }




     /**
     * Adds card to the top of this stack. (top = tail)
     * @param card
     */
    public void addCardToTop (Card card) {
        if (card == null)
            return;

        card.setParent(null);
        if (head == null) {
            addCardToBottom(card);
        }else if (tail == head) {
            tail = card;
            head.setNext(card);
            tail = getEndOfStack(card);
        } else {
            Card bottom = getEndOfStack(card);
            tail.setNext(card);
            tail = bottom;
        }
        tail.setNext(null);
        updateCardStack(head);
        head.update();

    }

    /**
     * Adds card to the bottom of this stack. (bottom = head)
     * @param card
     */
    public void addCardToBottom (Card card) {
        if (card == null) {
            return;
        }

        if (head == null) {
            head = card;
            head.setParent(null);
            tail = getEndOfStack(head);
        } else {
            Card oldHead = head;
            head = card;
            head.setParent(null);
            Card newBottom = getEndOfStack(head);
            newBottom.setNext(oldHead);
            oldHead.setParent(newBottom);

        }
        tail.setNext(null);

        updateCardStack(head);
        head.update();
    }

    public void updateStack() {
        if (head != null)
            updateCardStack(head);
    }

    /**
     * Fixes all cards in stack starting at card
     *      - Ensures card.cardstack = this stack
     *      - Ensures offsets are set correctly
     * @param card - the card to start at
     */
    private void updateCardStack(Card card) {
        if (card == null)
            return;
        card.setOffsets(childOffsetX, childOffsetY);
        card.setStack(this);
        updateCardStack(card.getNext());
    }

    /**
     * Renders this stack
     * @param canvas
     * @param paint
     */
    public void render(Canvas canvas, Paint paint) {
        canvas.drawRect(x, y, x + Card.width, y+ Card.height, paint);
        canvas.drawText(this.TAG, x, y + Card.height / 2, paint);
        if (head != null) {
            head.renderStack(canvas, paint);
            canvas.drawText( countStack(head) + "", x, y + 30, paint);
        }
    }

    /**
     * Removes card from this stack.
     * @param card
     */
    public void remove(Card card) {
        if (card == null) {
            return;
        }
        resetStack(card);

        if (card == head || card.getParent() == null) {
            head = null;
            tail = null;
            card.setParent(null);
        } else {
            tail = card.getParent();
            card.setParent(null);
            tail.setNext(null);
        }
    }

    public Card emptyStack() {
        Card card = head;
        head = null;
        tail = null;

        return card;
    }

    private void resetStack(Card card) {
        if (card != null) {
            card.setStack(null);
            resetStack(card.getNext());
        }
    }

    public static Card getEndOfStack(Card card) {
        if (card == null)
            return null;
        else if (!card.hasNext())
            return card;
        else
            return getEndOfStack(card.getNext());
    }

    public static int countStack(Card card) {
        if (card == null) {
            return 0;
        } else {
            return 1 + countStack(card.getNext());
        }

    }


    public static Card[] stackToArray(Card card) {
        Card head = card;
        ArrayList<Card> cards = new ArrayList<Card>();
        while(head != null) {
            cards.add(head);
            head = head.getNext();
        }
        return cards.toArray(new Card[cards.size()]);
    }

    public static void setRevealed(Card card, boolean reveal) {
        if (card == null)
            return;
        card.setRevealed(reveal);
        setRevealed(card.getNext(), reveal);
    }



    public boolean isEmpty() {return head == null;}
    public Card getHead () {return head;}
    public Card getTail () {return tail;}
    public int getX() {return x;}
    public int getY() {return y;}
    public int getChildOffsetX() {return childOffsetX;}
    public int getChildOffsetY() {return childOffsetY;}
}
