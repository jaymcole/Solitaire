package jasoncole.solitaire;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * Created by Jason Cole on 7/13/2018.
 */

public class Card {
    public String debug_name = "unnamed";

    public static final int BLACK = 0;
    public static final int RED = 1;

    private static Bitmap front, back;

    private Suit suit;
    private int color;
    private int number;

    private float x, y;
    public static float width, height;

    private int offsetX, offsetY;
    private Card parent, next;

    private boolean revealed;
    private boolean placeHolder;

    public static void initCards(Context context) {
        width = Resources.getSystem().getDisplayMetrics().widthPixels / 8;
        height = Resources.getSystem().getDisplayMetrics().heightPixels / 4;

        front = BitmapFactory.decodeResource(context.getResources(), R.drawable.card_front);
        float ratio = width / front.getWidth();
        width = front.getWidth() * ratio;
        height = front.getHeight() * ratio;

        front = Bitmap.createScaledBitmap(front, (int)(width), (int)(height), false);


        back = BitmapFactory.decodeResource(context.getResources(), R.drawable.card_back);
        back = Bitmap.createScaledBitmap(back, (int)(width), (int)(height), false);
    }


    public Card(int color, int number, Suit suit) {
        this.color = color;
        this.number = number;
        this.suit = suit;
        revealed = false;
        offsetX = 0;
        offsetY = 0;
        x = 0;
        y = 0;
        placeHolder = false;
    }

    /**
     * Creates a palceholder at (x, y)
     * @param x
     * @param y
     */
    public Card (int x, int y, int ox, int oy) {
        this.color = Card.BLACK;
        this.number = -1;
        this.suit = Suit.Clubs;

        placeHolder = true;
        this.x = x;
        this.y = y;
        offsetX = ox;
        offsetY = oy;
    }




    /**
     * Sets the front of the card face to resource
     * Default = (R.drawable.cardFront)
     * @param context
     * @param resource
     */
    public static void setCardFront (Context context, int resource) {
        front = BitmapFactory.decodeResource(context.getResources(), resource);
    }

    /**
     * Sets the back of the card face to resource
     * Default = (R.drawable.cardBack)
     * @param context
     * @param resource
     */
    public static void setCardBack (Context context, int resource) {
        back = BitmapFactory.decodeResource(context.getResources(), resource);
    }


    private static char getCharacterRepresentation (int number) {
        switch (number) {
            case 1:
                return 'A';
            case 11:
                return 'J';
            case 12:
                return 'Q';
            case 13:
                return 'K';
            default:
        return (char) number;
    }
}

    public void render(Canvas canvas, Paint paint) {
        if (placeHolder) {
            canvas.drawLine((int)x, (int)y, (int)x+width, (int)y+height, paint);
            canvas.drawLine((int)x + width, (int)y, (int)x, (int)y+height, paint);
        } else {
            canvas.drawBitmap(
                    back,
                    x,
                    y,
                    paint);
        }

        if (next != null) {
            next.render(canvas, paint);
        }
    }


    public void update() {
        updatePosition();

        if (next != null) {
            next.update();
        }
    }

    public void updatePosition() {
        if (parent != null) {
            offsetX = parent.offsetX;
            offsetY = parent.offsetY;

            x = parent.getX() + offsetX;
            y = parent.getY() + offsetY;
        }
    }

    public void addCard(Card card) {
        if (next == null) {
            next = card;
            card.setParent(this);
            updatePosition();
        }else {
            next.addCard(card);
        }
    }

    public void setParent(Card card) {
        this.parent = card;
        card.setOffsets(offsetX, offsetY);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setOffsets(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Suit getSuit() {
        return suit;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

}
