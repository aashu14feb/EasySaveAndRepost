package com.networks.okhttp

import android.util.Log
import com.networks.NetworkListener
import com.networks.NetworkResponse
import com.networks.NetworkUtils
import okhttp3.*
import java.io.IOException

/**
 * Created by Vikas on 25-01-2018.
 */

class OkHttpGetRequest(private val params: Map<String, String>?, private val networkListener: NetworkListener?) {

    fun execute(url: String) {
        var url = url
        networkListener?.onRequest()

        try {
            val urlBuilder = HttpUrl.parse(url)!!.newBuilder()
            if (params != null) {
                for ((key, value) in params) {
                    urlBuilder.addQueryParameter(key, value)
                }
            }
            url = urlBuilder.build().toString()
        } catch (e: NullPointerException) {
            networkListener?.onResponse(NetworkResponse(false, "", e))
        }

        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (NetworkUtils.DEBUG) {
                    val responseHeaders = response.headers()
                    for (i in 0 until responseHeaders.size()) {
                        Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i))
                    }
                }

                if (networkListener != null) {
                    val responseBody = response.body()
                    if (response.code() == 200 && responseBody != null) {
                        networkListener.onResponse(NetworkResponse(true, responseBody.string(), null))
                    } else {
                        networkListener.onResponse(NetworkResponse(false, "", Exception()))
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                networkListener?.onResponse(NetworkResponse(false, "", e))
            }
        })
    }

    companion object {

        private val TAG = OkHttpGetRequest::class.java.simpleName
    }
}
