package com.github.jinahya.persistence.encryption.hibernate;

import org.h2.api.Trigger;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An H2 trigger which records every row the database is asked to write, so that a value which is written and then
 * overwritten can still be seen.
 * <p>
 * A row which never reaches the database cannot be recovered from the final table contents; this is the only way to
 * tell "the column ends up NULL" from "the column was never written with anything else". A binary column is kept as
 * written, and searched for the UTF-8 form of a text: a plaintext handed to a binary column is found as well as one
 * handed to a text column.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class WriteRecorder implements Trigger {

    /**
     * A row written: its non-binary columns rendered as text, and its binary columns as written.
     */
    record Write(String text, List<byte[]> binaries) {

        boolean contains(final String value) {
            if (text.contains(value)) {
                return true;
            }
            final var bytes = value.getBytes(StandardCharsets.UTF_8);
            return binaries.stream().anyMatch(b -> indexOf(b, bytes) >= 0);
        }

        private static int indexOf(final byte[] haystack, final byte[] needle) {
            outer:
            for (int i = 0; i <= haystack.length - needle.length; i++) {
                for (int j = 0; j < needle.length; j++) {
                    if (haystack[i + j] != needle[j]) {
                        continue outer;
                    }
                }
                return i;
            }
            return -1;
        }
    }

    /**
     * Every row written since the last {@link #clear()}.
     */
    static final List<Write> WRITES = Collections.synchronizedList(new ArrayList<>());

    static void clear() {
        WRITES.clear();
    }

    static int count() {
        return WRITES.size();
    }

    /**
     * Tells whether any row written held the text, in a text column or, UTF-8 encoded, inside a binary one.
     */
    static boolean anyWriteContains(final String text) {
        synchronized (WRITES) {
            return WRITES.stream().anyMatch(w -> w.contains(text));
        }
    }

    @Override
    public void fire(final Connection conn, final Object[] oldRow, final Object[] newRow) {
        if (newRow == null) {
            return;
        }
        final var builder = new StringBuilder();
        final var binaries = new ArrayList<byte[]>();
        for (final var value : newRow) {
            if (value instanceof byte[] bytes) {
                binaries.add(bytes.clone());
                builder.append('<').append(bytes.length).append(" bytes>");
            } else {
                builder.append(value);
            }
            builder.append('|');
        }
        WRITES.add(new Write(builder.toString(), binaries));
    }
}
