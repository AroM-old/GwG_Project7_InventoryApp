package com.example.android.gwg_project7_inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.android.gwg_project7_inventoryapp.data.BooksContract.BooksEntry;
import static com.example.android.gwg_project7_inventoryapp.data.BooksContract.CONTENT_AUTHORITY;
import static com.example.android.gwg_project7_inventoryapp.data.BooksContract.PATH_BOOKS;


public class BookProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS + "/#", BOOKS_ID);
    }

    private static final String LOG_TAG = BookProvider.class.getSimpleName();
    private BooksDbHelper booksDbHelper;

    @Override
    public boolean onCreate() {
        booksDbHelper = new BooksDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = booksDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BooksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case BOOKS_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BooksEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BooksEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BooksEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + "with match" + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    @Nullable
    private Uri insertBook(Uri uri, ContentValues values) {

        //Perform sanity check
        String productName = values.getAsString(BooksEntry.COLUMN_PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("Book name or title required");
        }

        Integer productPrice = values.getAsInteger(BooksEntry.COLUMN_PRODUCT_PRICE);
        if (productPrice != null && productPrice < 0) {
            throw new IllegalArgumentException("Price required");
        }

        Integer productQuantity = values.getAsInteger(BooksEntry.COLUMN_PRODUCT_QUANTITY);
        if (productQuantity != 0 && productQuantity < 0) {
            throw new IllegalArgumentException("Price required");
        }

        String supplierName = values.getAsString(BooksEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Supplier name required");
        }

        String supplierNumber = values.getAsString(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierNumber == null) {
            throw new IllegalArgumentException("Phone number required");
        }

        // Get writable database
        SQLiteDatabase database = booksDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(BooksEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the book content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = booksDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            // Delete all rows that match the selection and selection args
            case BOOKS:
                rowsDeleted = database.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            // Delete a single row given by the ID in the URI
            case BOOKS_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                return updateBook(uri, values, selection, selectionArgs);
            // For the BOOK_ID code, extract out the ID from the URI,
            // so we know which row to update. Selection will be "_id=?" and selection
            // arguments will be a String array containing the actual ID.
            case BOOKS_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link BookEntry#COLUMN_PRODUCT_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(BooksEntry.COLUMN_PRODUCT_NAME)) {
            String productName = values.getAsString(BooksEntry.COLUMN_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException("Book name or title required");
            }
        }
        // If the {@link BookEntry#COLUMN_PRODUCT_PRICE} key is present,
        // check that the price value is not null.
        if (values.containsKey(BooksEntry.COLUMN_PRODUCT_PRICE)) {
            Integer productPrice = values.getAsInteger(BooksEntry.COLUMN_PRODUCT_PRICE);
            if (productPrice != null && productPrice < 0) {
                throw new IllegalArgumentException("Price required");
            }
        }
        // If the {@link BookEntry#COLUMN_PRODUCT_QUANTITY} key is present,
        // check that the quantity value is not null.
        if (values.containsKey(BooksEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer productQuantity = values.getAsInteger(BooksEntry.COLUMN_PRODUCT_QUANTITY);
            if (productQuantity != 0 && productQuantity < 0) {
                throw new IllegalArgumentException("Price required");
            }
        }
        // If the {@link BookEntry#COLUMN_SUPPLIER_NAME} key is present,
        // check that the supplier name value is not null.
        if (values.containsKey(BooksEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BooksEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Supplier name required");
            }
        }
        // If the {@link BookEntry#COLUMN_PRODUCT_PHONE_NUMBER} key is present,
        // check that the supplier phone value is not null.
        if (values.containsKey(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierNumber = values.getAsString(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierNumber == null) {
                throw new IllegalArgumentException("Phone number required");
            }
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        // Otherwise, get writable database to update the data
        SQLiteDatabase database = booksDbHelper.getWritableDatabase();


        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BooksEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }
}
