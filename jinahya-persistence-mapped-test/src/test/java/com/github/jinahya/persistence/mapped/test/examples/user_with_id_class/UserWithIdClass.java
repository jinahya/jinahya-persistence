package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;

@IdClass(IdForUserWithIdClass.class)
@Entity
@Table(name = _MappedUserWithIdClass.TABLE_NAME)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class UserWithIdClass extends _MappedUserWithIdClass<IdForUserWithIdClass> {

    @Serial
    private static final long serialVersionUID = -2013583858263692371L;
}
