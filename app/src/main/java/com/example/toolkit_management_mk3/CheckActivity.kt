package com.example.toolkit_management_mk3

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.toolkit_management_mk3.models.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CheckActivity : AppCompatActivity() {
    private lateinit var listViewRedItems: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
        listViewRedItems = findViewById(R.id.listViewRedItems)

        val identifiers = listOf("A", "B", "C", "D", "E","F","G","H")  // 여러 식별자 리스트
        val allRedItems = mutableListOf<Item>()

        identifiers.forEach { identifier ->
            val prefs = getSharedPreferences("AppPrefs_$identifier", MODE_PRIVATE)
            val itemsJson = prefs.getString("items", null)
            if (!itemsJson.isNullOrEmpty()) {
                val type = object : TypeToken<List<Item>>() {}.type
                val itemsList: List<Item> = Gson().fromJson(itemsJson, type)
                val redItems = itemsList.filter { item ->
                    val colorStateJson = prefs.getString("colorState", "{}")
                    val colorStateType = object : TypeToken<Map<Int, Int>>() {}.type
                    val colorState: Map<Int, Int> = Gson().fromJson(colorStateJson, colorStateType)
                    colorState.getOrDefault(item.id, 0) == 2
                }
                allRedItems.addAll(redItems)
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, allRedItems.map { it.mainText })
        listViewRedItems.adapter = adapter
    }
}
