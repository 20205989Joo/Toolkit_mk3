package com.example.toolkit_management_mk3.com.example.toolkit_management_mk3

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.toolkit_management_mk3.models.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ColorViewModel : ViewModel() {
    val colorState = MutableLiveData<MutableMap<Int, Int>>()
    val items = MutableLiveData<List<Item>>()
    private var identifier: String = "default"

    // 색상 상태를 로드하는 함수
    fun loadColorState(context: Context, identifier: String) {
        val prefs = context.getSharedPreferences("AppPrefs_$identifier", Context.MODE_PRIVATE)
        val colorStateJson = prefs.getString("colorState", "{}")
        if (colorStateJson.isNullOrEmpty()) {
            Log.e("ColorViewModel", "Color state JSON is null or empty for identifier: $identifier")
        } else {
            val type = object : TypeToken<MutableMap<Int, Int>>() {}.type
            colorState.value = Gson().fromJson(colorStateJson, type)
            Log.d("ColorViewModel", "Loaded color state for $identifier: ${colorState.value}")
        }
    }

    // 색상 상태를 업데이트하는 함수
    fun updateColorState(context: Context, itemId: Int, identifier: String) {
        val currentState = colorState.value ?: mutableMapOf()
        val newState = (currentState.getOrDefault(itemId, 0) + 1) % 3
        currentState[itemId] = newState
        colorState.value = currentState
        saveColorState(context, currentState, identifier)
        Log.d("ColorViewModel", "Updated color state for $identifier: $currentState")
    }

    // 변경된 색상 상태를 저장하는 함수
    private fun saveColorState(context: Context, colorState: MutableMap<Int, Int>, identifier: String) {
        val prefs = context.getSharedPreferences("AppPrefs_$identifier", Context.MODE_PRIVATE)
        prefs.edit().putString("colorState", Gson().toJson(colorState)).apply()
        Log.d("ColorViewModel", "Saved color state for $identifier")
    }

    // 아이템을 로드하는 함수
    fun loadItems(context: Context, identifier: String) {
        val prefs = context.getSharedPreferences("AppPrefs_$identifier", Context.MODE_PRIVATE)
        val itemsJson = prefs.getString("items", null)
        if (itemsJson.isNullOrEmpty()) {
            Log.e("ColorViewModel", "Items JSON is null or empty for identifier: $identifier")
        } else {
            val itemType = object : TypeToken<List<Item>>() {}.type
            items.value = Gson().fromJson(itemsJson, itemType)
            Log.d("ColorViewModel", "Loaded items for $identifier: ${items.value?.size}")
        }
    }
}
