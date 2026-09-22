package com.example.downstream;

import com.github.jinahya.persistence.more.converter.__AttributeEnumConverter;

/** The converter a downstream registers for {@link DownstreamStatus}. */
public class DownstreamStatusConverter extends __AttributeEnumConverter.__OfString<DownstreamStatus> {

    public DownstreamStatusConverter() {
        super(DownstreamStatus.class);
    }
}
