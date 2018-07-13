package com.example.asus.prueba_android.Classes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Routes {

    private String id;
    private String location,destination, locationLat,locationLon,desLat,desLon;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    public Routes(){
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Routes");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
    }

    public String getLocationLon() {
        return locationLon;
    }

    public void setLocationLon(String locationLon) {
        this.locationLon = locationLon;
    }

    public String getDesLat() {
        return desLat;
    }

    public void setDesLat(String desLat) {
        this.desLat = desLat;
    }

    public String getDesLon() {
        return desLon;
    }

    public void setDesLon(String desLon) {
        this.desLon = desLon;
    }
    public void firebaseSave()
    {
        if(this!=null)
            if(this.id!=null&&id.length()>0)
            {
                String referenciaa=id;
                reference.child(referenciaa).setValue(this);
                id=referenciaa;

            }
            else
                reference.push().setValue(this);

    }
    public void delete()
    {
        reference.child(getId()).removeValue();
    }
}
