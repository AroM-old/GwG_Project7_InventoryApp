package com.example.android.gwg_project7_inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/* API contract for the books apps */
public final class BooksContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.gwg_project7_inventoryapp";

    private static final String LOG_TAG = BooksContract.class.getSimpleName();
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BooksContract() {
    }

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static final class BooksEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * Name of database table for books
         */
        public static final String TABLE_NAME = "books";

        /**
         * Unique ID number for the book (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Name of the book.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        /**
         * Price of the book.
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_PRODUCT_PRICE = "price";
        /**
         * Quantity of the book.
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        /**
         * Book supplier name.
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        /**
         * Book supplier phone number.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

    }
}
