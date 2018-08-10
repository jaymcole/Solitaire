package stacks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

import gameViews.GameView;
import jasoncole.solitaire.Card;

/**
 * Created by Jason Cole on 7/15/2018.
 */

public abstract class CardStack {

    public final String TAG = "Empty";

    protected int childOffsetX, childOffsetY;
    protected int x, y;
    protected RectF bounds;

    protected static Bitmap placeholder;
    protected static Paint paint;

    protected static Paint placeholderPaint;

    protected ArrayList<Card> cards;

    public static void init(Context context) {
        paint = new Paint();
        placeholder = BitmapFactory.decodeResource(context.getResources(), GameView.placeholder_resource);
        placeholder = Bitmap.createScaledBitmap(placeholder, (int) Card.width, (int) Card.height, false);

        placeholderPaint = new Paint();
        int temp = Color.argb(100, 255,255,255);
        placeholderPaint.setColor(temp);
        placeholderPaint.setStyle(Paint.Style.STROKE);
        placeholderPaint.setStrokeWidth(5);

    }

    public CardStack(int x, int y) {
        this.x = x;
        this.y = y;
        this.childOffsetX = 0;
        this.childOffsetY = 0;
        initBounds();
        cards = new ArrayList<Card>();

    }

    public CardStack(int x, int y, int offsetX, int offsetY) {
        this.x = x;
        this.y = y;
        this.childOffsetX = offsetX;
        this.childOffsetY = offsetY;
        initBounds();
        cards = new ArrayList<Card>();

    }

    protected void update() {}

    public boolean drop (Card card) {
        if (validDrop(card)) {
            card.getStack().remove(card);
//            cards.add(card);
            this.addCardToTop(card);
            return true;
        }
        return false;
    }

    /**
     * Checks if dropping card onto the top of this stack is valid. Changes depending on the stack type and game being played.
     * @param card - The card being droppped onto this stack.
     * @return True if drop is legal.
     */
    protected abstract boolean validDrop(Card card);

    public Card pickup(int x, int y) {
        Card card = null;
        for (int i = cards.size()-1; i >= 0; i--) {
            card = cards.get(i);
            if (card.inBounds(x, y)) {
                if (this.validStack(card))
                    return card;
                return null;
            }
        }
        return null;
    }

    protected abstract boolean validPickup(Card card);

    public boolean validStack (Card card) {
        return false;
    }

    public void giveCardsStartingAt(CardStack stack, Card card) {
        int index = cards.indexOf(card);
        if (index == -1)
            return;
        while (index < cards.size()) {
            Card c = cards.get(index);
            this.remove(index);
            stack.addCardToTop(c);
        }
    }


    protected void initBounds() {
        bounds = new RectF(x, y, x + Card.width, y + Card.height);
    }

    /**
     *
     * @return the bounds of this stack.
     *          Note: Currently only works for stacks where the top-most card is lowest on the screen.
     */
    public RectF getBounds() {
        if (cards.isEmpty())
            bounds.set(x, y, x + Card.width, y + Card.height);
        else {


            for (int i = cards.size()-1; i >= 0; i--) {
                Card topCard = cards.get(i);
                if (topCard.isDoneUpdating()) {
                    bounds.set(x, y, topCard.getX() + Card.width, topCard.getY() + Card.height);
                    break;
                }
            }
        }
        return bounds;
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
        card.setStack(this);
        cards.add(card);
        updateStack();
    }

    /**
     * Adds card to the bottom of this stack. (bottom = head)
     * @param card
     */
    public void addCardToBottom (Card card) {
        card.setStack(this);
        cards.add(0, card);
        updateStack();
    }

    public void forceStackPosition() {
        if (cards.isEmpty())
            return;

        int offX = 0, offY = 0;
        for (Card card : cards) {
            card.setPosition(x + offX,y + offY);
            card.setStack(this);
            if (card.isRevealed()) {
                offX += childOffsetX;
                offY += childOffsetY;
            } else {
                offX += childOffsetX * 0.5f;
                offY += childOffsetY * 0.5f;
            }

        }
    }

    public void updateStack() {
        if (cards.isEmpty())
            return;

        int offX = 0, offY = 0;
        for (Card card : cards) {
            card.setTarget(x + offX,y + offY);
            card.setStack(this);
            if (card.isRevealed()) {
                offX += childOffsetX;
                offY += childOffsetY;
            } else {
                offX += childOffsetX * 0.5f;
                offY += childOffsetY * 0.5f;
            }

        }
    }

    public void render (Canvas canvas) {
        canvas.drawRoundRect(x, y, x + Card.width, y + Card.height, Card.cornerRoundness, Card.cornerRoundness, placeholderPaint);
    }

    /**
     * Renders this stack
     * @param canvas
     */
    public void renderStack(Canvas canvas) {
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).render(canvas);;
        }
    }

    public void remove(int cardIndex) {
        if (cardIndex < 0 || cardIndex >= cards.size())
            return;
        cards.remove(cardIndex);
        updateStack();
    }

    /**
     * Removes card from this stack.
     * @param card
     */
    public void remove(Card card) {
        cards.remove(card);
        updateStack();
    }


    public void emptyStackToTop(CardStack emptyTo) {
        for(Card card : cards) {
            emptyTo.addCardToTop(card);
        }
        cards.clear();
    }

    public void emptyStackToBottom(CardStack emptyTo) {
         for(Card card : cards) {
             emptyTo.addCardToBottom(card);
         }
        cards.clear();
    }

    /**
     *
     * @return the topmost card in this stack.
     *          Returns null if the stack is empty.
     */
    public Card tail() {
        if (cards.isEmpty())
            return null;
        return cards.get(cards.size()-1);
    }

    /**
     *
     * @return the bottom-most card in this stack.
     *          Returns null if this stack is empty.
     */
    public Card head() {
        if (cards.isEmpty())
            return null;
        return cards.get(0);
    }

    /**
     * Sets all cards in this stack to revealed.
     * @param revealed
     */
    protected void setRevealedStatus(boolean revealed) {
        for (Card card : cards) {
            card.setRevealed(revealed);
        }
    }


    private void resetStack(Card card) {
        if (card != null) {
            card.setStack(null);
            resetStack(card.getNext());
        }
    }

    //TODO: finish this
    public Card getNext(Card card) {
        int index = cards.indexOf(card)+1;
        if (index > -1 && index < cards.size()) {
            return cards.get(index);
        }
        return null;
    }

    public void pokeStack() {
        if (cards.isEmpty())
            return;
        int last = cards.size() - 1;
        if (last < cards.size())
            cards.get(last).poke();
    }

    public int getChildOffsetX() {
        return childOffsetX;
    }

    public int getChildOffsetY() {
        return childOffsetY;
    }

    public int cardsInStack() {
        if (cards.isEmpty())
            return 0;
        else
            return cards.size();
    }


    public int getX() {return x;}
    public int getY() {return y;}
}
