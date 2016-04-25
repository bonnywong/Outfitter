package Models;

import javax.jws.soap.SOAPBinding;
import javax.persistence.*;

/**
 * This entity associates a user with a meta-tag and a weight.
 * Created by swebo_000 on 2016-04-22.
 */
@Entity
@Table(name = "user_weights", schema = "", catalog = "")
public class UserWeightEntity {
    private int id;
    private int userId;
    private int tagId;
    private double weight;

    public UserWeightEntity() {

    }

    public UserWeightEntity(int userId, int tagId, double weight) {
        this.userId = userId;
        this.tagId = tagId;
        this.weight = weight;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "user_id")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "tag_id")
    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    @Basic
    @Column(name = "weight")
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserWeightEntity that = (UserWeightEntity) o;

        if (id != that.id) return false;
        if (userId != that.userId) return false;
        if (tagId != that.tagId) return false;
        if (Double.compare(that.weight, weight) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + userId;
        result = 31 * result + tagId;
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
