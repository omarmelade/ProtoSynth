package com.omarmelade.studio.protosynth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.*
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.files.fileChooser
import kotlinx.coroutines.*
import java.io.File
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    // fichier shared preferences
    private val sharedPrefFile = "kotlinsharedpreference"

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var flist: FreqList
    private lateinit var adapter: ArrayAdapter<Double>
    private var played = false
    private var tempo: Long = 1000

    private val READ_STORAGE_PERMISSION_REQUEST_CODE = 41;
    // creation de la vue

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        // demande la permission d'ecrire
        checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            READ_STORAGE_PERMISSION_REQUEST_CODE)
        checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            READ_STORAGE_PERMISSION_REQUEST_CODE)


        // Liste de frequences modifiable (Mutable)
        val mlist: MutableList<Double> = ArrayList();
        // on stocke cette liste dans un objet publique
        flist = FreqList(mlist);

        // ---------- Recuperation de la vue

        // listView
        val freq_list = findViewById<ListView>(R.id.freq_list)

        // input
        val freqInput = findViewById<EditText>(R.id.freq)
        val tempoInput = findViewById<EditText>(R.id.tempo)

        // btns
        val play_btn = findViewById<Button>(R.id.play_btn)
        val tempo_btn = findViewById<TextView>(R.id.tempo_btn)
        val btn_sin_sqrt = findViewById<Switch>(R.id.btn_sin_sqrt)
        val freq_add_btn = findViewById<Button>(R.id.freq_btn)


        // ---------- Creation des adapteurs et listeners

        // adapter for Freq Liste, il regarde la liste et observe les changements
        adapter = ArrayAdapter<Double>(this, android.R.layout.simple_spinner_item, flist.list)
        // on affecte l'adapter a la liste
        freq_list.adapter = adapter

        // on defini les conditions de sauvegarde
        sharedPreferences = this.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )

        freq_list.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItemText = parent.getItemAtPosition(position)
                buttonOpenDialogClicked(flist, selectedItemText.toString().toDouble(), position)
            }

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


    // Demande la permission de lecture



    private fun buttonOpenDialogClicked(freq_list: FreqList, previousfreq: Double, pos : Int) {

        val dialog = MaterialDialog(this)
            .noAutoDismiss()
            .customView(R.layout.modify_frequency)

        // recupere les composants de la vue "modify_frequency"
        val editFreq = dialog.findViewById<EditText>(R.id.editTextNumberDecimal)
        val okButton = dialog.findViewById<Button>(R.id.button_ok)
        val cancelBtn = dialog.findViewById<Button>(R.id.button_cancel)

        editFreq.setText(previousfreq.toString())

        // lors de l'appui sur okButton
        okButton.setOnClickListener {
            // si le champ de text n'est pas vide
            if(editFreq.text.toString().isNotEmpty() && !editFreq.text.toString().toDouble().equals(previousfreq)) {
                // on recupere l'element de la liste correspondant et on le modifie avec la nouvelle valeur
                freq_list.list[pos] = editFreq.text.toString().toDouble()
                // on notifie l'adapter des changements sur la liste
                adapter.notifyDataSetChanged()
                // on cache le dialog
            }else {
                Toast.makeText(this@MainActivity, "Content where identical", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }


        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

                loadInSharedPreferences(flist = flist, sharedPreferences = sharedPreferences, applicationContext = applicationContext, adapter = adapter)

/*                val initialFolder = File(getExternalStorageDirectory(), "Download")

                MaterialDialog(this).show {
                    fileChooser(initialDirectory = initialFolder, context = this@MainActivity) { dialog, file ->
                        // File selected
                    }
                }*/
                true
            }
            R.id.action_save ->{
                saveInSharedPreferences(flist = flist, sharedPreferences = sharedPreferences, applicationContext = applicationContext)
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
        playStop(played)
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
        var pauseZero = played
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
            if(list[ i % max ] != 0.0){
                if(!pauseZero){
                    pauseZero = true
                    playStop(pauseZero)
                }
                setFreq(list[ i % max ])
            }else{
                pauseZero = false
                playStop(pauseZero)
            }

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
        playStop(played)
    }


    fun playStop(boolean: Boolean) {
        playEngine(boolean);
    }


    override fun onDestroy() {
        stopEngine();
        super.onDestroy()
    }


    /**
     * Native method to access sound streams
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

