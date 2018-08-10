package jasoncole.solitaire;

import org.junit.Test;

import java.util.ArrayList;

import stacks.CardStack;
import stacks.Tableau;

import static org.junit.Assert.*;

/**
 * Created by Jason Cole on 7/16/2018.
 */
public class CardTest {
    @Test
    public void poke() throws Exception {

    }

    @Test
    public void cardsInStack() throws Exception {
        Tableau tableau = new Tableau(0, 0, 0,0);

        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new Card(Card.RED, 1, Suit.Diamonds));
        cards.add(new Card(Card.RED, 2, Suit.Clubs));
        cards.add(new Card(Card.RED, 3, Suit.Hearts));
        cards.add(new Card(Card.RED, 4, Suit.Spades));


        for (Card c : cards) {
            tableau.addCardToTop(c);
        }
        Card[] expected = cards.toArray(new Card[cards.size()]);
//        Card[] actual = CardStack.stackToArray(tableau.head());

//        assertEquals(actual.length, CardStack.countStack(tableau.head()));
    }

    @Test
    public void setPosition_1() throws Exception {
        Card card = new Card(Card.RED, 1, Suit.Diamonds);
        card.setPosition(100, 203);

        assertEquals("", 100, card.getX(), 0);
        assertEquals("", 203, card.getY(), 0);
    }

    @Test
    public void setPosition_2() throws Exception {
        Tableau tableau = new Tableau(0, 0, 0,0);

        ArrayList<Card> cards = new ArrayList<Card>();
        Card c1 = new Card(Card.RED, 1, Suit.Diamonds);
        cards.add(c1);
        Card c2 = new Card(Card.RED, 2, Suit.Clubs);
        cards.add(c2);
        Card c3 = new Card(Card.RED, 3, Suit.Hearts);
        cards.add(c3);
        Card c4 = new Card(Card.RED, 4, Suit.Spades);
        cards.add(c4);

        for (Card c : cards) {
            tableau.addCardToTop(c);
        }

        tableau.remove(c1);
        c1.setPosition(123,8723);
        tableau.updateStack();

        assertEquals("", 123, c1.getX(), 0);
        assertEquals("", 8723, c1.getY(), 0);
    }

}