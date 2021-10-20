package com.omarmelade.studio.protosynth

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<Double>
    private var played = false;
    private var tempo: Long = 1000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val list: MutableList<Double> = ArrayList();

        val freqInput = findViewById<EditText>(R.id.freq)
        val tempoInput = findViewById<EditText>(R.id.tempo)

        val play_btn = findViewById<TextView>(R.id.play_btn)
        val tempo_btn = findViewById<TextView>(R.id.tempo_btn)
        val btn_sin_sqrt = findViewById<Switch>(R.id.btn_sin_sqrt)

        val freq_add_btn = findViewById<Button>(R.id.freq_btn)
        val freq_list = findViewById<ListView>(R.id.freq_list)

        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        freq_list.adapter = adapter

        freq_add_btn.setOnClickListener(View.OnClickListener { view ->
            val f = freqInput.text.toString();
            if(f.isNotEmpty()){
                list.add(f.toDouble());
                freqInput.text.clear()
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(this@MainActivity, "La fréquence est necessaire", Toast.LENGTH_SHORT).show()
            }
        })

        btn_sin_sqrt.setOnClickListener(View.OnClickListener { v ->
            setIsSin(!btn_sin_sqrt.isChecked)
        })

        tempo_btn.setOnClickListener(View.OnClickListener { v ->
            val value = tempoInput.text.toString()
            if(value.isNotEmpty()){
                val bpm = value.toDouble()
                tempo = ((60 / bpm) * 1000).toLong();
                Toast.makeText(this@MainActivity, "tempo mis a $bpm", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@MainActivity, "Le tempo est necessaire", Toast.LENGTH_SHORT).show()
            }

            tempoInput.text.clear()
        })

        play_btn.setOnClickListener(View.OnClickListener { v ->
            played = true
            play_stop(played)
            playList(list)
        })

        startEngine();
    }

    private fun playList(list: MutableList<Double>) {
        var i = 0;
        var max = list.size
        while (i < max) {
            setFreq(list[i]);
            Thread.sleep(tempo);
            i++;
        }
        played = false
        play_stop(played)
    }

    fun play_stop(boolean: Boolean) {
        playEngine(boolean);
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            touchEvent(event.action)
        }
        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        stopEngine();
        super.onDestroy()
    }

    /**
     * A native method that is implemented by the 'protosynth' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String
    private external fun startEngine();
    private external fun stopEngine();
    private external fun touchEvent(action: Int);
    private external fun setFreq(double: Double);
    private external fun playEngine(boolean: Boolean);
    private external fun setIsSin(boolean: Boolean);


    companion object {
        // Used to load the 'protosynth' library on application startup.
        init {
            System.loadLibrary("protosynth")
        }
    }
}

