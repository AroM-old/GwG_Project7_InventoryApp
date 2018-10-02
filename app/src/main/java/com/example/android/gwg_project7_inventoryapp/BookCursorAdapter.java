package com.example.android.gwg_project7_inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.gwg_project7_inventoryapp.data.BooksContract.BooksEntry;


/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    @BindView(R.id.name_text_view)
    TextView bookNameTextView;

    @BindView(R.id.price_textview)
    TextView bookPriceTextView;

    @BindView(R.id.quantity_textview)
    TextView bookQuantityTextView;

    @BindView(R.id.sale_btn)
    Button bookQuantitySaleButton;

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
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find the columns of book attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(BooksEntry._ID);
        int bNameColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_NAME);
        int bPriceColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_PRICE);
        int bQuantityColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_QUANTITY);

        final int id = cursor.getInt(idColumnIndex);
        String bookName = cursor.getString(bNameColumnIndex);
        final int bookPrice = cursor.getInt(bPriceColumnIndex);
        final int bQuantity = cursor.getInt(bQuantityColumnIndex);

        // Populate fields with extracted properties
        ButterKnife.bind(this, view);

        bookNameTextView.setText(bookName);
        bookPriceTextView.setText("" + bookPrice);
        bookQuantityTextView.setText("" + bQuantity);

        bookQuantitySaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InventoryActivity activity = (InventoryActivity) context;
                activity.bookSale(id, bQuantity);
            }
        });

    }
}
