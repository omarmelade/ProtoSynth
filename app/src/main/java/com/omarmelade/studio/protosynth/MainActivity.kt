package com.omarmelade.studio.protosynth

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.*
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_notes.view.*
import kotlinx.android.synthetic.main.player_bar.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    // fichier shared preferences
    private val sharedPrefFile = "kotlinsharedpreference"

    private lateinit var notesAdapter : NotesAdapter

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
            //Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
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

        // demande la permission d'ecrire et de lire
        checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            READ_STORAGE_PERMISSION_REQUEST_CODE)
        checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            READ_STORAGE_PERMISSION_REQUEST_CODE)

        // cree l'adapter avec une liste
        notesAdapter = NotesAdapter(Note.createNoteList(32))

        val itemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        rvNotesList.addItemDecoration(itemDecoration)


        // ajoute l'adapter a la liste pour gerer son fonctionnement
        rvNotesList.adapter = notesAdapter
        rvNotesList.layoutManager = LinearLayoutManager(this)

        forw.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            val notes = notesAdapter.getAllNotes()
            for (i in sel){
                notes[i] = notes[i].next()
                notesAdapter.notifyItemChanged(i)
            }
        }

        back.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            val notes = notesAdapter.getAllNotes()
            for (i in sel){
                notes[i] = notes[i].prev()
                notesAdapter.notifyItemChanged(i)
            }
        }

        // soundPlayer
        var player : SoundPlayer = SoundPlayer();

        play.setOnClickListener {
            val notes = notesAdapter.getAllNotes()
            System.err.println(notes)
            player.play_sound(notes)
        }

        stop.setOnClickListener {
            player.setPlayedBool(false)
        }

        // demare le audio engine
        player.startAudioEngine();
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




/*    *//**
     * Native method to access sound streams
     *//*
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
    }*/
}

