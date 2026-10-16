package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.AnotherEntityEncryptionServiceImpl;
import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.EntityEncryptionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The {@code ServiceLoader} path, through a class loader of the test's own, so that what {@code META-INF/services/}
 * names is the test's to decide: none, one, or two.
 */
@SuppressWarnings({"java:S101"})
class EntityEncryptionServices_Test {

    private static final String RESOURCE = "META-INF/services/" + EntityEncryptionService.class.getName();

    /**
     * A class loader which answers the service resource with the given files only, and delegates the rest.
     */
    private static final class Naming extends ClassLoader {

        private final List<URL> entries;

        private Naming(final List<URL> entries) {
            super(EntityEncryptionServices_Test.class.getClassLoader());
            this.entries = entries;
        }

        @Override
        public Enumeration<URL> getResources(final String name) throws IOException {
            if (RESOURCE.equals(name)) {
                return Collections.enumeration(entries);
            }
            return super.getResources(name);
        }
    }

    private static URL naming(final Path dir, final String... names) throws IOException {
        final var file = dir.resolve("services-" + names.length);
        Files.write(file, List.of(names), StandardCharsets.UTF_8);
        return file.toUri().toURL();
    }

    @DisplayName("no implementation named: fails, saying how to provide one")
    @Test
    void none() {
        assertThatThrownBy(() -> EntityEncryptionServices.find(new Naming(List.of())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no ")
                .hasMessageContaining(RESOURCE);
    }

    @DisplayName("one implementation named: found")
    @Test
    void one(@TempDir final Path dir) throws IOException {
        final var found = EntityEncryptionServices.find(
                new Naming(List.of(naming(dir, EntityEncryptionServiceImpl.class.getName()))));
        assertThat(found).isInstanceOf(EntityEncryptionServiceImpl.class);
    }

    @DisplayName("two implementations named: fails, naming both")
    @Test
    void two(@TempDir final Path dir) throws IOException {
        final var loader = new Naming(List.of(naming(
                dir, EntityEncryptionServiceImpl.class.getName(), AnotherEntityEncryptionServiceImpl.class.getName())));
        assertThatThrownBy(() -> EntityEncryptionServices.find(loader))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("more than one")
                .hasMessageContaining(EntityEncryptionServiceImpl.class.getName())
                .hasMessageContaining(AnotherEntityEncryptionServiceImpl.class.getName());
    }
}
