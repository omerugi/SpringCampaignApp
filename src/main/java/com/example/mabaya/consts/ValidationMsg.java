package com.example.mabaya.consts;


public class ValidationMsg {
    public ValidationMsg() {}

    // Empty fields msgs:
    public static final String EMPTY_PSN = "Product Serial Numbers cannot be empty";
    public static final String EMPTY_CATEGORY_NAME = "Category Name cannot be null";

    // Size fields msgs:
    public static final String SIZE_CONSTRAINT_NAME_2_25 = "Name should be between 2-25 chars";
    public static final String SIZE_CONSTRAINT_TITLE_2_25 = "Title should be between 2-25 chars";

    // Null fields msgs:
    public static final String NULL_START_DATE = "Start Date cannot be null";
    public static final String NULL_NAME = "Name cannot be null";
    public static final String NULL_TITLE = "Title cannot be null";
    public static final String NULL_CATEGORY = "Category cannot be null";

    // Numeric field msgs:
    public static final String NUM_BID_NEGATIVE = "Bid cannot be negative";
    public static final String NUM_PRICE_NEGATIVE = "Price cannot be negative";

    // Invalid field msgs:
    public static final String INVALID_PSN = "Product Serial Number should be at size 1-255 and contain only letters and digits";

    // Delete msgs:
    public static final String CANNOT_DELETE_CATEGORY_ATTACHED_PRODUCTS = "Cannot delete Categories, that products are attached to";

    // Not found msgs:
    public static final String NOT_FOUND_PSN = "Product Serial Number was not found in the DB";
    public static final String NOT_FOUND_CATEGORY_NAME = "Category Name was not found in the DB";
    public static final String NOT_FOUND_ID = "ID was not found in the DB";
}
