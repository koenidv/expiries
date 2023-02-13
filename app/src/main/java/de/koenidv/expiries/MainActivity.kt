package de.koenidv.expiries

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
            launchEditor(null)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}