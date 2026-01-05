package com.stampify.passport.models;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
@DiscriminatorValue("ADMIN")
public class Admin extends User {
}
