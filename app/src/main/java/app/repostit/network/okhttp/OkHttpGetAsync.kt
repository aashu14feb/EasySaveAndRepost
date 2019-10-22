package com.networks.okhttp

import android.os.AsyncTask
import android.util.Log
import com.networks.NetworkListener
import com.networks.NetworkResponse
import com.networks.NetworkUtils
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Created by Vikas on 24-01-2018.
 */

class OkHttpGetAsync(private val params: Map<String, String>?, private val networkListener: NetworkListener?) : AsyncTask<String, Void, NetworkResponse>() {

    override fun onPreExecute() {
        super.onPreExecute()
        networkListener?.onRequest()
    }

    override fun doInBackground(vararg strings: String): NetworkResponse {
        var url = strings[0]
        val urlBuilder: HttpUrl.Builder
        try {
            urlBuilder = HttpUrl.parse(url)!!.newBuilder()
            params?.forEach { (key, value) ->
                urlBuilder.addQueryParameter(key, value)
            }
            url = urlBuilder.build().toString()
        } catch (e: NullPointerException) {
            return NetworkResponse(false, "", e)
        }

        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .build()
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
            return if (response.code() == 200 && responseBody != null) {
                NetworkResponse(true, responseBody.string(), null)
            } else {
                NetworkResponse(false, "", Exception())
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
        private val TAG = OkHttpGetAsync::class.java.simpleName
    }
}
