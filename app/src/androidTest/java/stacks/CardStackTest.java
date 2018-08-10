package stacks;

import org.junit.Test;

import java.util.ArrayList;

import jasoncole.solitaire.Suit;

/**
 * Created by Jason Cole on 7/16/2018.
 */
public class CardStackTest {

    @Test
    public void CardStack_AddCardToTop_1 () {
        Tableau tableau = new Tableau(0, 0, 0,0);

        ArrayList<Card_Old> cards = new ArrayList<Card_Old>();
        cards.add(new Card_Old(Card_Old.RED, 1, Suit.Diamonds));
        cards.add(new Card_Old(Card_Old.RED, 2, Suit.Clubs));
        cards.add(new Card_Old(Card_Old.RED, 3, Suit.Hearts));
        cards.add(new Card_Old(Card_Old.RED, 4, Suit.Spades));


        for (Card_Old c : cards) {
            tableau.addCardToTop(c);
        }
        Card_Old[] expected = cards.toArray(new Card_Old[cards.size()]);
        Card_Old[] actual = CardStack.stackToArray(tableau.getHead());

        assertArrayEquals(actual, expected);
        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
    }

    @Test
    public void CardStack_AddCardToTop_2 () {
        Tableau tableau = new Tableau(0, 0, 0,0);

        ArrayList<Card_Old> cards = new ArrayList<Card_Old>();
        cards.add(new Card_Old(Card_Old.RED, 1, Suit.Diamonds));

        for (Card_Old c : cards) {
            tableau.addCardToTop(c);
        }
        Card_Old[] expected = cards.toArray(new Card_Old[cards.size()]);
        Card_Old[] actual = CardStack.stackToArray(tableau.getHead());

        assertArrayEquals(actual, expected);
        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
    }

    @Test
    public void CardStack_AddCardToTop_3 () {
        Tableau tableau = new Tableau(0, 0, 0,0);

        ArrayList<Card_Old> cards = new ArrayList<Card_Old>();
        cards.add(new Card_Old(Card_Old.RED, 1, Suit.Diamonds));
        cards.add(new Card_Old(Card_Old.RED, 2, Suit.Clubs));
        cards.add(new Card_Old(Card_Old.RED, 3, Suit.Hearts));
        cards.add(new Card_Old(Card_Old.RED, 4, Suit.Spades));
        cards.add(new Card_Old(Card_Old.RED, 1, Suit.Diamonds));
        cards.add(new Card_Old(Card_Old.RED, 2, Suit.Clubs));
        cards.add(new Card_Old(Card_Old.RED, 3, Suit.Hearts));
        cards.add(new Card_Old(Card_Old.RED, 4, Suit.Spades));
        cards.add(new Card_Old(Card_Old.RED, 1, Suit.Diamonds));
        cards.add(new Card_Old(Card_Old.RED, 2, Suit.Clubs));
        cards.add(new Card_Old(Card_Old.RED, 3, Suit.Hearts));
        cards.add(new Card_Old(Card_Old.RED, 4, Suit.Spades));

        for (Card_Old c : cards) {
            tableau.addCardToTop(c);
        }
        Card_Old[] expected = cards.toArray(new Card_Old[cards.size()]);
        Card_Old[] actual = CardStack.stackToArray(tableau.getHead());

        assertArrayEquals(actual, expected);
        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
    }

    @Test
    public void CardStack_AddCardToBottom_1 () {
        Tableau tableau = new Tableau(0, 0, 0,0);

        ArrayList<Card_Old> cards = new ArrayList<Card_Old>();
        cards.add(new Card_Old(Card_Old.RED, 1, Suit.Diamonds));
        cards.add(new Card_Old(Card_Old.RED, 2, Suit.Clubs));
        cards.add(new Card_Old(Card_Old.RED, 3, Suit.Hearts));
        cards.add(new Card_Old(Card_Old.RED, 4, Suit.Spades));


        for (Card_Old c : cards) {
            tableau.addCardToBottom(c);
        }

        // Flip array
        int size = cards.size();
        for (int i = 0; i < size / 2; i++) {
            final Card_Old card = cards.get(i);
            cards.set(i, cards.get(size - i - 1)); // swap
            cards.set(size - i - 1, card); // swap
        }


        Card_Old[] expected = cards.toArray(new Card_Old[cards.size()]);
        Card_Old[] actual = CardStack.stackToArray(tableau.getHead());

        assertArrayEquals(actual, expected);
        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
    }

    @Test
    public void CardStack_Remove_1() {
        Tableau tableau = new Tableau(0, 0, 0,0);

        ArrayList<Card_Old> cards = new ArrayList<Card_Old>();
        Card_Old test = new Card_Old(Card_Old.RED, 1, Suit.Diamonds);
        cards.add(test);
        cards.add(new Card_Old(Card_Old.RED, 2, Suit.Clubs));
        cards.add(new Card_Old(Card_Old.RED, 3, Suit.Hearts));
        cards.add(new Card_Old(Card_Old.RED, 4, Suit.Spades));


        for (Card_Old c : cards) {
            tableau.addCardToTop(c);
        }

        tableau.remove(test);
        cards.remove(test);

        Card_Old[] expected = cards.toArray(new Card_Old[cards.size()]);
        Card_Old[] actual = CardStack.stackToArray(tableau.getHead());

        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
        assertArrayEquals(actual, expected);
    }

    @Test
    public void CardStack_Remove_2() {
        Tableau tableau = new Tableau(0, 0, 0,0);

        Card_Old test = new Card_Old(Card_Old.RED, 1, Suit.Diamonds);
        Card_Old test2 = new Card_Old(Card_Old.RED, 7, Suit.Diamonds);
        Card_Old test3 = new Card_Old(Card_Old.RED, 8, Suit.Diamonds);

        ArrayList<Card_Old> cards = new ArrayList<Card_Old>();

        cards.add(new Card_Old(Card_Old.RED, 2, Suit.Clubs));
        cards.add(test);
        cards.add(new Card_Old(Card_Old.RED, 3, Suit.Hearts));
        cards.add(test2);
        cards.add(test3);
        cards.add(new Card_Old(Card_Old.RED, 4, Suit.Spades));


        for (Card_Old c : cards) {
            tableau.addCardToTop(c);
        }

        tableau.remove(test);
        tableau.remove(test2);
        tableau.remove(test3);

        cards.remove(test);
        cards.remove(test2);
        cards.remove(test3);

        Card_Old[] expected = cards.toArray(new Card_Old[cards.size()]);
        Card_Old[] actual = CardStack.stackToArray(tableau.getHead());

        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
        assertArrayEquals(actual, expected);
    }

    @Test
    public void CardStack_Pickup_1() {

        Tableau tableau = new Tableau(0, 0, 10,10);

        ArrayList<Card_Old> cards = new ArrayList<Card_Old>();
        Card_Old test = new Card_Old(Card_Old.RED, 1, Suit.Diamonds);
        cards.add(test);
        cards.add(new Card_Old(Card_Old.RED, 2, Suit.Clubs));
        cards.add(new Card_Old(Card_Old.RED, 3, Suit.Hearts));
        cards.add(new Card_Old(Card_Old.RED, 4, Suit.Spades));


        for (Card_Old c : cards) {
            tableau.addCardToTop(c);
        }

        Card_Old recovered = tableau.pickup((int)test.getX(), (int)test.getY());
        cards.remove(test);

        tableau.remove(test);

        Card_Old[] expected = cards.toArray(new Card_Old[cards.size()]);
        Card_Old[] actual = CardStack.stackToArray(tableau.getHead());

        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
        assertArrayEquals(actual, expected);
        assertEquals(recovered, test);
    }

    @Test
    public void CardStack_Pickup_2() {
        Tableau tableau = new Tableau(0, 0, 10,10);

        Card_Old test = new Card_Old(Card_Old.RED, 1, Suit.Diamonds);
        Card_Old test2 = new Card_Old(Card_Old.RED, 7, Suit.Diamonds);
        Card_Old test3 = new Card_Old(Card_Old.RED, 8, Suit.Diamonds);

        ArrayList<Card_Old> cards = new ArrayList<Card_Old>();

        cards.add(new Card_Old(Card_Old.RED, 2, Suit.Clubs));
        cards.add(test);
        cards.add(new Card_Old(Card_Old.RED, 3, Suit.Hearts));
        cards.add(test2);
        cards.add(test3);
        cards.add(new Card_Old(Card_Old.RED, 4, Suit.Spades));


        for (Card_Old c : cards) {
            tableau.addCardToTop(c);
        }

        Card_Old recovered_1 = tableau.pickup((int)test.getX(), (int)test.getY());
        tableau.remove(recovered_1);
        Card_Old recovered_2 = tableau.pickup((int)test2.getX(), (int)test2.getY());
        tableau.remove(recovered_2);
        Card_Old recovered_3 = tableau.pickup((int)test3.getX(), (int)test3.getY());
        tableau.remove(recovered_3);


        cards.remove(test);
        cards.remove(test2);
        cards.remove(test3);

        Card_Old[] expected = cards.toArray(new Card_Old[cards.size()]);
        Card_Old[] actual = CardStack.stackToArray(tableau.getHead());

        assertEquals(CardStack.countStack(tableau.getHead()),cards.size());
        assertArrayEquals(actual, expected);

        assertEquals(recovered_1, test);
        assertEquals(recovered_2, test2);
        assertEquals(recovered_3, test3);
    }
}