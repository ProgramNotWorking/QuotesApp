package com.example.quotesapp.api_interaction

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class FormismaticApiClient(private val context: Context) {

    companion object {
        const val QUOTE_TEXT_KEY = "quoteText"
        const val QUOTE_AUTHOR_KEY = "quoteAuthor"
    }

    private val baseUrl = "http://api.forismatic.com/api/1.0/"
    private val key = "1"
    private val format = "json"
    private val lang = "ru"

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun getQuote(
        onSuccess: (quoteText: String, quoteAuthor: String) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val url = "$baseUrl?method=getQuote&key=$key&format=$format&lang=$lang"

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                val quoteText = response.getString(QUOTE_TEXT_KEY)
                val quoteAuthor = response.getString(QUOTE_AUTHOR_KEY)
                onSuccess(quoteText, quoteAuthor)
            },
            { error ->
                onError(error.message ?: "An error occurred")
            })

        requestQueue.add(request)
    }

}