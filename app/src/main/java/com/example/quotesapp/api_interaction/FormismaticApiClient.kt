package com.example.quotesapp.api_interaction

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class FormismaticApiClient(private val contex: Context) {

    private val baseUrl = "http://api.forismatic.com/api/1.0/"
    private val key = "1"
    private val format = "json"
    private val lang = "ru"

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(contex.applicationContext)
    }

    fun getQuote(
        onSuccess: (quoteText: String) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val url = "$baseUrl?method=getQuote&key=$key&format=$format&lang=$lang"

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                val quoteText = response.getString("quoteText")
                onSuccess(quoteText)
            },
            { error ->
                onError(error.message ?: "An error occurred")
            })

        requestQueue.add(request)
    }

}