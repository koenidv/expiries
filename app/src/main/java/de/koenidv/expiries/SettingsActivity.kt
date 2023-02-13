package de.koenidv.expiries

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.koenidv.expiries.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backupButton.setOnClickListener {
            shareDbFile(this)
        }
    }
}