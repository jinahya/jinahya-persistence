package com.github.jinahya.persistence.more.entity_relationships.example.__unidirectional_one_to_one;

import com.github.jinahya.persistence.more.entity_relationships.__UnidirectionalOneToOne;
import com.github.jinahya.persistence.more.entity_relationships.___Owned;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(catalog = "entity_relationships", schema = "unidirectional_one_to_one", name = "EMPLOYEE")
public class Employee implements __UnidirectionalOneToOne<TravelProfile> {

    private TravelProfile profile;

    @___Owned(value = TravelProfile.class, owner = Employee.class)
    @OneToOne
    @JoinColumn(name = "PROFILE_<PK of TRAVELPROFILE>")
    public TravelProfile getProfile() {
        return profile;
    }

    public void setProfile(TravelProfile profile) {
        this.profile = profile;
    }

    // ...
}
