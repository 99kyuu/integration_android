package com.virtusize.android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.virtusize.libsource.data.local.*
import com.virtusize.libsource.data.local.VirtusizeOrder
import com.virtusize.libsource.ui.FitIllustratorButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MAIN_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register message handler to listen to events from Virtusize
        (application as App)
            .Virtusize.registerMessageHandler(activityMessageHandler)

        // setup Virtusize fit illustrator button
        (application as App)
            .Virtusize
            .setupFitButton(
                fitIllustratorButton = exampleFitButton,
                virtusizeProduct = VirtusizeProduct(externalId = "694", imageUrl = "http://simage-kr.uniqlo.com/goods/31/12/11/71/414571_COL_COL02_570.jpg")
            )

        // Fit Illustrator opens automatically when button is clicked

        /*
         * To close fit illustrator use
         * exampleFitButton.dismissFitIllustratorView()
         */

        sendOrderSample()
    }

    /**
     * Demonstrates how to send an order to the Virtusize server
     */
    private fun sendOrderSample() {
        val order = VirtusizeOrder("888400111032")
        order.items = mutableListOf(
            VirtusizeOrderItem(
                "P001",
                "L",
                "Large", // sizeAlias is optional
                "P001_SIZEL_RED", // variantId is optional
                "http://images.example.com/products/P001/red/image1xl.jpg",
                "Red", // color is optional
                "W", // gender is optional
                5100.00,
                "JPY",
                1,
                "http://example.com/products/P001" // url is optional
            )
        )

        (application as App)
            .Virtusize
            .sendOrder(order,
                // this optional success callback is called when the app successfully sends the order
                onSuccess = {
                    Log.i(TAG, "Successfully sent the order")
                },
                // this optional error callback is called when an error occurs when the app is sending the order
                onError = { error ->
                    Log.e(TAG, error.message())
                })
    }

    override fun onPause() {
        // Always un register message handler in onPause() or depending on implementation onStop().
        (application as App)
            .Virtusize.unregisterMessageHandler(activityMessageHandler)
        super.onPause()
    }

    private val activityMessageHandler = object : VirtusizeMessageHandler {
        override fun virtusizeControllerShouldClose(fitIllustratorButton: FitIllustratorButton) {
            Log.i(TAG, "Close fit illustrator")
            fitIllustratorButton.dismissFitIllustratorView()
        }

        override fun onEvent(fitIllustratorButton: FitIllustratorButton?, event: VirtusizeEvents) {
            Log.i(TAG, event.getEventName())
        }

        override fun onError(fitIllustratorButton: FitIllustratorButton?, error: VirtusizeError) {
            Log.e(TAG, error.message())
        }
    }
}
