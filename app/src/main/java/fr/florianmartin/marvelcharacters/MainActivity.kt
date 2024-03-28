package fr.florianmartin.marvelcharacters

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import fr.florianmartin.marvelcharacters.data.local.AppDatabase
import fr.florianmartin.marvelcharacters.data.remote.service.MarvelApi
import fr.florianmartin.marvelcharacters.data.repository.MarvelCharactersRepository
import fr.florianmartin.marvelcharacters.ui.theme.MarvelCharactersTheme
import fr.florianmartin.marvelcharacters.ui.theme.screens.MarvelCharactersScreen
import fr.florianmartin.marvelcharacters.ui.theme.screens.MarvelCharactersViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MarvelCharactersViewModel by viewModels {
            val appDatabase = AppDatabase.getAppDatabase(application)
            val marvelApiService = MarvelApi.retrofitService
            val repository = MarvelCharactersRepository(appDatabase, marvelApiService)
            MarvelCharactersViewModel.provideFactory(repository)
        }
        setContent {
            MarvelCharactersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    MarvelCharactersScreen(
                        viewModel = viewModel,
                        context = context
                    )
                }
            }
        }
    }
}