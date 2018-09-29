package com.example.android.gwg_project7_inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.gwg_project7_inventoryapp.data.BooksContract.BooksEntry;

/**
 * Allows user to create a new book or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri currentBookUri;

    /**
     * EditText field to enter the book's name
     */
    @BindView(R.id.book_name_edit_text)
    EditText mBookNameEditText;

    /**
     * EditText field to enter the book's price
     */
    @BindView(R.id.book_price_edit_text)
    EditText mBookPriceEditText;

    /**
     * EditText field to enter the book's quantity
     */
    @BindView(R.id.book_quantity_edit_text)
    EditText mBookQuantityEditText;

    /**
     * EditText field to enter the supplier's name
     */
    @BindView(R.id.supplier_name_edit_text)
    EditText mSupplierNameEditText;

    /**
     * EditText field to enter the supplier's phone number
     */
    @BindView(R.id.supplier_phone_edit_text)
    EditText mSuppplierPhoneNumberEditText;

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean bookHasChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            bookHasChanged = true;
            return false;
        }
    };

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the bookHasChanged boolean to true.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        currentBookUri = intent.getData();

        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book.
        if (currentBookUri == null) {
            // This is a new book, so change the app bar to say "Add a Book"
            setTitle(getString(R.string.addBook));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing book, so change app bar to say "Edit Book"
            setTitle(getString(R.string.editBook));


            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }


        // Find all relevant views that we will need to read user input from
        // Using ButterKnife Library
        ButterKnife.bind(this);

        mBookNameEditText.setOnTouchListener(touchListener);
        mBookPriceEditText.setOnTouchListener(touchListener);
        mBookQuantityEditText.setOnTouchListener(touchListener);
        mSupplierNameEditText.setOnTouchListener(touchListener);
        mSuppplierPhoneNumberEditText.setOnTouchListener(touchListener);

    }

    /**
     * Get user input from editor and save new book into database.
     */
    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String bookNameString = mBookNameEditText.getText().toString().trim();
        String bookPriceString = mBookPriceEditText.getText().toString().trim();
        int bookPriceInt = Integer.parseInt(bookPriceString);
        String bookQuantityString = mBookQuantityEditText.getText().toString().trim();
        int bookQuantityInt = Integer.parseInt(bookQuantityString);
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSuppplierPhoneNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (currentBookUri == null && TextUtils.isEmpty(bookNameString) && TextUtils.isEmpty(bookPriceString) && TextUtils.isEmpty(bookQuantityString)
                && TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneString)) {
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksEntry.COLUMN_PRODUCT_NAME, bookNameString);
        contentValues.put(BooksEntry.COLUMN_PRODUCT_PRICE, bookPriceInt);
        contentValues.put(BooksEntry.COLUMN_PRODUCT_QUANTITY, bookQuantityInt);
        contentValues.put(BooksEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        contentValues.put(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

        // Determine if this is a new or existing book by checking if currentBookUri is null or not
        if (currentBookUri == null) {
            // Insert a new book into the provider, returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BooksEntry.CONTENT_URI, contentValues);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the row ID is null, then there was an error with insertion.
                Toast.makeText(this, R.string.error_saving, Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, getString(R.string.info_saved), Toast.LENGTH_LONG).show();
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: currentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because currentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(currentBookUri, contentValues, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Update book failed", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Update book succesful", Toast.LENGTH_SHORT).show();
            }
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
                // Save book to database
                saveBook();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case R.id.action_delete:
                // Do nothing for now
                showDeleteConfirmationDialog();
                return true;
            // Navigate back to parent activity (CatalogActivity)
            case R.id.home:
                if (!bookHasChanged) {
                    // Navigate back to parent activity (CatalogActivity)
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangedDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_PRODUCT_NAME,
                BooksEntry.COLUMN_PRODUCT_PRICE,
                BooksEntry.COLUMN_PRODUCT_QUANTITY,
                BooksEntry.COLUMN_SUPPLIER_NAME,
                BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,       // Parent activity context
                currentBookUri,                     // Query the content URI for thr current book
                projection,                         // Columns to include in the resulting cursor
                null,                      // No selection clause
                null,                  // No selection arguments
                null);                    // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            int bNameColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_NAME);
            int bPriceColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_PRICE);
            int bQuantityColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_QUANTITY);
            int sNameColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_NAME);
            int sPhoneColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String bookName = cursor.getString(bNameColumnIndex);
            int bookPrice = cursor.getInt(bPriceColumnIndex);
            int bookQuantity = cursor.getInt(bQuantityColumnIndex);
            String supplierName = cursor.getString(sNameColumnIndex);
            String supplierPhone = cursor.getString(sPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            mBookNameEditText.setText(bookName);
            mBookPriceEditText.setText(Integer.toString(bookPrice));
            mBookQuantityEditText.setText(Integer.toString(bookQuantity));
            mSupplierNameEditText.setText(supplierName);
            mSuppplierPhoneNumberEditText.setText(supplierPhone);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mBookNameEditText.setText("");
        mBookPriceEditText.setText("");
        mBookQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSuppplierPhoneNumberEditText.setText("");

    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClikListener is the click listener for what to do when
     *                                  the user confirms they want to discard their changes
     */
    private void showUnsavedChangedDialog(DialogInterface.OnClickListener discardButtonClikListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing");
        builder.setPositiveButton("Discard", discardButtonClikListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        // Show dialog that there are unsaved changes
        showUnsavedChangedDialog(discardButtonClickListener);
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (currentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the currentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }
}
