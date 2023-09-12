package com.limited.services;


import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class VSPSDServiceApplication extends Application {
    private Set<Object> singletons = new HashSet ();

    public VSPSDServiceApplication () {
        this.singletons.add (new VSPSDService ());
    }

    public Set<Object> getSingletons () {
        return this.singletons;
    }
}