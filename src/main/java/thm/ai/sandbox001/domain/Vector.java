package thm.ai.sandbox001.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "vectors")
public class Vector {
    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("description")
    private int description;

    @Field("size")
    private int size;

    @Field("origin")
    private String origin;


    @Field("tags")
    private String tags;


    @Field("vector")
    private List<Double> vector;

}
