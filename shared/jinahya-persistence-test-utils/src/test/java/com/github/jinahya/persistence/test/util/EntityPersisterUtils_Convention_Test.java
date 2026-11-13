package com.github.jinahya.persistence.test.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class EntityPersisterUtils_Convention_Test {

//SEP:a sibling persister, and a subclass which declares one of its own

    static class Sup {

    }

    static class SupPersister
            extends AbstractEntityPersister<Sup> {

        SupPersister() {
            super(Sup.class);
        }
    }

    static class Sub
            extends Sup {

    }

    static class SubPersister
            extends AbstractEntityPersister<Sub> {

        SubPersister() {
            super(Sub.class);
        }
    }

//SEP:a nested entity class, whose counterpart is nested in the counterpart of its enclosing class:
//SEP:an arrangement the convention deliberately does not consult

    static class Outer {

        static class Inner {

        }
    }

    static class OuterPersister
            extends AbstractEntityPersister<Outer> {

        OuterPersister() {
            super(Outer.class);
        }

        static class InnerPersister
                extends AbstractEntityPersister<Outer.Inner> {

            InnerPersister() {
                super(Outer.Inner.class);
            }
        }
    }

//SEP:a class following no convention

    static class Bare {

    }

//SEP:an entity carrying both postfixes, and one carrying only the underscored one

    static class Both {

    }

    static class BothPersister
            extends AbstractEntityPersister<Both> {

        BothPersister() {
            super(Both.class);
        }
    }

    static class Both_Persister
            extends AbstractEntityPersister<Both> {

        Both_Persister() {
            super(Both.class);
        }
    }

    static class Underscored {

    }

    static class Underscored_Persister
            extends AbstractEntityPersister<Underscored> {

        Underscored_Persister() {
            super(Underscored.class);
        }
    }

//SEP:an entity whose conventionally named sibling is not an EntityPersister at all

    static class Misnamed {

    }

    static class MisnamedPersister {

    }

    /**
     * A class loader which defines the nested classes of this test itself, and fails one chosen name with a
     * {@link NoClassDefFoundError} -- which is what {@link Class#forName(String, boolean, ClassLoader)} raises for a
     * class whose supertype can not be loaded, and which is an {@link Error}, not a {@link ClassNotFoundException}.
     */
    private static final class UnloadableClassLoader
            extends ClassLoader {

        private UnloadableClassLoader(final String unloadableName) {
            super(EntityPersisterUtils_Convention_Test.class.getClassLoader());
            this.unloadableName = unloadableName;
        }

        @Override
        protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
            if (name.equals(unloadableName)) {
                throw new NoClassDefFoundError(name);
            }
            // only this test's own nested classes are defined here; everything else, the supertypes included,
            // stays with the parent, so a defined class still links against the ordinary types
            if (!name.startsWith(EntityPersisterUtils_Convention_Test.class.getName() + "$")) {
                return super.loadClass(name, resolve);
            }
            synchronized (getClassLoadingLock(name)) {
                final var loaded = findLoadedClass(name);
                if (loaded != null) {
                    return loaded;
                }
                final byte[] bytes;
                try (var stream = getParent().getResourceAsStream(name.replace('.', '/') + ".class")) {
                    if (stream == null) {
                        throw new ClassNotFoundException(name);
                    }
                    bytes = stream.readAllBytes();
                } catch (final IOException ioe) {
                    throw new ClassNotFoundException(name, ioe);
                }
                return defineClass(name, bytes, 0, bytes.length);
            }
        }

        private final String unloadableName;
    }

    // ---------------------------------------------------------------------------------------------------------------------
    @DisplayName("persisterClassOf(Sup.class) -> SupPersister")
    @Test
    void persisterClassOf_SupPersister_Sup() {
        assertThat(EntityPersisterUtils.persisterClassOf(Sup.class)).contains(SupPersister.class);
    }

    @DisplayName("persisterClassOf(Sub.class) -> SubPersister; not the persister of its superclass")
    @Test
    void persisterClassOf_SubPersister_Sub() {
        assertThat(EntityPersisterUtils.persisterClassOf(Sub.class)).contains(SubPersister.class);
    }

    @DisplayName("persisterClassOf(Outer.Inner.class) -> empty;"
                 + " the enclosing chain is not consulted, so a nested entity class has no persister")
    @Test
    void persisterClassOf_Empty_EnclosingChainNotConsulted() {
        assertThat(EntityPersisterUtils.persisterClassOf(Outer.Inner.class)).isEmpty();
    }

    @DisplayName("persisterClassOf(Bare.class) -> empty")
    @Test
    void persisterClassOf_Empty_Bare() {
        assertThat(EntityPersisterUtils.persisterClassOf(Bare.class)).isEmpty();
    }

    @DisplayName("persisterClassOf(Underscored.class) -> Underscored_Persister; the second postfix is probed too")
    @Test
    void persisterClassOf_UnderscoredPersister_Underscored() {
        assertThat(EntityPersisterUtils.persisterClassOf(Underscored.class)).contains(Underscored_Persister.class);
    }

    @DisplayName("persisterClassOf(Both.class) -> BothPersister; \"Persister\" is probed before \"_Persister\"")
    @Test
    void persisterClassOf_TheUnderscorelessOneWins_Both() {
        assertThat(EntityPersisterUtils.persisterClassOf(Both.class)).contains(BothPersister.class);
    }

    @DisplayName("persisterClassOf(Misnamed.class) -> empty;"
                 + " a sibling named by the convention which is not an EntityPersister is passed over")
    @Test
    void persisterClassOf_Empty_SiblingIsNotAPersister() {
        assertThat(EntityPersisterUtils.persisterClassOf(Misnamed.class)).isEmpty();
    }

    @DisplayName("a probe whose every candidate fails to load finds nothing, rather than raising the error")
    @Test
    void persisterClassOf_Empty_EveryCandidateFailsToLoad() throws ClassNotFoundException {
        final var loader = new UnloadableClassLoader(SupPersister.class.getName());
        final var target = loader.loadClass(Sup.class.getName());
        assertThat(EntityPersisterUtils.persisterClassOf(target)).isEmpty();
    }
}
