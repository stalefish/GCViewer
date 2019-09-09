package com.tagtraum.perf.gcviewer.exp.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public enum PersistenceManager {
    INSTANCE;

    private EntityManagerFactory emFactory;

    PersistenceManager() {
        try {
            emFactory = Persistence.createEntityManagerFactory("gcsummary");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

    public void close() {
        emFactory.close();
    }

}
