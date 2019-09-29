package com.networks.okhttp

import android.os.AsyncTask
import android.util.Log
import com.networks.NetworkListener
import com.networks.NetworkResponse
import com.networks.NetworkUtils
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Created by Vikas on 24-01-2018.
 */

class OkHttpPostAsync(private val params: Map<String, String>?, private val headers: Map<String, String>?, private val postData: String?, private val networkListener: NetworkListener?) : AsyncTask<String, Void, NetworkResponse>() {

    override fun onPreExecute() {
        super.onPreExecute()
        networkListener?.onRequest()
    }

    override fun doInBackground(vararg strings: String): NetworkResponse {
        val builder = Request.Builder()
        if (headers != null) {
            headers.forEach { (key, value) -> builder.header(key, value) }
        } else {
            builder.header("Content-Type", "application/x-www-form-urlencoded")
            builder.header("Authorization", "BT.YWJjZDEyMzQ6YWJjZGVmMTIzNDU2")
        }
        builder.url(strings[0])
        builder.post(FormBody.Builder().apply {
            params?.forEach { (t, u) ->
                add(t, u)
            }
        }.build())

        val request = builder.build()
        val client = OkHttpClient()
        val call = client.newCall(request)

        try {
            val response = call.execute()
            if (NetworkUtils.DEBUG) {
                val responseHeaders = response.headers()
                for (i in 0 until responseHeaders.size()) {
                    Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i))
                }
            }

            val responseBody = response.body()
            return when {
                response.code() == 200 && responseBody != null -> {
                    val data = responseBody.string()
                    NetworkResponse(true, data, null)
                }
                else -> {
                    Log.e(TAG, "Response error code: " + response.code())
                    NetworkResponse(false, "", Exception())
                }
            }
        } catch (e: IOException) {
            return NetworkResponse(false, "", e)
        }
    }

    override fun onPostExecute(networkResponse: NetworkResponse?) {
        super.onPostExecute(networkResponse)
        networkResponse?.run {
            networkListener?.onResponse(this)
        }
    }

    companion object {
        private val TAG = OkHttpPostAsync::class.java.simpleName
    }
}