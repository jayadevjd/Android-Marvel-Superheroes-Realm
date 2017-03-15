package xyz.jayadev.marvel;

import io.realm.RealmObject;

/**
 * Created by Jayadev on 23/05/16.
 */
public class DataModel extends RealmObject {


    private String name;
    private int id;
    private String image;

    public DataModel() {

    }


    public DataModel(String name, int id, String image) {
        this.name = name;
        this.id = id;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setId(int id) {
        this.id = id;
    }

}

