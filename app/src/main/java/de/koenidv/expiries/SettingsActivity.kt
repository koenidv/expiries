package de.koenidv.expiries

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import de.koenidv.expiries.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_RESTORE = 1
    }


    var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backupButton.setOnClickListener {
            shareDbFile(this)
        }

        binding.restoreButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.restore_warning_title)
                .setMessage(R.string.restore_warning_message)
                .setPositiveButton(R.string.action_continue) { _, _ -> launchRestoreFilePicker(this) }
                .setNegativeButton(R.string.action_cancel) { _, _ -> }
                .show()
        }

        if (intent?.action == Intent.ACTION_VIEW && intent?.data != null) {
            AlertDialog.Builder(this)
                .setTitle(R.string.restore_warning_title)
                .setMessage(R.string.restore_warning_message)
                .setPositiveButton(R.string.action_continue) { _, _ ->
                    restoreDbFile(
                        this,
                        intent?.data!!
                    )
                }
                .setNegativeButton(R.string.action_cancel) { _, _ -> }
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) return
        if (requestCode == REQUEST_RESTORE) {
            val uri = data?.data ?: return
            restoreDbFile(this, uri)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}