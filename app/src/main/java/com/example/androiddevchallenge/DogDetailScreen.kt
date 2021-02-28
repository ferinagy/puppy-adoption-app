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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.androiddevchallenge.ui.theme.MyTheme
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.launch

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun DetailScreenLightPreview() {
    val navController = rememberNavController()
    MyTheme {
        DogDetailScreen(navController, demoData[0])
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DetailcreenDarkPreview() {
    val navController = rememberNavController()
    MyTheme(darkTheme = true) {
        DogDetailScreen(navController, demoData[0])
    }
}

@Composable fun DogDetailScreen(navController: NavHostController, dog: Dog) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    var isFavorite by remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            ExtendedFloatingActionButton(text = { Text(text = "Adopt ${dog.name}") }, onClick = {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = "Hooray! ${dog.name} is already wagging his/her tail."
                    )
                }
            })
        }
    ) {
        Column {
            HeaderImage(dog)
            HeaderInfo(dog)
            Text(
                modifier = Modifier.padding(all = 8.dp),
                text = dog.shortDescription,
                style = MaterialTheme.typography.body1
            )
        }

        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back to list")
                }
            },
            actions = {
                IconButton(onClick = { isFavorite = !isFavorite }) {
                    val icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                    Icon(imageVector = Icons.Filled.Favorite, contentDescription = "Toggle favorite")
                }
            },
            backgroundColor = Color.Black.copy(alpha = 0.2f),
            contentColor = Color.White,
            elevation = 0.dp
        )
    }
}

@Composable
private fun HeaderInfo(dog: Dog) {
    Surface(color = MaterialTheme.colors.primary) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(text = dog.name, style = MaterialTheme.typography.h4)
            }
        }
    }
}

@Composable
private fun HeaderImage(dog: Dog) {
    CoilImage(
        data = dog.pictureUrl,
        contentDescription = "Header image of ${dog.name}",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        loading = {
            Box(modifier = Modifier.background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f)))
        },
        contentScale = ContentScale.Crop,
        alignment = Alignment.TopCenter
    )
}
