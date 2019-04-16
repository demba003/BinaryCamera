package pl.padm.binarycamera

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_camera.*
import pl.padm.binarycamera.camera.PreviewCallback
import pl.padm.binarycamera.camera.SurfaceListener
import android.widget.ArrayAdapter
import android.widget.SeekBar


class CameraActivity : Activity() {
    private lateinit var camera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        textureView.surfaceTextureListener = SurfaceListener(this::updateFps, this::initCamera)
        initSeekBar()
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
            fpsCounterOriginal.text = with("%2.2f") { format(fps) }
        }
    }

    private fun updateProcessedFps(fps: Double) {
        this@CameraActivity.runOnUiThread {
            fpsCounterProcessed.text = with("%2.2f") { format(fps) }
        }
    }

    private fun initSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                factor.text = with("%1.2f") { format(progress / 100.0) }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun initResolutionPicker(sizes: List<Camera.Size>) {
        val list = sizes.map { "${it.width}x${it.height}" }.toList()
        resolutions.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list)

        resolutions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
        }
    }

    private fun initCamera(surfaceTexture: SurfaceTexture) {
        camera = Camera.open()

        val parameters = camera.parameters
        parameters.supportedPreviewSizes.forEach { println("${it.width}x${it.height}") }
        parameters.setRecordingHint(true)
        parameters.colorEffect = Camera.Parameters.EFFECT_MONO
        parameters.setPreviewSize(1280, 720)
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

        initResolutionPicker(parameters.supportedPreviewSizes)
        camera.startPreview()
    }
}
