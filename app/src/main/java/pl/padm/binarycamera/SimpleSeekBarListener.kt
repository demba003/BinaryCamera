package pl.padm.binarycamera

import android.widget.SeekBar

interface SimpleSeekBarListener : SeekBar.OnSeekBarChangeListener {
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
}