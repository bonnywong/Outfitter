package Authentication;

import Models.UserEntity;
import persist.AccountStore;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Logic for handling user authentication and
 * registration.
 *
 * Created by swebo_000 on 2016-05-01.
 */
public class UserLogic {

    AccountStore db = new AccountStore();

    public boolean authUser(String username, String password) {

        if (db.userExists(username)) {
            UserEntity user = db.fetchUser(username);
            String saltString = user.getSalt();
            String passwordHash = user.getPassword();
            byte[] salt = new byte[] {};
            try {
                salt = new BASE64Decoder().decodeBuffer(saltString);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            return passwordHash.equals(hashPassword(password, salt));
        } else {
            //User doesn't exist.
            return false;
        }
    }

    public boolean registerUser(String username, String password, String email) {
        if (db.userExists(username)){
            return false;
        } else {
            byte[] salt = generateSalt();
            String saltString = new BASE64Encoder().encode(salt).toString();
            String hashedPassword = hashPassword(password, salt);

            UserEntity user = new UserEntity(username, email, hashedPassword, saltString, "user");
            db.persistUser(user);
            return true;
        }
    }


    //A bit unnecessary maybe.
    public UserEntity getUser(String username) {
        if (db.userExists(username)) {
            UserEntity user = db.fetchUser(username);
            System.out.println("\nFetched user with username: " + user.getUsername() + "\n");
            return db.fetchUser(username);
        } else {
            return null;
        }
    }

    /**
     * Generates a random 256-bit salt value.
     **/
    public byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Hashes a password string and its salt.
     * @param password
     * @param salt
     * @return
     */
    public String hashPassword(String password, byte[] salt ) {
        String hashedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            hashedPassword = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }

    public void close() {
        db.close();
    }
}
