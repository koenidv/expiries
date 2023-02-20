package de.koenidv.expiries

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.koenidv.expiries.databinding.SheetEditorBinding
import de.koenidv.expiries.lazyDatePicker.LazyDatePicker
import de.koenidv.expiries.lazyDatePicker.LazyLocalDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


class EditorSheet(private val article: Article?, val callback: (Article?) -> Unit) :
    BottomSheetDialogFragment() {

    private var _binding: SheetEditorBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SheetEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveButton.setOnClickListener { saveArticle() }
        binding.deleteButton.setOnClickListener { deleteArticle() }
        if (article == null) binding.deleteButton.visibility = View.GONE

        binding.nameEdittext.setText(article?.name)
        if (article?.name == null) binding.nameEdittext.focusAndShowKeyboard()

        loadImage()
        setupDatePicker()

        if (article?.created_at != null && article.expiry != null) {
            // Do not show for new articles
            binding.addedOnTextview.visibility = View.VISIBLE
            binding.addedOnTextview.text = getString(
                R.string.info_added_on, article.created_at.format(
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
                )
            )
        }

        checkInputsValid()
        preventAccidentalCancel()


        CoroutineScope(Dispatchers.IO).launch {
            val previousEntries = Database.get(requireContext()).articleDao().getSuggestedNames()
            requireActivity().runOnUiThread {
                binding.nameEdittext.apply {
                    threshold = 1
                    setAdapter(
                        ArrayAdapter(
                            requireContext(),
                            R.layout.suggestion_item,
                            previousEntries
                        )
                    )
                }
            }
        }


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
        binding.datepicker.apply {
            setDateFormat(LazyDatePicker.DateFormat.DD_MM_YY)
            setMinLocalDate(minOf(LocalDate.now(), article?.expiry ?: LocalDate.now()))
            setMaxLocalDate(LocalDate.now().withYear(2099))
            autofocus = article?.expiry == null && article?.name != null

            setOnLocalDateSelectedListener(object :
                LazyLocalDatePicker.OnLocalDateSelectedListener {
                override fun onLocalDateSelected(dateSelected: Boolean?) {
                    checkInputsValid()
                }
            })

            article?.expiry?.let { expiry ->
                localDate = LocalDate.ofEpochDay(expiry.toEpochDay())
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
                if ((article?.name ?: "") == binding.nameEdittext.text.toString() &&
                    article?.expiry == binding.datepicker.localDate
                ) {
                    callback(null)
                    dismiss()
                    return@setOnClickListener
                }
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dialog_cancel_edit_title)
                    .setPositiveButton(R.string.action_yes) { _, _ ->
                        run {
                            callback(null)
                            this.dismiss()
                        }
                    }
                    .setNegativeButton(R.string.action_no) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
    }

    private fun saveArticle() {
        callback(
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

    private fun deleteArticle() {
        if (article == null) {
            dismiss()
            callback(null)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            Database.get(requireContext()).articleDao().delete(article)
            dismiss()
            callback(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
