package com.library.anishelf.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * The interface Generic dao.
 *
 * @param <E> the type parameter
 */
public interface GenericDAO<E> {
    /**
     * Insert.
     *
     * @param entity the entity
     * @throws SQLException the sql exception
     */
    void insert(E entity) throws SQLException;

    /**
     * Update entity boolean.
     *
     * @param entity the entity
     * @return the boolean
     * @throws SQLException the sql exception
     */
    boolean updateEntity(E entity) throws SQLException;

    /**
     * Delete entity boolean.
     *
     * @param entity the entity
     * @return the boolean
     * @throws SQLException the sql exception
     */
    boolean deleteEntity(E entity) throws SQLException;

    /**
     * Find by id e.
     *
     * @param keywords the keywords
     * @return the e
     * @throws SQLException the sql exception
     */
    E findById(Number keywords) throws SQLException;

    /**
     * Find by criteria list.
     *
     * @param criteria the criteria
     * @return the list
     * @throws SQLException the sql exception
     */
    List<E> findByCriteria(Map<String, Object> criteria) throws SQLException;

    /**
     * Find all list.
     *
     * @return the list
     * @throws SQLException the sql exception
     */
    List<E> findAll() throws SQLException;
}
