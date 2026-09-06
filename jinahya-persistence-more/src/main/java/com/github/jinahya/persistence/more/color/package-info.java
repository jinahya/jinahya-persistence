/**
 * Abstract mapped superclasses for colors, of several color models, sharing one way of addressing their components.
 * <p>
 * {@link com.github.jinahya.persistence.more.color.___MappedColor} is the root. It maps no column of its own; it fixes
 * only what every color model has in common — a component count, normalized access to each component, an alpha, and a
 * conversion to and from <a href="https://www.w3.org/TR/css-color-4/#numeric-srgb">sRGB</a> — and leaves each model to
 * declare the columns natural to it. Code which does not care which model a color is in can still read it, render it as
 * a {@linkplain com.github.jinahya.persistence.more.color.___MappedColor#toHexNotation() hex notation}, or serialize it
 * in either the {@linkplain com.github.jinahya.persistence.more.color.___MappedColor#toLegacyRgbNotation() legacy} or
 * the {@linkplain com.github.jinahya.persistence.more.color.___MappedColor#toModernRgbNotation() modern} {@code rgb()}
 * syntax of CSS Color 4.
 *
 * <h2>Models</h2>
 * <ul>
 *   <li>{@link com.github.jinahya.persistence.more.color.__MappedRgb} —
 *       {@code red}, {@code green}, {@code blue}</li>
 *   <li>{@link com.github.jinahya.persistence.more.color.__MappedRgba} — the above, plus {@code alpha}</li>
 *   <li>{@link com.github.jinahya.persistence.more.color.__MappedCmyk} —
 *       {@code cyan}, {@code magenta}, {@code yellow}, {@code black}</li>
 *   <li>{@link com.github.jinahya.persistence.more.color.__MappedHsl} —
 *       {@code hue}, {@code saturation}, {@code lightness}</li>
 *   <li>{@link com.github.jinahya.persistence.more.color.__MappedHwb} —
 *       {@code hue}, {@code whiteness}, {@code blackness}</li>
 * </ul>
 * {@link com.github.jinahya.persistence.more.color.___MappedHueColor} carries the {@code hue} column the last two
 * share, and {@link com.github.jinahya.persistence.more.color.___MappedColorUtils} holds the conversions between the
 * models and sRGB, along with a parser for the hex notation.
 *
 * <h2>Components</h2>
 * Every component is persisted normalized, between
 * {@value com.github.jinahya.persistence.more.color.___MappedColor#MIN_COMPONENT} and
 * {@value com.github.jinahya.persistence.more.color.___MappedColor#MAX_COMPONENT}, with one exception: a hue is
 * persisted in degrees, the unit CSS Color 4 gives it. Values familiar from CSS — eight-bit components, degrees —
 * remain available through {@link jakarta.persistence.Transient @Transient} accessors alongside.
 * <p>
 * The alpha is not a component. CSS Color 4 models a color as coordinates in a color space <em>plus</em> an alpha, and
 * these classes follow it: a component count never includes the alpha, and a model with no alpha column reports
 * {@value com.github.jinahya.persistence.more.color.___MappedColor#ALPHA_OPAQUE}.
 *
 * <h2>Bounds, and where they live</h2>
 * A bound is declared on the class which owns the column it constrains, in four shapes — a {@code double} pair for
 * callers and a {@code String} pair for the constraint annotations, which take only strings. The component bounds sit
 * on {@code ___MappedColor} because every model below uses them; the hue bounds sit on {@code ___MappedHueColor} for
 * the same reason, and {@code ___MappedColorUtils} reads them rather than keeping a copy.
 *
 * <h2>Access type</h2>
 * Every mapped superclass here is annotated {@link jakarta.persistence.Access @Access}({@code FIELD}). An entity
 * placing its {@link jakarta.persistence.Id @Id} on a getter would otherwise flip the whole hierarchy to property
 * access, and the {@link jakarta.persistence.Transient @Transient} accessors alongside each component would then
 * unmap the components themselves — every component would silently stop being persisted. That is not a hypothetical:
 * removing the annotation makes exactly that happen, and a test in this package pins it.
 * <p>
 * The annotation binds only the class it sits on. It does not dictate the access type of an entity extending it —
 * an entity may keep {@code PROPERTY} access for its own identity — and it does not take
 * {@link jakarta.persistence.AttributeOverride @AttributeOverride} away: an inherited column can still be renamed,
 * from any depth of the hierarchy. Both are covered by tests, on both providers.
 * <p>
 * <strong>An entity extending any of these should declare {@code @Access(AccessType.FIELD)} itself.</strong>
 * Hibernate infers it from the {@code @Id} placement and does not need it; EclipseLink does, and without it rejects
 * the entity at deployment with &quot;has no primary key specified &hellip; mixed access-type&quot; even though the
 * {@code @Id} is plainly on a field. Declaring it explicitly costs one line and works on both.
 *
 * <h2>What is deliberately not here</h2>
 * <ul>
 *   <li><b>Missing components.</b> CSS Color 4 lets a component be {@code none} — {@code rgb(none 0 0)} — to mark it
 *       missing, which matters when interpolating. A component here is a {@code double} in a {@code NOT NULL} column
 *       and is always present.</li>
 *   <li><b>Clamping.</b> The spec clamps an out-of-range component rather than rejecting it. These classes throw:
 *       a component is persisted, and quietly storing something other than what a caller asked for is worse, in a
 *       database, than saying no. Converted values, whose rounding is nobody's fault, are clamped instead.</li>
 *   <li><b>Value equality.</b> No {@code equals}/{@code hashCode} is defined; entities extending these classes keep
 *       their own identity. Use
 *       {@link com.github.jinahya.persistence.more.color.___MappedColor#hasSameComponentsAs(com.github.jinahya.persistence.more.color.___MappedColor)}
 *       for a component-wise comparison.</li>
 *   <li><b>Embeddables.</b> These are mapped superclasses, for entities. Jakarta Persistence does not portably let an
 *       {@link jakarta.persistence.Embeddable @Embeddable} extend a
 *       {@link jakarta.persistence.MappedSuperclass @MappedSuperclass}.</li>
 * </ul>
 *
 * <h2>Extending</h2>
 * {@snippet lang = "java":
 *
 * @Access(AccessType.FIELD) // Hibernate infers this; EclipseLink requires it
 * @Entity
 * @Table(name = "swatch") public class Swatch extends __MappedRgba {
 *         <p>
 *         @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; }
 *         <p>
 *         final var swatch = new Swatch(); swatch.setSrgb(1.0d, .0d, .0d); swatch.setAlpha(.5d);
 *         <p>
 *         swatch.toModernRgbNotation();  // rgb(255 0 0 / 0.5) swatch.toLegacyRgbNotation();  // rgba(255, 0, 0, 0.5)
 *         swatch.toHexNotation();        // #ff000080 } Converting between models goes through sRGB, in either
 *         direction:
 *         {@snippet lang = "java":
 *         hsl.applySrgb(r -> g -> b -> { cmyk.setSrgb(r, g, b); return null; });
 *}
 *
 *         <h2>References</h2>
 *         Everything in this package is derived from these, and each class and conversion method links the section it
 *         implements.
 *         <table class="striped">
 *           <caption>Normative sources</caption>
 *           <thead>
 *             <tr><th scope="col">Source</th><th scope="col">What it governs here</th></tr>
 *           </thead>
 *           <tbody>
 *             <tr><td><a href="https://www.w3.org/TR/css-color-4/#numeric-srgb">CSS Color 4, &sect;5</a></td>
 *                 <td>sRGB, {@code rgb()}/{@code rgba()}, the eight-bit component scale</td></tr>
 *             <tr><td><a href="https://www.w3.org/TR/css-color-4/#hex-notation">CSS Color 4, &sect;4.2</a></td>
 *                 <td>the hex notation, and its three- and four-digit short forms</td></tr>
 *             <tr><td><a href="https://www.w3.org/TR/css-color-4/#the-hsl-notation">CSS Color 4, &sect;7</a></td>
 *                 <td>HSL, and the conversions in &sect;7.1 and &sect;7.2</td></tr>
 *             <tr><td><a href="https://www.w3.org/TR/css-color-4/#the-hwb-notation">CSS Color 4, &sect;8</a></td>
 *                 <td>HWB, and the conversions in &sect;8.1 and &sect;8.2</td></tr>
 *             <tr><td><a href="https://www.w3.org/TR/css-color-4/#typedef-hue">CSS Color 4, &lt;hue&gt;</a></td>
 *                 <td>the hue in degrees, normalized to {@code [0, 360)}</td></tr>
 *             <tr><td><a href="https://www.w3.org/TR/css-color-4/#color-conversion-code">CSS Color 4, &sect;19</a></td>
 *                 <td>the sample code every conversion here follows</td></tr>
 *             <tr><td><a href="https://www.w3.org/TR/css-color-5/#device-cmyk">CSS Color 5, &sect;6</a></td>
 *                 <td>{@code device-cmyk()}, and the naive conversion to sRGB in &sect;6.1</td></tr>
 *             <tr><td><a href="https://www.w3.org/TR/css-values-4/">CSS Values 4</a></td>
 *                 <td>{@code <number>} and {@code <percentage>}, as serialized</td></tr>
 *           </tbody>
 *         </table>
 *         One conversion has <em>no</em> normative source: sRGB to CMYK. CSS defines only the direction into sRGB, so
 *         {@link com.github.jinahya.persistence.more.color.___MappedColorUtils#rgbToCmyk(double, double, double,
 *         java.util.function.DoubleFunction) rgbToCmyk} states its own contract — the exact algebraic inverse of the naive
 *         forward conversion, under maximum black generation. Its javadoc says precisely what that guarantees.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.w3.org/TR/css-color-4/">CSS Color Module Level 4</a>
 * @see <a href="https://www.w3.org/TR/css-color-5/">CSS Color Module Level 5</a>
 * @see <a href="https://www.w3.org/TR/css-values-4/">CSS Values and Units Module Level 4</a>
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.more.color;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
