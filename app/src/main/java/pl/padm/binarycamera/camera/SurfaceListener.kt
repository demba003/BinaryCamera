package pl.padm.binarycamera.camera

import android.graphics.SurfaceTexture
import android.view.TextureView

class SurfaceListener(val fps: (Double) -> Unit, val setTexture: (SurfaceTexture) -> Unit) :
    TextureView.SurfaceTextureListener {
    private var lastFrameTimestamp: Long = 0

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        val diff = (System.currentTimeMillis() - lastFrameTimestamp)
        lastFrameTimestamp = System.currentTimeMillis()
        fps(1.0 / diff * 1000)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) = setTexture(surface)

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
}