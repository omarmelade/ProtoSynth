package com.omarmelade.studio.protosynth

import android.widget.ImageButton

class SoundPlayerHandler(
    list: MutableList<Note>
) : Thread() {

    private var soundPlayer = SoundPlayer();
    var list        = list;
    lateinit var btn : ImageButton;

    fun running(){
        if(this.isAlive) {
            this.interrupt()
        }
        this.start()
    }


    override fun start() {
        soundPlayer.playSound(list)
        if (!soundPlayer.played) {
            setBtnToPlay()
        }
    }

    private fun setBtnToPlay() {
        btn.setImageResource(R.drawable.ic_forward)
    }

    override fun interrupt() {
        soundPlayer.playStart(false)
        setBtnToPlay()
    }
}
