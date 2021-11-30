package com.omarmelade.studio.protosynth

import android.os.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
        notesAdapter = NotesAdapter(Note.createNoteList(16))

        // not really usefull but i have to work on stable ids in my list

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
        var playing = false
        val player = SoundPlayerHandler(notes, playing)
        player.startEngine()

        btn_sin_sqrt.setOnClickListener{
            player.setisSin(!btn_sin_sqrt.isChecked)
        }

        // ----------------------- changement de notes

        forwBtn.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            val notes = notesAdapter.getAllNotes()
            for (i in sel) {
                notes[i] = notes[i].next()
                // better way to do, update only one elements
                // notesAdapter.notifyItemChanged(i)
            }
            // reload everything to prevent id change
            notesAdapter.notifyDataSetChanged()
        }

        backBtn.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            val notes = notesAdapter.getAllNotes()
            for (i in sel) {
                notes[i] = notes[i].prev()
            }
            notesAdapter.notifyDataSetChanged()
        }

        fast_forwBtn.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            val notes = notesAdapter.getAllNotes()
            for (i in sel) {
                notes[i] = notes[i].nextOct()
            }
            notesAdapter.notifyDataSetChanged()
        }

        fast_backBtn.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            val notes = notesAdapter.getAllNotes()
            for (i in sel) {
                notes[i] = notes[i].prevOct()
            }
            notesAdapter.notifyDataSetChanged()
        }

        // ----------------------- gestion du player

        playBtn.setOnClickListener {
            val notes = notesAdapter.getAllNotes()


            // on modifie l'image du boutton
            if(player.playing){
                System.err.println("stop")
                player.interrupt()
                playBtn.setImageResource(R.drawable.ic_play_btn)
            }else{
                System.err.println("start")
                player.list = notes
                player.btn = playBtn;
                player.running()
                playBtn.setImageResource(R.drawable.ic_pause_btn)
                playBtn.scaleX = 0.75F
                playBtn.scaleY = 0.75F
            }
        }

        stopBtn.setOnClickListener {
            player.interrupt()
            playBtn.setImageResource(R.drawable.ic_play_btn)
        }
    }


}

