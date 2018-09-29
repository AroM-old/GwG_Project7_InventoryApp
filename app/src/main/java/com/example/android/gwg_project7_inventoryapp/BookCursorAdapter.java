package com.example.android.gwg_project7_inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.gwg_project7_inventoryapp.data.BooksContract;


/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context   app context
     * @param cursor    The cursor from which to get the data. The cursor is already
     *                  moved to the correct position.
     * @param viewGroup The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView bookNameTextView = view.findViewById(R.id.name_text_view);
        TextView supplierNameTextView = view.findViewById(R.id.supplier_textview);

        // Find the columns of book attributes that we're interested in
        int bNameColumnIndex = cursor.getColumnIndex(BooksContract.BooksEntry.COLUMN_PRODUCT_NAME);
        int sNameColumnIndex = cursor.getColumnIndex(BooksContract.BooksEntry.COLUMN_SUPPLIER_NAME);

        String bookName = cursor.getString(bNameColumnIndex);
        String supplierName = cursor.getString(sNameColumnIndex);

        // If the supplier name is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(supplierName)) {
            supplierName = context.getString(R.string.unknown_breed);
        }

        // Populate fields with extracted properties
        bookNameTextView.setText(bookName);
        supplierNameTextView.setText(supplierName);


    }
}
