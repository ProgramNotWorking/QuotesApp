package com.example.quotesapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.lifecycle.ViewModelProvider
import com.example.quotesapp.api_interaction.FormismaticApiClient
import com.example.quotesapp.databinding.ActivityMainBinding
import com.example.quotesapp.viewmodels.QuoteViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var quoteViewModel: QuoteViewModel
    private lateinit var apiClient: FormismaticApiClient

    @SuppressLint("SetTextI18n", "ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiClient = FormismaticApiClient(this@MainActivity)
        quoteViewModel = ViewModelProvider(this)[QuoteViewModel::class.java]

        binding.apply {

            quoteTextView.text = quoteViewModel.getQuoteText()

            getQuoteButton.setOnClickListener {

                quoteViewModel.getQuote(apiClient)
                quoteViewModel.quoteLiveData.observe(this@MainActivity) { quote ->
                    quoteTextView.text = quote
                    quoteViewModel.setQuoteText(quote)
                }

            }

        }

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString(QUOTE_KEY, quoteViewModel.getQuoteText())
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        if (savedInstanceState != null) {
            quoteViewModel.setQuoteText(savedInstanceState.getString(QUOTE_KEY).toString())
        }
    }

    companion object {
        const val QUOTE_KEY = "quote_key"
    }

}