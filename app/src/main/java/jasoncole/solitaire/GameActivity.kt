package jasoncole.solitaire

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import gameViews.Klondike


/**
 * Created by Jason Cole on 7/13/2018.
 */


class GameActivity : AppCompatActivity() {

    //declaring gameview
    private var gameView: Klondike? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Initializing game view object
        gameView = Klondike(this)

        //adding it to contentview
        setContentView(gameView)
    }

    //pausing the game when activity is paused
    override fun onPause() {
        super.onPause()
        gameView!!.pause()
    }

    //running the game when activity is resumed
    override fun onResume() {
        super.onResume()
        gameView!!.resume()
    }
}