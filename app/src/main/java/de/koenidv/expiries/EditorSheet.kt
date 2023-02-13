package de.koenidv.expiries

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.koenidv.expiries.databinding.SheetEditorBinding
import de.koenidv.expiries.lazyDatePicker.LazyDatePicker
import de.koenidv.expiries.lazyDatePicker.LazyLocalDatePicker
import org.threeten.bp.LocalDate

class EditorSheet(private val article: Article?, val saveCallback: (Article) -> Unit) :
    BottomSheetDialogFragment() {

    private var _binding: SheetEditorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SheetEditorBinding.inflate(inflater, container, false)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameEdittext.setText(article?.name)
        loadImage()
        setupDatePicker()
        binding.saveButton.setOnClickListener { saveArticle() }

        checkInputsValid()
        preventAccidentalCancel()
    }

    private fun loadImage() {
        if (article?.image_url.isNullOrEmpty()) {
            binding.image.visibility = View.GONE
        } else {
            Glide.with(requireContext())
                .load(article?.image_url)
                .into(binding.image)
        }
    }

    private fun setupDatePicker() {
        binding.datepicker.also {
            it.setDateFormat(LazyDatePicker.DateFormat.DD_MM_YYYY)
            it.setMinLocalDate(LocalDate.now())
            it.setMaxLocalDate(LocalDate.now().plusYears(10))

            it.setOnLocalDateSelectedListener(object :
                LazyLocalDatePicker.OnLocalDateSelectedListener {
                override fun onLocalDateSelected(dateSelected: Boolean?) {
                    checkInputsValid()
                }
            })

            article?.expiry?.let { expiry ->
                it.localDate = LocalDate.ofEpochDay(expiry.toEpochDay())
            }
        }
    }

    private fun checkInputsValid() {
        var valid = true
        if (binding.datepicker.getDate() == null) valid = false
        binding.saveButton.isEnabled = valid
    }

    private fun preventAccidentalCancel() {
        dialog?.window?.decorView?.findViewById<View>(com.google.android.material.R.id.touch_outside)
            ?.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dialog_cancel_edit_title)
                    .setPositiveButton(R.string.action_yes) { _, _ -> this.dismiss() }
                    .setNegativeButton(R.string.action_no) { dialog, _ -> dialog.dismiss() }
                    .show()
            }
    }

    private fun saveArticle() {
        saveCallback(
            Article(
                article?.barcode,
                binding.nameEdittext.text.toString(),
                binding.datepicker.localDate,
                article?.image_url,
                null,
                article?.created_at ?: LocalDate.now(),
                article?.amount,
                article?.unit,
                article?.id
            )
        )
        dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
