package jasoncole.solitaire;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
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

/**
 * Created by Jason Cole on 7/13/2018.
 */

public class GameView extends SurfaceView implements Runnable {

    private static final int GAME_STATE_PAUSED = -1;
    private static final int GAME_STATE_DEALING = 0;
    private static final int GAME_STATE_GAME_PLAYING = 1;
    private static final int GAME_STATE_GAME_OVER = 2;
    private static int GAME_STATE = GAME_STATE_GAME_PLAYING;

    private Bitmap background;


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


    private Paint tableauDebug, stockDebug, foundationDebug, wasteDebug, backgroundPaint;

    public static ArrayList<Card> updateList;

    private static Random random;

    private static ArrayList<Rect> bounds;

    private Rect stockArea, wasteArea, foundationArea, tableauArea;


    public static int placeholder_resource;
    public static int card_back_resource;
    public static int card_front_resource;
    public static int background_resource;

    public GameView(Context context) {
        super(context);
        random = new Random();
        this.setFocusable(true);


        placeholder_resource = R.drawable.meme_3;
        background_resource = R.drawable.meme_4;
        card_back_resource = R.drawable.meme_2;
        card_front_resource = R.drawable.meme_1;



        fontPaint = new Paint();
        fontPaint.setTextSize(50);
        fontPaint.setColor(Color.RED);
        fontPaint.setStrokeWidth(5);
        fontPaint.setStyle(Paint.Style.STROKE);

        updateList = new ArrayList<Card>();

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


        backgroundPaint = new Paint();




        Card.initCards(context);

        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        xBuffer = (int)((screenWidth - (Card.width * 7.0)) / 7.0);
        yBuffer = 20;
        background = BitmapFactory.decodeResource(context.getResources(), background_resource);
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false);

        CardStack.init(context);


        waste = new Waste((int)((xBuffer / 2) + Card.width + xBuffer), (int)yBuffer, (int)(Card.width / 4), 0);
        stock = new Stock((xBuffer / 2), yBuffer, waste);

        tableaus = new Tableau[7];
        for(int i = 0; i < tableaus.length; i++) {
            tableaus[i] = new Tableau((xBuffer / 2) + i * ((int)Card.width + xBuffer), (int)(stock.getY() + Card.height + yBuffer), 0,  (int)(Card.width / 4));
        }

        foundations = new Foundation[4];
        for(int i = 0; i < Suit.values().length; i++) {
            foundations[i] = new Foundation((xBuffer / 2) + (int)( 3 * ((int)Card.width + xBuffer) + (i * (Card.width + xBuffer)) ), yBuffer, Suit.values()[i]);
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


        surfaceHolder = getHolder();

        paint = new Paint();

        cards = buildDeck();
        setGameState(GAME_STATE_DEALING);
    }

    private LinkedList<Card> cards ;
    private int tableauCol, cycle;
    private float dealingTime;
    private static final float TIME_BETWEEN_CARDS = 0.1f;
    private static int dealingPhase;
    private static final int DEAL_DECK = -1, DEAL_STOCK = 0, DEAL_TABLE = 1;
    private void dealCards(float deltaTime) {
        dealingTime += deltaTime;
        Card card;
        while(dealingTime >= TIME_BETWEEN_CARDS) {
            dealingTime -= TIME_BETWEEN_CARDS;
            switch (dealingPhase) {
                case DEAL_DECK:
                    tableauCol = 0;
                    cycle = 0;
                    dealingTime = 0;
                    cards = buildDeck();
                    dealingPhase = DEAL_STOCK;
                case DEAL_STOCK:
                    while (!cards.isEmpty()) {
                        card = cards.get(random.nextInt(cards.size()));
                        cards.remove(card);
                        stock.addCardToTop(card);
                        card.scheduleUpdate();
//
//                        float maxRot = 3;
//                        card.setRot(random.nextInt((int)maxRot) - (maxRot*0.5f) + random.nextFloat());
                    }

                    dealingPhase = DEAL_TABLE;
                    break;

                case DEAL_TABLE:

                    if (tableauCol >= tableaus.length) {
                        cycle++;
                        tableauCol = cycle;

                    }
                    card = stock.getTail();
                    stock.remove(card);
                    tableaus[tableauCol].addCardToTop(card);

                    if (cycle == tableauCol)
                        tableaus[tableauCol].getTail().setRevealed(true);
                    card.scheduleUpdate();
                    tableauCol++;

                    if (cycle == tableaus.length - 1) {
                        dealingPhase = DEAL_DECK;
                        setGameState(GAME_STATE_GAME_PLAYING);
                    }
                    break;
            }
        }
    }

    private LinkedList<Card> buildDeck() {
        LinkedList<Card> cards = new LinkedList<Card>();
        for(Suit s : Suit.values()) {
            for (int i = 1; i <= 13; i++) {
                Card card = new Card(s.getColor(), i, s);
                card.setPosition(stock.getX(), stock.getY());
                cards.add(card);

            }
        }
        return cards;
    }

    @Override
    public void run() {
        Long time = System.currentTimeMillis();
        float delta;
        while (playing) {
            delta = (System.currentTimeMillis() - time) * 0.001f;
            time = System.currentTimeMillis();
            update(delta);
            draw();
            control();

        }
    }

    private static int xBuffer, yBuffer, screenWidth, screenHeight;
    private void setGameState(int newGameState) {
        if (GAME_STATE == newGameState)
            return;

        // DO stuff upon move FROM a state.
        switch (GAME_STATE) {
            case GAME_STATE_DEALING:
                ((Stock) stock).drawHand();
                break;


            case GAME_STATE_GAME_PLAYING:

                break;


            case GAME_STATE_GAME_OVER:

                break;
        }

        // Do stuff upon moving TO a state.
        switch (GAME_STATE) {
            case GAME_STATE_PAUSED:
                break;

            case GAME_STATE_DEALING:
                dealingPhase = DEAL_DECK;

                break;


            case GAME_STATE_GAME_PLAYING:

                break;


            case GAME_STATE_GAME_OVER:

                break;


        }
        GAME_STATE = newGameState;
    }


    private void update(float deltaTime) {
        switch (GAME_STATE) {
            case GAME_STATE_PAUSED:

                break;

            case GAME_STATE_DEALING:
                dealCards(deltaTime);
                for(int i = updateList.size()-1; i >= 0; i--) {
                    updateList.get(i).update(deltaTime);
                    updateList.remove(i);
                }
                break;


            case GAME_STATE_GAME_PLAYING:
                for(int i = updateList.size()-1; i >= 0; i--) {
                    updateList.get(i).update(deltaTime);
                    updateList.remove(i);
                }
                break;


            case GAME_STATE_GAME_OVER:

                break;


        }
    }

    public static boolean gameover = false;
    private void draw() {
        //checking if surface is valid
        if (surfaceHolder.getSurface().isValid()) {
            //locking the canvas
            canvas = surfaceHolder.lockCanvas();
            //drawing a background color for canvas
            if (!gameover)
                canvas.drawColor(Color.GREEN);

            canvas.drawBitmap(background, 0, 0, backgroundPaint);

            stock.render(canvas);
            waste.render(canvas);

            for(CardStack t : tableaus) {
                t.render(canvas);
            }

            for(CardStack f : foundations) {
                f.render(canvas);
            }

            stock.renderStack(canvas, stockDebug);
            waste.renderStack(canvas, wasteDebug);

            for(CardStack t : tableaus) {
                t.renderStack(canvas, tableauDebug);
            }

            for(CardStack f : foundations) {
                f.renderStack(canvas, foundationDebug);
            }

            if (selected != null) {
                selected.renderStack(canvas, paint);
            }

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