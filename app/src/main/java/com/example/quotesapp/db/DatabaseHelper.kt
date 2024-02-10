package com.example.quotesapp.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.quotesapp.dataclasses.QuoteInfo

class DatabaseHelper(context: Context): SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "quotes.db"
        private const val TABLE_QUOTES = "quotes_table"
        private const val COLUMN_ID = "id"
        private const val COLUMN_QUOTE_TEXT = "column_quote_text"
        private const val COLUMN_QUOTE_AUTHOR = "column_quote_author"
        private const val COLUMN_IS_FAVORITE = "column_is_favorite"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_QUOTES ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_QUOTE_TEXT TEXT, $COLUMN_QUOTE_AUTHOR TEXT, $COLUMN_IS_FAVORITE INTEGER)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_QUOTES")
        onCreate(db)
    }

    fun addQuote(quote: QuoteInfo) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_QUOTE_TEXT, quote.quoteText)
        values.put(COLUMN_QUOTE_AUTHOR, quote.quoteAuthor)
        values.put(COLUMN_IS_FAVORITE, if (quote.isFavorite) 1 else 0)
        db.insert(TABLE_QUOTES, null, values)
        db.close()
    }

    fun deleteQuote(quote: QuoteInfo) {
        val db = this.writableDatabase
        db.delete(
            TABLE_QUOTES,
            "$COLUMN_ID = ?",
            null
        )
        db.close()
    }

    @SuppressLint("Recycle", "Range")
    fun getAllQuotes(): List<QuoteInfo> {
        val quotesList = mutableListOf<QuoteInfo>()
        val selectQuery = "SELECT * FROM $TABLE_QUOTES"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val quoteText = cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE_TEXT))
                val quoteAuthor = cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE_AUTHOR))
                val isFavorite = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_FAVORITE)) == 1

                val quote = QuoteInfo(
                    quoteText, quoteAuthor, isFavorite
                )
                quotesList.add(quote)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return quotesList
    }

}