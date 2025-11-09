//package com.example.MovieReservationApp.infrastructure.persistence.jpa;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.UUID;
//
///**
// * Implementare generică a IRepository pentru toate entitățile
// * folosind EntityManager.
// */
//@Repository
//@Transactional
//public class EntityRepositoryJPA<T> implements IRepository<T> {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    private final Class<T> entityClass;
//
//
//    public EntityRepositoryJPA(Class<T> entityClass) {
//        this.entityClass = entityClass;
//    }
//
//    @Override
//    public T save(T entity) {
//        entityManager.persist(entity);
//        return entity;
//    }
//
//    @Override
//    public T update(T entity) {
//        return entityManager.merge(entity);
//    }
//
//    @Override
//    public void delete(T entity) {
//        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
//    }
//
//    @Override
//    public T findById(UUID id) {
//        return entityManager.find(entityClass, id);
//    }
//
//    @Override
//    public List<T> findAll() {
//        return entityManager
//                .createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
//                .getResultList();
//    }
//
//
//    public List<T> findByFieldContaining(String fieldName, String value) {
//        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE LOWER(e." + fieldName + ") LIKE LOWER(CONCAT('%', :value, '%'))";
//        return entityManager.createQuery(jpql, entityClass)
//                .setParameter("value", value)
//                .getResultList();
//    }
//}
