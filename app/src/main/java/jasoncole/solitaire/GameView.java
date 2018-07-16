package jasoncole.solitaire;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import stacks.CardStack;
import stacks.Foundation;
import stacks.Stock;
import stacks.Tableau;
import stacks.Waste;

import static jasoncole.solitaire.Card.height;


/**
 * Created by Jason Cole on 7/13/2018.
 */

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing;
    private Thread gameThread = null;

    private Paint paint;
    private Paint fontPaint;

    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private CardStack[] tableaus;
    private CardStack[] foundations;
    private CardStack stock;
    private CardStack waste;
    private Deck deck;
    private Card hand;


    private Paint tableauDebug, stockDebug, foundationDebug, wasteDebug;


    private static Random random;

    private static ArrayList<Rect> bounds;

    private Rect stockArea, wasteArea, foundationArea, tableauArea;

    public GameView(Context context) {
        super(context);
        random = new Random();
        this.setFocusable(true);

        fontPaint = new Paint();
        fontPaint.setTextSize(50);
        fontPaint.setColor(Color.RED);
        fontPaint.setStrokeWidth(5);
        fontPaint.setStyle(Paint.Style.STROKE);


        tableauDebug = new Paint(fontPaint);
        tableauDebug.setColor(Color.GREEN);
//        tableauDebug.setColor(Color.TRANSPARENT);

        stockDebug = new Paint(fontPaint);
        stockDebug.setColor(Color.CYAN);
//        stockDebug.setColor(Color.TRANSPARENT);

        foundationDebug = new Paint(fontPaint);
        foundationDebug.setColor(Color.RED);
//        foundationDebug.setColor(Color.TRANSPARENT);

        wasteDebug = new Paint(fontPaint);
        wasteDebug.setColor(Color.GREEN);
//        wasteDebug.setColor(Color.TRANSPARENT);



        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        Card.initCards(context);
        int xBuffer = (int)((screenWidth - (Card.width * 7.0)) / 7.0);
        int yBuffer = 20;
        LinkedList<Card> cards = buildDeck();



        waste = new Waste((int)((xBuffer / 2) + Card.width + xBuffer), (int)yBuffer, (int)(Card.width / 4), 0);
        stock = new Stock((xBuffer / 2), yBuffer, waste);

        int sideBuffer = (int)(height/20);
        foundations = new Foundation[4];
        for(int i = 0; i < Suit.values().length; i++) {
            foundations[i] = new Foundation((xBuffer / 2) + (int)( 3 * ((int)Card.width + xBuffer) + (i * (Card.width + xBuffer)) ), yBuffer, Suit.values()[i]);
        }

        tableaus = new Tableau[7];
        for(int i = 0; i < tableaus.length; i++) {
            tableaus[i] = new Tableau((xBuffer / 2) + i * ((int)Card.width + xBuffer), (int)(stock.getY() + Card.height + yBuffer), 0,  (int)(Card.width / 4));
            for(int j = 0; j <= i; j++) {
                Card card = cards.get(random.nextInt(cards.size()));
                cards.remove(card);
                tableaus[i].addCardToTop(card);
                if (j == i)
                    card.setRevealed(true);
                card.update();
            }
        }

        for(Card c : cards) {
            stock.addCardToTop(c);
        }


//        bounds = new ArrayList<Rect>();
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


        surfaceHolder = getHolder();

        paint = new Paint();
    }

    private LinkedList<Card> buildDeck() {
        LinkedList<Card> cards = new LinkedList<Card>();
        for(Suit s : Suit.values()) {
            for (int i = 1; i <= 13; i++) {
                cards.add(new Card(s.getColor(), i, s));
            }
        }
        return cards;
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
//        for(CardStack tableau : tableaus) {
//            Tableau t = (Tableau)tableau;
//            for (CardStack foundation : foundations) {
//                Foundation f = (Foundation)foundation;
//                Card card = t.getTail();
//                if (f.drop(card)) {
//                    t.remove(card);
//                    card.setRevealed(true);
//                    f.addCardToTop(card);
//                    return;
//                }
//            }
//        }
    }

    public static boolean gameover = false;
    private void draw() {
        //checking if surface is valid
        if (surfaceHolder.getSurface().isValid()) {
            //locking the canvas
            canvas = surfaceHolder.lockCanvas();
            //drawing a background color for canvas
            if (!gameover)
                canvas.drawColor(Color.BLUE);
            //Drawing the player



            for(CardStack t : tableaus) {
                t.render(canvas, tableauDebug);
            }

            for(CardStack f : foundations) {
                f.render(canvas, foundationDebug);
            }

            stock.render(canvas, stockDebug);
            waste.render(canvas, wasteDebug);

            if (selected != null) {
                selected.renderStack(canvas, paint);
            }
//            debug_render(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void debug_render(Canvas canvas) {
        canvas.drawRect(stockArea, stockDebug);
        canvas.drawRect(wasteArea, wasteDebug);
        canvas.drawRect(foundationArea, foundationDebug);
        canvas.drawRect(tableauArea, tableauDebug);
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

    private static Card selected;
    private static CardStack previousStack;
    private static int selectedOffsetX, selectedOffsetY;

    private CardStack findStack(int x, int y) {
        CardStack stack = null;
        if (tableauArea.contains(x, y)) {
            for (CardStack cs : tableaus) {
                if (cs.contains(x, y)) {
                    stack = cs;
                    break;
                }
            }
        } else if (stockArea.contains(x, y)) {
            stack = stock;
        } else if (wasteArea.contains(x, y)) {
            stack = waste;
        } else if (foundationArea.contains(x, y)) {
            for (CardStack cs : foundations) {
                if (cs.contains(x, y)) {
                    stack = cs;
                    break;
                }
            }
        }
        return stack;
    }

    private void printStack(CardStack stack) {
        Card c = stack.getHead();

        StringBuilder sb = new StringBuilder();
        while(c != null) {
            sb.append(" -> " + c.toString());
            c = c.getNext();
        }
        Log.d("Stack",sb.toString());
    }

    @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    CardStack stack = findStack((int)motionEvent.getX(), (int)motionEvent.getY());
                    if (stack == null)
                        return true;
                    printStack(stack);
                    selected = stack.pickup((int)motionEvent.getX(), (int)motionEvent.getY());
                    if (selected != null) {
                        previousStack = selected.getStack();
                        previousStack.remove(selected);
                        selectedOffsetX = (int)((selected.getX()) - motionEvent.getX());
                        selectedOffsetY = (int)((selected.getY()) - motionEvent.getY());
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (selected != null) {
                        selected.setPosition( selectedOffsetX + (int)motionEvent.getX(), selectedOffsetY + (int)motionEvent.getY());
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (selected != null) {
                        CardStack dropStack =  findStack((int)motionEvent.getX(), (int)motionEvent.getY());
                        if (dropStack == null || !dropStack.drop(selected)) {
                            previousStack.addCardToTop(selected);
                        } else {
                            dropStack.addCardToTop(selected);
                        }
                    }
                    selected = null;
                    previousStack = null;
                    selectedOffsetX = 0;
                    selectedOffsetY = 0;
                    break;
            }
        return true;
    }
}