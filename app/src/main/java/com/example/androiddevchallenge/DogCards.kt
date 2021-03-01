/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.example.androiddevchallenge.network.AnimalDTO
import com.example.androiddevchallenge.network.escapeDescription
import com.example.androiddevchallenge.network.tagSet
import com.example.androiddevchallenge.ui.theme.MyTheme
import dev.chrisbanes.accompanist.coil.CoilImage

@Preview("Light Card - Round image", widthDp = 360, heightDp = 240)
@Composable
fun LightCard() {
    MyTheme {
        DogCardRound(demoData[0], false) {}
    }
}

@Preview("Dark Card - Round image", widthDp = 360, heightDp = 240)
@Composable
fun DarkCard() {
    MyTheme(darkTheme = true) {
        DogCardRound(demoData[0], true) {}
    }
}

@Preview("Dog Card - Stretched Image", widthDp = 360, heightDp = 240)
@Composable
fun LightCard3() {
    MyTheme {
        DogCardWrong(demoData[0], true) {}
    }
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun DogCardRound(
    dog: AnimalDTO,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    onFavoriteClicked: (AnimalDTO) -> Unit
) {
    Card(modifier = modifier, border = BorderStroke(width = 1.dp, MaterialTheme.colors.primary)) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Box(
                    Modifier
                        .size(64.dp)
                        .padding(start = 8.dp, top = 8.dp)
                ) {
                    val image = dog.photos.firstOrNull()
                    if (image != null) {
                        CoilImage(
                            modifier = Modifier.matchParentSize(),
                            data = image.small,
                            fadeIn = true,
                            contentDescription = "A picture of ${dog.name}",
                            requestBuilder = {
                                transformations(CircleCropTransformation())
                            },
                        )
                    } else {
                        Surface(
                            modifier = Modifier.matchParentSize(),
                            shape = RoundedCornerShape(CornerSize(percent = 50)),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                        ) {}
                    }
                }

                TextContentContent(
                    dog,
                    isFavorite,
                    Modifier.weight(1f),
                    onFavoriteClicked
                )
            }
            TagPanel(modifier = Modifier.fillMaxWidth(), tags = dog.tagSet())
        }
    }
}

@Composable
fun DogCardWrong(
    dog: AnimalDTO,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    onFavoriteClicked: (AnimalDTO) -> Unit
) {
    Card(modifier = modifier, elevation = 2.dp) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(64.dp)
                    .background(Color.Red)
            ) {
                // does not work with Coil image :-(
//                CoilImage(
//                    modifier = Modifier.matchParentSize(),
//                    data = dog.pictureUrl,
//                    fadeIn = true,
//                    contentDescription = "A picture of ${dog.name}",
//                    contentScale = ContentScale.FillBounds
//                )
            }
            TextContentContent(
                dog,
                isFavorite,
                Modifier.weight(1f),
                onFavoriteClicked
            )
        }
    }
}

@Composable
fun TextContentContent(
    dog: AnimalDTO,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    onFavoriteClicked: (AnimalDTO) -> Unit
) {
    Column(modifier.padding(start = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp, end = 8.dp),
                text = dog.name,
                style = MaterialTheme.typography.h6
            )
            IconButton(
                onClick = { onFavoriteClicked(dog) }
            ) {
                val icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                Icon(
                    imageVector = icon,
                    contentDescription = "Toggle favorite for ${dog.name}",
                    tint = MaterialTheme.colors.secondary
                )
            }
        }

        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = dog.escapeDescription().ifEmpty { "No description given" },
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun TagPanel(modifier: Modifier, tags: List<String>) {
    Surface(modifier = modifier, color = MaterialTheme.colors.primary) {
        Row(
            modifier = Modifier.padding(all = 8.dp).heightIn(min = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End)
        ) {
            tags.forEach {
                Surface(
                    color = MaterialTheme.colors.primary,
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(width = 1.dp, LocalContentColor.current)
                ) {
                    Text(modifier = Modifier.padding(horizontal = 4.dp), text = it)
                }
            }
        }
    }
}
