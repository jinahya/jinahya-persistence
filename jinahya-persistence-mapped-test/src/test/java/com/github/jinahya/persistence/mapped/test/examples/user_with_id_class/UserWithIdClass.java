package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@IdClass(IdForUserWithIdClass.class)
@Entity
@Table(name = _MappedUserWithIdClass.TABLE_NAME)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class UserWithIdClass extends _MappedUserWithIdClass<IdForUserWithIdClass> {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS
    public static UserWithIdClass newInstance(final String name, final Integer age) {
        return newInstance(UserWithIdClass::new, name, age);
    }
}
