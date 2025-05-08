package com.library.anishelf.model;

/**
 * The type Category.
 */
public class Category {
    private int categoryId;
    private String categoryName;

    /**
     * Instantiates a new Category.
     *
     * @param categoryName the category name
     */
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * Instantiates a new Category.
     *
     * @param catagoryId   the catagory id
     * @param catagoryName the catagory name
     */
    public Category(int catagoryId, String catagoryName) {
        this.categoryId = catagoryId;
        this.categoryName = catagoryName;
    }

    /**
     * Gets category id.
     *
     * @return the category id
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * Gets catagory name.
     *
     * @return the catagory name
     */
    public String getCatagoryName() {
        return categoryName;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
