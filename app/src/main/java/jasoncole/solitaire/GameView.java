package jasoncole.solitaire;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static jasoncole.solitaire.Card.height;
import static jasoncole.solitaire.Card.width;

/**
 * Created by Jason Cole on 7/13/2018.
 */

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing;
    private Thread gameThread = null;

    //adding the player to this class
    private Card player;

    //These objects will be used for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private Card[] bottomRow;
    private Card[] complete;
    private Deck deck;
    private Card hand;



    public GameView(Context context) {
        super(context);

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        Card.initCards(context);

        int sideBuffer = (int)(height/20);

        bottomRow = new Card[7];
        for(int i = 0; i < 7; i++) {
            bottomRow[i] = new Card((int)(i * (width + (height / 10.0))) + sideBuffer, screenHeight/2, 0, (int)(height / 10.0));
            bottomRow[i].debug_name = "" + i + " of " + bottomRow[i].getSuit().name();

            for(int j = 0; j < i+1; j++) {
                Card card = new Card(Card.RED, j+i, Suit.Diamonds);
                bottomRow[i].addCard(card);

                if (j == i)
                    card.setRevealed(true);
            }
            bottomRow[i].update();

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
        //updating player position
//        player.update();
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
                c.render(canvas, paint);
            }

            for(Card c : complete) {
                c.render(canvas, paint);
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
}