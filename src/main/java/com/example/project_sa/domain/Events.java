package com.example.project_sa.domain;

import com.example.project_sa.domain.Entity;

import java.sql.Date;
import java.util.Objects;

public class Events extends Entity<Long> {
private String event_name;
private Date date;
private String location;
public Events(String event_name,String location,Date date){
    this.event_name=event_name;
    this.location=location;
    this.date=date;

}
    public String getEvent_name() {
        return event_name;
    }

    public Date getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Events that = (Events) o;
        return Objects.equals(getEvent_name(), that.getEvent_name()) && Objects.equals(getDate(), that.getDate()) && Objects.equals(getLocation(), that.getLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEvent_name(), getLocation(), getDate());
    }

    @Override
    public String toString() {
        return
               /* "ID = "+ id+" | "+*/
                        "Event name = '" + event_name + "' |   " +
                        "Location = '" + location + "' |   " +
                        "Date = '"+date+"' "
                ;
    }

    public String forListView(){
        return "Event name: "+this.event_name+ " | Location: "+this.location+" | Date: "+this.date;
    }
}
