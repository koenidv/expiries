package de.koenidv.expiries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONObject

class EditorSheet(val article: JSONObject) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sheet_edit, container, false)

        val nameEditText = view.findViewById<EditText>(R.id.name_edittext)

        nameEditText.setText(article.getJSONObject("product").getString("product_name"))
        Glide.with(requireContext())
            .load(article.getJSONObject("product").getString("image_small_url"))
            .into(view.findViewById(R.id.image))

        return view
    }

}