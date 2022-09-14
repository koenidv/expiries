package de.koenidv.expiries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class EditorSheet(private val article: Article, val saveCallback: (Article) -> Unit) :
    BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sheet_edit, container, false)

        val nameEditText = view.findViewById<EditText>(R.id.name_edittext)

        nameEditText.setText(article.name)
        Glide.with(requireContext())
            .load(article.image_url)
            .into(view.findViewById(R.id.image))

        view.findViewById<MaterialButton>(R.id.save_button).setOnClickListener {
            saveCallback(
                Article(
                    article.barcode,
                    nameEditText.text.toString(),
                    null,
                    article.image_url,
                    null,
                    article.id
                )
            )
            dismiss()
        }

        return view
    }

}