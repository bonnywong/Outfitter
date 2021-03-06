package testing;

import Models.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

        //Generate and add 5 random users into database
        //for (int i = 0; i < 5; i++) {
        //    insertUser(generateRandomUser());
        //}

//        //Insert some tags
//        String[] tags = new String[] {"thin", "red", "blue","thick", "wool", "t-shirt", "shorts"};
//        if (getAllTags().size() == 0) {
//            for (String tag : tags) {
//                insertTag(new TagEntity(tag));
//            }
//        }
//
//
//        //Associate each user with a tag and generate some random weight.
//        if (getAllUserWeightMaps().size() == 0) {
//            for (UserEntity user : getAllUsers()) {
//                Set<UserWeightEntity> userWeightSet = new HashSet<UserWeightEntity>();
//                for (TagEntity tag : getAllTags()) {
//                    double random = ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
//                    UserWeightEntity userWeight = new UserWeightEntity(user.getUserId(), tag.getTagId(), random);
//                    userWeightSet.add(userWeight); //Add the new UserWeightEntity to the set.
//                }
//                setUserWeightSet(user, userWeightSet); //Associate the new user-tag-weights with the user.
//            }
//        }
//
//        //Let's see if this worked. Fetch a random user by their id. And print the list of weights associated with them.
//        UserEntity user = em.find(UserEntity.class, new Random().nextInt(getAllUsers().size() + 4)); //Adding 4 here because the user_id has been shifted by 4 for some reason.
//        for (UserWeightEntity uw : user.getWeights()) {
//            System.out.println("user_id: " + uw.getUserId() + ". tag_id: " + uw.getTagId() + ". weight: " + uw.getWeight());
//        }

//        UserEntity user = em.find(UserEntity.class, 20);
//
//        System.out.println(user.getUsername());
//        for (UserWeightEntity uw : user.getWeights()) {
//            System.out.println("user_id: " + uw.getUserId() + ". tag_id: " + uw.getTagId() + ". weight: " + uw.getWeight());
//        }

        ProductEntity product = fetchProduct("0351721007");
        System.out.println(product.getTags().size());
        UserEntity userEntity = em.find(UserEntity.class, 27);

        Set<UserWeightEntity> uweSet = getUserTags(userEntity);
        List<UserWeightEntity> uweList = new LinkedList<UserWeightEntity>();
        uweList.addAll(uweSet);

        //Collections.sort(uweList);
        for (UserWeightEntity uwe : uweList) {
            TagEntity tag = em.find(TagEntity.class, uwe.getTagId());
            System.out.println(tag.getName() + ": " + uwe.getWeight());
        }

        //Remember to close the connection.
        em.close();
        emfactory.close();

//        try {
//            DatabaseHandler dHandler = new DatabaseHandler();
//            ProductEntity p = dHandler.findProduct(userEntity, "top");
//            System.out.println(dHandler.getMeta(p).toString());
//        }   catch (SQLException e) {
//            e.printStackTrace();
//        }


    }

    /**
     * Generates a random UserEntity
     * @return UserEntity
     */
    private static UserEntity generateRandomUser() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String username = "user" + uuid.substring(0, 5);
        return new UserEntity(username, username + "@domain.com", uuid.substring(5, 15),"auto-generated-user", "user");
    }

    /*-------------------------------Database stuff below------------------------------------*/


    /**
     * Returns all non-zero metatags belonging to a user.
     * @param user
     * @return
     */
    public static Set<UserWeightEntity> getUserTags(UserEntity user) {
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

    public static ProductEntity fetchProduct(String pid) {
        Query query = em.createQuery("select p from ProductEntity p where p.pid = :pid");
        query.setParameter("pid", pid);
        return (ProductEntity) query.getSingleResult();
    }


    /**
     *
     */
    public static void inserTags() {

    }

    /**
     * Inserts a TagEntity into the database
     * @param tag TagEntity
     */
    private static void insertTag(TagEntity tag) {
        em.getTransaction().begin();
        em.persist(tag);
        em.getTransaction().commit();
    }

    /**
     * Returns a list of all TagEntities in the database
     * @return List of TagEntity
     */
    private static List<TagEntity> getAllTags() {
        Query query = em.createQuery("select t from TagEntity t");
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

    /**
     * Returns a list of all UserEntities in the database.
     *
     * @return List of UserEntity
     */
    private static List<UserEntity> getAllUsers() {
        Query query = em.createQuery("select u from UserEntity u");
        return query.getResultList();
    }

    /**
     * Assigns a set of UserWeightEntity to a UserEntity
     * @param user UserEntity
     * @param set A set of UserWeightEntity
     */
    private static void setUserWeightSet(UserEntity user, Set<UserWeightEntity> set) {
        em.getTransaction().begin();
        user.setWeights(set);
        em.getTransaction().commit();
    }

    private static List<UserWeightMapEntity> getAllUserWeightMaps() {
        Query query = em.createQuery("select uw from UserWeightMapEntity uw");
        return query.getResultList();
    }

}
