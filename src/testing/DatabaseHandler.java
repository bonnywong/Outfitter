import Models.*;

import javax.persistence.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class DatabaseHandler {

    private static final double ALPHA = 0.3;
    private static final double INIT_WEIGHT = 0.0;
    private EntityManagerFactory emfactory;
    private EntityManager em;

    public static void main(String[] args) {
        // testing the DatabaseHandler class
        DatabaseHandler d = new DatabaseHandler();
        UserEntity testUser = new UserEntity("forDBtesting", "mail@kth.se", "password123", "user");
        ProductEntity p = d.findProduct(testUser, "top");
        System.out.println(d.getMeta(p).toString());
        //d.update(testUser, p, 1);
    }

    public DatabaseHandler() {
        emfactory = Persistence.createEntityManagerFactory("NewPersistenceUnit");
        em = emfactory.createEntityManager();
    }

    public void insertUser(UserEntity user) {
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        Query query = em.createQuery("SELECT t FROM TagEntity t");
        for(TagEntity tags : (List<TagEntity>) query.getResultList()) {
            UserWeightEntity uw = new UserWeightEntity(user.getUserId(), tags.getTagId(), INIT_WEIGHT);

            em.getTransaction().begin();
            em.persist(uw);
            em.getTransaction().commit();
        }
    }

    public ProductEntity findProduct(UserEntity user, String top) {
        Query query = em.createNativeQuery("SELECT productTag.pid FROM productTag " +
                "LEFT OUTER JOIN (SELECT uw.weight, uw.tagId " +
                "FROM user_weights uw WHERE uw.user_id = "+user.getUserId()+") AS w " +
                "ON w.tagId = p.tag" +
                "WHERE productTag.pid IN (SELECT p FROM Product p WHERE p.top = '" + top + "') " +
                "GROUP BY productTag.pid ORDER BY SUM(w.weight)/COUNT(*) LIMIT 1");
        for(ProductTagEntity product : (List<ProductTagEntity>) query.getResultList()) {
            return new ProductEntity(product.getPid());
        }
        return null;
    }

    public HashSet<String> getMeta(ProductEntity p) {
        HashSet<String> tags = new HashSet<String>();
        Query ts = em.createQuery("SELECT t.getName() FROM ProductTagEntity pt, TagEntity t " +
                "WHERE t.getTagId() = pt.getTag() AND pt.getPid() = " + p.getPid());
        for(TagEntity pt : (List<TagEntity>) ts.getResultList()) {
            tags.add(pt.getName());
        }
        return tags;
    }

    public void update(UserEntity user, ProductEntity product, int like) {
        Query uwe = em.createQuery("SELECT uw FROM UserWeightEntity uw WHERE uw.getUserId() = " + user.getUserId());
        Query pte = em.createQuery("SELECT pt FROM ProductTagEntity pt WHERE pt.getPid() = " + product.getPid());
        List<ProductTagEntity> tags = (List<ProductTagEntity>) pte.getResultList();
        for(UserWeightEntity userTags : (List<UserWeightEntity>) uwe.getResultList()) {
            if(tags.contains(userTags.getTagId())) {
                userTags.setWeight(userTags.getWeight() + (like * ALPHA * Math.abs(like - userTags.getWeight())));
            }
        }
    }

    public void getTagsFile() {
        try {
            PrintWriter writer = new PrintWriter("allUsedTags", "UTF-8");
            Query ts = em.createQuery("SELECT t FROM TagEntity t");
            List<TagEntity> tags = (List<TagEntity>) ts.getResultList();
            for(TagEntity pTags : tags) {
                writer.println(pTags.getName());
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
