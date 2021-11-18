package com.omarmelade.studio.protosynth

import kotlin.concurrent.thread

class SoundPlayer
{
    var played = false
    private var tempo: Long = 500
    private var started = false

    fun playSound(list: MutableList<Note>): Boolean {
        // si le player ne joue pas & la liste n'est pas vide
        if(!played && list.isNotEmpty()){

            played = !played // on met jouer a True
            playStart(played) // on lance le moteur audio
            // on lance un thread non bloquant
                playList(list) // on appele playList
        }
        return true
    }

    fun playList(list: MutableList<Note>) {

        var i = 0
        val max = list.size - 1
        var pauseZero = played
        while (played && i < max) {
            var prevFreq : Double;
            var currFreq = list[ i ].frequency

            if( i < 1){
                setFreq(currFreq) // on met la frequence a jour la premiere fois
                prevFreq = currFreq
            }else{
                // frequence precedente
                prevFreq = list[ i - 1 ].frequency
            }

            if(currFreq != 0.0){
                if(!pauseZero){
                    pauseZero = true
                    playStart(pauseZero)
                }

                if(prevFreq != currFreq)
                    setFreq(list[ i ].frequency)

            }else{
                pauseZero = false
                playStart(pauseZero)
            }

            Thread.sleep(tempo)

            i++
        }
        played = false
        Thread.interrupted()
        playStart(played)
    }

    // Met a jour l'information Jouer
    fun setPlayedBool(boolean: Boolean){
        played = boolean
    }

    // Recupere l'information Jouer
    fun getPlayedBool() : Boolean{
        return played
    }

    fun getStarted() : Boolean{
        return started
    }

    // Lance ou Arrete le moteur audio
    fun playStart(boolean: Boolean) {
        playEngine(boolean)
    }

    // Lance le moteur audio
    fun startAudioEngine(){
        startEngine()
        started = true
    }

    // Detruit l'instance du moteur audio
    fun destroy(){
        stopEngine()
        started = false
    }

    /**
     * Native method to access sound streams
     */
    external fun stringFromJNI(): String
    private external fun startEngine()
    private external fun stopEngine()
    private external fun setFreq(double: Double)
    private external fun playEngine(boolean: Boolean)
    private external fun setIsSin(boolean: Boolean)


    companion object {
        // Used to load the 'protosynth' library on application startup.
        init {
            System.loadLibrary("protosynth")
        }
    }
}