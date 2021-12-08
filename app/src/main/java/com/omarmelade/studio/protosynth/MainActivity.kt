package com.omarmelade.studio.protosynth

import android.os.*
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
    private var selectAll = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)
        // cree l'adapter avec une liste
        notesAdapter = NotesAdapter(Note.createNoteList(32))
        notesAdapter.setHasStableIds(true)

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

            if( sel.isNotEmpty() ){

                for (i in sel) {
                    notes[i] = notes[i].next()
                    // better way to do, update only one elements
                    // notesAdapter.notifyItemChanged(i)
                }
                // reload everything to prevent id change
                notesAdapter.notifyDataSetChanged()

            }
        }

        backBtn.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            val notes = notesAdapter.getAllNotes()

            if( sel.isNotEmpty() ) {

                for (i in sel) {
                    notes[i] = notes[i].prev()
                }
                notesAdapter.notifyDataSetChanged()

            }
        }

        fast_forwBtn.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            val notes = notesAdapter.getAllNotes()

            if( sel.isNotEmpty() ) {

                for (i in sel) {
                    notes[i] = notes[i].nextOct()
                }
                notesAdapter.notifyDataSetChanged()

            }
        }

        fast_backBtn.setOnClickListener {
            val sel = notesAdapter.selectedItems()
            val notes = notesAdapter.getAllNotes()
            if( sel.isNotEmpty() ) {

                for (i in sel) {
                    notes[i] = notes[i].prevOct()
                }
                notesAdapter.notifyDataSetChanged()

            }
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
                playBtn.scaleX = 0.5F
                playBtn.scaleY = 0.5F
            }
        }

        stopBtn.setOnClickListener {
            player.interrupt()
            playBtn.setImageResource(R.drawable.ic_play_btn)
        }
    }

   /* override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_settings -> {
            // User chose the "Settings" item, show the app settings UI...
            true
        }

        R.id.checkbox -> {
            if(selectAll){
                selectAll = false
                unselectAllElements()
            }else{
                selectAll = true
                selectAllElements()
            }
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }*/

    private fun selectAllElements(){
        val notes = notesAdapter.getAllNotes()
        val selNotes = notesAdapter.selectedItems()
        for(i in 0..notes.size)
        {

            selNotes.add(i)
        }
        notesAdapter.notifyDataSetChanged()
    }

    private fun unselectAllElements(){
        notesAdapter.selectedItems = mutableListOf()
        notesAdapter.notifyDataSetChanged()
    }

}

