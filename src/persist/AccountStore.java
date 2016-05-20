package persist;

import Models.UserEntity;
import Models.UserSettingEntity;
import Models.UserWeightEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by swebo_000 on 2016-04-27.
 */
public class AccountStore {

    private EntityManagerFactory emfactory;
    private EntityManager em;

    public AccountStore() {
        emfactory = Persistence.createEntityManagerFactory("NewPersistenceUnit");
        //emfactory = Persistence.createEntityManagerFactory("MYSQLUnit");
        em = emfactory.createEntityManager();
    }

    /**
     * Returns all non-zero metatags belonging to a user.
     * @param user
     * @return
     */
    public Set<UserWeightEntity> getUserTags(UserEntity user) {
        Set<UserWeightEntity> resultSet = new HashSet<UserWeightEntity>();

        Query query = em.createQuery("select uwe from UserWeightEntity uwe where uwe.userId = :userId");
        query.setParameter("userId", user.getUserId());
        if (query.getResultList().size() == 0) {
            return null;
        } else {
            for (UserWeightEntity uwe : (List<UserWeightEntity>) query.getResultList()) {
                if (uwe.getWeight() != 0) {
                    resultSet.add(uwe);
                }
            }
            return resultSet;
        }
    }

    public UserSettingEntity getUserSettings(UserEntity user) {
        Query query = em.createQuery("select use from UserSettingEntity use where use.userId = :userId");
        query.setParameter("userId", user.getUserId());
        UserSettingEntity use = (UserSettingEntity) query.getSingleResult();
        return use;
    }

    public void incrementCalibrateCount(UserSettingEntity userSettings) {
        em.getTransaction().begin();
        userSettings.incrementCalibrate();
        em.getTransaction().commit();
    }

    public void persistUser(UserEntity user) {
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }

    public void persistSettings(UserEntity user) {
        UserSettingEntity userSettings= new UserSettingEntity(user.getUserId());
        userSettings.setCalibrate(0);
        em.getTransaction().begin();
        em.persist(userSettings);
        em.getTransaction().commit();
    }

    public UserEntity fetchUser(String username) {
        Query query = em.createQuery("select u from UserEntity u where u.username = :username");
        query.setParameter("username", username);
        return (UserEntity) query.getSingleResult();
    }


    public boolean userExists(String username) {
        Query query = em.createQuery("select u from UserEntity u where u.username = :username");
        query.setParameter("username", username);
        return query.getResultList().size() == 1;
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
