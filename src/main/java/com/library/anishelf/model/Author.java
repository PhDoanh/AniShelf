package com.library.anishelf.model;

/**
 * The type Author.
 */
public class Author {
    private int authorId;
    private String name;

    /**
     * Instantiates a new Author.
     *
     * @param name the name
     */
    public Author(String name) {
        this.name = name;
    }

    /**
     * Instantiates a new Author.
     *
     * @param authorId   the id
     * @param name the name
     */
    public Author(int authorId, String name) {
        this.authorId = authorId;
        this.name = name;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getAuthorId() {
        return authorId;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
}
