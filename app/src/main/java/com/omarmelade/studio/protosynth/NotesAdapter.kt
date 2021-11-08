package com.omarmelade.studio.protosynth

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_notes.view.*

class NotesAdapter(

    // la liste modifiable de notes
    private var notes: MutableList<Note>

) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private var selectedItems : MutableList<Int> = mutableListOf()

    // le view holder permet de gerer les elements affiché de la liste
    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_notes,
                parent,
                false
            )
        )
    }

    fun getAllNotes(): MutableList<Note>{
        return notes
    }

    fun selectedItems(): MutableList<Int> {
        return selectedItems
    }

    // ajoute une note a la liste et notifie le view holder
    fun addNotes(note: Note) {
        notes.add(note)
        notifyItemInserted(notes.size - 1)
    }

    private fun changeColorTxt(v : View, back : Int, txt : Int){
        v.setBackgroundColor(back)
        v.tvNotes.setTextColor(txt)
    }

    // permet de gerer les elements du type Note affiché
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        var curNotes = notes[position]

        var txtViewNotes = holder.itemView.tvNotes
        var itemV = holder.itemView

        itemV.apply {
            txtViewNotes.text = curNotes.note
        }

        txtViewNotes.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
                changeColorTxt(itemV, Color.WHITE, Color.GRAY)
            } else {
                selectedItems.add(position)
                changeColorTxt(itemV, Color.GRAY, Color.WHITE)
            }
            println("click")
        }

        txtViewNotes.setOnLongClickListener { view ->
            Toast.makeText(holder.itemView.context, "hello form long click  ", Toast.LENGTH_SHORT)
                .show()
            return@setOnLongClickListener true
        }
    }



    override fun getItemCount(): Int {
        return notes.size
    }
}