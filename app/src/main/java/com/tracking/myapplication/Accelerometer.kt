package com.tracking.myapplication

/**
 * Integrable object used it the tracker
 *
 * @param initialState specify initial state of this object
 * */
class Accelerometer(initialState: DoubleArray) {

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

}