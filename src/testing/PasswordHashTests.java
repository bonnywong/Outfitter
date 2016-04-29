package testing;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by swebo_000 on 2016-04-27.
 */
public class PasswordHashTests {

    public static void main(String[] args) {
        String password = "abc123";
        byte[] salt = generateSalt();
        String saltString = new BASE64Encoder().encode(salt).toString();
        String hashedPassword = hashPassword(password, salt);


        System.out.println("Password: " + password);
        System.out.println("Hash: " + saltString);
        System.out.println("Hashed Password: " + hashedPassword);
        //
        //System.out.println(hashPassword(password, salt));
    }

    public boolean check() {
        return true;
    }

    /**
     * Generates a random 256-bit salt value.
     *
     * @return byte[]
     */
    public static byte[] generateSalt() {
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
    public static String hashPassword(String password, byte[] salt ) {
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
}
