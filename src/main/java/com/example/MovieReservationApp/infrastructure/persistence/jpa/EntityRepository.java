package com.example.MovieReservationApp.infrastructure.persistence.jpa;

import java.util.Collection;

public interface EntityRepository<T> {
    T getById(Object id);
    boolean contains(T entitySample);
    Collection<T> getAll(T entitySample);
    T get(T entitySample);
    Collection<T> toCollection();
    T[] toArray();
    T add(T entity);
    Collection<T> addAll(Collection<T> entities);
    boolean remove(T entity);
    boolean removeAll(Collection<T> entities);
    int size();
    T refresh(T entity);
}
