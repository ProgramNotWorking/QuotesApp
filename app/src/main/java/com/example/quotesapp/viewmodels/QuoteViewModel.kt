package com.example.quotesapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quotesapp.api_interaction.FormismaticApiClient

class QuoteViewModel : ViewModel() {

    private var quoteText: String = DEFAULT_VALUE
    val quoteLiveData: MutableLiveData<String> = MutableLiveData()

    fun getQuoteText(): String = quoteText

    fun setQuoteText(text: String) {
        quoteText = text
    }

    fun getQuote(apiClient: FormismaticApiClient) {
        apiClient.getQuote(
            { text ->
                quoteLiveData.postValue(text)
            }, { error ->
                Log.d("Response error: ", error)
            }
        )
    }

    companion object {
        const val DEFAULT_VALUE = "Empty"
    }

}