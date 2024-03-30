package fr.florianmartin.marvelcharacters

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import fr.florianmartin.marvelcharacters.data.local.AppDatabase
import fr.florianmartin.marvelcharacters.data.remote.service.MarvelApi
import fr.florianmartin.marvelcharacters.data.repository.MarvelCharactersRepository
import fr.florianmartin.marvelcharacters.ui.theme.MarvelCharactersTheme
import fr.florianmartin.marvelcharacters.ui.screens.characters.MarvelCharactersScreen
import fr.florianmartin.marvelcharacters.ui.screens.characters.MarvelCharactersViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MarvelCharactersViewModel by viewModels {
            val appDatabase = AppDatabase.getAppDatabase(application)
            val marvelApiService = MarvelApi.retrofitService
            val repository = MarvelCharactersRepository(appDatabase, marvelApiService)
            MarvelCharactersViewModel.providerFactory(repository)
        }
        setContent {
            MarvelCharactersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val windowSizeClass = calculateWindowSizeClass(this)
                    MarvelCharactersScreen(
                        viewModel = viewModel,
                        windowSizeClass = windowSizeClass
                    )
                }
            }
        }
    }
}