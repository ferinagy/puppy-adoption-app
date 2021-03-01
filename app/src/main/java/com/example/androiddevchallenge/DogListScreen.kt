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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.primarySurface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.example.androiddevchallenge.network.AnimalDTO
import com.example.androiddevchallenge.network.PetFinderRepo
import com.example.androiddevchallenge.ui.theme.MyTheme
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.statusBarsHeight
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import dev.chrisbanes.accompanist.insets.toPaddingValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable fun DogListScreen(
    navController: NavHostController,
    listState: State<PetFinderRepo.State>,
    favorites: MutableState<Set<String>>
) {
    val filterFavorites = remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            PuppyListToolbar(
                filterFavorites = filterFavorites.value,
                favoritesToggled = { toolbarToggleClicked(filterFavorites, favorites, scope, scaffoldState) },
                searchClicked = { searchClicked(scope, scaffoldState) }
            )
        }
    ) {

        when (val state = listState.value) {
            PetFinderRepo.State.Loading -> Text("Loading")
            PetFinderRepo.State.Error -> Text("Error")
            is PetFinderRepo.State.Data -> {
                MainContent(state, filterFavorites, favorites, navController, scope, scaffoldState)
            }
        }
    }
}

@Composable
private fun MainContent(
    state: PetFinderRepo.State.Data,
    filterFavorites: MutableState<Boolean>,
    favorites: MutableState<Set<String>>,
    navController: NavHostController,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    val animals = state.animals

    val list = animals.filter { !filterFavorites.value || it.id in favorites.value }

    DogList(
        list = list,
        favorites = favorites.value,
        onDogClicked = { navController.navigate("puppy_detail/${it.id}") },
        onFavoriteClicked = { onFavoriteItemClicked(it, favorites, filterFavorites, scope, scaffoldState) }
    )
}

fun searchClicked(scope: CoroutineScope, scaffoldState: ScaffoldState) {
    scope.launch {
        scaffoldState.snackbarHostState.showSnackbar("Search not implemented yet <Sad puppy eyes>")
    }
}

fun toolbarToggleClicked(
    filterFavorites: MutableState<Boolean>,
    favorites: State<Set<String>>,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    val newValue = !filterFavorites.value

    if (newValue && favorites.value.isEmpty()) {
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar("You have no favorites yet")
        }
    } else {
        filterFavorites.value = newValue
    }
}

fun onFavoriteItemClicked(
    animal: AnimalDTO,
    favorites: MutableState<Set<String>>,
    filterFavorites: State<Boolean>,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    favorites.value = if (animal.id in favorites.value) {
        if (filterFavorites.value) {
            scope.launch {
                val result = scaffoldState.snackbarHostState.showSnackbar(
                    message = "${animal.name} was removed from favorites",
                    actionLabel = "Undo",
                )
                if (result == SnackbarResult.ActionPerformed) {
                    favorites.value = favorites.value + animal.id
                }
            }
        }

        favorites.value - animal.id
    } else {
        favorites.value + animal.id
    }
}

@Composable
private fun PuppyListToolbar(filterFavorites: Boolean, favoritesToggled: () -> Unit, searchClicked: () -> Unit) {
    Surface(elevation = 4.dp) {
        Column {
            Spacer(
                modifier = Modifier
                    .background(MaterialTheme.colors.primarySurface)
                    .statusBarsHeight() // Match the height of the status bar
                    .fillMaxWidth()
            )
            TopAppBar(
                elevation = 0.dp,
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
    }

}

@Composable
private fun DogList(
    list: List<AnimalDTO>,
    favorites: Set<String>,
    onDogClicked: (AnimalDTO) -> Unit,
    onFavoriteClicked: (AnimalDTO) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(list) { dog ->
            DogCardRound(
                dog = dog,
                isFavorite = dog.id in favorites,
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
    val state = mutableStateOf<PetFinderRepo.State>(PetFinderRepo.State.Data(demoData))
    val favs = mutableStateOf(emptySet<String>())
    MyTheme {
        DogListScreen(navController, state, favs)
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun ListScreenDarkPreview() {
    val navController = rememberNavController()
    val state = mutableStateOf<PetFinderRepo.State>(PetFinderRepo.State.Data(demoData))
    val favs = mutableStateOf(emptySet<String>())
    MyTheme(darkTheme = true) {
        DogListScreen(navController, state, favs)
    }
}
