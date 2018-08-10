package gameViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.LinkedList;

import jasoncole.solitaire.Card;
import jasoncole.solitaire.Suit;
import stacks.CardStack;
import stacks.Foundation;
import stacks.Stock;
import stacks.Tableau;
import stacks.Waste;

/**
 * Created by Jason Cole on 8/5/2018.
 */

public class Klondike extends GameView {

    /**
     * Score values for specific card moves.
     */
    public static final int VALUE_FOR_WASTE_TO_TABLEAU = 5;
    public static final int VALUE_FOR_WASTE_TO_FOUNDATION = 10;
    public static final int VALUE_FOR_TABLEAU_TO_FOUNDATION = 10;
    public static final int VALUE_FOR_TURNING_OVER_TABLEAU = 5;
    public static final int VALUE_FOR_FOUNDATION_TO_TABLEAU = -15;
    public static final int VALUE_FOR_RECYCLE_WASTE = 0;


    private static final int DEAL_DECK = -1, DEAL_STOCK = 0, DEAL_TABLE = 1;


    public Klondike(Context context) {
        super(context);
        initPaint();
    }



    private Waste waste;
    private Stock stock;
    private Tableau[] tableaus;
    private Foundation[] foundations;

    private Rect stockArea;
    private Rect wasteArea;
    private Rect foundationArea;
    private Rect tableauArea;


    @Override
    protected void initStacks() {
        stacks = new LinkedList<CardStack>();

        waste = new Waste((int)((xBuffer / 2) + Card.width + xBuffer), (int)yBuffer, (int)(Card.width / 4), 0);
        stacks.add(waste);

        stock = new Stock((xBuffer / 2), yBuffer, waste);
        stacks.add(stock);


        tableaus = new Tableau[7];
        for(int i = 0; i < tableaus.length; i++) {
            tableaus[i] = new Tableau((xBuffer / 2) + i * ((int)Card.width + xBuffer),
                    (int)(stock.getY() + Card.height + yBuffer),
                    0,
                    (int)(Card.width / 4));
            stacks.add(tableaus[i]);
        }

        foundations = new Foundation[4];
        for(int i = 0; i < Suit.values().length; i++) {
            foundations[i] = new Foundation((xBuffer / 2) + (int)( 3 * ((int)Card.width + xBuffer) + (i * (Card.width + xBuffer)) ), yBuffer, Suit.values()[i]);
            stacks.add(foundations[i]);
        }

        stockArea = new Rect(
                0,
                0,
                (int)((xBuffer / 2) + Card.width),
                (int)Card.height + yBuffer); // STOCK

        wasteArea = new Rect(
                (int)(stock.getX() + Card.width + xBuffer),
                (int)0,
                (int)foundations[0].getX(),
                (int)Card.height + yBuffer); // WASTE

        foundationArea = new Rect(
                (int)(foundations[0].getX()),
                (int)0,
                (int)screenWidth,
                (int)Card.height + yBuffer); // Foundation

        tableauArea = new Rect(
                (int)0,
                (int)(stock.getY() + Card.height),
                (int)screenWidth-1,
                (int)(screenHeight-1) ); // Tableau
    }

    private int col = 0;
    private int row = 0;
    @Override
    protected boolean dealCard() {
        if (cards.isEmpty())
            return true;



        col %= foundations.length;
        Card card = cards.remove(random.nextInt(cards.size()));
        foundations[col].addCardToTop(card);
        col++;


//        if (row < tableaus.length) {
//            tableaus[col].addCardToTop(card);
//            col++;
//
//            if (row+1 == col)
//                card.setRevealed(true);
//
//
//            if (col >= tableaus.length) {
//                row++;
//                col = row;
//            }
//        } else {
//            stock.addCardToTop(card);
//        }
        return false;
    }

    private int foundation = 0;
    @Override
    protected boolean letCardLoose(float deltaTime) {
        foundation %= foundations.length;
        Card card = foundations[foundation].tail();

        if (card != null) {
            card.setGameOverPartyTime(true);
            foundations[foundation].remove(card);
            scheduleUpdate(card);
        } else if (foundation == foundations.length - 1)
            return true;
        foundation++;
        return false;
    }



    @Override
    protected int getMoveScore(CardStack origin, CardStack arrival, Card card) {
        return 0;
    }


    private Paint debugPaint;
    private Paint debugText;
    private void initPaint() {
        debugPaint = new Paint();
        debugPaint.setTextSize(30);
        debugPaint.setColor(Color.RED);
        debugPaint.setStyle(Paint.Style.STROKE);
        debugPaint.setStrokeWidth(5);

        debugText = new Paint();
        debugText.setStyle(Paint.Style.FILL);
        debugText.setTextSize(30);
        debugText.setColor(Color.GREEN);
    }


    @Override
    protected void drawGame(Canvas canvas) {

//        for (CardStack stack : stacks) {
//            canvas.drawRect(stack.getBounds(), debugPaint);
//            canvas.drawText("" + stack.cardsInStack(), stack.getX(), stack.getY() + Card.height, debugText);
//        }

    }

    @Override
    protected boolean checkGameOver() {
//        int cards = 0;
//        for (CardStack f : foundations) {
//            cards += f.cardsInStack();
//        }
//        return cards == 52;
        return true;
    }


    @Override
    public void onCardFlip(CardStack stack, Card card) {

    }
}
