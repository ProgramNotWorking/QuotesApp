package com.example.quotesapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import com.example.quotesapp.databinding.ActivitySearchBinding
import com.example.quotesapp.dataclasses.QuoteInfo

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    // TEMP:
    private val handler = Handler()
    private val delay = 350L
    private lateinit var runnable: Runnable

    // TEMP TOO:
    private val quotesList = mutableListOf(
        QuoteInfo("aboba", "abiba")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runnable = createRunnable()

        binding.apply {

            searchPlainTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    handler.removeCallbacks(runnable)
                    handler.postDelayed(runnable, delay)

                    if (s?.isEmpty() == true) {
                        testTextView.text = "EMPTY"
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        }

    }

    @SuppressLint("SetTextI18n")
    private fun createRunnable(): Runnable = with(binding) {
        return Runnable {
            // clean screen method here

            val keyWord = searchPlainTextView.text.toString()

            for (quote in quotesList) {
                if (
                    quote.quoteText.contains(keyWord, ignoreCase = true) ||
                    quote.quoteAuthor.contains(keyWord, ignoreCase = true)
                    ) {
                    testTextView.text = "${quote.quoteText}\n${quote.quoteAuthor}"
                } else { testTextView.text = "AMENGUS" }
            }
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