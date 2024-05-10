package com.example.toolkit_management_mk3

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.toolkit_management_mk3.com.example.toolkit_management_mk3.ColorViewModel
import com.example.toolkit_management_mk3.models.Item

class DisplayDialogFragment : DialogFragment() {
    private lateinit var listView: ListView
    private var adapter: CustomItemAdapter_2? = null
    private var itemList: MutableList<Item> = mutableListOf()
    private lateinit var viewModel: ColorViewModel
    private lateinit var identifier: String  // 클래스 변수로 식별자를 저장

    companion object {
        fun newInstance(identifier: String): DisplayDialogFragment {
            val fragment = DisplayDialogFragment()
            val args = Bundle()
            args.putString("IDENTIFIER", identifier)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        identifier = arguments?.getString("IDENTIFIER") ?: "Default"

        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialogStyle)
        viewModel = ViewModelProvider(requireActivity()).get(ColorViewModel::class.java)
        viewModel.loadColorState(requireContext(), identifier)  // 식별자를 포함하여 호출
        viewModel.loadItems(requireContext(), identifier)  // 식별자를 포함하여 호출
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_display_dialog, container, false)
    }

    private fun replaceUnderscoreWithSpace(text: String): String {
        return text.replace('_', ' ')
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.listViewDisplayItems)
        adapter = CustomItemAdapter_2(requireContext(), R.layout.custom_dispactview, identifier, itemList, viewModel)
        listView.adapter = adapter

        observeViewModel()
        setupItemListeners()

        val titleTextView: TextView = view.findViewById(R.id.dialogTitle)
        titleTextView.text = replaceUnderscoreWithSpace(identifier)

        val closeButton: Button = view.findViewById(R.id.buttonClose)
        closeButton.setOnClickListener {
            dismiss()
            val intent = Intent(context, DisplayActivity::class.java)
            intent.putExtra("IDENTIFIER", identifier)
            startActivity(intent)
        }

        val editButton: Button = view.findViewById(R.id.buttonEdit)
        editButton.setOnClickListener {
            val intent = Intent(activity, PocketActivity::class.java)
            intent.putExtra("IDENTIFIER", identifier)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner) { items ->
            itemList.clear()
            itemList.addAll(items)
            adapter?.notifyDataSetChanged()
        }

        viewModel.colorState.observe(viewLifecycleOwner) { updatedColorState ->
            itemList.forEach { item ->
                item.color = updatedColorState[item.id] ?: Color.BLACK
            }
            adapter?.notifyDataSetChanged()  // 색상이 업데이트된 후 화면에 반영
        }
    }

    private fun setupItemListeners() {
        listView.setOnItemClickListener { _, _, position, _ ->
            itemList.getOrNull(position)?.let { item ->
                Log.d("DisplayDialogFragment", "Item clicked: ${item.mainText}")
                viewModel.updateColorState(requireContext(), item.id, identifier)
                adapter?.notifyDataSetChanged()
            } ?: Log.e("DisplayDialogFragment", "Error: Item at position $position is null")
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // 다이얼로그의 크기 조정 (화면의 2/3 높이, 9/10 너비)
            val metrics = resources.displayMetrics
            val width = (metrics.widthPixels * 0.8).toInt()
            val height = (metrics.heightPixels * 0.5).toInt()

            setLayout(width, height)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            setGravity(android.view.Gravity.CENTER_HORIZONTAL)

            val yOffset = (metrics.heightPixels * 0.1).toInt() * -1
            attributes = attributes.apply {
                y = yOffset
            }
            setDimAmount(0.3f)
        }
        loadData()
    }

    private fun loadData() {
        // 아이템과 색상 상태를 뷰모델에서 로드합니다.
        viewModel.loadColorState(requireContext(), identifier)
        viewModel.loadItems(requireContext(), identifier)

        // 뷰모델 관찰자를 설정합니다.
        observeViewModel()
    }
}
