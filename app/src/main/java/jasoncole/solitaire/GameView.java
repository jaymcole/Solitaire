package jasoncole.solitaire;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.LinkedList;

import static jasoncole.solitaire.Card.height;
import static jasoncole.solitaire.Card.width;

/**
 * Created by Jason Cole on 7/13/2018.
 */

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing;
    private Thread gameThread = null;

    //These objects will be used for drawing
    private Paint paint;
    private Paint fontPaint;

    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private Card[] bottomRow;
    private Card[] complete;
    private Deck deck;
    private Card hand;

    private static LinkedList<Card> cards;

    public GameView(Context context) {
        super(context);
        this.setFocusable(true);

        fontPaint = new Paint();
        fontPaint.setTextSize(50);
        fontPaint.setColor(Color.RED);
        fontPaint.setStrokeWidth(5);
        fontPaint.setStyle(Paint.Style.STROKE);

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        Card.initCards(context);
        cards = new LinkedList<Card>();
        int sideBuffer = (int)(height/20);

        bottomRow = new Card[8];
        for(int i = 0; i < bottomRow.length; i++) {
            bottomRow[i] = new Card((int)(i * (width + (height / 10.0))) + sideBuffer, screenHeight/3, 0, (int)(height / 5.0));
            Log.d("nothing", (int)(height / 10.0) + "");
            bottomRow[i].debug_name = "" + i + " of " + bottomRow[i].getSuit().name();

            for(int j = 0; j < i+1; j++) {
                Card card = new Card(Card.RED, j+i, Suit.Diamonds);
                bottomRow[i].addCard(card);
                cards.add(card);
                if (j == i)
                    card.setRevealed(true);
                card.update();
            }
            Log.d("Offset", bottomRow[0].debug_name + " " + bottomRow[0].getOffX() + bottomRow[0].getOffY());
            bottomRow[0].update();

        }



        complete = new Card[4];
        for(int i = 0; i < 4; i++) {
            complete[i] = new Card((int)((screenWidth / 2) + (i * (Card.width))), sideBuffer, 0, 0);
        }

        deck = new Deck(sideBuffer, sideBuffer, 0, 0);

        //initializing drawing objects
        surfaceHolder = getHolder();

        paint = new Paint();
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
//        for(Card c : bottomRow) {
//            c.update();
//        }
    }

    private void draw() {
        //checking if surface is valid
        if (surfaceHolder.getSurface().isValid()) {
            //locking the canvas
            canvas = surfaceHolder.lockCanvas();
            //drawing a background color for canvas
            canvas.drawColor(Color.BLUE);
            //Drawing the player



            for(Card c : bottomRow) {
                canvas.drawText("" + c.cardsInStack(), c.getX(), c.getY() + c.getHeight() + 50, fontPaint);
                c.renderStack(canvas, fontPaint);
            }

            for(Card c : complete) {
                c.renderStack(canvas, paint);
                canvas.drawText("" + c.cardsInStack(), c.getX(), c.getY() + c.getHeight() + 50, fontPaint);
            }

            if (selected != null) {
                selected.renderStack(canvas, paint);
            }

            deck.render(canvas, paint);

            paint.setColor(Color.CYAN);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {



        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private static Card selected, selectedParent;
    private static int selectedOffsetX, selectedOffsetY;

    private Card findCard(int x, int y) {
        Card card = findCardAll(x, y);
        if (card == null || card.isPlaceHolder()) {
            return null;
        } else if(!card.isRevealed()) {
            return null;
        }
        return card;
    }

    /**
     * Finds the best match card INCLUDE placeholders. Use findCard() to get playable cards.
     * @param x screen coordinate
     * @param y screen coordinate
     * @return The top-most card at screen coordinates (x, y).
     */
    private Card findCardAll(int x, int y) {
        Card card = null;

        // Check the bottomRow Cards
        if (y >= bottomRow[0].getY()) {
            for(Card c : bottomRow) {
                if (x < c.getX() + c.getWidth()) {
                    card = checkStack(c, x, y);
                    break;
                }
            }
        } else {
            // Check the 'complete' stacks
            if (x < complete[0].getX()) {
                for(Card c : complete) {
                    if (x < c.getX() + c.getWidth()) {
                        card = checkStack(c, x, y);
                        break;
                    }
                }
            } else { // check the deck
                card = deck.getHand();
            }
        }
        return card;
    }

    private Card checkStack(Card topCard, int x, int y) {
        Card c = topCard;
        Card bestMatch = null;
        while (c != null) {
            if (c.inBounds(x, y)) {
                bestMatch = c;
            }
            c = c.getNext();
        }
        if (bestMatch != null) {
            bestMatch.poke();
        }
        return bestMatch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                selected = findCard((int)motionEvent.getX(), (int)motionEvent.getY());
                if (selected != null) {
                    selectedOffsetX = (int)((selected.getX()) - motionEvent.getX());
                    selectedOffsetY = (int)((selected.getY()) - motionEvent.getY());
                    selectedParent = selected.getParent();

                    selectedParent.setNext(null);
                    selected.setParent(null);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (selected != null) {
                    selected.setPosition( selectedOffsetX + (int)motionEvent.getX(), selectedOffsetY + (int)motionEvent.getY());
                    selected.update();
                }
//                for(Card c : cards) {
//                    if (c.inBounds((int)motionEvent.getX(), (int)motionEvent.getY())) {
//                        c.setSelected(true);
//                    } else {
//                        c.setSelected(false);
//                    }
//                }
                break;

            case MotionEvent.ACTION_UP:
                if (selected != null) {
                    Card destination = findCardAll((int)motionEvent.getX(), (int)motionEvent.getY());
                    if (destination != null) {
                        destination.addCard(selected);
                    } else {
                        selectedParent.addCard(selected);
                    }
                    selected = null;
                    selectedParent = null;
                    selectedOffsetX = 0;
                    selectedOffsetY = 0;
                }
                break;
        }
        return true;
    }
}