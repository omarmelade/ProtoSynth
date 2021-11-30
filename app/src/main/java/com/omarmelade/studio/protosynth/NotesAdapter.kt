package com.omarmelade.studio.protosynth

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_notes.view.*

class NotesAdapter(

    // la liste modifiable de notes
    private var notes: MutableList<Note>,
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    var selectedItems: MutableList<Int> = mutableListOf()

    // le view holder permet de gerer les elements affiché de la liste
    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        var holder = NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_notes,
                parent,
                false
            )
        )
        holder.setIsRecyclable(false)
        return holder
    }

    override fun getItemId(position: Int): Long {
        return (position * 2.2).hashCode().toLong()
    }

    fun getAllNotes(): MutableList<Note> {
        return notes
    }

    fun selectedItems(): MutableList<Int> {
        return selectedItems
    }

    private fun changeColorTxt(v: View, back: Int, txt: Int) {
        v.setBackgroundColor(back)
        v.tvNotes.setTextColor(txt)
    }

    // permet de gerer les elements du type Note affiché
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {

        var curNotes = notes[position]

        var itemV = holder.itemView


        itemV.apply {
            itemV.tvNotes.text = curNotes.note
            if (selectedItems.contains(position)) {
                changeColorTxt(itemV, Color.parseColor("#6d9f71"), Color.WHITE)
                itemV.isSelected = true
            }
        }

        itemV.setOnClickListener {
            println("before : $selectedItems")
            println( "id :" + getItemId(position))
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
                changeColorTxt(itemV, Color.WHITE, Color.parseColor("#6d9f71"))
            } else {
                selectedItems.add(position)
                changeColorTxt(itemV, Color.parseColor("#6d9f71"), Color.WHITE)
            }
            println("after : $selectedItems")
        }

        // menu contextuel sur l'item

/*        txtViewNotes.setOnCreateContextMenuListener { menu, v, menuInfo ->
                menu.add("COPY").setOnMenuItemClickListener {
                    changeColorTxt(v, Color.DKGRAY, Color.BLUE)
                    return@setOnMenuItemClickListener true
                }
                menu.add("PASTE").setOnMenuItemClickListener {
                    return@setOnMenuItemClickListener true
                }
        }*/
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}