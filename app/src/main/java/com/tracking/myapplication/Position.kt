package com.tracking.myapplication

import kotlin.math.round

/**
 * Integrable object used it the tracker
 *
 * @param initialState specify initial state of this object
 * */
class Position(initialState: DoubleArray) {

    var x: Double = 0.0
    var y: Double = 0.0

    var currentState: DoubleArray = initialState

    /**
     * Set x, y, z values of this object
     *
     * @param array specific values to set
     * */
    fun setValues(array: DoubleArray) {
        x = array[0]
        y = array[1]
    }

    /**
     * Get [x, y, z] values of this object
     * */
    fun getValues(): DoubleArray {
        return doubleArrayOf(x, y)
    }

    fun strX(): String {
        return (round(x * 1000.0)/1000.0).toString()
    }

    fun strY(): String {
        return (round(y * 1000.0)/1000.0).toString()
    }
}