package jasoncole.solitaire

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageButton
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.support.v4.view.ViewCompat.getDisplay
import android.util.Log
import android.view.View
import jasoncole.solitaire.R.id.buttonPlay
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    //image button
//    private var buttonPlay: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setting the orientation to landscape
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        //getting the button
//        buttonPlay = findViewById<View>(R.id.buttonPlay)

        //adding a click listener
        buttonPlay!!.setOnClickListener(this)
//        buttonPlay


    }

    override fun onClick(v: View) {

        //starting game activity
        startActivity(Intent(this, GameActivity::class.java))
    }
}
