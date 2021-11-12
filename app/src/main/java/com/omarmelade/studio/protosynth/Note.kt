package com.omarmelade.studio.protosynth

// enumeration des notes

enum class Note {

    // BLANK : nom de l'enumeration
    // frequency : la frequence jouée
    // printableName : le nom affiché dans la liste
    BLANK(0.0, "--"),
    C3(261.63, "C3"),
    C3BIS(277.18, "C#3"),
    D3(293.66, "D3");



    var frequency: Double = 0.0
    var note: String? = null

    constructor()

    constructor(
        frequency: Double,
        printableName: String
    ) {
        this.frequency = frequency
        this.note = printableName
    }



    fun next(): Note {
        val values = enumValues<Note>()
        val nextOrdinal = (ordinal + 1) % values.size
        return values[nextOrdinal]
    }

    fun prev(): Note {
        val values = enumValues<Note>()
        val nextOrdinal = (ordinal - 1 + values.size) % values.size
        return values[nextOrdinal]
    }

    companion object {
        fun getNumberOfNotes() = values().size

        fun getNote(name : String): Note {
            for (v in values())
                if(v.note.equals(name))
                    return v
            return BLANK
        }

        private var lastNoteId = 0
        fun createNoteList(numNotes: Int): MutableList<Note> {
            val notes = mutableListOf<Note>()
            for (i in 1..numNotes) {
                notes.add(BLANK)
            }
            return notes
        }
    }

}