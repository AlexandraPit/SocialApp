package com.example.project_sa.service;

import com.example.project_sa.domain.Entity;

import java.util.ArrayList;

/**
 * Service class
 * @param <ID>  ID param placeholder
 * @param <E>  Entity placeholder
 */

public interface Service<ID, E extends Entity> {

    /**
     * This method adds an Entity E in the given Repository.
     * @param entity : E
     */
    public void add(E entity);

    /**
     * This method removes an Entity from the repository, given an id of type ID.
     * @param id : ID
     * @return E (deleted entity)
     */
    public E delete(ID id);

    /**
     * This method returns the number of elements in the repository.
     * @return int (number of elements in the repository)
     */
    public int number();

    /**
     * This method tries to find an Entity in the repository, given an id of type ID.
     * @param id : ID
     * @return E (found entity)
     */
    public E find(ID id);

    /**
     * This method returns an ArrayList which includes all the Entities in the repository.
     * @return ArrayList<E> (all the current entities)
     */
    public ArrayList<E> findAll();

}