package com.omarmelade.studio.protosynth

import kotlin.concurrent.thread

class SoundPlayer
{
    var played = false
    private var tempo: Long = 1000

    fun playSound(list: MutableList<Note>) {
        played = !played
        playStart(played)
        if(list.isNotEmpty()) {
            thread(start = true, name = "playThread") {
                playList(list)
            }
        }
    }

    fun playList(list: MutableList<Note>) {
        System.err.println(list)

        var i = 0
        val max = list.size
        var pauseZero = played
        while (played) {
            val index = i % max

            if(list[ index ].frequency != 0.0){
                if(!pauseZero){
                    pauseZero = true
                    playStart(pauseZero)
                }
                setFreq(list[ index ].frequency)
            }else{
                pauseZero = false
                playStart(pauseZero)
            }

            Thread.sleep(tempo)

            i++
        }
        played = false
        playStart(played)
    }

    fun setPlayedBool(boolean: Boolean){
        played = boolean
    }

    fun getPlayedBool() : Boolean{
        return played
    }

    fun playStart(boolean: Boolean) {
        playEngine(boolean)
    }

    fun startAudioEngine(){
        startEngine()
    }

    fun destroy(){
        stopEngine()
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