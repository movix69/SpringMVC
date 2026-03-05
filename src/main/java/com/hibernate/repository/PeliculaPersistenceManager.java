package com.hibernate.repository;

import com.hibernate.entity.Genero;
import com.hibernate.entity.Pelicula;

import java.util.List;

public class PeliculaPersistenceManager {
    private static final String FETCH_BASE_QUERY =
            "select distinct p from Pelicula p left join fetch p.reparto left join fetch p.genero";

    private PeliculaPersistenceManager() {}

    public static void create(Pelicula pelicula) {
        SessionManager.runInTransaction(session -> session.persist(pelicula));
    }

    public static void create(List<Pelicula> peliculas) {
        SessionManager.runInTransaction(session -> {
            for (Pelicula pelicula : peliculas) {
                session.persist(pelicula);
            }
        });

    }

    public static Pelicula update(Pelicula peliculaModificada) {
        return SessionManager.executeInTransaction(session -> session.merge(peliculaModificada));
    }

    public static Pelicula read(int id) {
        return readById(id);
    }

    public static Pelicula readById(int id) {
        return SessionManager.executeRead(session -> session
                .createQuery(
                        FETCH_BASE_QUERY + " where p.id = :id",
                        Pelicula.class
                )
                .setParameter("id", id)
                .uniqueResult());
    }

    public static List<Pelicula> readAllByGenero(Genero genero) {
        return SessionManager.executeRead(session -> session
                .createQuery(
                        FETCH_BASE_QUERY + " where p.genero = :genero",
                        Pelicula.class
                )
                .setParameter("genero", genero)
                .getResultList());
    }

    public static List<Pelicula> readAllByAnio(int anio) {
        return SessionManager.executeRead(session -> session
                .createQuery(
                        FETCH_BASE_QUERY + " where p.anio >= :anioMinimo",
                        Pelicula.class
                )
                .setParameter("anioMinimo", anio)
                .getResultList());
    }

    public static Pelicula readByTitulo(String titulo) {
        return SessionManager.executeRead(session -> {
            Integer id = session.createQuery(
                            "select p.id from Pelicula p where p.titulo = :titulo order by p.id",
                            Integer.class
                    )
                    .setParameter("titulo", titulo)
                    .setMaxResults(1)
                    .uniqueResult();

            if (id == null) {
                return null;
            }

            return session.createQuery(
                            FETCH_BASE_QUERY + " where p.id = :id",
                            Pelicula.class
                    )
                    .setParameter("id", id)
                    .uniqueResult();
        });
    }

    public static List<Pelicula> readAllByAnio() {
        return readAll();
    }

    public static List<Pelicula> readAll() {
        return SessionManager.executeRead(session -> session
                .createQuery(FETCH_BASE_QUERY, Pelicula.class)
                .getResultList());
    }

    public static void delete(Pelicula pelicula) {
        SessionManager.runInTransaction(session -> {
            Pelicula managed = session.contains(pelicula) ? pelicula : session.merge(pelicula);
            session.remove(managed);
        });
    }

    public static void delete(int id) {
        SessionManager.runInTransaction(session -> {
            Pelicula pelicula = session.find(Pelicula.class, id);
            if (pelicula != null) {
                session.remove(pelicula);
            }
        });
    }

    public static void delete(String titulo) {
        SessionManager.runInTransaction(session -> session
                .createMutationQuery("delete from Pelicula p where p.titulo = :titulo")
                .setParameter("titulo", titulo)
                .executeUpdate());
    }
}
