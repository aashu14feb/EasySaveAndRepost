package com.networks.okhttp

import android.util.Log
import com.networks.NetworkListener
import com.networks.NetworkResponse
import com.networks.NetworkUtils
import okhttp3.*
import java.io.IOException
import java.util.*

/**
 * Created by Vikas on 25-01-2018.
 */

class OkHttpPostRequest(private val params: HashMap<String, String>?, private val networkListener: NetworkListener?) {

    fun execute(url: String) {
        networkListener?.onRequest()

        val formBody = FormBody.Builder()
        if (params != null) {
            for ((key, value) in params) {
                formBody.add(key, value)
            }
        }

        val body = formBody.build()
        val request = Request.Builder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "BT.YWJjZDEyMzQ6YWJjZGVmMTIzNDU2")
                .url(url)
                .post(body)
                .build()

        val client = OkHttpClient()
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
        private val TAG = OkHttpPostRequest::class.java.simpleName
    }
}
