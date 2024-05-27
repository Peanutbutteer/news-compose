package com.satjanut.news.data

import androidx.compose.runtime.Composable

sealed class AppState<out T> {
    data object Loading : AppState<Nothing>()
    data object Initial : AppState<Nothing>()
    data class Error(val throwable: Throwable) : AppState<Nothing>()
    data class Success<out T>(val data: T) : AppState<T>()

    @Composable
    fun IfSuccess(onSuccess: @Composable (T) -> Unit) {
        if (this is Success) {
            onSuccess(this.data)
        }
    }

    @Composable
    fun IfLoading(onLoading: @Composable () -> Unit) {
        if (this is Loading) {
            onLoading()
        }
    }

    @Composable
    fun IfError(onError: @Composable (Throwable) -> Unit) {
        if (this is Error) {
            onError(throwable)
        }
    }

    fun ifSuccess(onSuccess: (T) -> Unit) {
        if (this is Success) {
            onSuccess(this.data)
        }
    }

    fun ifError(onError: (Throwable) -> Unit) {
        if (this is Error) {
            onError(this.throwable)
        }
    }

    fun ifInitial(onInitial: () -> Unit) {
        if (this is Initial) {
            onInitial()
        }
    }
}
