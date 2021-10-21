package com.omarmelade.studio.protosynth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.lang.Exception
import java.security.AccessController.getContext
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    // fichier shared preferences
    private val sharedPrefFile = "kotlinsharedpreference"

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var flist: FreqList;
    private lateinit var adapter: ArrayAdapter<Double>
    private var played = false;
    private var tempo: Long = 1000;
    private var mHandler: Handler = Handler()


    val CREATE_FILE = 1
    // creation de la vue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        // Liste de frequences
        val mlist: MutableList<Double> = ArrayList();
        flist = FreqList(mlist);
        val freq_list = findViewById<ListView>(R.id.freq_list)

        // input
        val freqInput = findViewById<EditText>(R.id.freq)
        val tempoInput = findViewById<EditText>(R.id.tempo)

        // btns
        val play_btn = findViewById<Button>(R.id.play_btn)
        val tempo_btn = findViewById<TextView>(R.id.tempo_btn)
        val btn_sin_sqrt = findViewById<Switch>(R.id.btn_sin_sqrt)
        val freq_add_btn = findViewById<Button>(R.id.freq_btn)

        // adapter for Freq Liste
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, flist.list)
        freq_list.adapter = adapter

        sharedPreferences = this.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )

        freq_add_btn.setOnClickListener(View.OnClickListener { view ->
            val f = freqInput.text.toString();
            if(f.isNotEmpty()){
                flist.list.add(f.toDouble());
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
            if(played){
                played = !played
            }else{
                if(flist.list.isNotEmpty())
                    play_sound(flist.list)
                else
                    Toast.makeText(applicationContext, "Ajouté au moins une fréquence", Toast.LENGTH_SHORT)
            }
        })

        startEngine();
    }


    // creation du menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_load -> {

                val str : String = sharedPreferences.getString("tab", "").toString();
                val strFList = import(str)
                adapter.addAll(strFList.list)
                adapter.notifyDataSetChanged()

                var load_toast = "Echec du chargement"
                if(!adapter.isEmpty){
                    load_toast = "Chargement reussi"
                }
                Toast.makeText(applicationContext, load_toast, Toast.LENGTH_SHORT).show()

                true
            }
            R.id.action_save ->{
                val strFList = flist.export();
                var save_toast = "Echec de la sauvegarde"

                if(strFList.isNotEmpty()){
                    val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                    editor.putString("tab",strFList)
                    editor.apply()
                    editor.commit()
                    save_toast ="Sauvegarde effectué"
                }

                Toast.makeText(applicationContext, save_toast, Toast.LENGTH_SHORT).show()

                return true
            }
            R.id.action_clear ->{

                adapter.clear()
                adapter.notifyDataSetChanged()

                Toast.makeText(applicationContext, "Données supprimée", Toast.LENGTH_SHORT).show()

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun updateListColor(index: Int, color: Int) {
        val freq_list = findViewById<ListView>(R.id.freq_list)
        if(freq_list.getChildAt(index) != null){
            freq_list.smoothScrollToPosition( index )
            Log.e("MyActivity", "index : $index color : $color")
            freq_list.getChildAt( index ).setBackgroundColor(color)
        }
    }

    @SuppressLint("HandlerLeak")
    private fun play_sound(list: MutableList<Double>) {
        played = !played
        play_stop(played)
        if(list.isNotEmpty()) {

            // color the sound played

           /* mHandler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    if (msg.arg1 >= 0)
                        updateListColor(msg.arg1, msg.arg2)
                }
            }*/

            thread(start = true, name = "playThread") {
                playList(list)
            }
        }
    }

    private fun playList(list: MutableList<Double>) {

        var i = 0;
        var max = list.size
        while (played) {
            val index = i % max

            // color things
/*
            var msg = Message.obtain()
            msg.arg1 = index;
            msg.arg2 = Color.BLUE
            mHandler.sendMessage(msg)
            Log.i("MyActivity","HEEEEEEEEEEEEEEEEEE" + index)
*/

            setFreq(list[ i % max ])
            Thread.sleep(tempo)

/*
            Log.i("MyActivity","HEEEEEEEEEEEEEEEEEE" + index)

            var msgClose = Message.obtain()
            msgClose.arg1 = index;
            msgClose.arg2 = Color.WHITE
            mHandler.sendMessage(msgClose)
*/

            i++;
        }
        played = false
        play_stop(played)
    }


    fun play_stop(boolean: Boolean) {
        playEngine(boolean);
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

