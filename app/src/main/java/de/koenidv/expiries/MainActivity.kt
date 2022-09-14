package de.koenidv.expiries

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.room.Room
import de.koenidv.expiries.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        db = Room.databaseBuilder(applicationContext, Database::class.java, "database").build()

        CoroutineScope(Dispatchers.IO).launch {
            binding.root.findViewById<TextView>(R.id.debugtext).text =
                db.articleDao().getAll().toString()
            Log.d("Database", db.articleDao().getAll().toString())
        }

        binding.fab.setOnClickListener { _ ->
            launchScanner()
        }
    }

    private fun launchScanner() {
        ScannerSheet { handleScanResult(it) }.show(supportFragmentManager, "scanner")
    }

    private fun handleScanResult(result: JSONObject) {
        try {
            launchEditor(JsonParser().parseArticle(result))
        } catch (JSONException: JSONException) {
            launchScanner()
        }
    }

    private fun launchEditor(article: Article) {
        EditorSheet(article) {
            CoroutineScope(Dispatchers.IO).launch {
                db.articleDao().insert(it)
                Log.d("Database", db.articleDao().getAll().toString())
            }
        }.show(supportFragmentManager, "editor")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}