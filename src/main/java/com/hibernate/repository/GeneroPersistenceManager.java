package com.hibernate.repository;

import com.hibernate.entity.Genero;

import java.util.List;

public class GeneroPersistenceManager {
    private static final String FETCH_BASE_QUERY =
            "select distinct g from Genero g left join fetch g.peliculas";

    private GeneroPersistenceManager() {
    }

    public static void create(Genero genero) {
        SessionManager.runInTransaction(session -> session.persist(genero));
    }

    public static Genero update(Genero generoModificado) {
        return SessionManager.executeInTransaction(session -> session.merge(generoModificado));
    }

    public static Genero readById(int id) {
        return SessionManager.executeRead(session -> session
                .createQuery(FETCH_BASE_QUERY + " where g.id = :id", Genero.class)
                .setParameter("id", id)
                .uniqueResult());
    }

    public static Genero readByName(String nombre) {
        return SessionManager.executeRead(session -> {
            Integer id = session.createQuery(
                            "select g.id from Genero g where g.nombre = :nombre order by g.id",
                            Integer.class
                    )
                    .setParameter("nombre", nombre)
                    .setMaxResults(1)
                    .uniqueResult();

            if (id == null) {
                return null;
            }

            return session.createQuery(FETCH_BASE_QUERY + " where g.id = :id", Genero.class)
                    .setParameter("id", id)
                    .uniqueResult();
        });
    }

    public static List<Genero> readAll() {
        return SessionManager.executeRead(session -> session
                .createQuery(FETCH_BASE_QUERY, Genero.class)
                .getResultList());
    }

    public static void delete(Genero genero) {
        SessionManager.runInTransaction(session -> {
            Genero managed = session.contains(genero) ? genero : session.merge(genero);
            session.remove(managed);
        });
    }

    public static void delete(int id) {
        SessionManager.runInTransaction(session -> {
            Genero genero = session.find(Genero.class, id);
            if (genero != null) {
                session.remove(genero);
            }
        });
    }

    public static void delete(String nombre) {
        SessionManager.runInTransaction(session -> session
                .createMutationQuery("delete from Genero g where g.nombre = :nombre")
                .setParameter("nombre", nombre)
                .executeUpdate());
    }
}
