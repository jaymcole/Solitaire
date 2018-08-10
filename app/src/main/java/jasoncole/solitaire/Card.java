package jasoncole.solitaire;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

import gameViews.GameView;
import stacks.CardStack;


/**
 * Created by Jason Cole on 7/13/2018.
 */

public class Card {

    public static final String TAG = "Card";

    private String name;

    public static final int BLACK = 0;
    public static final int RED = 1;

    private static Bitmap back;
    private Bitmap localFront;

    private Suit suit;
    private int color;
    private int number;

    private boolean gameOverPartyTime = false;
    private static final Random random = new Random();

    /**
     * initial gameover trajectory vectors
     */
    private float goY, goX;

    /**
     * The current x and y position of this card.
     */
    private float
            x,
            y;

    /**
     * The screen coordinates this card should be moving towards.
     */
    private float
            targetX,
            targetY;

    /**
     * The speed Cards should move at.
     */
    private static float speed = 2500;

    /**
     * The CardStack this card is a part of.
     */
    private CardStack cardStack;

    /**
     * Renders face up if revealed == true
     *          - face down if revealed == false.
     */
    private boolean revealed;

    /**
     * Various dimensions/settings shared among all cards.
     */
    public static float
            width,
            height,
            frontInset,
            backInset,
            cornerRoundness;

    /**
     * Various render settings shared among all cards.
     */
    private static Paint
            background,
            outerBorder,
            border,
            numberRed,
            symbolRed,
            numberBlack,
            symbolBlack;

    private static int fontOffset;
    private static int symbolOffset;
    private float numberLength;


    /**
     * Initials settings shared among all cards.
     * @param context
     */
    public static void initCards(Context context) {
        int fontSize = 45;
        numberBlack = new Paint();
        numberBlack.setTextSize(fontSize);
        symbolBlack = new Paint(numberBlack);
        symbolBlack.setTextSize(fontSize / 2);

        numberRed = new Paint(numberBlack);
        numberRed.setColor(Color.RED);
        symbolRed = new Paint(numberRed);
        symbolRed .setTextSize(fontSize / 2);

        background = new Paint();
        background.setColor(Color.WHITE);

        border = new Paint();
        border.setColor(Color.BLACK);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(3);

        outerBorder = new Paint(border);
        outerBorder.setColor(Color.argb(100,100,100,100));
        outerBorder.setStrokeWidth(2);

        fontOffset = (int) numberBlack.getFontMetrics().top;
        symbolOffset =  (int)(fontOffset * 1.5f);

        width = Resources.getSystem().getDisplayMetrics().widthPixels / 8;
        height = width * 1.4f;

        frontInset = width * 0.2f;
        cornerRoundness = width * 0.03f;
        backInset = frontInset * 0.5f;

//        front = BitmapFactory.decodeResource(context.getResources(), Klondike_Old.card_front_resource);
//        front = Bitmap.createScaledBitmap(front, (int)(width - (frontInset *2)), (int)(height - (frontInset *2)), true);

        back = BitmapFactory.decodeResource(context.getResources(), GameView.card_back_resource);

        if (back == null)
            Log.d("ass", "Back is nmull.");

        back = Bitmap.createScaledBitmap(back, (int)(width - (backInset*2)), (int)(height - (backInset *2)), true);

        Card.setCardBack(context, GameView.card_back_resource);


    }

    public Card(int color, int number, Suit suit) {
        numberLength = (Math.abs(numberBlack.measureText("" + number) - numberBlack.measureText(getCharacterRepresentation(number))) * 0.5f);
        this.color = color;
        this.number = number;
        this.suit = suit;
        revealed = false;
        name = suit.getCharacter() + "" + getCharacterRepresentation(number);
        localFront = generateFront();
        setPosition((int)-width, (int)-height);
        goX = (random.nextInt(10) * 0.01f + 0.1f) * speed * 0.5f;
        goY = random.nextFloat() * speed * -0.01f + 0.1f;
        if (random.nextBoolean())
            goX *= -1;
//        isMoving = false;
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
//        front = BitmapFactory.decodeResource(context.getResources(), resource);
    }

    /**
     * Sets the back of the card face to resource
     * Default = (R.drawable.cardBack)
     * @param context
     * @param resource
     */
    public static void setCardBack (Context context, int resource) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap((int)width, (int)height, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);


        canvas.drawRoundRect(0, 0, width, height, cornerRoundness, cornerRoundness, background);
        canvas.drawRoundRect(0, 0, width, height, cornerRoundness, cornerRoundness, outerBorder);

        canvas.drawBitmap(
                back,
                backInset,
                backInset,
                background);
        canvas.drawRect(backInset, backInset, (width-(backInset)), (height-(backInset)), border);
        back = bmp;
    }

    /**
     * Generates a card face for this specific card;
     * @return a bitmap unique for this card.
     */
    private Bitmap generateFront() {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap((int)width, (int)height, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);



        canvas.drawRoundRect(x, y, x + width, y + height, cornerRoundness, cornerRoundness, background);
        canvas.drawRoundRect(x, y, x + width, y + height, cornerRoundness, cornerRoundness, outerBorder);
//        canvas.drawBitmap(
//                localFront,
//                x + frontInset,
//                y + frontInset,
//                background);

        canvas.drawRect(x + frontInset, y + frontInset, x + (width-(frontInset)), y + (height-(frontInset)), border);
        if (color == Card.RED) {
            paintCharacters(canvas, numberRed, symbolRed);
        } else {
            paintCharacters(canvas, numberBlack, symbolBlack);
        }
        return bmp;
    }

    /**
     *  Paints the card value and suit onto canvas
     * @param canvas
     * @param numberPaint
     * @param symbolPaint
     */
    private void paintCharacters(Canvas canvas, Paint numberPaint, Paint symbolPaint) {
        canvas.drawText(Card.getCharacterRepresentation(number), x + 10, y - fontOffset, numberPaint);
        canvas.drawText(suit.getCharacter(), x + numberLength + 10, y - (symbolOffset), symbolPaint);

        canvas.translate(x + width , y + height);
        canvas.rotate(180);
        canvas.drawText(Card.getCharacterRepresentation(number), x + 10, y - fontOffset, numberPaint);
        canvas.drawText(suit.getCharacter(), x + numberLength + 10, y - (symbolOffset), symbolPaint);

    }

    /**
     *
     * @param number
     * @return the playing representation for number.
     *      Example: 13 returns K since king has a value of 13.
     */
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

    //TODO: if background renders correctly, refactor the name to "cardPaint"

    /**
     * Renders only this card to canvas.
     *      - canvas must not be null.
     * @param canvas - The canvas to render this card to
     */
    public void render(Canvas canvas) {
        if (revealed) {
            canvas.drawBitmap(
                    localFront,
                    x,
                    y,
                    background);
        } else {
            canvas.drawBitmap(
                    back,
                    x,
                    y,
                    background);
        }
    }

    /**
     * Updates this card.
     *      deltaTime is the time since the last frame.
     * @param deltaTime : time since the last frame.
     */
    public void update(float deltaTime) {
        if (gameOverPartyTime) {
            gameOverUpdate(deltaTime);
        }else{
            scheduleUpdate();
            move(deltaTime);
        }
    }

    private void gameOverUpdate(float deltaTime) {
        x += goX * deltaTime;
        goY += (9.8f * deltaTime);
        y += goY;

        if (x > GameView.screenWidth - Card.width) {
            goX *= -1;
            x = GameView.screenWidth - Card.width;
//            goY *= 0.85f;
        }

        if (y > GameView.screenHeight - Card.height) {
            goY *= -1;
            y = GameView.screenHeight - Card.height;
            goY *= 0.85f;
        }

        if (x < -Card.width) {
//            this.setPosition(cardStack.getX(), cardStack.getY());
            updateComplete();
        }

    }

    private boolean doneUpdating;
    public boolean isDoneUpdating() {return doneUpdating;}
    private void updateComplete() {
        doneUpdating = true;
    }

    /**
     * Moves this card toward it's target destination.
     *      - Pixels moved is dependant on deltaTime.
     * @param deltaTime : time since the last frame.
     */
    private void move (float deltaTime) {
        if ((int)targetX != (int)x || (int)targetY != (int)y) {
            float deltaY = targetY - y;
            float deltaX = targetX - x;
            float distance = (float)Math.sqrt((deltaX*deltaX) + (deltaY*deltaY));
            deltaX /= distance;
            deltaY /= distance;

            if (x < targetX)
                x += Math.min(targetX - x, speed * deltaX * deltaTime);
            else
                x += Math.max(targetX - x, speed * deltaX * deltaTime);


            if (y < targetY)
                y += Math.min(targetY - y, speed * deltaY * deltaTime);
            else
                y += Math.max(targetY - y, speed * deltaY * deltaTime);
        } else {
            updateComplete();
        }
    }

    //TODO: Score needs to change depending on the gamemode.
    public int poke() {
        if (!isRevealed()) {
            if (cardStack.getNext(this) == null) {
                this.revealed = true;
//                GameView.OnCardFlip(getStack(), this);
//                return Klondike.VALUE_FOR_TURNING_OVER_TABLEAU;
            }

        }
        return 0;
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


    public Card getNext() {
        return cardStack.getNext(this);
    }


    public int getValue() {
        return number;
    }

    public String getName() {return name;}

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

    public void setTarget (int x, int y) {
        scheduleUpdate();
        this.targetX = x;
        this.targetY = y;
        doneUpdating = false;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean hasNext() {
        if (cardStack.tail() == null)
            return false;
        else if (cardStack.tail() == this)
            return false;
        return true;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }
    public void setStack(CardStack stack) {this.cardStack = stack;}

    public CardStack getStack() {return cardStack;}
    public boolean isRevealed() {return revealed;}

    public void setGameOverPartyTime(boolean status) {
        gameOverPartyTime = status;
    }

    public void scheduleUpdate() {
        if (doneUpdating)
            return;
        GameView.scheduleUpdate(this);
    }
}
