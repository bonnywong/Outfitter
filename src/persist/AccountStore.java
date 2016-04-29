package persist;

import Models.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.security.MessageDigest;

/**
 *
 * Created by swebo_000 on 2016-04-27.
 */
public class AccountStore {

    private EntityManagerFactory emfactory;
    private EntityManager em;

    public AccountStore() {
        emfactory = Persistence.createEntityManagerFactory("NewPersistenceUnit");
        em = emfactory.createEntityManager();
    }

    public void persistUser() {
    }

    public UserEntity fetchUser(String username) {
        //Query query = em.createQuery("select u from UserEntity u where ")
        return null;
    }

    public UserEntity fetchUser(int id) {
        return em.find(UserEntity.class, id);
    }

    /**
     * Closes the EntityManager and EntityManagerFactory
     */
    public void close() {
        em.close();
        emfactory.close();
    }

}
