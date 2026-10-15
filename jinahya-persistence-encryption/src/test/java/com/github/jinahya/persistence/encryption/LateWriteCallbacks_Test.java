package com.github.jinahya.persistence.encryption;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.ExcludeSuperclassListeners;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies which callbacks {@link LateWriteCallbacks} finds after the encryption listener, from annotations only.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://github.com/jinahya/jinahya-persistence/issues/101">#101</a>
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "unused"
})
class LateWriteCallbacks_Test {

    static class Ours extends EntityEncryptionListener {
    }

    static class Writer {

        @PreUpdate
        void preUpdate(final Object entity) {
        }
    }

    static class Auditor {

        @PrePersist
        void prePersist(final Object entity) {
        }
    }

    static class Reader {
    }

    @EntityListeners(Ours.class)
    static class Clean {
    }

    @EntityListeners(Ours.class)
    static class OwnCallback {

        @PreUpdate
        private void touch() {
        }
    }

    static class BaseWithCallback {

        @PrePersist
        void created() {
        }
    }

    @EntityListeners(Ours.class)
    static class InheritedCallback extends BaseWithCallback {
    }

    @EntityListeners({Auditor.class, Ours.class})
    static class ListenerBefore {
    }

    @EntityListeners({Ours.class, Writer.class, Reader.class})
    static class ListenerAfter {
    }

    @EntityListeners(Ours.class)
    static class SuperclassOurs {
    }

    @EntityListeners(Writer.class)
    static class SubclassListener extends SuperclassOurs {
    }

    @EntityListeners(Writer.class)
    static class SuperclassWriter {
    }

    @ExcludeSuperclassListeners
    @EntityListeners(Ours.class)
    static class ExcludedSuperclassWriter extends SuperclassWriter {
    }

    @EntityListeners({Ours.class, LifecycleListenerEntity.class})
    static class SecondEncryptionListener {
    }

    @EntityListeners(Writer.class)
    static class OursElsewhere {

        @PreUpdate
        void touch() {
        }
    }

    @DisplayName("an entity with no callback, and nothing after the listener, has none")
    @Test
    void __clean() {
        assertThat(LateWriteCallbacks.find(Clean.class, Ours.class)).isEmpty();
    }

    @DisplayName("an entity's own callback, even a private one, always runs after")
    @Test
    void __ownCallback() {
        assertThat(LateWriteCallbacks.find(OwnCallback.class, Ours.class))
                .singleElement().asString().startsWith("@PreUpdate").endsWith("OwnCallback.touch()");
    }

    @DisplayName("a superclass's callback runs after")
    @Test
    void __inheritedCallback() {
        assertThat(LateWriteCallbacks.find(InheritedCallback.class, Ours.class))
                .singleElement().asString().startsWith("@PrePersist").endsWith("BaseWithCallback.created()");
    }

    @DisplayName("a listener declared before the encryption listener runs before it")
    @Test
    void __listenerBefore() {
        assertThat(LateWriteCallbacks.find(ListenerBefore.class, Ours.class)).isEmpty();
    }

    @DisplayName("a listener declared after the encryption listener, with a write callback, runs after")
    @Test
    void __listenerAfter() {
        assertThat(LateWriteCallbacks.find(ListenerAfter.class, Ours.class))
                .singleElement().asString().endsWith("Writer.preUpdate()");
    }

    @DisplayName("a subclass's listener runs after its superclass's encryption listener")
    @Test
    void __subclassListener() {
        assertThat(LateWriteCallbacks.find(SubclassListener.class, Ours.class))
                .singleElement().asString().endsWith("Writer.preUpdate()");
    }

    @DisplayName("@ExcludeSuperclassListeners drops a superclass's listener")
    @Test
    void __excluded() {
        assertThat(LateWriteCallbacks.find(ExcludedSuperclassWriter.class, Ours.class)).isEmpty();
    }

    @DisplayName("another encryption listener after it is not a late writer")
    @Test
    void __secondEncryptionListener() {
        assertThat(LateWriteCallbacks.find(SecondEncryptionListener.class, Ours.class)).isEmpty();
    }

    @DisplayName("when the encryption listener isn't annotated, only the entity's own callbacks are known to run after")
    @Test
    void __oursElsewhere() {
        assertThat(LateWriteCallbacks.find(OursElsewhere.class, Ours.class))
                .singleElement().asString().endsWith("OursElsewhere.touch()");
    }
}
