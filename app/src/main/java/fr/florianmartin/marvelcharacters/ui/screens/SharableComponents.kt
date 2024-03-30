package fr.florianmartin.marvelcharacters.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.florianmartin.marvelcharacters.R

@Composable
fun Arrow(degrees: Float) {
    Icon(
        painter = painterResource(id = R.drawable.ic_down_arrow_24),
        contentDescription = stringResource(id = R.string.expand_arrow),
        modifier = Modifier.rotate(degrees),
    )
}

@Composable
fun ExpandableItemHeader(
    arrowRotationDegree: Float,
    name: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Arrow(degrees = arrowRotationDegree)
    }
}