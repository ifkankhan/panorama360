package com.gps.location.a360images

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var panoramaView: Panorama360View
    private lateinit var instructionContainer: RelativeLayout
    private lateinit var swipeLeft: ImageView
    private lateinit var swipeRight: ImageView
    private lateinit var swipeUp: ImageView
    private lateinit var swipeDown: ImageView
    private var instructionsVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Custom 360 Panorama View (No 16kb alignment issues!)
        panoramaView = findViewById(R.id.panorama_view)
        instructionContainer = findViewById(R.id.instruction_container)
        swipeLeft = findViewById(R.id.swipe_left)
        swipeRight = findViewById(R.id.swipe_right)
        swipeUp = findViewById(R.id.swipe_up)
        swipeDown = findViewById(R.id.swipe_down)
        
        // Load 360 image
        load360Image()
        
        // Start swipe animations
        startSwipeAnimations()
        
        // Set up touch listener to hide instructions when user touches screen
        panoramaView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && instructionsVisible) {
                hideInstructions()
            }
            false // Return false to let the view handle the touch normally
        }
    }

    private fun load360Image() {
        // Loading your 360 image from drawable resources
        panoramaView.loadImageFromResources(R.drawable.threesixty)
    }

    private fun startSwipeAnimations() {
        // Load and start animations for each arrow
        val leftAnim = AnimationUtils.loadAnimation(this, R.anim.swipe_left_animation)
        val rightAnim = AnimationUtils.loadAnimation(this, R.anim.swipe_right_animation)
        val upAnim = AnimationUtils.loadAnimation(this, R.anim.swipe_up_animation)
        val downAnim = AnimationUtils.loadAnimation(this, R.anim.swipe_down_animation)
        
        swipeLeft.startAnimation(leftAnim)
        swipeRight.startAnimation(rightAnim)
        swipeUp.startAnimation(upAnim)
        swipeDown.startAnimation(downAnim)
    }

    private fun hideInstructions() {
        instructionsVisible = false
        instructionContainer.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                instructionContainer.visibility = View.GONE
            }
            .start()
    }

    override fun onResume() {
        super.onResume()
        panoramaView.onResume()
    }

    override fun onPause() {
        super.onPause()
        panoramaView.onPause()
    }
}