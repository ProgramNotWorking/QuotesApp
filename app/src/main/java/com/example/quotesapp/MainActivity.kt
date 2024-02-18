package com.example.quotesapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.lifecycle.ViewModelProvider
import com.example.quotesapp.api_interaction.FormismaticApiClient
import com.example.quotesapp.databinding.ActivityMainBinding
import com.example.quotesapp.dataclasses.QuoteInfo
import com.example.quotesapp.db.DatabaseHelper
import com.example.quotesapp.viewmodels.QuoteViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    companion object {
        const val QUOTE_KEY = "quote_key"
        const val QUOTE_AUTHOR_KEY = "quote_author_key"
        const val IS_RESTORING_KEY = "is_restoring"
        const val IS_FAVORITE_KEY = "is_fav_key"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var quoteViewModel: QuoteViewModel
    private lateinit var apiClient: FormismaticApiClient
    private val database = DatabaseHelper(this)

    @SuppressLint("SetTextI18n", "ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiClient = FormismaticApiClient(this@MainActivity)
        quoteViewModel = ViewModelProvider(this)[QuoteViewModel::class.java]

        renderElementsOnStartup()

        binding.apply {

            // Implement something there, like if person had add some quote to fav, show it there

            getQuoteButton.setOnClickListener {

                fetchDataAndSetText()

            }

            searchButton.setOnClickListener {

                // Some search system there

            }

            addToFavoriteButton.setOnClickListener {

                addToFavoriteButtonProcess()

            }

            menuButton.setOnClickListener {

                val intent = Intent(this@MainActivity, QuotesActivity::class.java)
                startActivity(intent)

            }

        }

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString(QUOTE_KEY, quoteViewModel.getQuoteText())
        outState.putString(QUOTE_AUTHOR_KEY, quoteViewModel.getQuoteAuthor())
        outState.putBoolean(IS_RESTORING_KEY, true)
        outState.putBoolean(IS_FAVORITE_KEY, quoteViewModel.getFavoriteState())
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        if (savedInstanceState != null) {
            quoteViewModel.setQuoteText(savedInstanceState.getString(QUOTE_KEY).toString())
            quoteViewModel.setQuoteAuthor(savedInstanceState.getString(QUOTE_AUTHOR_KEY).toString())
            quoteViewModel.setRestoringState(savedInstanceState.getBoolean(IS_RESTORING_KEY))
            quoteViewModel.setFavoriteState(savedInstanceState.getBoolean(IS_FAVORITE_KEY))
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addToFavoriteButtonProcess() = with(binding) {
        addQuoteToFavorite(
            {
                database.addQuote(
                    QuoteInfo(
                        quoteViewModel.getQuoteText(), quoteViewModel.getQuoteAuthor()
                    )
                )

                addToFavoriteButton.setImageResource(R.drawable.filled_heart_icon)
                quoteViewModel.setFavoriteState(true)

                database.close()
            }, {
                database.deleteQuote(
                    QuoteInfo(
                        quoteViewModel.getQuoteText(), quoteViewModel.getQuoteAuthor()
                    )
                )

                addToFavoriteButton.setImageResource(R.drawable.unfilled_heart_icon)
                quoteViewModel.setFavoriteState(false)

                database.close()
            }, {
                quoteViewModel.setFavoriteState(
                    addToFavoriteButton.drawable.constantState ==
                            resources.getDrawable(R.drawable.filled_heart_icon).constantState
                )

                addToFavoriteButton.invalidate()
            }
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addQuoteToFavorite(
        onFollow: () -> Unit,
        onUnfollow: () -> Unit,
        finalAction: () -> Unit
    ) = with(binding) {
        if (addToFavoriteButton.drawable.constantState ==
            resources.getDrawable(R.drawable.unfilled_heart_icon).constantState
        ) {
            onFollow()
        } else {
            onUnfollow()
        }
        finalAction()
    }

    private fun fetchDataAndSetText() = with(binding) {
        isNetworkAvailable(
            {
                quoteViewModel.getQuote(apiClient)
                quoteViewModel.quoteTextLiveData.observe(this@MainActivity) { quote ->
                    quoteTextView.text = quote
                    quoteViewModel.setQuoteText(quote)
                }
                quoteViewModel.quoteAuthorLiveData.observe(this@MainActivity) { author ->
                    quoteAuthorTextView.text = author
                    quoteViewModel.setQuoteAuthor(author)
                }
                quoteViewModel.setFavoriteState(
                    database.isQuoteExists(
                        QuoteInfo(quoteViewModel.getQuoteText(), quoteViewModel.getQuoteAuthor())
                    )
                )
                addToFavoriteButton.setImageResource( // This is temp problem solve
                    R.drawable.unfilled_heart_icon
                )
                addToFavoriteButton.invalidate()
            }, {
                Snackbar.make(
                    mainHolder, R.string.internet_connection_problems, Snackbar.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun renderElementsOnStartup() = with(binding) {
        if (!quoteViewModel.getRestoringState()) {
            fetchDataAndSetText()
        } else {
            quoteTextView.text = quoteViewModel.getQuoteText()
            quoteAuthorTextView.text = quoteViewModel.getQuoteAuthor()
            addToFavoriteButton.setImageResource(
                if (quoteViewModel.getFavoriteState())
                    R.drawable.filled_heart_icon
                else
                    R.drawable.unfilled_heart_icon
            )
            addToFavoriteButton.invalidate()
        }
    }

    private fun isNetworkAvailable(
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo?.isConnectedOrConnecting == true) onSuccess() else onError()
    }

}