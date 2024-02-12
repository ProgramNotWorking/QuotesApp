package com.example.quotesapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quotesapp.R
import com.example.quotesapp.databinding.QuoteRcviewItemBinding
import com.example.quotesapp.dataclasses.QuoteInfo

class QuoteAdapter : RecyclerView.Adapter<QuoteAdapter.QuoteHolder>() {

    private val quotesList = mutableListOf<QuoteInfo>()

    inner class QuoteHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = QuoteRcviewItemBinding.bind(view)

        fun bind(quote: QuoteInfo) = with(binding) {
            quoteTextViewRcViewItemText.text = quote.quoteText
            quoteAuthorRcViewItemText.text = quote.quoteAuthor
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.quote_rcview_item, parent, false
        )
        return QuoteHolder(view)
    }

    override fun getItemCount(): Int = quotesList.size

    override fun onBindViewHolder(holder: QuoteHolder, position: Int) {
        holder.bind(quotesList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addQuote(quote: QuoteInfo) {
        quotesList.add(quote)
        notifyDataSetChanged()
    }

}