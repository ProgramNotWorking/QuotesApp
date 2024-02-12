package com.example.quotesapp

import android.database.DatabaseUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quotesapp.adapters.QuoteAdapter
import com.example.quotesapp.databinding.ActivityQuotesBinding
import com.example.quotesapp.dataclasses.QuoteInfo
import com.example.quotesapp.db.DatabaseHelper

class QuotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuotesBinding
    private val adapter = QuoteAdapter()
    private val databaseHelper = DatabaseHelper(this)
    private var favoriteQuotesList: MutableList<QuoteInfo> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            quotesRecyclerView.layoutManager = GridLayoutManager(this@QuotesActivity, 1)
            quotesRecyclerView.adapter = adapter

            // reading from database here

            readFromDatabase(
                {
                    favoriteQuotesList = databaseHelper.getAllQuotes()
                    for (quote in favoriteQuotesList) {
                        adapter.addQuote(quote)
                    }
                }, {
                    quotesRecyclerView.visibility = View.GONE
                    quotesEmptyCaseTextView.visibility = View.VISIBLE
                }
            )

        }

    }

    private fun readFromDatabase(
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        val db = databaseHelper.writableDatabase
        if (DatabaseUtils.queryNumEntries(db, DatabaseHelper.TABLE_QUOTES) > 0) {
            onSuccess()
        } else {
            onError()
        }
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}