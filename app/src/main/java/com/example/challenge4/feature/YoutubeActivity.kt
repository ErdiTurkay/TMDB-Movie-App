package com.example.challenge4.feature

import android.os.Bundle
import com.example.challenge4.R
import com.example.challenge4.common.Constant
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView

class YoutubeActivity : YouTubeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube)
        val videoID = intent.getStringExtra("key")
        initializePlayer(videoID!!)
    }

    private fun initializePlayer(videoID: String) {
        val youtubePlayer = findViewById<YouTubePlayerView>(R.id.youtube_player)

        youtubePlayer.initialize(
            Constant.YOUTUBE_API_KEY,
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubePlayer,
                    p2: Boolean
                ) {
                    p1.run {
                        loadVideo(videoID)
                        play()
                    }
                }

                override fun onInitializationFailure(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubeInitializationResult?
                ) {}
            }
        )
    }
}
