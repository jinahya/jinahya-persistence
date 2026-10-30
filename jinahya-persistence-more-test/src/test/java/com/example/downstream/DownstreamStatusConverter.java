package com.example.downstream;

import com.github.jinahya.persistence.more.converter.AttributeEnumConverter;

/** The converter a downstream registers for {@link DownstreamStatus}. */
public class DownstreamStatusConverter extends AttributeEnumConverter.OfString<DownstreamStatus> {

    public DownstreamStatusConverter() {
        super(DownstreamStatus.class);
    }
}
