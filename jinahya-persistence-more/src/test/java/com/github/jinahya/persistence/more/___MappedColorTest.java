package com.github.jinahya.persistence.more;

import org.mockito.Mockito;

import java.util.Objects;

abstract class ___MappedColorTest<T extends ___MappedColor2<T>> {

    ___MappedColorTest(final Class<T> colorClass) {
        super();
        this.colorClass = Objects.requireNonNull(colorClass, "colorClass is null");
    }

    // ------------------------------------------------------------------------------------------------------ colorClass
    T newColorInstance() {
        try {
            final var constructor = colorClass.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException("failed to create a new instance of " + colorClass, e);
        }
    }

    T newColorInstanceSpy() {
        return Mockito.spy(newColorInstance());
    }

    // -----------------------------------------------------------------------------------------------------------------
    final Class<T> colorClass;
}
