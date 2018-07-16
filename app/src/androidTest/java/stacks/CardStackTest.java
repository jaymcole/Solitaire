package stacks;

import org.junit.Test;

import java.util.ArrayList;

import jasoncole.solitaire.Card;
import jasoncole.solitaire.Suit;

import static org.junit.Assert.*;

/**
 * Created by Jason Cole on 7/16/2018.
 */
public class CardStackTest {

    @Test
    public void CardStack_AddCardToTop_1 () {
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
        Card[] actual = CardStack.stackToArray(tableau.getHead());

        assertArrayEquals(actual, expected);
        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
    }

    @Test
    public void CardStack_AddCardToTop_2 () {
        Tableau tableau = new Tableau(0, 0, 0,0);

        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new Card(Card.RED, 1, Suit.Diamonds));

        for (Card c : cards) {
            tableau.addCardToTop(c);
        }
        Card[] expected = cards.toArray(new Card[cards.size()]);
        Card[] actual = CardStack.stackToArray(tableau.getHead());

        assertArrayEquals(actual, expected);
        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
    }

    @Test
    public void CardStack_AddCardToTop_3 () {
        Tableau tableau = new Tableau(0, 0, 0,0);

        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new Card(Card.RED, 1, Suit.Diamonds));
        cards.add(new Card(Card.RED, 2, Suit.Clubs));
        cards.add(new Card(Card.RED, 3, Suit.Hearts));
        cards.add(new Card(Card.RED, 4, Suit.Spades));
        cards.add(new Card(Card.RED, 1, Suit.Diamonds));
        cards.add(new Card(Card.RED, 2, Suit.Clubs));
        cards.add(new Card(Card.RED, 3, Suit.Hearts));
        cards.add(new Card(Card.RED, 4, Suit.Spades));
        cards.add(new Card(Card.RED, 1, Suit.Diamonds));
        cards.add(new Card(Card.RED, 2, Suit.Clubs));
        cards.add(new Card(Card.RED, 3, Suit.Hearts));
        cards.add(new Card(Card.RED, 4, Suit.Spades));

        for (Card c : cards) {
            tableau.addCardToTop(c);
        }
        Card[] expected = cards.toArray(new Card[cards.size()]);
        Card[] actual = CardStack.stackToArray(tableau.getHead());

        assertArrayEquals(actual, expected);
        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
    }

    @Test
    public void CardStack_AddCardToBottom_1 () {
        Tableau tableau = new Tableau(0, 0, 0,0);

        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new Card(Card.RED, 1, Suit.Diamonds));
        cards.add(new Card(Card.RED, 2, Suit.Clubs));
        cards.add(new Card(Card.RED, 3, Suit.Hearts));
        cards.add(new Card(Card.RED, 4, Suit.Spades));


        for (Card c : cards) {
            tableau.addCardToBottom(c);
        }

        // Flip array
        int size = cards.size();
        for (int i = 0; i < size / 2; i++) {
            final Card card = cards.get(i);
            cards.set(i, cards.get(size - i - 1)); // swap
            cards.set(size - i - 1, card); // swap
        }


        Card[] expected = cards.toArray(new Card[cards.size()]);
        Card[] actual = CardStack.stackToArray(tableau.getHead());

        assertArrayEquals(actual, expected);
        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
    }

    @Test
    public void CardStack_Remove_1() {
        Tableau tableau = new Tableau(0, 0, 0,0);

        ArrayList<Card> cards = new ArrayList<Card>();
        Card test = new Card(Card.RED, 1, Suit.Diamonds);
        cards.add(test);
        cards.add(new Card(Card.RED, 2, Suit.Clubs));
        cards.add(new Card(Card.RED, 3, Suit.Hearts));
        cards.add(new Card(Card.RED, 4, Suit.Spades));


        for (Card c : cards) {
            tableau.addCardToTop(c);
        }

        tableau.remove(test);
        cards.remove(test);

        Card[] expected = cards.toArray(new Card[cards.size()]);
        Card[] actual = CardStack.stackToArray(tableau.getHead());

        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
        assertArrayEquals(actual, expected);
    }

    @Test
    public void CardStack_Remove_2() {
        Tableau tableau = new Tableau(0, 0, 0,0);

        Card test = new Card(Card.RED, 1, Suit.Diamonds);
        Card test2 = new Card(Card.RED, 7, Suit.Diamonds);
        Card test3 = new Card(Card.RED, 8, Suit.Diamonds);

        ArrayList<Card> cards = new ArrayList<Card>();

        cards.add(new Card(Card.RED, 2, Suit.Clubs));
        cards.add(test);
        cards.add(new Card(Card.RED, 3, Suit.Hearts));
        cards.add(test2);
        cards.add(test3);
        cards.add(new Card(Card.RED, 4, Suit.Spades));


        for (Card c : cards) {
            tableau.addCardToTop(c);
        }

        tableau.remove(test);
        tableau.remove(test2);
        tableau.remove(test3);

        cards.remove(test);
        cards.remove(test2);
        cards.remove(test3);

        Card[] expected = cards.toArray(new Card[cards.size()]);
        Card[] actual = CardStack.stackToArray(tableau.getHead());

        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
        assertArrayEquals(actual, expected);
    }

    @Test
    public void CardStack_Pickup_1() {

        Tableau tableau = new Tableau(0, 0, 10,10);

        ArrayList<Card> cards = new ArrayList<Card>();
        Card test = new Card(Card.RED, 1, Suit.Diamonds);
        cards.add(test);
        cards.add(new Card(Card.RED, 2, Suit.Clubs));
        cards.add(new Card(Card.RED, 3, Suit.Hearts));
        cards.add(new Card(Card.RED, 4, Suit.Spades));


        for (Card c : cards) {
            tableau.addCardToTop(c);
        }

        Card recovered = tableau.pickup((int)test.getX(), (int)test.getY());
        cards.remove(test);

        tableau.remove(test);

        Card[] expected = cards.toArray(new Card[cards.size()]);
        Card[] actual = CardStack.stackToArray(tableau.getHead());

        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
        assertArrayEquals(actual, expected);
        assertEquals(recovered, test);
    }

    @Test
    public void CardStack_Pickup_2() {
        Tableau tableau = new Tableau(0, 0, 10,10);

        Card test = new Card(Card.RED, 1, Suit.Diamonds);
        Card test2 = new Card(Card.RED, 7, Suit.Diamonds);
        Card test3 = new Card(Card.RED, 8, Suit.Diamonds);

        ArrayList<Card> cards = new ArrayList<Card>();

        cards.add(new Card(Card.RED, 2, Suit.Clubs));
        cards.add(test);
        cards.add(new Card(Card.RED, 3, Suit.Hearts));
        cards.add(test2);
        cards.add(test3);
        cards.add(new Card(Card.RED, 4, Suit.Spades));


        for (Card c : cards) {
            tableau.addCardToTop(c);
        }

        Card recovered_1 = tableau.pickup((int)test.getX(), (int)test.getY());
        tableau.remove(recovered_1);
        Card recovered_2 = tableau.pickup((int)test2.getX(), (int)test2.getY());
        tableau.remove(recovered_2);
        Card recovered_3 = tableau.pickup((int)test3.getX(), (int)test3.getY());
        tableau.remove(recovered_3);


        cards.remove(test);
        cards.remove(test2);
        cards.remove(test3);

        Card[] expected = cards.toArray(new Card[cards.size()]);
        Card[] actual = CardStack.stackToArray(tableau.getHead());

        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
        assertArrayEquals(actual, expected);

        assertEquals(recovered_1, test);
        assertEquals(recovered_2, test2);
        assertEquals(recovered_3, test3);
    }
}