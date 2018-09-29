package com.example.android.gwg_project7_inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.gwg_project7_inventoryapp.data.BooksDbHelper;

import static com.example.android.gwg_project7_inventoryapp.data.BooksContract.BooksEntry;

/**
 * Displays list of books that were entered and stored in the app.
 */
public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int BOOK_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    BookCursorAdapter bookCursorAdapter;

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

        // Find ListView to populate
        ListView lvItems = findViewById(R.id.book_list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        lvItems.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        // There is no book data yet (until the loader finishes) so pass in null for the Cursor.
        bookCursorAdapter = new BookCursorAdapter(this, null);
        lvItems.setAdapter(bookCursorAdapter);

        // Setup the item click listener
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific book that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link BookEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.books/books/2"
                Uri currentBookUri = ContentUris.withAppendedId(BooksEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentBookUri);

                // Launch the {@link EditorActivity} to display the data for the current book.
                startActivity(intent);

            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded book data into the database. For debugging purposes only.
     */
    private void insertBook() {
        // Create a ContentValues object where column names are the keys,
        // and Lord of ring book attributes are the values.
        ContentValues values = new ContentValues();
        values.put(BooksEntry.COLUMN_PRODUCT_NAME, "The Lord of The Ring");
        values.put(BooksEntry.COLUMN_PRODUCT_PRICE, "58");
        values.put(BooksEntry.COLUMN_PRODUCT_QUANTITY, "1");
        values.put(BooksEntry.COLUMN_SUPPLIER_NAME, "Wizard Editorial");
        values.put(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "254-200-5555");

        // Insert a new row for Lord of Ring into the provider using the ContentResolver.
        // Use the {@link BookEntry#CONTENT_URI} to indicate that we want to insert
        // into the books database table.
        // Receive the new content URI that will allow us to access Lord of ring data in the future.
        Uri newUri = getContentResolver().insert(BooksEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_inventory.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.insert_dummy_data:
                insertBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to delete all books in the database.
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BooksEntry.CONTENT_URI, null, null);
        Log.v("InventoryActivity", rowsDeleted + " rows deleted from book database");
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_PRODUCT_NAME,
                BooksEntry.COLUMN_PRODUCT_PRICE,
                BooksEntry.COLUMN_PRODUCT_QUANTITY,
                BooksEntry.COLUMN_SUPPLIER_NAME,
                BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                BooksEntry.CONTENT_URI,         // Provider activity context
                projection,                     // Columns to include in the resulting Cursor
                null,                  // No selection clause
                null,              // No selection arguments
                null);                // Default sort order
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        bookCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        bookCursorAdapter.swapCursor(null);

    }
}
