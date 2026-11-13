package com.github.jinahya.persistence.more.converter;

import com.github.jinahya.persistence.more.AttributeEnum;

/**
 * A shared interface of sizes, which a library defines with {@link _Size}, and to which a downstream adds
 * {@link _ExtendedSize}.
 *
 * @param <S> self type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AttributeEnumConverter3
 */
interface _ISize<S extends Enum<S> & _ISize<S>> extends AttributeEnum.OfString<S> {

}
