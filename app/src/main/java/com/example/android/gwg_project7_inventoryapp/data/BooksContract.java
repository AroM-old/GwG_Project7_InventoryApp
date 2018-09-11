package com.example.android.gwg_project7_inventoryapp.data;

import android.provider.BaseColumns;

/* API contract for the books apps */
public final class BooksContract {

    private static final String LOG_TAG = BooksContract.class.getSimpleName();

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
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        /**
         * Book supplier phone number.
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

    }
}
