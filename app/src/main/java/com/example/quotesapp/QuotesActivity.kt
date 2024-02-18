package com.example.quotesapp

import android.content.Context
import android.database.DatabaseUtils
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quotesapp.adapters.QuoteAdapter
import com.example.quotesapp.databinding.ActivityQuotesBinding
import com.example.quotesapp.dataclasses.QuoteInfo
import com.example.quotesapp.db.DatabaseHelper
import com.google.android.material.snackbar.Snackbar

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

            setDeletingListener()

            // TODO: check colors on selector (R.color.favorite_item_color.xml)
            // TODO: set deleting listener also on database (deleting from there)
            // TODO: change color on selector from red onto something different

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

    private fun setDeletingListener() = with(binding) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        quotesRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            private val gestureDetector = GestureDetectorCompat(
                this@QuotesActivity, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onLongPress(e: MotionEvent) {
                        val view = quotesRecyclerView.findChildViewUnder(e.x, e.y)

                        if (view != null) {
                            val position = quotesRecyclerView.getChildAdapterPosition(view)
                            val currentTime = System.currentTimeMillis()
                            val elapsedTime = currentTime - adapter.lastClickTime

                            if (position == adapter.lastClickedPosition && elapsedTime >= 2000) {
                                adapter.deleteQuote(position)

                                Snackbar.make(
                                    favoriteQuotesMainHolder, R.string.quote_has_deleted, Snackbar.LENGTH_LONG
                                ).show()

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(
                                        100, VibrationEffect.DEFAULT_AMPLITUDE
                                    ))
                                } else {
                                    vibrator.vibrate(100)
                                }
                            }

                        }

                        super.onLongPress(e)
                    }
                }
            )

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(e)
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

        })
    }

}