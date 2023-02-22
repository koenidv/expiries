package de.koenidv.expiries

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.koenidv.expiries.databinding.SheetLocationEditBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LocationEditSheet(private val location: Location?) :
    BottomSheetDialogFragment() {

    private var _binding: SheetLocationEditBinding? = null
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
        _binding = SheetLocationEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveButton.setOnClickListener { saveArticle() }
        binding.deleteButton.setOnClickListener { deleteArticle() }
        if (location == null) binding.deleteButton.visibility = View.GONE

        binding.nameEdittext.setText(location?.name)
        if (location?.name == null) binding.nameEdittext.focusAndShowKeyboard()

        checkInputsValid()
        preventAccidentalCancel()
    }

    private fun checkInputsValid() {
        var valid = true
        if (binding.nameEdittext.text == null) valid = false
        binding.saveButton.isEnabled = valid
    }

    private fun preventAccidentalCancel() {
        dialog?.window?.decorView?.findViewById<View>(com.google.android.material.R.id.touch_outside)
            ?.setOnClickListener {
                if ((location?.name ?: "") == binding.nameEdittext.text.toString()) {
                    dismiss()
                    return@setOnClickListener
                }
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dialog_cancel_edit_title)
                    .setPositiveButton(R.string.action_yes) { _, _ ->
                        run {
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
        CoroutineScope(Dispatchers.IO).launch {
            Database.get(requireContext()).locationDao().insert(
                Location(
                    id = location?.id ?: 0,
                    name = binding.nameEdittext.text.toString(),
                    icon_name = "",
                    comment = null
                )
            )
        }
        dismiss()
    }

    private fun deleteArticle() {
        if (location == null) {
            dismiss()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            Database.get(requireContext()).locationDao().delete(location)
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
