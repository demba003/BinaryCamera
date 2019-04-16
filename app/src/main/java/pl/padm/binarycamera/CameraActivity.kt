package pl.padm.binarycamera

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_camera.*
import pl.padm.binarycamera.camera.PreviewCallback
import pl.padm.binarycamera.camera.SurfaceListener

class CameraActivity : Activity() {
    private lateinit var camera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        textureView.surfaceTextureListener = SurfaceListener(this::updateFps, this::initCamera)
    }

    override fun onDestroy() {
        if (::camera.isInitialized) {
            camera.setPreviewCallback(null)
            camera.stopPreview()
            camera.release()
        }
        super.onDestroy()
    }

    private fun updateImage(bitmap: Bitmap) {
        this@CameraActivity.runOnUiThread {
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun updateFps(fps: Double) {
        this@CameraActivity.runOnUiThread {
            fpsCounter.text = with("%2.2f") { format(fps) }
        }
    }

    private fun updateProcessedFps(fps: Double) {
        this@CameraActivity.runOnUiThread {
            fpsCounter2.text = with("%2.2f") { format(fps) }
        }
    }

    private fun initCamera(surfaceTexture: SurfaceTexture) {
        camera = Camera.open()

        val parameters = camera.parameters
        parameters.supportedPreviewSizes.forEach { println("${it.width}x${it.height}") }

        parameters.setRecordingHint(true)
        parameters.colorEffect = Camera.Parameters.EFFECT_MONO
        parameters.setPreviewSize(1920, 1080)
        parameters.focusMode = FOCUS_MODE_CONTINUOUS_PICTURE
        camera.parameters = parameters

        camera.setPreviewTexture(surfaceTexture)
        camera.setPreviewCallback(
            PreviewCallback(
                this::updateImage,
                this::updateProcessedFps,
                parameters.previewSize.width,
                parameters.previewSize.height
            )
        )

        camera.startPreview()
    }
}
