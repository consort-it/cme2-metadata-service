package com.consort.database.entities;

import java.util.List;

public class Phase {

    private String name;

    private List<Service> services;

    public Phase() {}

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setService(final List<Service> service) {
        this.services = services;
    }


}
