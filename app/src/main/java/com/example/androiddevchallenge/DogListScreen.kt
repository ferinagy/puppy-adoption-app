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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.launch

@Composable fun DogListScreen(dogs: List<Dog>, navController: NavHostController) {
    var favorites by remember { mutableStateOf(emptySet<Dog>()) }
    var filterFavorites by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    fun toolbarToggleClicked() {
        val newValue = !filterFavorites

        if (newValue && favorites.isEmpty()) {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar("You have no favorites yet")
            }
        } else {
            filterFavorites = newValue
        }
    }

    fun searchClicked() {
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar("Search not implemented yet <Sad puppy eyes>")
        }
    }

    fun onFavoriteItemClicked(it: Dog) {
        favorites = if (it in favorites) {
            if (filterFavorites) {
                scope.launch {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = "${it.name} was removed from favorites",
                        actionLabel = "Undo",
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        favorites = favorites + it
                    }
                }
            }

            favorites - it
        } else {
            favorites + it
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            PuppyListToolbar(
                filterFavorites = filterFavorites,
                favoritesToggled = ::toolbarToggleClicked,
                searchClicked = ::searchClicked
            )
        }
    ) {
        val list = dogs.filter { !filterFavorites || it in favorites }

        DogList(
            list = list,
            favorites = favorites,
            onDogClicked = { navController.navigate("puppy_detail/${it.id}") },
            onFavoriteClicked = ::onFavoriteItemClicked
        )
    }
}

@Composable
private fun PuppyListToolbar(filterFavorites: Boolean, favoritesToggled: () -> Unit, searchClicked: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Puppy adopter") },
        actions = {
            IconButton(onClick = searchClicked) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
            }
            IconButton(onClick = favoritesToggled) {
                val icon = if (filterFavorites) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                Icon(imageVector = icon, contentDescription = "Toggle favorites")
            }
        }
    )
}

@Composable
private fun DogList(
    list: List<Dog>,
    favorites: Set<Dog>,
    onDogClicked: (Dog) -> Unit,
    onFavoriteClicked: (Dog) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(list) { dog ->
            DogCardRound(
                dog = dog,
                isFavorite = dog in favorites,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDogClicked(dog) },
                onFavoriteClicked = onFavoriteClicked
            )
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun ListScreenLightPreview() {
    val navController = rememberNavController()
    MyTheme {
        DogListScreen(demoData, navController)
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun ListScreenDarkPreview() {
    val navController = rememberNavController()
    MyTheme(darkTheme = true) {
        DogListScreen(demoData, navController)
    }
}
