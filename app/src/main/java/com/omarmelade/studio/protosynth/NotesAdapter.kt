package com.omarmelade.studio.protosynth

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_notes.view.*
import java.security.AccessController.getContext

class NotesAdapter(

    // la liste modifiable de notes
    private val notes: MutableList<Note>

) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    // le view holder permet de gerer les elements affiché de la liste
    class NotesViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_notes,
                parent,
                false
            )
        )
    }


    // ajoute une note a la liste et notifie le view holder
    fun addNotes(note : Note){
        notes.add(note)
        notifyItemInserted(notes.size - 1)
    }

    fun modifySelectedItems(){
    }

    // permet de gerer les elements du type Note affiché
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val curNotes = notes[position]

        var txtViewNotes = holder.itemView.tvNotes
        var itemV = holder.itemView;

        itemV.apply {
            tvNotes.text = curNotes.note
        }

        itemV.tvNotes.setOnClickListener{
            if(itemV.isSelected){
                itemV.isSelected = false;
                itemV.setBackgroundColor(Color.WHITE)
                txtViewNotes.setTextColor(Color.GRAY)
            }else{
                itemV.isSelected = true;
                itemV.setBackgroundColor(Color.GRAY)
                txtViewNotes.setTextColor(Color.WHITE)
            }
        }

        itemV.tvNotes.setOnLongClickListener { view ->
            Toast.makeText(holder.itemView.context, "hello form long click  ", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}