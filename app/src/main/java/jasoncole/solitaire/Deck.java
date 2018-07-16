package jasoncole.solitaire;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.LinkedList;

import stacks.Waste;

/**
 * Created by SYSTEM on 7/13/2018.
 */

public class Deck {

    public static final int CARDS_PER_HAND = 1;
    private Card hand;
    private Waste discard;

    public Deck(int x, int y) {
//        super(x, y);

//        hand = new Card((int)(this.getX() + Card.width) + (int)(Card.width/10), (int)this.getY(), (int)(Card.width/2), 0);
//        discard = new Card(0, 0,0,0);
    }

    public void setDeck(LinkedList<Card> cards) {
        for(Card c : cards) {
//            this.addCard(c);
            c.setRevealed(false);
        }
        dealHand();
    }

    /**
     *
     * @param x
     * @param y
     * @return True if event should be consumed.
     */
    public boolean click(int x, int y) {
//        if (inBounds(x,y)) {
//            dealHand();
//            return true;
//        }
        return false;
    }

    public Card getHand() {
        return hand;
    }

    public void dealHand() {
//        if (this.getNext() == null) {
//            this.addCard(discard.getNext());
//            discard.setNext(null);
//            Deck.setRevealStack(this, false);
//            return;
//        }
//
//        discard.addCard(hand.getNext());
//        hand.setNext(null);
//
//        Card bottomOfNewHand = getNext();
//        Card topOfNewHand = getNext();
//
//        // Moves of the stack of cards until topOfNewHand is CARDS_PER_HAND cards from bottomOfNewHand.
//        for(int i = 1 ; i < CARDS_PER_HAND; i++) {
//            if (!topOfNewHand.hasNext())
//                break;
//            topOfNewHand = topOfNewHand.getNext();
//        }
//
//        // Moves topOfNewHand and bottomOfNewHand up the stack until topOfNewHand reaches the end of the stack.
//        while (topOfNewHand.hasNext()) {
//            bottomOfNewHand = bottomOfNewHand.getNext();
//            topOfNewHand = topOfNewHand.getNext();
//        }
//        bottomOfNewHand.getParent().setNext(null);
//        bottomOfNewHand.setParent(null);
//        hand.addCard(bottomOfNewHand);
//        Deck.setRevealStack(hand, true);

    }

    private void print(String tag) {
//        StringBuilder sb = new StringBuilder();
//        Card c = this.getNext();
//        sb.append("(p)");
//        while(c != null) {
//            sb.append(" " + c.getName());
//            c = c.getNext();
//        }
//        sb.append(" --- ");
//        c = hand.getNext();
//        sb.append("(p)");
//        while (c != null) {
//            sb.append(" " + c.getName());
//            c = c.getNext();
//        }
//        Log.d(tag, sb.toString());
    }


//    while (top.hasNext()) {
//        top = top.getNext();    // Traverse stack until the top most card is found.
//    }
//
//        for(int i = 1 ; i < CARDS_PER_HAND; i++) {
//        if (top == null || top.isPlaceHolder())
//            break;
//        top = top.getParent();
//    }

    private static void setRevealStack (Card card, boolean reveal) {
        if (card == null)
            return;
        card.setRevealed(reveal);
        setRevealStack(card.getNext(), reveal);
    }


//    @Override
//    public void render(Canvas canvas, Paint paint) {
//        super.render(canvas, paint);
//
//        if (this.getNext() != null) {
//            this.getNext().render(canvas, paint);
//        }
//        hand.renderStack(canvas, paint);
//    }
}
