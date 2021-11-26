package com.omarmelade.studio.protosynth

// enumeration des notes

enum class Note {

    // BLANK : nom de l'enumeration
    // frequency : la frequence jouée
    // printableName : le nom affiché dans la liste
    BLANK(0.0, "--"),
    C3(261.63, "C3"),
    C3BIS(277.18, "C#3"),
    D3(293.66, "D3"),
    D3BIS(311.13, "D#3"),
    E3(329.63, "E3"),
    F3(349.23, "F3"),
    F3BIS(369.99, "F#3"),
    G3(392.00, "G3"),
    G3BIS(415.30, "G#3"),
    A3(440.00, "A3"),
    A3BIS(466.16, "A#3"),
    B3(493.88, "B3"),

    C4(C3.freq * 2, "C4"),
    C4BIS(C3BIS.freq * 2, "C#4"),
    D4(D3.freq * 2, "D4"),
    D4BIS(D3BIS.freq *2, "D#4"),
    E4(E3.freq * 2, "E4"),
    F4(F3.freq * 2, "F4"),
    F4BIS(F3BIS.freq * 2, "F#4"),
    G4(G3.freq * 2, "G4"),
    G4BIS(G3BIS.freq * 2, "G#4"),
    A4(A3.freq * 2, "A4"),
    A4BIS(A3BIS.freq * 2, "A#4"),
    B4(B3.freq * 2, "B4"),

    C5(C4.freq * 2, "C5"),
    C5BIS(C4BIS.freq * 2, "C#5"),
    D5(D4.freq * 2, "D5"),
    D5BIS(D4BIS.freq * 2, "D#5"),
    E5(E4.freq * 2, "E5"),
    F5(F4.freq * 2, "F5"),
    F5BIS(F4BIS.freq * 2, "F#5"),
    G5(G4.freq * 2, "G5"),
    G5BIS(G4BIS.freq * 2, "G#5"),
    A5(A4.freq * 2, "A5"),
    A5BIS(A4BIS.freq * 2, "A#5"),
    B5(B4.freq * 2, "B5"),

    C6(C5.freq * 2, "C6"),
    C6BIS(C5BIS.freq * 2, "C#6"),
    D6(D5.freq * 2, "D6"),
    D6BIS(D5BIS.freq * 2, "D#6"),
    E6(E5.freq * 2, "E6"),
    F6(F5.freq * 2, "F6"),
    F6BIS(F5BIS.freq * 2, "F#6"),
    G6(G5.freq * 2, "G6"),
    G6BIS(G5BIS.freq * 2, "G#6"),
    A6(A5.freq * 2, "A6"),
    A6BIS(A5BIS.freq * 2, "A#6"),
    B6(B5.freq * 2, "B6"),

    C7(C6.freq * 2, "C7"),
    C7BIS(C6BIS.freq * 2, "C#7"),
    D7(D6.freq * 2, "D7"),
    D7BIS(D6BIS.freq * 2, "D#7"),
    E7(E6.freq * 2, "E7"),
    F7(F6.freq * 2, "F7"),
    F7BIS(F6BIS.freq * 2, "F#7"),
    G7(G6.freq * 2, "G7"),
    G7BIS(G6BIS.freq * 2, "G#7"),
    A7(A6.freq * 2, "A7"),
    A7BIS(A6BIS.freq * 2, "A#7"),
    B7(B6.freq * 2, "B7");


    var freq: Double = 0.0
    var note: String? = null

    constructor()

    constructor(
        frequency: Double,
        printableName: String
    ) {
        this.freq = frequency
        this.note = printableName
    }



    fun next(): Note {
        val values = enumValues<Note>()
        val nextOrdinal = (ordinal + 1) % values.size
        return values[nextOrdinal]
    }

    fun nextOct(): Note {
        val values = enumValues<Note>()
        val nextOrdinal = (ordinal + 12) % values.size
        println("index : "  + nextOrdinal + "value : " + values[nextOrdinal])
        return values[nextOrdinal]
    }

    fun prevOct(): Note {
        val values = enumValues<Note>()
        val nextOrdinal = (ordinal - 12 + values.size) % values.size
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