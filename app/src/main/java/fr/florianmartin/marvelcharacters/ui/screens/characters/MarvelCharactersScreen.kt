package fr.florianmartin.marvelcharacters.ui.screens.characters

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fr.florianmartin.marvelcharacters.R
import fr.florianmartin.marvelcharacters.data.model.MarvelCharacter
import fr.florianmartin.marvelcharacters.ui.screens.ExpandableItemHeader
import fr.florianmartin.marvelcharacters.utils.constants.CARD_EXPANSION_DURATION
import fr.florianmartin.marvelcharacters.utils.constants.FIRE_EMOJI
import fr.florianmartin.marvelcharacters.utils.extenstions.parseEmoji

@Composable
fun MarvelCharactersScreen(viewModel: MarvelCharactersViewModel) {
    val context = LocalContext.current
    val characters = viewModel.characters.collectAsLazyPagingItems()

    LaunchedEffect(key1 = characters.loadState) {
        if (characters.loadState.refresh is LoadState.Error) {
            Toast.makeText(
                context,
                "Error: " + (characters.loadState.refresh as LoadState.Error).error.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (characters.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(characters.itemCount) { index ->
                    characters[index]?.let { MarvelCharacter(character = it) }
                }
                item {
                    when (characters.loadState.append) {
                        is LoadState.Error -> LoadingError(characters)
                        LoadState.Loading -> CircularProgressIndicator()
                        is LoadState.NotLoading -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingError(characters: LazyPagingItems<MarvelCharacter>) {
    Column {
        Text(text = stringResource(id = R.string.loading_error))
        Button(
            onClick = { characters.retry() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.try_again),
                color = White
            )
        }
    }
}

@Composable
fun MarvelCharacter(character: MarvelCharacter) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }

    val transition = updateTransition(transitionState, label = "transition")

    val cardElevation by transition.animateDp({
        tween(durationMillis = CARD_EXPANSION_DURATION)
    }, label = "elevationTransition") {
        if (expanded) 24.dp else 4.dp
    }

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = CARD_EXPANSION_DURATION)
    }, label = "rotationDegreeTransition") {
        if (expanded) 180f else 0f
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp,
                vertical = 8.dp
            ),
        onClick = { expanded = !expanded }
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(character.thumbnail)
                    .crossfade(true).build(),
                error = painterResource(R.drawable.ic_img_error),
                placeholder = painterResource(R.drawable.ic_loading),
                contentDescription = stringResource(R.string.character_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )
            ExpandableItemHeader(
                arrowRotationDegree = arrowRotationDegree,
                name = character.name
            )
            ExpandableItemContent(
                character = character,
                visible = expanded
            )
        }
    }
}


@Composable
fun ExpandableItemContent(
    character: MarvelCharacter,
    visible: Boolean = false,
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(CARD_EXPANSION_DURATION)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(CARD_EXPANSION_DURATION)
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(CARD_EXPANSION_DURATION)
        ) + fadeOut(
            animationSpec = tween(CARD_EXPANSION_DURATION)
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Spacer(modifier = Modifier.heightIn(15.dp))
            Text(
                text = character.description,
                textAlign = TextAlign.Left,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.heightIn(15.dp))
            Text(
                text = (stringResource(
                    R.string.appears_in_comics,
                    character.appearancesInComics
                ) + FIRE_EMOJI.parseEmoji()),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
            if (character.modifiedOn.isNotEmpty()) {
                Spacer(modifier = Modifier.heightIn(15.dp))
                Text(
                    text = stringResource(R.string.modified_on, character.modifiedOn),
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
