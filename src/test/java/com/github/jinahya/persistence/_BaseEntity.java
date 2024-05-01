package com.github.jinahya.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
public abstract class _BaseEntity
        implements Serializable {

    @Serial
    private static final long serialVersionUID = 7911338123166632365L;

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = _BaseEntityConstants.COLUMN_NAME_ID, nullable = false, insertable = true, updatable = false)
    private Long id;
}
