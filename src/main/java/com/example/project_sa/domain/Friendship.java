package com.example.project_sa.domain;

import java.time.LocalDateTime;

public class Friendship extends Entity<Tuple<Long, Long>>{
    LocalDateTime friendsFrom;
    /* Used in updating in the database*/
    public Friendship(Long u1, Long u2)
    {
        super.setId(new Tuple<Long, Long>(u1,u2));
        this.friendsFrom = LocalDateTime.now();
    }

    public void setFriendsFrom(LocalDateTime friendsFrom){this.friendsFrom=friendsFrom;}
    /**
     *
     * @return the date when the friendship was created
     */

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    @Override
    public String toString() {

        return "Friendship id= ("+ this.getId().toString()+") | Date= ("+this.getFriendsFrom()+")";
    }
}
