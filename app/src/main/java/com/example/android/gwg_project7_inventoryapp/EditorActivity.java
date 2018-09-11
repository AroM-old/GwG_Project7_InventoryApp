package com.example.android.gwg_project7_inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.gwg_project7_inventoryapp.data.BooksDbHelper;

import static com.example.android.gwg_project7_inventoryapp.data.BooksContract.BooksEntry;

public class EditorActivity extends AppCompatActivity {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    /**
     * EditText field to enter the book's name
     */
    private EditText mBookNameEditText;

    /**
     * EditText field to enter the book's price
     */
    private EditText mBookPriceEditText;

    /**
     * EditText field to enter the book's quantity
     */
    private EditText mBookQuantityEditText;

    /**
     * EditText field to enter the supplier's name
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter the supplier's phone number
     */
    private EditText mSuppplierPhoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mBookNameEditText = findViewById(R.id.book_name_edit_text);
        mBookPriceEditText = findViewById(R.id.book_price_edit_text);
        mBookQuantityEditText = findViewById(R.id.book_quantity_edit_text);
        mSupplierNameEditText = findViewById(R.id.supplier_name_edit_text);
        mSuppplierPhoneNumberEditText = findViewById(R.id.supplier_phone_edit_text);
    }

    /**
     * Get user input from editor and save new book into database.
     */
    public void insertBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String bookNameString = mBookNameEditText.getText().toString().trim();
        String bookPriceString = mBookPriceEditText.getText().toString().trim();
        String bookQuantityString = mBookQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSuppplierPhoneNumberEditText.getText().toString().trim();

        // Create database helper
        BooksDbHelper booksDbHelper = new BooksDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = booksDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksEntry.COLUMN_PRODUCT_NAME, bookNameString);
        contentValues.put(BooksEntry.COLUMN_PRODUCT_PRICE, bookPriceString);
        contentValues.put(BooksEntry.COLUMN_PRODUCT_QUANTITY, bookQuantityString);
        contentValues.put(BooksEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        contentValues.put(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(BooksEntry.TABLE_NAME, null, contentValues);
        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error saving book information", Toast.LENGTH_LONG).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Book Information Saved row: " + newRowId, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                insertBook();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Navigate back to parent activity (CatalogActivity)
            case R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
