package com.omarmelade.studio.protosynth

// enumeration des notes

enum class Note {

    // BLANK : nom de l'enumeration
    // frequency : la frequence jouée
    // printableName : le nom affiché dans la liste
    BLANK(0.0, "--"),
    C3(261.63, "C3"),
    CBIS3(277.18, "C#3"),
    D3(293.66, "D3");


    var frequency : Double? = null
    var note : String? = null

    constructor()

    constructor(
        frequency: Double,
        printableName: String
    ) {
        this.frequency = frequency
        this.note = printableName
    }

    companion object {
        fun getNumberOfNotes() = values().size
    }
}