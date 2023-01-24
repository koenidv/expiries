package de.koenidv.expiries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import de.koenidv.expiries.ui.theme.AblaufdatenTheme

@OptIn(ExperimentalMaterial3Api::class)
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AblaufdatenTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Settings") },
                            navigationIcon = {
                                // todo navigating back doesnt work
                                IconButton(onClick = { navController.navigateUp() }) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    }
                ) { padding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        BackupCard()
                    }
                }
            }
        }
    }
}

@Composable
fun BackupCard() {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            Text(text = "Backup & Restore", style = MaterialTheme.typography.labelLarge)
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Create Backup")
            }
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Restore Backup")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BackupCardPreview() {
    AblaufdatenTheme {
        BackupCard()
    }
}
