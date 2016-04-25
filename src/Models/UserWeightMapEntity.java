package Models;

import javax.persistence.*;

/**
 * This entity is a map between a user and a tag.
 * Created by swebo_000 on 2016-04-25.
 */
@Entity
@Table(name = "user_weight_maps", schema = "", catalog = "")
public class UserWeightMapEntity {
    private Integer id;
    private int userId;
    private int tagId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserWeightMapEntity that = (UserWeightMapEntity) o;

        if (userId != that.userId) return false;
        if (tagId != that.tagId) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + userId;
        result = 31 * result + tagId;
        return result;
    }
}
