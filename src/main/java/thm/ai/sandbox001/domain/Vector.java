package thm.ai.sandbox001.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "vectors")
public class Vector {
    @Id
    private String id;

    @Indexed
    private String parentId;

    @Field("name")
    private String name;

    @Field("origin")
    private String origin;

    @Field("sizeOrigin")
    private int sizeOrigin;

    @Field("hashCodeOrigin")
    private int hashCodeOrigin;

    @Field("fileName")
    private String fileName;

    @Field("tags")
    private List<String> tags;


    @Field("vector")
    @Indexed
    private float[] embedding;

    @Override
    public String toString() {
        return "Vector{" +
                "  id:'" + id + "'" +
                ", name:'" + name + "'" +
                ", origin:'" + (origin != null ? "length:" + origin.length() : null) + "'" +
                ", fileName:'" + fileName + "'" +
                ", tags:'" + tags + "'" +
                ", embedding:" + (embedding != null ? "size:" + embedding.length : null) + "'" +
                '}';
    }

}
