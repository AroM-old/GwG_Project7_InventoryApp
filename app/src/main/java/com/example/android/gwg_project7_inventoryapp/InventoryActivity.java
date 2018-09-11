package com.example.android.gwg_project7_inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.android.gwg_project7_inventoryapp.data.BooksDbHelper;

import static com.example.android.gwg_project7_inventoryapp.data.BooksContract.BooksEntry;

/**
 * Displays list of books that were entered and stored in the app.
 */
public class InventoryActivity extends AppCompatActivity {

    /**
     * Database helper that will provide us access to the database
     */
    private BooksDbHelper booksDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        booksDbHelper = new BooksDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the books database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = booksDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_PRODUCT_NAME,
                BooksEntry.COLUMN_PRODUCT_PRICE,
                BooksEntry.COLUMN_PRODUCT_QUANTITY,
                BooksEntry.COLUMN_SUPPLIER_NAME,
                BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // Perform a query on the books table
        Cursor cursor = db.query(
                BooksEntry.TABLE_NAME, // The table to query
                projection,             // The columns to return
                null,           // The columns for the WHERE clause
                null,        // The values for the WHERE clause
                null,           // Don't group the rows
                null,           // Don't filter by row groups
                null,            // The sort order
                null);          // limits to display

        TextView displayView = findViewById(R.id.book_text_view);

        // Create a header in the Text View that looks like this:
        //
        // The books table contains <number of rows in Cursor> books.
        // _id - product_name - price - quantity - supplier_name - supplier_phone_number
        //
        // In the while loop below, iterate through the rows of the cursor and display
        // the information from each column in this order.

        try {
            displayView.setText("The Books inventory contains " + cursor.getCount() + " Books.\n\n");
            displayView.append(BooksEntry._ID + " - " +
                    BooksEntry.COLUMN_PRODUCT_NAME + " - " +
                    BooksEntry.COLUMN_PRODUCT_PRICE + " - " +
                    BooksEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                    BooksEntry.COLUMN_SUPPLIER_NAME + " - " +
                    BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BooksEntry._ID);
            int bookNameIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_NAME);
            int bookPriceIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_PRICE);
            int bookQuantityIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentId = cursor.getInt(idColumnIndex);
                String currentBookName = cursor.getString(bookNameIndex);
                String currentBookPrice = cursor.getString(bookPriceIndex);
                String currentBookQuantity = cursor.getString(bookQuantityIndex);
                String currentSupplierName = cursor.getString(supplierNameIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append("\n" + currentId + " - " +
                        currentBookName + " - " +
                        currentBookPrice + " - " +
                        currentBookQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhone);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
}
