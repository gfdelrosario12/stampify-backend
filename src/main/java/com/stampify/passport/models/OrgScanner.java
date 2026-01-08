package com.stampify.passport.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "scanners")
@DiscriminatorValue("SCANNER")
public class OrgScanner extends User {

    @OneToMany(
            mappedBy = "scanner",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Stamp> stamps;

    public List<Stamp> getStamps() {
        return stamps;
    }

    public void setStamps(List<Stamp> stamps) {
        this.stamps = stamps;
    }
}
