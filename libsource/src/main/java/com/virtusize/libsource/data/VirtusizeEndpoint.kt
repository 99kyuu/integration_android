package com.virtusize.libsource.data

/**
 * This enum represents all available Virtusize endpoints
 */
enum class VirtusizeEndpoint {
    ProductCheck,
    FitIllustrator,
    ProductMetaDataHints,
    Events
}

/**
 * This method returns url corresponding to the VirtusizeEndpoint that it is called upon
 * @return Virtusize Endpoint URL
 */
fun VirtusizeEndpoint.getUrl(): String {
     return when(this) {
        VirtusizeEndpoint.ProductCheck -> {
            "/integration/v3/product-data-check"
        }
        VirtusizeEndpoint.FitIllustrator -> {
            "/a/fit-illustrator/v1/index.html"
        }
         VirtusizeEndpoint.ProductMetaDataHints -> {
             "/rest-api/v1/product-meta-data-hints"
         }
         VirtusizeEndpoint.Events -> {
             "/a/api/v3/events"
         }
    }
}