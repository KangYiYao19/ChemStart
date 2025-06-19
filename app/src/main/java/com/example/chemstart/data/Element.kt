package com.example.chemstart.data

// data/Element.kt
data class Element(
    val atomicNumber: Int,
    val symbol: String,
    val name: String,
    val group: Int,
    val period: Int,
    val category: String // "metal", "nonmetal", "metalloid"
)