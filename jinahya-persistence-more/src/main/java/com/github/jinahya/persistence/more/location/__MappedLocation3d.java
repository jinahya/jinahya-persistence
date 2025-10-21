package com.github.jinahya.persistence.more.location;

import com.github.jinahya.persistence.mapped.__Mapped;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedLocation3d extends  __Mapped {

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_LATITUDE = __MappedLocation2d.COLUMN_NAME_LATITUDE;

    public static final String ATTRIBUTE_NAME_LATITUDE = __MappedLocation2d.ATTRIBUTE_NAME_LATITUDE;

    public static final String DECIMAL_MIN_LATITUDE = __MappedLocation2d.DECIMAL_MIN_LATITUDE;

    public static final String DECIMAL_MAX_LATITUDE = __MappedLocation2d.DECIMAL_MAX_LATITUDE;

    public static final double MIN_LATITUDE = __MappedLocation2d.MIN_LATITUDE;

    public static final double MAX_LATITUDE = __MappedLocation2d.MAX_LATITUDE;

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_LONGITUDE = __MappedLocation2d.COLUMN_NAME_LONGITUDE;

    public static final String ATTRIBUTE_NAME_LONGITUDE = __MappedLocation2d.ATTRIBUTE_NAME_LONGITUDE;

    public static final String DECIMAL_MIN_LONGITUDE = __MappedLocation2d.DECIMAL_MIN_LONGITUDE;

    public static final String DECIMAL_MAX_LONGITUDE = __MappedLocation2d.DECIMAL_MAX_LONGITUDE;

    public static final double MIN_LONGITUDE = __MappedLocation2d.MIN_LONGITUDE;

    public static final double MAX_LONGITUDE = __MappedLocation2d.MAX_LONGITUDE;

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_ALTITUDE = "altitude";

    public static final String ATTRIBUTE_NAME_ALTITUDE = "altitude";

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedLocation3d() {
        super();
    }

    protected __MappedLocation3d(final __MappedLocation3dBuilder<?, ?> builder) {
        super();
        latitude = builder.latitude();
        longitude = builder.longitude();
        altitude = builder.altitude();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "latitude=" + latitude +
               ",longitude=" + longitude +
               ",altitude=" + altitude +
               '}';
    }

    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof __MappedLocation3d that)) {
            return false;
        }
//        return Objects.equals(latitude, that.latitude)
//               && Objects.equals(longitude, that.longitude);
        return (latitude != null && latitude.compareTo(that.latitude) == 0)
               && (longitude != null && longitude.compareTo(that.longitude) == 0)
               && (altitude != null && altitude.compareTo(that.altitude) == 0);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(latitude, longitude, altitude);
    }

    // -------------------------------------------------------------------------------------------------------- latitude
    @Nonnull
    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(@Nonnull final BigDecimal latitude) {
        this.latitude = latitude;
    }

    public <N extends Number> N getLatitudeAsMapped(final Function<? super BigDecimal, ? extends N> mapper) {
        return Optional.ofNullable(getLatitude())
                .map(v -> Objects.requireNonNull(mapper, "mapper is null").apply(v))
                .orElse(null);
    }

    public <N extends Number> void setLatitudeFromMapped(final N latitude,
                                                         final Function<? super N, ? extends BigDecimal> mapper) {
        setLatitude(
                Optional.ofNullable(latitude)
                        .map(v -> Objects.requireNonNull(mapper, "mapper is null").apply(v))
                        .orElse(null)
        );
    }

    @Transient
    public Double getLatitudeAsDouble() {
        return getLatitudeAsMapped(BigDecimal::doubleValue);
    }

    @Transient
    public void setLatitudeFromDouble(final Double latitude) {
        setLatitudeFromMapped(latitude, BigDecimal::valueOf);
    }

    @Transient
    public Float getLatitudeAsFloat() {
        return getLatitudeAsMapped(BigDecimal::floatValue);
    }

    @Transient
    public void setLatitudeFromFloat(final Float latitude) {
        setLatitudeFromMapped(latitude, BigDecimal::valueOf);
    }

    // ------------------------------------------------------------------------------------------------------- longitude
    @Nonnull
    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(@Nonnull final BigDecimal longitude) {
        this.longitude = longitude;
    }

    public <N extends Number> N getLongitudeAsMapped(final Function<? super BigDecimal, ? extends N> mapper) {
        return Optional.ofNullable(getLongitude())
                .map(v -> Objects.requireNonNull(mapper, "mapper is null").apply(v))
                .orElse(null);
    }

    public <N extends Number> void setLongitudeFromMapped(final N longitude,
                                                          final Function<? super N, ? extends BigDecimal> mapper) {
        setLongitude(
                Optional.ofNullable(longitude)
                        .map(v -> Objects.requireNonNull(mapper, "mapper is null").apply(v))
                        .orElse(null)
        );
    }

    @Transient
    public Double getLongitudeAsDouble() {
        return getLongitudeAsMapped(BigDecimal::doubleValue);
    }

    @Transient
    public void setLongitudeFromDouble(final Double longitude) {
        setLongitudeFromMapped(longitude, BigDecimal::valueOf);
    }

    @Transient
    public Float getLongitudeAsFloat() {
        return getLongitudeAsMapped(BigDecimal::floatValue);
    }

    @Transient
    public void setLongitudeFromFloat(final Float longitude) {
        setLongitudeFromMapped(longitude, BigDecimal::valueOf);
    }

    // -------------------------------------------------------------------------------------------------------- altitude
    @Nonnull
    public BigDecimal getAltitude() {
        return altitude;
    }

    public void setAltitude(@Nonnull final BigDecimal altitude) {
        this.altitude = altitude;
    }

    public <N extends Number> N getAltitudeAsMapped(final Function<? super BigDecimal, ? extends N> mapper) {
        return Optional.ofNullable(getAltitude())
                .map(v -> Objects.requireNonNull(mapper, "mapper is null").apply(v))
                .orElse(null);
    }

    public <N extends Number> void setAltitudeFromMapped(final N altitude,
                                                         final Function<? super N, ? extends BigDecimal> mapper) {
        setAltitude(
                Optional.ofNullable(altitude)
                        .map(v -> Objects.requireNonNull(mapper, "mapper is null").apply(v))
                        .orElse(null)
        );
    }

    @Transient
    public Double getAltitudeAsDouble() {
        return getAltitudeAsMapped(BigDecimal::doubleValue);
    }

    @Transient
    public void setAltitudeFromDouble(final Double altitude) {
        setAltitudeFromMapped(altitude, BigDecimal::valueOf);
    }

    @Transient
    public Float getAltitudeAsFloat() {
        return getAltitudeAsMapped(BigDecimal::floatValue);
    }

    @Transient
    public void setAltitudeFromFloat(final Float altitude) {
        setAltitudeFromMapped(altitude, BigDecimal::valueOf);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @DecimalMax(DECIMAL_MAX_LATITUDE)
    @DecimalMin(DECIMAL_MIN_LATITUDE)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_LATITUDE, nullable = false, insertable = true, updatable = true)
    private BigDecimal latitude;

    @Nonnull
    @DecimalMax(DECIMAL_MAX_LONGITUDE)
    @DecimalMin(DECIMAL_MIN_LONGITUDE)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_LONGITUDE, nullable = false, insertable = true, updatable = true)
    private BigDecimal longitude;

    @Nonnull
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_ALTITUDE, nullable = false, insertable = true, updatable = true)
    private BigDecimal altitude;
}
