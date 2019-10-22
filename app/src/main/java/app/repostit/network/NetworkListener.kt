package com.networks

/**
 * Created by Vikas on 07-02-2018.
 */
interface NetworkListener {
    fun onRequest()

    fun onResponse(response: NetworkResponse)
}
