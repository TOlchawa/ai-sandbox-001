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

    @Field("name")
    private String name;

    @Field("origin")
    private String origin;

    @Field("fileName")
    private String fileName;

    @Field("tags")
    private List<String> tags;


    @Field("vector")
    @Indexed
    private List<Double> vector;

    @Override
    public String toString() {
        return "Vector{" +
                "  id:'" + id + "'" +
                ", name:'" + name + "'" +
                ", origin:'" + (origin != null ? "length:" + origin.length() : null) + "'" +
                ", fileName:'" + fileName + "'" +
                ", tags:'" + tags + "'" +
                ", vactor:" + (vector != null ? "size:" + vector.size() : null) + "'" +
                '}';
    }

}
