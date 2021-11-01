package com.omarmelade.studio.protosynth

import android.content.Context
import android.content.SharedPreferences
import android.widget.ArrayAdapter
import android.widget.Toast
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

fun saveInSharedPreferences(sharedPreferences: SharedPreferences, flist: FreqList, applicationContext: Context){
    val strFList = flist.export();
    var save_toast = "Echec de la sauvegarde"

    if(strFList.isNotEmpty()){
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putString("tab",strFList)
        editor.apply()
        editor.commit()
        save_toast ="Sauvegarde effectué"
    }

    Toast.makeText(applicationContext, save_toast, Toast.LENGTH_SHORT).show()

}

fun loadInSharedPreferences(sharedPreferences: SharedPreferences, flist: FreqList, applicationContext: Context, adapter: ArrayAdapter<Double>){

    var load_toast = "Echec du chargement"
    if(sharedPreferences.getString("tab", "").toString() != "") {
        val str: String = sharedPreferences.getString("tab", "").toString();
        val strFList = import(str)
        adapter.addAll(strFList.list)
        adapter.notifyDataSetChanged()

        if (!adapter.isEmpty) {
            load_toast = "Chargement reussi"
        }
        Toast.makeText(applicationContext, load_toast, Toast.LENGTH_SHORT).show()
    }else{
        Toast.makeText(applicationContext, load_toast, Toast.LENGTH_SHORT).show()
    }

}

