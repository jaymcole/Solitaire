package jasoncole.solitaire;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by SYSTEM on 7/13/2018.
 */

public class Deck extends Card{

    public static final int CARDS_PER_HAND = 3;
    private Card hand;

    public Deck(int x, int y, int ox, int oy) {
        super(x, y, ox, oy);

        hand = new Card((int)(this.getX() + Card.width), (int)this.getY(), (int)(Card.width/10), 0);



    }

    @Override
    public void render(Canvas canvas, Paint paint) {
        super.render(canvas, paint);

        if (hand != null)
            hand.render(canvas, paint);


    }
}
