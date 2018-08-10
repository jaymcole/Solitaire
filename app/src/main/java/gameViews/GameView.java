package gameViews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import jasoncole.solitaire.Card;
import jasoncole.solitaire.R;
import jasoncole.solitaire.Suit;
import stacks.CardStack;
import stacks.Hand;

/**
 * Created by Jason Cole on 7/13/2018.
 */

public abstract class GameView extends SurfaceView implements Runnable {

    /**
     * Game states available for use by all solitaire games.
     */
    protected static final int GAME_STATE_PAUSED = -1;
    protected static final int GAME_STATE_DEALING = 0;
    protected static final int GAME_STATE_GAME_PLAYING = 1;
    protected static final int GAME_STATE_GAME_OVER = 2;
    protected static final int GAME_STATE_SOLVE = 3;
    protected static final int GAME_STATE_NEW_GAME = 4;
    protected static int GAME_STATE = GAME_STATE_GAME_PLAYING;

    /**
     * The delay between computer card moves.
     *  Example: At the beginning of a new game, cards will be dealt ever DEALER_DELAY seconds.
     */
    protected static final float DEALER_DELAY = 0.05f;
    protected static final float TOSSER_DELAY = 1.0f;

    /**
     * A float to remeember when last a dealer action occurred.
     */
    protected static float dealerTimer;


    private static boolean gameOver;

    private static int score = 0;

    protected static Hand hand;

    /**
     * List of all cards not currently in play.
     */
    protected LinkedList<Card> cards;

    private static ArrayList<Card> updateList = new ArrayList<Card>();

    protected static LinkedList<CardStack> stacks;

    volatile boolean playing;
    private Thread gameThread = null;


    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    /**
     * Blocks user input if true.
     */
    protected static boolean blockUserInput = false;


    public static  int placeholder_resource;
    public static int background_resource;
    public static int card_back_resource;
    public static int card_front_resource;

    protected Paint fontPaint;
    protected Paint statsPaint;
    protected Paint statsBoxPaint;
    protected Paint backgroundPaint;

    public static int screenWidth;
    public static int screenHeight;
    protected int xBuffer;
    protected int yBuffer;

    protected Bitmap background;

    protected static Random random;

    public GameView(Context context) {
        super(context);
        this.setFocusable(true);
        surfaceHolder = getHolder();
        random = new Random();
        initResources(context);
        initStacks();

        hand = new Hand(0,0);
        stacks.add(hand);

        newGame();
    }

    private void newGame() {
        setGameState(GAME_STATE_NEW_GAME);
        cards = buildDeck();
        setGameState(GAME_STATE_DEALING);
    }


    private void initResources(Context context) {
        placeholder_resource = R.drawable.placeholder;
        background_resource = R.drawable.felt;
        card_back_resource = R.drawable.fb_2;
        card_front_resource = R.drawable.pills;

        Card.initCards(context);

        fontPaint = new Paint();
        fontPaint.setTextSize(50);
        fontPaint.setColor(Color.RED);
        fontPaint.setStrokeWidth(5);
        fontPaint.setStyle(Paint.Style.STROKE);

        statsPaint = new Paint();
        statsPaint.setColor(Color.BLACK);
        statsPaint.setTextSize(30);

        statsBoxPaint = new Paint();
        statsBoxPaint.setColor(Color.argb(100,100,100,100));

        updateList = new ArrayList<Card>();

        backgroundPaint = new Paint();

        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        xBuffer = (int)((screenWidth - (Card.width * 7.0)) / 7.0);
        yBuffer = 20;
        background = BitmapFactory.decodeResource(context.getResources(), background_resource);
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, true);

        CardStack.init(context);


    }

    private void letCardsLoose(float deltaTime) {
        dealerTimer += deltaTime;
        if (dealerTimer >= TOSSER_DELAY) {
            dealerTimer -= TOSSER_DELAY;
            Log.d("Dealer", "Releasing Card!");
            if (letCardLoose(deltaTime)) {
                Log.d("done", "all done");
            }
        }
    }

    protected abstract boolean letCardLoose(float deltaTime);

    /**
     * Deals the cards out.
     * @param deltaTime
     */
    protected void dealCards(float deltaTime) {
        dealerTimer += deltaTime;
        if (dealerTimer >= DEALER_DELAY) {
            dealerTimer -= DEALER_DELAY;
            Log.d("Dealer", "Dealing Card");
            if (dealCard()) {
                setGameState(GAME_STATE_GAME_PLAYING);
            }
        }
    }

    /**
     * Deals a single card.
     * @return true if done dealing.
     */
    protected abstract boolean dealCard ();

    /**
     * Build all of the stacks this game type needs.
     */
    protected abstract void initStacks();

    /**
     * Calculates the score from moving a card.
     *
     * @param origin - The CardStack card started from.
     * @param arrival - The CardStack card is moving to.
     * @param card - The card that's moving.
     * @return The score from moving card from origin to arrival.
     */
    protected abstract int getMoveScore(CardStack origin, CardStack arrival, Card card);


    /**
     * Checks if the game has been won or lost.
     * @return - True if the game is over.
     */
    protected abstract boolean checkGameOver();

    /**
     * Builds a deck of cards including Bitmap generation for each card.
     * @return A new full deck of cards
     *      Note: Does not include Jokers
     */
    private static LinkedList<Card> buildDeck() {
        LinkedList<Card> cards = new LinkedList<Card>();
        for(Suit s : Suit.values()) {
            for (int i = 1; i <= 13; i++) {
                Card card = new Card(s.getColor(), i, s);
                cards.add(card);
            }
        }
        return cards;
    }

    private static int frames = 0;
    private static float fpsTime = 0;

    @Override
    public void run() {
        Long time = System.currentTimeMillis();
        float delta;
        float fps;
        while (playing) {
            delta = (System.currentTimeMillis() - time) * 0.001f;
            fpsTime += delta;
            frames++;
            if (fpsTime > 1) {
                fpsTime -= 1;
                fps = frames;
                frames = 0;
            }
            time = System.currentTimeMillis();
            update(delta);
            draw();
            control();
        }
    }

    protected void setGameState(int newGameState) {
        if (GAME_STATE == newGameState)
            return;

        switch (GAME_STATE) {
            case GAME_STATE_DEALING:
                break;


            case GAME_STATE_GAME_PLAYING:

                break;


            case GAME_STATE_GAME_OVER:

                break;
        }

        // Do stuff upon moving TO a state.
        switch (newGameState) {
            case GAME_STATE_PAUSED:
                break;

            case GAME_STATE_NEW_GAME:
                gameOver = false;
                break;

            case GAME_STATE_DEALING:
                break;


            case GAME_STATE_GAME_PLAYING:
                break;


            case GAME_STATE_GAME_OVER:
                gameOver = true;
                break;


        }
        GAME_STATE = newGameState;
    }

    protected void update(float deltaTime) {
        switch (GAME_STATE) {
            case GAME_STATE_PAUSED:
                break;
            case GAME_STATE_DEALING:
                update_dealing(deltaTime);
                break;

            case GAME_STATE_GAME_PLAYING:
                update_playing(deltaTime);
                break;

            case GAME_STATE_SOLVE:
                break;

            case GAME_STATE_GAME_OVER:
                update_gameOver(deltaTime);
                break;
        }
    }

    /**
     * The default update method during the playing phase.
     * @param deltaTime - The time since the last frame was rendered.
     */
    protected void update_dealing(float deltaTime) {
        dealCards(deltaTime);
        updateCards(deltaTime);
    }

    /**
     * The default update method during the playing phase.
     * @param deltaTime - The time since the last frame was rendered.
     */
    protected void update_playing(float deltaTime) {
        updateCards(deltaTime);
    }



    /**
     * The default update method during the gameOver phase.
     * @param deltaTime - The time since the last frame was rendered.
     */
    protected void update_gameOver(float deltaTime){
        letCardsLoose(deltaTime);
        updateCards(deltaTime);
    }

    private Bitmap GameOverSpecialBitmap;

    public Bitmap takeScreenShot(View view) {
        // configuramos para que la view almacene la cache en una imagen
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        view.buildDrawingCache();

        if(view.getDrawingCache() == null) return null; // Verificamos antes de que no sea null

        // utilizamos esa cache, para crear el bitmap que tendra la imagen de la view actual
        Bitmap snapshot = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        return snapshot;
    }

    private Bitmap screenshot;
    private void draw() {
        //checking if surface is valid
        if (surfaceHolder.getSurface().isValid()) {
            //locking the canvas
            canvas = surfaceHolder.lockCanvas();
            if (!gameOver) {
                canvas.drawBitmap(background, 0, 0, backgroundPaint);
                for(CardStack stack : stacks) {
                    stack.render(canvas);
                    stack.renderStack(canvas);
                }
            } else {
                canvas.drawBitmap(screenshot, 0, 0, backgroundPaint);
            }

            for (Card card : updateList) {
                card.render(canvas);
            }

            drawGame(canvas);
            screenshot = takeScreenShot(this);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * Renders the scene to canvas.
     * @param canvas - The canvas to render to.
     */
    protected void drawGame(Canvas canvas) {
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


    /**
     * Searches for a card stack that player pressed on.
     * @param x - x screen coordinate
     * @param y - y screen coordinate
     * @return The stack containing screen coordinates (x, y).
     *          null if no stack contains (x, y).
     */
    private CardStack findStack(int x, int y) {
        for (CardStack stack : stacks) {
            if (stack.contains(x, y)) {
                return stack;
            }
        }
        return null;
    }

    protected void updateCards(float deltaTime) {
        for(int i = updateList.size()-1; i >= 0; i--) {
            updateList.get(i).update(deltaTime);
            if (updateList.get(i).isDoneUpdating())
                updateList.remove(i);
        }
    }

    public static void addScore(int score) {
        GameView.score += score;
    }


    /**
     *
     * @param card - The Card that needs updating
     */
    public static void scheduleUpdate (Card card) {
        if (!updateList.contains(card))
            updateList.add(card);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (blockUserInput)
            return false;

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                onTouchStart((int)motionEvent.getX(), (int)motionEvent.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                onMove((int)motionEvent.getX(), (int)motionEvent.getY());
                break;

            case MotionEvent.ACTION_UP:
                onTouchEnd((int)motionEvent.getX(), (int)motionEvent.getY());
                if (checkGameOver()) {
                    setGameState(GAME_STATE_GAME_OVER);

                }
                break;
        }
        return true;
    }

    /**
     * Default method for ACTION_DOWN events
     * @param x - x screen coordinate
     * @param y - y screen coordinate
     */
    protected void onTouchStart(int x, int y) {
        if (gameOver) {
            setGameState(GAME_STATE_NEW_GAME);
            return;
        }



        CardStack stack = findStack(x, y);
        if (stack == null)
            return;
        stack.pokeStack();
        Card selected = stack.pickup(x, y);
        if (selected != null) {
            hand.setChildOffsets(stack.getChildOffsetX(), stack.getChildOffsetY());
            hand.setPositionOffsets((int)((selected.getX()) - x), (int)((selected.getY()) - y));
            hand.setOffsetPosition(x, y);
            hand.setPreviousStack(selected.getStack());

            selected.getStack().giveCardsStartingAt(hand, selected);

        }

    }

    /**
     * Default method for ACTION_MOVE events
     * @param x - x screen coordinate
     * @param y - y screen coordinate
     */
    protected void onMove(int x, int y) {
        hand.setOffsetPosition(x, y);

    }

    /**
     * Default method for ACTION_UP events
     * @param x - x screen coordinate
     * @param y - y screen coordinate
     */
    protected void onTouchEnd(int x, int y) {
        hand.setOffsetPosition(x, y);

        CardStack stack = findStack(x, y);
        if (stack != null) {
            hand.dropStack(stack);
            stack.updateStack();
        } else {
            hand.returnCards();
        }
        hand.setPreviousStack(null);
    }


    public void onCardFlip(CardStack stack, Card card) {}

}