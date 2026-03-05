package com.hibernate.repository;

import com.hibernate.entity.Interprete;

import java.util.List;

public class InterpretePersistenceManager {
    private static final String FETCH_BASE_QUERY =
            "select distinct i from Interprete i left join fetch i.peliculas";

    private InterpretePersistenceManager() {}

    public static void create(Interprete interprete) {
        SessionManager.runInTransaction(session -> session.persist(interprete));
    }

    public static Interprete update(Interprete interpreteModificada) {
        return SessionManager.executeInTransaction(session -> session.merge(interpreteModificada));
    }

    public static Interprete read(int id) {
        return readById(id);
    }

    public static Interprete readById(int id) {
        return SessionManager.executeRead(session -> session
                .createQuery(FETCH_BASE_QUERY + " where i.id = :id", Interprete.class)
                .setParameter("id", id)
                .uniqueResult());
    }

    public static Interprete read(String nombre) {
        return readByNombre(nombre);
    }

    public static Interprete readByNombre(String nombre) {
        return SessionManager.executeRead(session -> {
            Integer id = session.createQuery(
                            "select i.id from Interprete i where i.nombre = :nombre order by i.id",
                            Integer.class
                    )
                    .setParameter("nombre", nombre)
                    .setMaxResults(1)
                    .uniqueResult();

            if (id == null) {
                return null;
            }

            return session.createQuery(FETCH_BASE_QUERY + " where i.id = :id", Interprete.class)
                    .setParameter("id", id)
                    .uniqueResult();
        });
    }

    public static List<Interprete> readAll() {
        return SessionManager.executeRead(session -> session
                .createQuery(
                        FETCH_BASE_QUERY,
                        Interprete.class
                )
                .getResultList());
    }

    public static void delete(Interprete interprete) {
        SessionManager.runInTransaction(session -> {
            Interprete managed = session.contains(interprete) ? interprete : session.merge(interprete);
            session.remove(managed);
        });
    }

    public static void delete(int id) {
        SessionManager.runInTransaction(session -> {
            Interprete interprete = session.find(Interprete.class, id);
            if (interprete != null) {
                session.remove(interprete);
            }
        });
    }

    public static void delete(String nombre) {
        SessionManager.runInTransaction(session -> session
                .createMutationQuery("delete from Interprete i where i.nombre = :nombre")
                .setParameter("nombre", nombre)
                .executeUpdate());
    }
}
