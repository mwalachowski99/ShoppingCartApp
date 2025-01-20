package com.example.shoppingchartapp
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.example.shoppingchartapp.databinding.ActivityOptionsBinding
import java.security.KeyStore.TrustedCertificateEntry

class OptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOptionsBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        editor = prefs.edit()

        binding.seekBarRed.max = 255
        binding.seekBarGreen.max = 255
        binding.seekBarBlue.max = 255
        binding.seekBarSize.max = 36
        binding.seekBarSize.min = 12

        binding.seekBarRed.progress = prefs.getInt("Red", 0)
        binding.seekBarGreen.progress = prefs.getInt("Green", 0)
        binding.seekBarBlue.progress = prefs.getInt("Blue", 0)
        binding.seekBarSize.progress = prefs.getInt("Size", 24)
        onSeekBarColorChange()
        onSeekBarSizeChange()

        setSeekBarColorListener(binding.seekBarRed)
        setSeekBarColorListener(binding.seekBarGreen)
        setSeekBarColorListener(binding.seekBarBlue)
        setSeekBarSizeListener(binding.seekBarSize)

        val currencyIdMap = mapOf( binding.radioButtonPLN.id to "PLN", binding.radioButtonUSD.id to "USD", binding.radioButtonEUR.id to "EUR")

        binding.saveButton.setOnClickListener{
            editor.putInt("Red", binding.seekBarRed.progress)
            editor.putInt("Green", binding.seekBarGreen.progress)
            editor.putInt("Blue", binding.seekBarBlue.progress)
            editor.putInt("Size", binding.seekBarSize.progress)
            editor.putString("Currency",
                currencyIdMap[binding.radioGroupCurrency.checkedRadioButtonId]
            )
            editor.apply()
            startActivity(Intent(this, MainActivity::class.java))
        }

        when(prefs.getString("Currency", "PLN")) {
            "PLN" -> binding.radioButtonPLN.isChecked = true
            "EUR" -> binding.radioButtonEUR.isChecked = true
            "USD" -> binding.radioButtonUSD.isChecked = true
        }
    }

    private fun onSeekBarColorChange(){
        val color = Color.rgb(binding.seekBarRed.progress, binding.seekBarGreen.progress, binding.seekBarBlue.progress)
        binding.colorView.setColorFilter(color)
    }

    private fun onSeekBarSizeChange(){
        binding.textViewTextSize.text = binding.seekBarSize.progress.toString()
    }

    private fun setSeekBarColorListener(seekBar: SeekBar){
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                onSeekBarColorChange()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }

    private fun setSeekBarSizeListener(seekBar: SeekBar){
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                onSeekBarSizeChange()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }
}