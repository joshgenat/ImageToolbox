package ru.tech.imageresizershrinker.presentation.single_edit_screen.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.PhotoFilter
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.presentation.filters_screen.components.AddFiltersSheet
import ru.tech.imageresizershrinker.presentation.filters_screen.components.FilterItem
import ru.tech.imageresizershrinker.presentation.filters_screen.components.FilterReorderSheet
import ru.tech.imageresizershrinker.presentation.root.theme.mixedColor
import ru.tech.imageresizershrinker.presentation.root.theme.onMixedColor
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.transformation.filter.FilterTransformation
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.presentation.root.widget.image.Picture
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterEditOption(
    visible: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    bitmap: Bitmap?,
    onGetBitmap: (Bitmap) -> Unit,
    imageManager: ImageManager<Bitmap, ExifInterface>,
    filterList: List<FilterTransformation<*>>,
    updateFilter: (Any, Int, (Throwable) -> Unit) -> Unit,
    removeAt: (Int) -> Unit,
    addFilter: (FilterTransformation<*>) -> Unit,
    updateOrder: (List<FilterTransformation<*>>) -> Unit
) {
    val scope = rememberCoroutineScope()
    val settingsState = LocalSettingsState.current
    val toastHostState = LocalToastHost.current
    val context = LocalContext.current
    bitmap?.let {
        val showFilterSheet = rememberSaveable { mutableStateOf(false) }
        val showReorderSheet = rememberSaveable { mutableStateOf(false) }

        var stateBitmap by remember(bitmap) { mutableStateOf(bitmap) }
        FullscreenEditOption(
            sheetSize = 0.4f,
            showControlsInScaffold = filterList.isNotEmpty(),
            canGoBack = stateBitmap != bitmap,
            visible = visible,
            onDismiss = onDismiss,
            useScaffold = useScaffold,
            controls = {
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (filterList.isNotEmpty()) {
                        Column(Modifier.block(MaterialTheme.shapes.extraLarge)) {
                            TitleItem(text = stringResource(R.string.filters))
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                filterList.forEachIndexed { index, filter ->
                                    FilterItem(
                                        filter = filter,
                                        onFilterChange = {
                                            updateFilter(
                                                it,
                                                index
                                            ) {
                                                scope.launch {
                                                    toastHostState.showError(
                                                        context,
                                                        it
                                                    )
                                                }
                                            }
                                        },
                                        onLongPress = {
                                            showReorderSheet.value = true
                                        },
                                        showDragHandle = false,
                                        onRemove = {
                                            removeAt(index)
                                        }
                                    )
                                }
                                OutlinedButton(
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.mixedColor,
                                        contentColor = MaterialTheme.colorScheme.onMixedColor
                                    ),
                                    border = BorderStroke(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant(
                                            onTopOf = MaterialTheme.colorScheme.mixedColor
                                        )
                                    ),
                                    onClick = { showFilterSheet.value = true },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ) {
                                    Icon(Icons.Rounded.PhotoFilter, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(id = R.string.add_filter))
                                }
                            }
                        }
                    } else {
                        OutlinedButton(
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.mixedColor,
                                contentColor = MaterialTheme.colorScheme.onMixedColor
                            ),
                            border = BorderStroke(
                                width = settingsState.borderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant(
                                    onTopOf = MaterialTheme.colorScheme.mixedColor
                                )
                            ),
                            onClick = { showFilterSheet.value = true },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(Icons.Rounded.PhotoFilter, null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(id = R.string.add_filter))
                        }
                    }
                }
            },
            fabButtons = {
                FloatingActionButton(
                    onClick = {
                        showFilterSheet.value = true
                    },
                    modifier = Modifier.fabBorder(),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                ) {
                    Icon(Icons.Rounded.PhotoFilter, null)
                }
            },
            actions = {},
            topAppBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Rounded.Close, null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    modifier = Modifier.drawHorizontalStroke(),
                    actions = {
                        AnimatedVisibility(visible = stateBitmap != bitmap) {
                            OutlinedIconButton(
                                colors = IconButtonDefaults.filledTonalIconButtonColors(),
                                onClick = {
                                    onGetBitmap(stateBitmap)
                                    onDismiss()
                                }
                            ) {
                                Icon(Icons.Rounded.Done, null)
                            }
                        }
                    },
                    title = {
                        Marquee(edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)) {
                            Text(
                                text = stringResource(R.string.filter),
                            )
                        }
                    }
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Picture(
                    model = bitmap,
                    shape = RectangleShape,
                    transformations = filterList,
                    onSuccess = {
                        stateBitmap = it.result.drawable.toBitmap()
                    },
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        AddFiltersSheet(
            visible = showFilterSheet,
            previewBitmap = stateBitmap,
            onFilterPicked = { addFilter(it.newInstance()) },
            onFilterPickedWithParams = { addFilter(it) },
            imageManager = imageManager
        )

        FilterReorderSheet(
            filterList = filterList,
            visible = showReorderSheet,
            updateOrder = updateOrder
        )
    }
}