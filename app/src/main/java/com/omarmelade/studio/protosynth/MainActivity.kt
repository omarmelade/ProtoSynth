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

    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // cree l'adapter avec une liste
        notesAdapter = NotesAdapter(Note.createNoteList(8))


        val itemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        rvNotesList.addItemDecoration(itemDecoration)


        // ajoute l'adapter a la liste pour gerer son fonctionnement
        rvNotesList.adapter = notesAdapter
        rvNotesList.layoutManager = LinearLayoutManager(this)

        registerForContextMenu(rvNotesList)

        // notes listes
        val notes = notesAdapter.getAllNotes()
        // soundPlayer
        val player = SoundPlayerHandler(notes)
        player.startEngine()

        btn_sin_sqrt.setOnClickListener{
            player.setisSin(!btn_sin_sqrt.isChecked)
        }

        forwBtn.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            //System.err.println("from forw click : $sel")
            val notes = notesAdapter.getAllNotes()
            for (i in sel) {
                notes[i] = notes[i].next()
                notesAdapter.notifyItemChanged(i)
            }
        }

        backBtn.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            val notes = notesAdapter.getAllNotes()
            for (i in sel) {
                notes[i] = notes[i].prev()
                notesAdapter.notifyItemChanged(i)
            }
        }

        fast_forwBtn.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            val notes = notesAdapter.getAllNotes()
            for (i in sel) {
                notes[i] = notes[i].nextOct()
                notesAdapter.notifyItemChanged(i)
            }
        }

        fast_backBtn.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            val notes = notesAdapter.getAllNotes()
            for (i in sel) {
                notes[i] = notes[i].prevOct()
                notesAdapter.notifyItemChanged(i)
            }
        }

        playBtn.setOnClickListener {

            val notes = notesAdapter.getAllNotes()

            // on modifie l'image du boutton
            if(player.isAlive){
                player.interrupt()
                playBtn.setImageResource(R.drawable.ic_pause)
            }else{
                player.list = notes
                player.btn = playBtn;
                player.running()
                playBtn.setImageResource(R.drawable.ic_forward)
            }
        }

        stopBtn.setOnClickListener {
            player.interrupt()
            playBtn.setImageResource(R.drawable.ic_forward)
        }


    }
}

