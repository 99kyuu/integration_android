package com.virtusize.libsource.model

import com.virtusize.libsource.ui.FitIllustratorButton

interface VirtusizeMessageHandler {

    fun virtusizeControllerShouldClose(fitIllustratorButton: FitIllustratorButton)

    fun onEvent(fitIllustratorButton: FitIllustratorButton, event: VirtusizeEvents)

    fun onError(fitIllustratorButton: FitIllustratorButton, error: VirtusizeError)

}