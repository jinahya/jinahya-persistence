package com.github.jinahya.persistence.test.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ___Utils}, covering the whitespace handling of the postfix probe, and the shapes which can, and can
 * not, be instantiated reflectively.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class ___Utils_Test {

    static class Target {

    }

    static class TargetRandomizer extends __Randomizer.___OfEasyRandom<Target> {

        TargetRandomizer() {
            super(Target.class, java.util.List.of());
        }
    }

    /**
     * A record which declares no component, and whose canonical constructor therefore takes no argument.
     */
    record Empty() {

    }

    /**
     * A record which declares components, and a no-argument constructor explicitly.
     */
    record Defaulted(int x, int y) {

        Defaulted() {
            this(0, 0);
        }
    }

    /**
     * A record whose only constructor is a canonical one which takes arguments.
     */
    record Componentized(int x) {

    }

    /**
     * A class loader which defines the nested classes of this test itself, and fails one chosen name with a
     * {@link NoClassDefFoundError} -- which is what {@link Class#forName(String, boolean, ClassLoader)} raises for a
     * class whose supertype can not be loaded, and which is an {@link Error}, not a {@link ClassNotFoundException}.
     */
    private static final class UnloadableClassLoader extends ClassLoader {

        private UnloadableClassLoader(final String unloadableName) {
            super(___Utils_Test.class.getClassLoader());
            this.unloadableName = unloadableName;
        }

        @Override
        protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
            if (name.equals(unloadableName)) {
                throw new NoClassDefFoundError(name);
            }
            // only this test's own nested classes are defined here; everything else, the supertypes included,
            // stays with the parent, so a defined class still links against the ordinary types
            if (!name.startsWith(___Utils_Test.class.getName() + "$")) {
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

    @DisplayName("a blank postfix is skipped, whatever kind of whitespace it is")
    @ParameterizedTest
    // ordinary space, tab, newline, and U+2003 EM SPACE, which Character.isWhitespace accepts
    @ValueSource(strings = {"", " ", "\t", "\n", " ", "  \t  "})
    void __blankPostfixIsSkipped(final String postfix) {
        assertThat(___Utils.siblingClassForPostfixes(Target.class, postfix)).isNull();
    }

    @DisplayName("a non-blank postfix is stripped before the name is built")
    @Test
    void __postfixIsStrippedForTheName() {
        // "  Randomizer  " has to locate TargetRandomizer, i.e. the surrounding whitespace is
        // normalized away rather than becoming part of the class name
        assertThat(___Utils.siblingClassForPostfixes(Target.class, "  Randomizer  "))
                .isEqualTo(TargetRandomizer.class);
    }

    @DisplayName("an all-blank postfix list finds nothing rather than probing an empty name")
    @Test
    void __allBlank() {
        assertThat(___Utils.siblingClassForPostfixes(Target.class, " ", "\t")).isNull();
    }

    @DisplayName("no postfixes at all is rejected")
    @Test
    void _IllegalArgumentException_NoPostfixes() {
        assertThatThrownBy(() -> ___Utils.siblingClassForPostfixes(Target.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("a probe whose every candidate fails to load finds nothing, rather than raising the error")
    @Test
    void __linkageErrorIsNotPropagated() throws ClassNotFoundException {
        final var loader = new UnloadableClassLoader(TargetRandomizer.class.getName());
        final var target = loader.loadClass(Target.class.getName());
        assertThat(___Utils.siblingClassForPostfixes(target, "Randomizer")).isNull();
    }

    // -----------------------------------------------------------------------------------------------------------------

    @DisplayName(
            "a record which has a no-argument constructor is instantiated, rather than rejected for being a record")
    @ParameterizedTest
    @ValueSource(classes = {Empty.class, Defaulted.class})
    void newInstance__RecordWithANoArgConstructor(final Class<?> clazz) {
        assertThat(___Utils.reasonNotInstantiable(clazz)).isNull();
        assertThat(___Utils.newInstance(clazz)).isNotNull();
    }

    @DisplayName("a record which has no no-argument constructor is reported as such, rather than as a record")
    @Test
    void newInstance_RuntimeException_RecordWithoutANoArgConstructor() {
        assertThat(___Utils.reasonNotInstantiable(Componentized.class)).isNull();
        assertThatThrownBy(() -> ___Utils.newInstance(Componentized.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no no-arg constructor");
    }
}
