package com.networks

import com.networks.okhttp.OkHttpGetAsync
import com.networks.okhttp.OkHttpPostAsync


/**
 * Created by Twist Mobile on 1/5/2016.
 */
object NetworkRequest {

    fun doGet(url: String, networkListener: NetworkListener) {
        NetworkRequest.doGet(url, null, networkListener)
    }

    fun doGet(url: String, params: Map<String, String>?, networkListener: NetworkListener) {
        OkHttpGetAsync(params, networkListener).execute(url)
    }

    fun doPost(url: String, params: Map<String, String>?, networkListener: NetworkListener) {
        NetworkRequest.sendPostRequest(url, params, null, networkListener)
    }

    fun sendPostRequest(url: String, params: Map<String, String>?, headers: Map<String, String>?, networkListener: NetworkListener) {
        OkHttpPostAsync(params, headers, null, networkListener).execute(url)
    }

    fun sendPostRequest(url: String, params: Map<String, String>?, headers: Map<String, String>?, postData: String?, networkListener: NetworkListener) {
        OkHttpPostAsync(params, headers, postData, networkListener).execute(url)
    }
}