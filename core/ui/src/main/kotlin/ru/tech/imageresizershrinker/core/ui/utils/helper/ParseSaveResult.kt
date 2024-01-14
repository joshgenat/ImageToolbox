package ru.tech.imageresizershrinker.core.ui.utils.helper

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.ReviewHandler.showReview
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showError

fun parseSaveResult(
    saveResult: SaveResult,
    onSuccess: suspend () -> Unit,
    toastHostState: ToastHostState,
    context: Context,
    scope: CoroutineScope
) {
    when (saveResult) {
        is SaveResult.Error.Exception -> {
            scope.launch {
                toastHostState.showError(context, saveResult.throwable)
            }
        }

        is SaveResult.Success -> {
            saveResult.message?.let {
                scope.launch {
                    toastHostState.showToast(
                        message = it,
                        icon = Icons.Rounded.Save,
                        duration = ToastDuration.Long
                    )
                }
            }
            scope.launch { onSuccess() }
            showReview(context)
        }

        SaveResult.Error.MissingPermissions -> Unit //Requesting permissions does FileController
    }
}