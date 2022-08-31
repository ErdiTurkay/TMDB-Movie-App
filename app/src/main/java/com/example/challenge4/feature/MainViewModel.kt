package com.example.challenge4.feature

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.challenge4.common.Constant

class MainViewModel : ViewModel() {
    var layoutManager = MutableLiveData<LayoutEnum>()

    init {
        layoutManager.value = Constant.DEFAULT_LAYOUT
    }

    fun setLayoutCount(layout: LayoutEnum) {
        // It sets the layoutManager variable according to the LayoutEnum parameter it receives.
        when (layout) {
            LayoutEnum.GRID_2 -> {
                layoutManager.value = LayoutEnum.GRID_2
            }
            LayoutEnum.GRID_3 -> {
                layoutManager.value = LayoutEnum.GRID_3
            }
            LayoutEnum.LINEAR -> {
                layoutManager.value = LayoutEnum.LINEAR
            }
        }
    }
}

enum class LayoutEnum {
    GRID_2,
    GRID_3,
    LINEAR
}
