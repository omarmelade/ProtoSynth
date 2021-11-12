package com.omarmelade.studio.protosynth

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.*
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_notes.view.*
import kotlinx.android.synthetic.main.player_bar.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    // fichier shared preferences
    private val sharedPrefFile = "kotlinsharedpreference"

    private lateinit var notesAdapter: NotesAdapter

    private lateinit var sharedPreferences: SharedPreferences

    private val READ_STORAGE_PERMISSION_REQUEST_CODE = 41
    // creation de la vue

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // demande la permission d'ecrire et de lire
/*        checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            READ_STORAGE_PERMISSION_REQUEST_CODE
        )
        checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            READ_STORAGE_PERMISSION_REQUEST_CODE
        )*/

        // cree l'adapter avec une liste
        notesAdapter = NotesAdapter(Note.createNoteList(32))


        val itemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        rvNotesList.addItemDecoration(itemDecoration)


        // ajoute l'adapter a la liste pour gerer son fonctionnement
        rvNotesList.adapter = notesAdapter
        rvNotesList.layoutManager = LinearLayoutManager(this)

        registerForContextMenu(rvNotesList)

        forw.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            System.err.println("from forw click : $sel")
            val notes = notesAdapter.getAllNotes()
            for (i in sel) {
                notes[i] = notes[i].next()
                notesAdapter.notifyItemChanged(i)
            }
        }

        back.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            val notes = notesAdapter.getAllNotes()
            for (i in sel) {
                notes[i] = notes[i].prev()
                notesAdapter.notifyItemChanged(i)
            }
        }

        // soundPlayer
        val player = SoundPlayer()

        play.setOnClickListener {
            if(player.getPlayedBool()){
                play.setImageResource(R.drawable.ic_pause)
            }else{
                play.setImageResource(R.drawable.ic_forward)
            }
            val notes = notesAdapter.getAllNotes()
            System.err.println(notes)
            player.playSound(notes)
        }

        stop.setOnClickListener {
            player.setPlayedBool(false)
        }

        // demare le audio engine
        player.startAudioEngine()
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


/*                val initialFolder = File(getExternalStorageDirectory(), "Download")

                MaterialDialog(this).show {
                    fileChooser(initialDirectory = initialFolder, context = this@MainActivity) { dialog, file ->
                        // File selected
                    }
                }*/
                true
            }
            R.id.action_save -> {

                return true
            }
            R.id.action_clear -> {

                Toast.makeText(applicationContext, "Données supprimée", Toast.LENGTH_SHORT).show()

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

