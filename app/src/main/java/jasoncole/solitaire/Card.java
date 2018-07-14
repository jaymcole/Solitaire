package jasoncole.solitaire;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

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

    private float offsetX, offsetY;
    private Card parent, next;
    private Card previousParent = null;

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
        this.next = null;
        revealed = false;
        offsetX = 0;
        offsetY = 0;
        x = 0;
        y = 0;
        placeHolder = false;
        debug_name = getColorName() + " " + getValue() + " of " + getSuit();
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
        this.next = null;
        placeHolder = true;
        this.x = x;
        this.y = y;
        offsetX = ox;
        offsetY = oy;
        debug_name = "placeholder";
    }

    /**
     *
     * @param x
     * @param y
     * @return true if the point x and y are within this cards bounds.
     */
    public boolean inBounds (int x, int y) {
        if (x < this.x || x > this.x + width) {
            return false;
        }
        if (y < this.y || y > this.y + height) {
            return false;
        }
        return true;
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

    public int getValue() {
        return number;
    }

    public String getColorName() {
        if (color == Card.RED)
            return "Red";
        else
            return "Black";
    }

    public static char getCharacterRepresentation (int number) {
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

//    public void renderStack(Canvas canvas, Paint paint) {
//        render(canvas, paint);
//        if (next != null)
//            next.renderStack(canvas, paint);
//    }

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


    private void updatePosition() {
        if (parent != null) {
            offsetX = parent.getOffX();
            offsetY = parent.getOffY();

            x = parent.getX();
            y = parent.getY();
            if (!parent.placeHolder) {
                x += offsetX;
                y += offsetY;
            }

        }
    }

    /**
     *
     * @param x screen coordinate
     * @param y screen coordinate
     * @return the top most card this that point (x, y) falls on.
     *         null if point (x, y) is not on a card.
     *         Will NOT return a placeholder card.
     */
    public Card pickCard (int x, int y) {
        Card card = null;
        if (inBounds(x,y) && !this.placeHolder) {
            card = this;
        }


        if (next != null) {
            if (next.inBounds(x, y)) {
                card = next.pickCard(x, y);
            }
        }
        return card;
    }

    /**
     * Picks up a card from a stack.
     */
    public void pickupCard() {
        if (parent == null)
            return;
        previousParent = parent;
        parent.next = null;
        parent = null;
    }

    /**
     * Drops THIS onto card.
     * @param parentCard - the card this card should be dropped on.
     */
    public void dropOn(Card parentCard) {
        if (parentCard == null) {
            if (previousParent != null) {
                this.dropOn(previousParent);
            }
            return;
        }

        if (parentCard.getNext() == null) {
            parentCard.next = this;
            this.parent = parentCard;
            update();
        }else {
            this.dropOn(parentCard.getNext());
        }
    }

    public boolean addCard(Card card) {
        if (card == null)
            return false;

        if (next == null) {
            next = card;
            next.setParent(this);
            next.update();
        }else {
            next.addCard(card);
        }

        return true;
    }

    /**
     *
     * @return the number of cards in a stack
     */
    public int cardsInStack() {
        if (next != null)
            return next.cardsInStack() + 1;
        else
            return 0;
    }

//    private void setNext(Card card) {next = card;}
    public void setParent(Card card) {
        this.parent = card;
    }
    public Card getParent() {
        return parent;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getOffX() {return offsetX;}
    public float getOffY() {return offsetY;}
    public float getWidth() {return width;}
    public float getHeight() {return height;}
    public Suit getSuit() {
        return suit;
    }
    public void setOffsets(float x, float y) {
        this.offsetX = x;
        this.offsetY = y;
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }
    public Card getNext() {
        return next;
    }
}
