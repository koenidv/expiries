package de.koenidv.expiries

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import de.koenidv.expiries.lazyDatePicker.LazyDatePicker
import de.koenidv.expiries.lazyDatePicker.LazyLocalDatePicker
import org.threeten.bp.LocalDate

class EditorSheet(private val article: Article, val saveCallback: (Article) -> Unit) :
    BottomSheetDialogFragment() {

    private lateinit var nameEditText: EditText
    private lateinit var datePicker: LazyLocalDatePicker
    private lateinit var saveButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sheet_edit, container, false)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)

        nameEditText = view.findViewById(R.id.name_edittext)
        datePicker = view.findViewById(R.id.datepicker)
        saveButton = view.findViewById(R.id.save_button)

        Glide.with(requireContext())
            .load(article.image_url)
            .into(view.findViewById(R.id.image))

        nameEditText.setText(article.name)

        datePicker.also {
            it.setDateFormat(LazyDatePicker.DateFormat.DD_MM_YYYY)
            it.setMinLocalDate(LocalDate.now())
            it.setMaxLocalDate(LocalDate.now().plusYears(10))
            article.expiry?.let { expiry ->
                it.localDate = LocalDate.ofEpochDay(expiry.toEpochDay())
            }
        }

        datePicker.setOnLocalDateSelectedListener(object : LazyLocalDatePicker.OnLocalDateSelectedListener {
            override fun onLocalDateSelected(dateSelected: Boolean?) {
                checkValid()
            }
        })

        saveButton.setOnClickListener {
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

        checkValid()
        return view
    }

    private fun checkValid() {
        var valid = true
        if (datePicker.getDate() == null ) valid = false

        saveButton.isEnabled = valid
    }

}
