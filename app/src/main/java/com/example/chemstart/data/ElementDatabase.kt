package com.example.chemstart.data
object ElementDatabase {
    data class Element(
        val atomicNumber: Int,
        val symbol: String,
        val name: String,
        val group: Int,
        val period: Int,
        val category: String, // "metal", "nonmetal", "metalloid", "noble gas"
        val state: String // "solid", "liquid", "gas" at room temp
    )

    val allElements = listOf(
        // Period 1
        Element(1, "H", "Hydrogen", 1, 1, "nonmetal", "gas"),
        Element(2, "He", "Helium", 18, 1, "noble gas", "gas"),

        // Period 2
        Element(3, "Li", "Lithium", 1, 2, "metal", "solid"),
        Element(4, "Be", "Beryllium", 2, 2, "metal", "solid"),
        Element(5, "B", "Boron", 13, 2, "metalloid", "solid"),
        Element(6, "C", "Carbon", 14, 2, "nonmetal", "solid"),
        Element(7, "N", "Nitrogen", 15, 2, "nonmetal", "gas"),
        Element(8, "O", "Oxygen", 16, 2, "nonmetal", "gas"),
        Element(9, "F", "Fluorine", 17, 2, "nonmetal", "gas"),
        Element(10, "Ne", "Neon", 18, 2, "noble gas", "gas"),

        // Period 3
        Element(11, "Na", "Sodium", 1, 3, "metal", "solid"),
        Element(12, "Mg", "Magnesium", 2, 3, "metal", "solid"),
        Element(13, "Al", "Aluminum", 13, 3, "metal", "solid"),
        Element(14, "Si", "Silicon", 14, 3, "metalloid", "solid"),
        Element(15, "P", "Phosphorus", 15, 3, "nonmetal", "solid"),
        Element(16, "S", "Sulfur", 16, 3, "nonmetal", "solid"),
        Element(17, "Cl", "Chlorine", 17, 3, "nonmetal", "gas"),
        Element(18, "Ar", "Argon", 18, 3, "noble gas", "gas"),

        // Period 4
        Element(19, "K", "Potassium", 1, 4, "metal", "solid"),
        Element(20, "Ca", "Calcium", 2, 4, "metal", "solid"),
        Element(21, "Sc", "Scandium", 3, 4, "metal", "solid"),
        Element(22, "Ti", "Titanium", 4, 4, "metal", "solid"),
        Element(23, "V", "Vanadium", 5, 4, "metal", "solid"),
        Element(24, "Cr", "Chromium", 6, 4, "metal", "solid"),
        Element(25, "Mn", "Manganese", 7, 4, "metal", "solid"),
        Element(26, "Fe", "Iron", 8, 4, "metal", "solid"),
        Element(27, "Co", "Cobalt", 9, 4, "metal", "solid"),
        Element(28, "Ni", "Nickel", 10, 4, "metal", "solid"),
        Element(29, "Cu", "Copper", 11, 4, "metal", "solid"),
        Element(30, "Zn", "Zinc", 12, 4, "metal", "solid"),
        Element(31, "Ga", "Gallium", 13, 4, "metal", "solid"),
        Element(32, "Ge", "Germanium", 14, 4, "metalloid", "solid"),
        Element(33, "As", "Arsenic", 15, 4, "metalloid", "solid"),
        Element(34, "Se", "Selenium", 16, 4, "nonmetal", "solid"),
        Element(35, "Br", "Bromine", 17, 4, "nonmetal", "liquid"),
        Element(36, "Kr", "Krypton", 18, 4, "noble gas", "gas"),

        // Period 5 (selected elements for your levels)
        Element(37, "Rb", "Rubidium", 1, 5, "metal", "solid"),
        Element(47, "Ag", "Silver", 11, 5, "metal", "solid"),
        Element(53, "I", "Iodine", 17, 5, "nonmetal", "solid"),

        // Period 6 (selected elements)
        Element(55, "Cs", "Cesium", 1, 6, "metal", "solid"),
        Element(79, "Au", "Gold", 11, 6, "metal", "solid"),

        // Period 7 (selected elements)
        Element(87, "Fr", "Francium", 1, 7, "metal", "solid")
    )

    fun getElementsForLevel(level: Int): List<Element> {
        return when (level) {
            1 -> allElements.filter { it.period <= 4 && it.atomicNumber <= 20 } // First 20 elements
            2 -> allElements.filter { it.group == 1 } // Alkali metals
            3 -> allElements.filter {
                it.symbol in listOf("Fe", "Cu", "Ag", "Au")
            } // Transition metals
            4 -> allElements.filter { it.group == 17 } // Halogens
            5 -> allElements // Full table
            else -> emptyList()
        }
    }

    fun getElementBySymbol(symbol: String): Element? {
        return allElements.firstOrNull { it.symbol == symbol }
    }

    fun getRandomElements(count: Int, level: Int): List<Element> {
        return getElementsForLevel(level).shuffled().take(count)
    }
}