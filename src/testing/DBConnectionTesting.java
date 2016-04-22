package testing;

import Models.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Let's see if Hibernate + SQLite even works...
 *
 * Created by swebo_000 on 2016-04-21.
 */
public class DBConnectionTesting {

    private static EntityManagerFactory emfactory;
    private static EntityManager em;
    public static void main(String[] args) {

        emfactory = Persistence.createEntityManagerFactory("NewPersistenceUnit");
        em = emfactory.createEntityManager();

        UserEntity user = new UserEntity();
        user.setUsername("bonnyw");
        user.setEmail("bonnyw@kth.se");
        user.setPassword("12345");
        user.setRole("user");
        //Note the ID is auto generated.

        //insertUser(user);
        for (UserEntity u : getAllUsers()) {
            System.out.println("Username: " + u.getUsername() + " " +
                               "Email: " + u.getEmail() + " " +
                               "Password: " + u.getPassword() + " " +
                               "Role: " + u.getRole());
        }
    }


    /**
     * Returns a list of all UserEntities in database.
     *
     * @return List containing UserEntities
     */
    private static List<UserEntity> getAllUsers() {
        Query query = em.createQuery("select u from UserEntity u");
        return query.getResultList();
    }

    /**
     * Inserts a UserEntity object into the database using Hibernate.
     * @param user UserEntity
     */
    private static void insertUser(UserEntity user) {
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }

}
