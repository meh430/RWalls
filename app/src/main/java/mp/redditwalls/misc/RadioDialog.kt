package mp.redditwalls.misc

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mp.redditwalls.R
import mp.redditwalls.repositories.SettingsItem

class RadioDialog<T : SettingsItem>(
    val context: Context,
    val title: String,
    val items: Array<T>,
    val selected: Int
) {
    private var dialog: MaterialAlertDialogBuilder =
        MaterialAlertDialogBuilder(context).setTitle(title)

    fun show(onSelect: (Int) -> Unit) {
        dialog.setAdapter(RadioAdapter()) { _, i ->
            onSelect(i)
        }.show()
    }


    inner class RadioAdapter :
        ArrayAdapter<T>(context, R.layout.dialog_radiobutton, items) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val tv = view.findViewById<CheckedTextView>(android.R.id.text1)
            val item = items[position]

            tv.text = item.displayText
            tv.isChecked = selected == position

            return view
        }
    }
}