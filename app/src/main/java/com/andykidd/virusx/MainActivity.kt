package com.andykidd.virusx

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var uptimeTextView: TextView
    private val mainHandler = Handler(Looper.getMainLooper())
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val photoImageView: ImageView = findViewById(R.id.photoImageView)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val percentageTextView: TextView = findViewById(R.id.percentageTextView)
        val deviceNameTextView: TextView = findViewById(R.id.deviceNameTextView)
        val countTextView: TextView = findViewById(R.id.countTextView)
        uptimeTextView = findViewById(R.id.uptimeTextView)

        val totalCount = getMediaCount()

        progressBar.max = 100
        val currentProgress = 100

        val animator = ObjectAnimator.ofInt(progressBar, "progress", currentProgress)
            .setDuration(10000)

        startTime = SystemClock.elapsedRealtime()

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {

                progressBar.visibility = View.GONE
                photoImageView.visibility = View.VISIBLE
                percentageTextView.visibility = View.GONE


                val deviceName = getDeviceName()
                deviceNameTextView.text = getString(R.string.device_name_format, deviceName)
                countTextView.text = getString(R.string.total_count_format, totalCount)

                startUptimeUpdate()

                scheduleSikeTextDisplay()
            }

            override fun onAnimationCancel(animation: Animator) {
                // Animation canceled
            }

            override fun onAnimationRepeat(animation: Animator) {
                // Animation repeated
            }
        })

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            percentageTextView.text = getString(R.string.percentage_format, progress)
        }

        animator.start()
    }

    private fun getMediaCount(): Int {
        val contentResolver: ContentResolver = contentResolver
        val photoUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI
        val photoCursor = contentResolver.query(photoUri, null, null, null, null)
        val photoCount = photoCursor?.count ?: 0
        photoCursor?.close()

        val videoUri = MediaStore.Video.Media.INTERNAL_CONTENT_URI
        val videoCursor = contentResolver.query(videoUri, null, null, null, null)
        val videoCount = videoCursor?.count ?: 0
        videoCursor?.close()

        return photoCount + videoCount
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        } else {
            "$manufacturer $model".replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    }

    private fun formatUptime(elapsedTime: Long): String {
        val hours = elapsedTime / (60 * 60 * 1000)
        val minutes = (elapsedTime % (60 * 60 * 1000)) / (60 * 1000)
        val seconds = (elapsedTime % (60 * 1000)) / 1000
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun startUptimeUpdate() {

        mainHandler.post(object : Runnable {
            override fun run() {

                val elapsedTime = SystemClock.elapsedRealtime() - startTime
                val formattedUptime = formatUptime(elapsedTime)
                uptimeTextView.text = getString(R.string.uptime_format, formattedUptime)

                mainHandler.postDelayed(this, 1000)
            }
        })
    }

    private fun scheduleSikeTextDisplay() {
        val randomSeconds = (5..10).random().toLong() * 1000 // Convert seconds to milliseconds
        mainHandler.postDelayed({
            displaySikeText()
        }, randomSeconds)
    }

    @SuppressLint("SetTextI18n")
    private fun displaySikeText() {

        val sikeTextView = TextView(this)
        val sikeText = "SiKe Biatch, your phone is mine now :)"
        val spannable = SpannableStringBuilder(sikeText)

        for (i in sikeText.indices) {
            val randomColor = getRandomColor()
            spannable.setSpan(ForegroundColorSpan(randomColor), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        sikeTextView.text = spannable
        sikeTextView.textSize = 24f
        sikeTextView.gravity = Gravity.CENTER

        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )


        val rootLayout = findViewById<RelativeLayout>(R.id.rootLayout)
        rootLayout.addView(sikeTextView, layoutParams)

        playSikeSound()


        rootLayout.removeView(findViewById(R.id.deviceNameTextView))
        rootLayout.removeView(findViewById(R.id.countTextView))
        rootLayout.removeView(findViewById(R.id.uptimeTextView))


        mainHandler.postDelayed({
            finish()
        }, 5000)
    }

    private fun getRandomColor(): Int {
        val r = (0..255).random()
        val g = (0..255).random()
        val b = (0..255).random()
        return Color.rgb(r, g, b)
    }

    private fun playSikeSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.sike) // Replace "your_sound_file" with the actual sound file in your "res/raw" folder
        mediaPlayer.start()
    }

}
