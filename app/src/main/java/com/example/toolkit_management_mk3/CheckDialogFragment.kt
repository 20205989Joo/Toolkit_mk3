import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.example.toolkit_management_mk3.R
import com.example.toolkit_management_mk3.models.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class CheckDialogFragment : DialogFragment() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    companion object {
        fun newInstance() = CheckDialogFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_check_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.listViewRedItems)
        adapter = ArrayAdapter(requireContext(), R.layout.custom_textview, R.id.customTextView, mutableListOf<String>())
        listView.adapter = adapter

        loadAllRedItems()
    }

    private fun loadAllRedItems() {
        val allRedItems = mutableListOf<String>()
        val sharedPrefsDir = File(requireActivity().applicationInfo.dataDir, "shared_prefs")
        val prefsFiles = sharedPrefsDir.listFiles { file -> file.name.endsWith(".xml") } ?: arrayOf()

        for (prefsFile in prefsFiles) {
            val prefsName = prefsFile.name.removeSuffix(".xml")
            val prefs = requireActivity().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
            val itemsJson = prefs.getString("items", null)
            val type = object : TypeToken<List<Item>>() {}.type
            val itemsList = Gson().fromJson<List<Item>>(itemsJson, type) ?: listOf()
            val colorStateJson = prefs.getString("colorState", "{}")
            val colorStateType = object : TypeToken<Map<Int, Int>>() {}.type
            val colorState = Gson().fromJson<Map<Int, Int>>(colorStateJson, colorStateType) ?: mapOf()

            val redItems = itemsList.filter { item -> colorState.getOrDefault(item.id, 0) == 2 }
            allRedItems.addAll(redItems.map { it.mainText })
        }

        adapter.clear()
        adapter.addAll(allRedItems)
        adapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}
