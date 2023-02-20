package de.koenidv.expiries

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
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

        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHost.navController
        appBarConfiguration = AppBarConfiguration(binding.navbar.menu)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navbar.setupWithNavController(navController)

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
            if (it == null) return@EditorSheet
            CoroutineScope(Dispatchers.IO).launch {
                db.articleDao().insert(it)
                Log.d("Database", db.articleDao().getAllSorted().toString())
            }
        }.show(supportFragmentManager, "editor")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHost.navController
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment

        (menu?.findItem(R.id.actionSearch)?.actionView as SearchView)
            .setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    (navHost.childFragmentManager.primaryNavigationFragment as ExpiriesFragment)
                        .filterRecycler(newText)
                    return false
                }
            })
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