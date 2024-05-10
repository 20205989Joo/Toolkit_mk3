package com.example.toolkit_management_mk3

import android.content.Intent
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity

class ZoomInActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var textureView: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.zoom_in_layout)

        textureView = findViewById(R.id.textureView)
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                playVideo(surface)
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                mediaPlayer.stop()
                mediaPlayer.release()
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0, Color.TRANSPARENT)
    }

    private fun playVideo(surface: SurfaceTexture) {
        mediaPlayer = MediaPlayer.create(this, Uri.parse("android.resource://$packageName/${R.raw.zoomin}"))
        mediaPlayer.setSurface(Surface(surface))
        mediaPlayer.isLooping = false
        mediaPlayer.start()

        // 비디오 재생이 완료되면 MainActivity_2로 전환
        mediaPlayer.setOnCompletionListener {
            val intent = Intent(this, MainActivity_2::class.java)
            startActivity(intent)
            finish()  // 현재 액티비티 종료

            // 화면 전환 애니메이션 제거
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0, Color.TRANSPARENT)
        }
    }
}
