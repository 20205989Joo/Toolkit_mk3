package com.example.toolkit_management_mk3

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.toolkit_management_mk3.com.example.toolkit_management_mk3.ColorViewModel
import com.example.toolkit_management_mk3.models.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DisplayActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: CustomItemAdapter_2
    private var itemList = mutableListOf<Item>()
    private lateinit var viewModel: ColorViewModel
    private lateinit var identifier: String  // 식별자를 저장할 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        // 인텐트에서 식별자 가져오기
        identifier = intent.getStringExtra("IDENTIFIER") ?: "Default"

        listView = findViewById(R.id.listViewItems)
        viewModel = ViewModelProvider(this).get(ColorViewModel::class.java)
        viewModel.loadColorState(this, identifier)  // 식별자 전달
        viewModel.loadItems(this, identifier)      // 식별자 전달

        adapter = CustomItemAdapter_2(this, R.layout.display_item_layout, identifier, itemList, viewModel)
        listView.adapter = adapter

        loadData()

        viewModel.colorState.observe(this) { updatedColorState ->
            adapter.updateColorState(updatedColorState)
            adapter.notifyDataSetChanged()
        }

        val buttonEdit: Button = findViewById(R.id.buttonEdit)
        buttonEdit.setOnClickListener {
            showDisplayDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()  // 데이터를 다시 로드하여 어댑터에 최신 정보를 제공
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("AppPrefs_$identifier", MODE_PRIVATE)
        val itemsJson = sharedPreferences.getString("items", null)
        if (itemsJson != null) {
            itemList = Gson().fromJson(itemsJson, object : TypeToken<MutableList<Item>>() {}.type)
            adapter.updateItems(itemList)  // 데이터 로드 후 어댑터에 아이템 업데이트
        } else {
            itemList.clear()
            adapter.updateItems(itemList)  // 데이터가 없을 경우, 어댑터에 빈 리스트 설정
        }
        adapter.notifyDataSetChanged()
    }

    private fun showDisplayDialog() {
        val fragment = DisplayDialogFragment.newInstance(identifier)
        fragment.show(supportFragmentManager, "displayDialog_$identifier")
    }
}
