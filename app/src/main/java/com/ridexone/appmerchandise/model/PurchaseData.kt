package com.ridexone.appmerchandise.model

data class PurchaseData(
    val orderNumber: String,
    val orderDate: String,
    val name: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    val address: String
)