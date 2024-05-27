package com.satjanut.news.ui.home.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satjanut.news.data.AppState
import com.satjanut.news.data.model.Section
import com.satjanut.news.data.repository.MasterDataRepository
import com.satjanut.news.ui.newslist.OnNewsItemClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiModel(
    val sections: List<Section> = listOf(),
    var onNewsItemClick: OnNewsItemClick? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val masterDataRepository: MasterDataRepository
) : ViewModel(), DefaultLifecycleObserver {
    private val _uiModel: MutableStateFlow<AppState<HomeUiModel>> = MutableStateFlow(AppState.Initial)
    val uiModel: StateFlow<AppState<HomeUiModel>> = _uiModel

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        getMetadata()
    }

    private fun getMetadata() {
        runCatching {
            viewModelScope.launch {
                val sections = masterDataRepository.getSections()

                _uiModel.value = AppState.Success(
                    data = HomeUiModel(
                        sections = sections.toMutableList()
                    )
                )
            }
        }.getOrElse {
            _uiModel.value = AppState.Error(it)
        }
    }
}
