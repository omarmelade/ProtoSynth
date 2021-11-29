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

/*        if(list[0].note != "--"){
            if(this.isAlive) {
                this.interrupt()
            }
            t.start()
            if (t.state == State.TERMINATED){
                playing = false
                btn.setImageResource(R.drawable.ic_forward)
            }
        }*/
        if(this.isAlive) {
            this.interrupt()
        }
        this.start()
    }


    override fun start() {
        soundPlayer.playSound(list)
        playing = soundPlayer.played
    }


    override fun interrupt() {
/*        if(t.isAlive){*/
            soundPlayer.playStart(false)
/*            t.join()
        }*/
        playing = soundPlayer.played
    }
}
