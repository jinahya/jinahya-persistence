package com.github.jinahya.persistence.more.misc.example.unidirectional_one_to_one;

import com.github.jinahya.persistence.more.misc.__UnidirectionalOneToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class Employee implements __UnidirectionalOneToOne<Employee, TravelProfile> {

    private TravelProfile profile;

    @OneToOne
    public TravelProfile getProfile() {
        return profile;
    }

    public void setProfile(TravelProfile profile) {
        this.profile = profile;
    }

    // ...
}
