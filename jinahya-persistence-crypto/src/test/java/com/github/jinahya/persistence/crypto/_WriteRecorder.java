package com.github.jinahya.persistence.crypto;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An H2 trigger which records every row the database is asked to write, so that a value which is written and then
 * overwritten can still be seen.
 * <p>
 * A row which never reaches the database cannot be recovered from the final table contents; this is the only way to
 * tell "the column ends up NULL" from "the column was never written with anything else".
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class _WriteRecorder implements Trigger {

    /**
     * Every row written since the last {@link #clear()}, rendered as text.
     */
    static final List<String> WRITES = Collections.synchronizedList(new ArrayList<>());

    static void clear() {
        WRITES.clear();
    }

    /**
     * Returns whether any recorded write carried the specified text.
     *
     * @param text the text to look for.
     * @return {@code true} when at least one recorded row contains the {@code text}.
     */
    static boolean anyWriteContains(final String text) {
        synchronized (WRITES) {
            return WRITES.stream().anyMatch(v -> v.contains(text));
        }
    }

    @Override
    public void fire(final Connection conn, final Object[] oldRow, final Object[] newRow) {
        if (newRow == null) {
            return;
        }
        final var builder = new StringBuilder();
        for (final var value : newRow) {
            builder.append(value instanceof byte[] bytes ? "<" + bytes.length + " bytes>" : String.valueOf(value))
                    .append('|');
        }
        WRITES.add(builder.toString());
    }
}
