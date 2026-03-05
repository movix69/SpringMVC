package com.hibernate.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.function.Consumer;
import java.util.function.Function;

public class SessionManager {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private SessionManager() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            // Cargar configuración desde hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Error al crear SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static <T> T executeRead(Function<Session, T> action) {
        try (Session session = sessionFactory.openSession()) {
            return action.apply(session);
        }
    }

    public static void runInTransaction(Consumer<Session> action) {
        executeInTransaction(session -> {
            action.accept(session);
            return null;
        });
    }

    public static <T> T executeInTransaction(Function<Session, T> action) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            T result = action.apply(session);
            transaction.commit();
            return result;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                try {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                } catch (RuntimeException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }
            }
            throw ex;
        }
    }

    public static void shutdown() {
        if (!sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
