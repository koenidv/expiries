package de.koenidv.expiries

import android.content.Intent
import android.os.Bundle
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
            launchRestoreFilePicker(this)
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