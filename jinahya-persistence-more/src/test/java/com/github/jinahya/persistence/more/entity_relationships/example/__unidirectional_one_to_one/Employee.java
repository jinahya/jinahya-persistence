package com.github.jinahya.persistence.more.entity_relationships.example.__unidirectional_one_to_one;

import com.github.jinahya.persistence.more.entity_relationships.__UnidirectionalOneToOne;
import com.github.jinahya.persistence.more.entity_relationships.___OwningSide;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@___OwningSide
@Entity
@Table(catalog = "entity_relationships", schema = "unidirectional_one_to_one", name = "EMPLOYEE")
public class Employee implements __UnidirectionalOneToOne<TravelProfile> {

    @JoinColumn(name = "PROFILE_<PK of TRAVELPROFILE>")
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
