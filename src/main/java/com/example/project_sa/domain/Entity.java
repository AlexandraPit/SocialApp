package com.example.project_sa.domain;

import java.io.Serializable;
import java.util.Objects;

public class Entity<ID> implements Serializable {
    /*the id attribute will be of type ID.*/
    protected  ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        /*if this object is equal to the other Object obj, return true*/
        if (this == obj) return true;

        /*if Object obj is not an instance of Entity, return false.*/
        if (!(obj instanceof Entity)) return false;

        /*Cast for the Object obj.*/
        Entity<?> entity=(Entity<?>) obj;
        return this.getId().equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}
