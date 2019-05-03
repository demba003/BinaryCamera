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
import pl.padm.binarizer.processor.jvm.BradleyProcessor
import pl.padm.binarizer.processor.Processor
import pl.padm.binarizer.processor.jvm.SauvolaProcessor
import pl.padm.binarizer.processor.jvm.SimpleProcessor
import pl.padm.binarizer.processor.native.BradleyNativeProcessor
import pl.padm.binarizer.processor.native.SimpleNativeProcessor

@ExperimentalUnsignedTypes
class CameraActivity : Activity() {
    private lateinit var camera: Camera
    private lateinit var previewCallback: PreviewCallback
    private var processor: Processor = SimpleProcessor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        initCamera()
        initSeekBar()
        initFrameBufferSlider()
        initResolutionPicker(camera.parameters.supportedPreviewSizes)
        initButtons()
    }

    private fun initButtons() {
        simpleButton.setOnClickListener { setProcessingMethod(SimpleProcessor()) }
        simpleNativeButton.setOnClickListener { setProcessingMethod(SimpleNativeProcessor()) }
        bradleyButton.setOnClickListener { setProcessingMethod(BradleyProcessor()) }
        bradleyNativeButton.setOnClickListener { setProcessingMethod(BradleyNativeProcessor()) }
        sauvolaButton.setOnClickListener { setProcessingMethod(SauvolaProcessor()) }
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

    private fun setProcessingMethod(processor: Processor) {
        camera.stopPreview()
        this.processor = processor
        val previewSize = camera.parameters.previewSize
        previewCallback = PreviewCallback(
            this::updateImage,
            this::updateProcessedFps,
            this.processor,
            previewSize.width,
            previewSize.height
        )
        camera.setPreviewCallback(previewCallback)
        camera.startPreview()
    }

    private fun initSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SimpleSeekBarListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                factor.text = with("%1.2f") { format(progress / 100.0) }
                processor.factor = progress / 100.0
            }
        })
    }

    private fun initFrameBufferSlider() {
        frameBufferSlider.setOnSeekBarChangeListener(object : SimpleSeekBarListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress + 1
                frameBufferText.text = value.toString()
                previewCallback.frameBufferSize = value
            }
        })
    }

    private fun initResolutionPicker(sizes: List<Camera.Size>) {
        val list = sizes.map { "${it.width}x${it.height}" }.toList()
        resolutions.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list)

        resolutions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                camera.stopPreview()
//                camera.setPreviewCallback(null)
//                val params = camera.parameters
//                params.setPreviewSize(sizes[position].width, sizes[position].height)
//                camera.parameters = params
//                initCamera()
            }
        }
    }

    private fun initCamera() {
        camera = Camera.open()
        textureView.surfaceTextureListener = SurfaceListener(this::updateFps, this::initCameraPreview)

        val parameters = camera.parameters
        parameters.setRecordingHint(true)
        parameters.colorEffect = Camera.Parameters.EFFECT_MONO
        parameters.focusMode = FOCUS_MODE_CONTINUOUS_PICTURE
        parameters.setPreviewSize(800, 480)
        camera.parameters = parameters

        setProcessingMethod(SimpleProcessor())
    }

    private fun initCameraPreview(surfaceTexture: SurfaceTexture) {
        camera.setPreviewTexture(surfaceTexture)
    }
}
