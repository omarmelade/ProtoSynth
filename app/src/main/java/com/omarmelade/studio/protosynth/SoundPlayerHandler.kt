package com.omarmelade.studio.protosynth

import android.widget.ImageButton

class SoundPlayerHandler(
    list: MutableList<Note>,
    playing: Boolean
) : Thread() {

    var t = Thread{
        start()
    }

    var playing = playing
    private var soundPlayer = SoundPlayer()
    var list        = list
    lateinit var btn : ImageButton

    fun setisSin(bool: Boolean){
        soundPlayer.setSinTo(bool)
    }

    fun startEngine(){
        soundPlayer.startAudioEngine()
    }

    fun stopEngine(){
        soundPlayer.destroy()
    }

    fun running(){
        try {
            t.start()
        }catch (e: IllegalThreadStateException){
            t.interrupt()
            t = Thread{
                start()
            }
            t.start()
        }
        playing = false
    }


    override fun start() {
        soundPlayer.playSound(list)
        playing = soundPlayer.played
    }


    override fun interrupt() {
        if(t.isAlive){
            soundPlayer.playStart(false)
            t.interrupt()
        }
        playing = soundPlayer.played
    }
}
