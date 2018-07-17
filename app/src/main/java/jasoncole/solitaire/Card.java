package jasoncole.solitaire;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import stacks.CardStack;


/**
 * Created by Jason Cole on 7/13/2018.
 */

public class Card {
    public String debug_name = "unnamed";
    private String name;

    private float targetX, targetY;
    private static float speed = 2500;
//    private float rotation = 0;

    public static final int BLACK = 0;
    public static final int RED = 1;

    private static Bitmap front, back;

    private Suit suit;
    private int color;
    private int number;

    private float x, y;
    public static float width = 5, height = 5;

    private float offsetX, offsetY;
    private Card parent, next;
    private CardStack cardStack;

    private boolean revealed;


    private static Paint background, border, textRed, textBlack;

    private static int fontOffset;

    public static void initCards(Context context) {
        width = Resources.getSystem().getDisplayMetrics().widthPixels / 8;
        height = Resources.getSystem().getDisplayMetrics().heightPixels / 4;

        front = BitmapFactory.decodeResource(context.getResources(), GameView.card_front_resource);
        float ratio = width / front.getWidth();
        width = front.getWidth() * ratio;
        height = front.getHeight() * ratio;
        front = Bitmap.createScaledBitmap(front, (int)(width), (int)(height), false);

        back = BitmapFactory.decodeResource(context.getResources(), GameView.card_back_resource);
        back = Bitmap.createScaledBitmap(back, (int)(width), (int)(height), false);

        background = new Paint();
        background.setColor(Color.WHITE);

        border = new Paint();
        border.setColor(Color.BLACK);
        border.setStyle(Paint.Style.STROKE);


        textBlack = new Paint();
        textBlack.setTextSize(60);

        textRed = new Paint(textBlack);
        textRed.setColor(Color.RED);

        fontOffset = (int)textBlack.getFontMetrics().top;

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
        debug_name = getColorName() + " " + getValue() + " of " + getSuit();
        name = suit.getCharacter() + "" + getCharacterRepresentation(number);
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



    public String getColorName() {
        if (color == Card.RED)
            return "Red";
        else
            return "Black";
    }

    public static String getCharacterRepresentation (int number) {
        switch (number) {
            case 1:
                return "A";
            case 11:
                return "J";
            case 12:
                return "Q";
            case 13:
                return "K";
            default:
                return number + "";
    }
}

//    public void setRot(float rot) {
//        rotation = rot;
//    }

    public void renderStack(Canvas canvas, Paint paint) {
        render(canvas, paint);
        if (next != null)
            next.renderStack(canvas, paint);
    }

    public void render(Canvas canvas, Paint paint) {
//        canvas.rotate(rotation);
            if (revealed) {


                canvas.drawRect(x, y, x + width, y + height, background);
                canvas.drawBitmap(
                        front,
                        x,
                        y,
                        paint);
                canvas.drawRect(x, y, x + width, y + height, border);

                canvas.drawRect(x + 2, y + 2, x + width -1, y + height - 1, border);
                if (color == Card.RED) {
                    canvas.drawText(name, x + 10, y - fontOffset, textRed);
                } else {
                    canvas.drawText(name, x + 10, y - fontOffset, textBlack);
                }
            } else {
                canvas.drawBitmap(
                        back,
                        x,
                        y,
                        paint);
                canvas.drawRect(x, y, x + width, y + height, border);
            }
//        canvas.rotate(-rotation);
    }

    public void update(float deltaTime) {
        updatePosition(deltaTime);
        if (next != null) {
            next.update(deltaTime);
        }
    }

    private void move (float deltaTime) {
        if ((int)targetX != (int)x || (int)targetY != (int)y) {
            float deltaY = targetY - y;
            float deltaX = targetX - x;
            float distance = (float)Math.sqrt((deltaX*deltaX) + (deltaY*deltaY));
            deltaX /= distance;
            deltaY /= distance;
            x  += Math.min(Math.abs(targetX - x), speed * deltaX * deltaTime);
            y += Math.min(Math.abs(targetY - y), speed * deltaY * deltaTime);
            GameView.updateList.add(this);
        }
    }

    private float xSpeed, ySpeed;
    private void updatePosition(float deltaTime) {
        if (parent != null) {
            if (!revealed || !parent.isRevealed()) {
                targetX = parent.getX() + (float)(offsetX * 0.5);
                targetY = parent.getY() + (float)(offsetY * 0.5);
            } else {
                targetX = parent.getX() + offsetX;
                targetY = parent.getY() + offsetY;
            }
            move(deltaTime);
        } else if (cardStack != null){
            targetX = cardStack.getX();
            targetY = cardStack.getY();
            move(deltaTime);

        }
    }

    public void poke() {
        if (!isRevealed()) {
            if (this.next == null) {
                this.revealed = true;
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        Card card = (Card)other;
        return this.number == card.number
                && this.suit == card.suit
                && this.color == card.color;
    }

    public void setNext(Card card) {
        if (card != null)
            card.setParent(this);
        next = card;
    }

    public Card getNext() {
        return next;
    }

    public boolean hasNext() {
        return next != null;
    }

    public int getValue() {
        return number;
    }
    public String getName() {return name;}
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
    public float getWidth() {return width;}
    public float getHeight() {return height;}
    public Suit getSuit() {
        return suit;
    }
    public void setOffsets(float x, float y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    public void setTarget (int x, int y) {
        this.targetX = x;
        this.targetY = y;
        scheduleUpdate();
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        scheduleUpdate();
    }
    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }
    public void setStack(CardStack stack) {this.cardStack = stack;}
    public CardStack getStack() {return cardStack;}
    public boolean isRevealed() {return revealed;}

    public void scheduleUpdate() {
        if (!GameView.updateList.contains(this))
            GameView.updateList.add(this);
    }
}
