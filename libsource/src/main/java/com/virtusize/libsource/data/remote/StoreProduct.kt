package com.virtusize.libsource.data.remote

data class StoreProduct(
    val id: Int,
    val sizes: List<ProductSize>,
    val externalId: String,
    val productType: Int,
    val name: String,
    val storeId: Int,
    val storeProductMeta: StoreProductMeta?
)