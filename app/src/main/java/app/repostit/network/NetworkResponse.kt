package com.networks

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Twist Mobile on 1/22/2016.
 */
class NetworkResponse(private val succeed: Boolean, val text: String?, val exception: Exception?) {
    private var jsonObject: JSONObject? = null
    private var errorCode = -1

    val isSucceed: Boolean
        get() = succeed && !text.isNullOrEmpty() && !hasError()

    @Throws(Exception::class)
    fun <T> getData(): T? {
        if (jsonObject == null)
            jsonObject = JSONObject(this.text)
        return jsonObject!!.get("data") as T
    }

    private fun hasError(): Boolean {
        return try {
            if (jsonObject == null)
                jsonObject = JSONObject(text)
            val result = jsonObject!!.getString("result")
            errorCode = jsonObject!!.getInt("error")
            !(result.equals("success", true) && errorCode == 0)
        } catch (ex: JSONException) {
            ex.printStackTrace()
            jsonObject = null
            true
        }
    }

    fun getErrorCode(): Int {
        if (errorCode == -1)
            hasError()
        return errorCode
    }
}

