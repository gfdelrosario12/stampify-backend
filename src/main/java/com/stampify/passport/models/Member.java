package com.stampify.passport.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "members")
@DiscriminatorValue("MEMBER")
public class Member extends User {

    @OneToMany(
            mappedBy = "member",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Passport> passports;

    public List<Passport> getPassports() {
        return passports;
    }

    public void setPassports(List<Passport> passports) {
        this.passports = passports;
    }
}
