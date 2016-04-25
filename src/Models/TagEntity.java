package Models;

import javax.persistence.*;
import javax.swing.text.html.HTML;

/**
 * This entity represents a single Meta-tag.
 *
 * Created by swebo_000 on 2016-04-25.
 */
@Entity
@Table(name = "tags", schema = "", catalog = "")
public class TagEntity {
    private int tagId;
    private String name;

    public TagEntity() {

    }

    public TagEntity(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagEntity that = (TagEntity) o;

        if (tagId != that.tagId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tagId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
