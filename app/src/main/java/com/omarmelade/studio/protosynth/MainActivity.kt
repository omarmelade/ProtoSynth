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

        // soundPlayer
        val player = SoundPlayer()

        playBtn.setOnClickListener {
            // On demarre le moteur audio
            if(!player.getStarted()){
                player.startAudioEngine();
            }

            // on modifie l'image du boutton
            if(!player.getPlayedBool()){
                playBtn.setImageResource(R.drawable.ic_pause)
            }else{
                playBtn.setImageResource(R.drawable.ic_forward)
            }

            val notes = notesAdapter.getAllNotes()  // on recupere la liste des notes
            System.err.println(notes)
            player.playSound(notes) // on joue la liste
        }

        stopBtn.setOnClickListener {
            player.playStart(false)
            playBtn.setImageResource(R.drawable.ic_forward)
            player.destroy()
        }
    }
}

