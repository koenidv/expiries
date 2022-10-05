package de.koenidv.expiries

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.jakewharton.threetenabp.AndroidThreeTen
import de.koenidv.expiries.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var db: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        db = Database.get(applicationContext)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { _ ->
            launchScanner()
        }
    }

    private fun launchScanner() {
        ScannerSheet { handleScanResult(it) }.show(supportFragmentManager, "scanner")
    }

    private fun handleScanResult(result: String?) {
        try {
            if (result === null) launchEditor(null)
            else launchEditor(ArticleParser().parseArticle(ArticleParser().parseString(result)))
        } catch (JSONException: java.lang.NullPointerException) {
            launchScanner()
        }
    }

    private fun launchEditor(article: Article?) {
        EditorSheet(article) {
            CoroutineScope(Dispatchers.IO).launch {
                db.articleDao().insert(it)
                Log.d("Database", db.articleDao().getAllSorted().toString())
            }
        }.show(supportFragmentManager, "editor")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}