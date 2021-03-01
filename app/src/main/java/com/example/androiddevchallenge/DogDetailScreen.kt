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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.primarySurface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.androiddevchallenge.network.AnimalDTO
import com.example.androiddevchallenge.network.escapeDescription
import com.example.androiddevchallenge.network.tagSet
import com.example.androiddevchallenge.ui.theme.MyTheme
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun DetailScreenLightPreview() {
    val navController = rememberNavController()
    val favs = mutableStateOf(emptySet<String>())
    MyTheme {
        DogDetailScreen(navController, demoData[0], favs) {}
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DetailcreenDarkPreview() {
    val navController = rememberNavController()
    val favs = mutableStateOf(emptySet<String>())
    MyTheme(darkTheme = true) {
        DogDetailScreen(navController, demoData[0], favs) {}
    }
}

@Composable fun DogDetailScreen(
    navController: NavHostController,
    dog: AnimalDTO,
    favorites: MutableState<Set<String>>,
    onFabClicked: (AnimalDTO) -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            ExtendedFloatingActionButton(text = { Text(text = "Adopt ${dog.name}") }, onClick = { onFabClicked(dog) })
        }
    ) {
        LazyColumn {
            item { HeaderImage(dog) }
            item { HeaderInfo(dog) }
            item {
                Text(
                    modifier = Modifier.padding(all = 8.dp),
                    text = dog.escapeDescription()
                        .ifEmpty { "No description given, check out the adoption site anyway?" },
                    style = MaterialTheme.typography.body1
                )
            }
            items(dog.photos.asReversed()) {
                CoilImage(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    data = it.full,
                    contentDescription = "Image of ${dog.name}",
                )
            }
        }

        PuppyDetailToolbar(navController, dog, favorites)
    }
}

@Composable
private fun PuppyDetailToolbar(
    navController: NavHostController,
    dog: AnimalDTO,
    favorites: MutableState<Set<String>>
) {
    Column {
        val color = Color.Black.copy(alpha = 0.3f)
        Spacer(
            Modifier
                .background(color)
                .statusBarsPadding() // Match the height of the status bar
                .fillMaxWidth()
        )
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back to list")
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        val isFavorite = dog.id in favorites.value
                        favorites.value = if (isFavorite) favorites.value - dog.id else favorites.value + dog.id
                    }
                ) {
                    val icon = if (dog.id in favorites.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                    Icon(imageVector = icon, contentDescription = "Toggle favorite")
                }
            },
            backgroundColor = color,
            contentColor = Color.White,
            elevation = 0.dp
        )
    }
}

@Composable
private fun HeaderInfo(dog: AnimalDTO) {
    Surface(color = MaterialTheme.colors.primarySurface, elevation = 2.dp) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(horizontal = 8.dp)
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(text = dog.name, style = MaterialTheme.typography.h4)
            }
            TagPanel(modifier = Modifier.fillMaxWidth(), tags = dog.tagSet())
        }
    }
}

@Composable
private fun HeaderImage(dog: AnimalDTO) {
    val image = dog.photos.firstOrNull()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
    ) {
        if (image != null) {
            CoilImage(
                modifier = Modifier.matchParentSize(),
                data = image.full,
                contentDescription = "Header image of ${dog.name}",
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )
        }
    }
}
