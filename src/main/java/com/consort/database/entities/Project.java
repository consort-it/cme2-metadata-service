package com.consort.database.entities;

import java.util.List;

public class Project extends NewProject{

    private String id;

    private String context;

    private List<Person> team;

    private List<Phase> phases;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Phase> getPhases() {
        return phases;
    }

    public void setPhases(List<Phase> phases) {
        this.phases = phases;
    }

    public List<Person> getTeam() {
        return team;
    }

    public void setTeam(List<Person> team) {
        this.team = team;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
