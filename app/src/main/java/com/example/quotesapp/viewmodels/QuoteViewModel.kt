package com.example.quotesapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quotesapp.R
import com.example.quotesapp.api_interaction.FormismaticApiClient
import com.example.quotesapp.dataclasses.QuoteInfo

class QuoteViewModel : ViewModel() {

    companion object {
        val DEFAULT_VALUE = R.string.stub.toString()
        val DEFAULT_AUTHOR_VALUE = R.string.unknown_author.toString()
    }

    private var quoteInfo = QuoteInfo(
        DEFAULT_VALUE, DEFAULT_AUTHOR_VALUE
    )
    val quoteTextLiveData: MutableLiveData<String> = MutableLiveData()
    val quoteAuthorLiveData: MutableLiveData<String> = MutableLiveData()
    private var favFlag: Boolean = false
    private var isRestoring: Boolean = false
    private var isRequestInProcess: Boolean = false

    fun getRestoringState(): Boolean = isRestoring

    fun setRestoringState(flag: Boolean) {
        isRestoring = flag
    }

    fun getQuoteText(): String = quoteInfo.quoteText

    fun setQuoteText(quoteText: String) {
        quoteInfo.quoteText = quoteText
    }

    fun getQuoteAuthor(): String = quoteInfo.quoteAuthor

    fun setQuoteAuthor(quoteAuthor: String) {
        quoteInfo.quoteAuthor = quoteAuthor
    }

    fun getFavoriteState(): Boolean = favFlag

    fun setFavoriteState(flag: Boolean) {
        favFlag = flag
    }

    fun getQuote(apiClient: FormismaticApiClient) {
        if (isRequestInProcess) {
            return
        }
        isRequestInProcess = true

        isRestoring = true

        apiClient.getQuote(
            { quoteText, quoteAuthor ->
                quoteTextLiveData.postValue(quoteText)
                quoteAuthorLiveData.postValue(quoteAuthor)
                isRequestInProcess = false
            }, { error ->
                Log.d("Response error: ", error)
                isRequestInProcess = false
            }
        )
    }

}