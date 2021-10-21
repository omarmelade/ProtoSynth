package com.omarmelade.studio.protosynth

import android.widget.ArrayAdapter
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class FreqList (var list: MutableList<Double>){



    fun export(): String {
        val data = FreqList(list)
        return Json.encodeToString(data)
    }


}

fun import(json:String): FreqList {
    return Json.decodeFromString(json);
}

