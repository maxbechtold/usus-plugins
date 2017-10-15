package org.projectusus.ui.colors;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

// TODO #2 Consider using hamcrest instead of assertj
public class DistinguishableHuesTest {

    @Test
    public void providesSingleHue() {
        DistinguishableHues distinguishableColors = new DistinguishableHues();
        Stream<Integer> hues = distinguishableColors.create( 1 );

        assertThat( hues ).allMatch( this::isValidHue ).hasSize( 1 );
    }

    @Test
    public void providesTwoDistinctHues() {
        DistinguishableHues distinguishableColors = new DistinguishableHues();
        List<Integer> hues = distinguishableColors.create( 2 ).collect( Collectors.toList() );

        assertThat( hues ).allMatch( this::isValidHue ).hasSize( 2 );
        assertThat( hues.get( 0 ) ).isLessThan( hues.get( 1 ) );
        assertThat( hues.get( 1 ) - hues.get( 0 ) ).isGreaterThan( 100 );
    }

    @Test
    public void providesThreeEquidistantDistantHues() {
        DistinguishableHues distinguishableColors = new DistinguishableHues();
        List<Integer> hues = distinguishableColors.create( 3 ).collect( Collectors.toList() );

        assertThat( hues ).allMatch( this::isValidHue ).hasSize( 3 );
        assertThat( hues.get( 0 ) ).isLessThan( hues.get( 1 ) );
        int distance0_1 = hues.get( 1 ) - hues.get( 0 );
        assertThat( distance0_1 ).isGreaterThan( 100 );

        assertThat( hues.get( 1 ) ).isLessThan( hues.get( 2 ) );
        int distance1_2 = hues.get( 2 ) - hues.get( 1 );
        assertThat( distance0_1 ).isEqualTo( distance1_2 );
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void canYieldUpTo360Hues() {
        DistinguishableHues distinguishableColors = new DistinguishableHues();
        Stream<Integer> hues = distinguishableColors.create( 360 );

        assertThat( hues ).allMatch( this::isValidHue ).hasSize( 360 );

        exception.expect( IllegalArgumentException.class );
        distinguishableColors.create( 361 );
    }

    @Test
    public void canExcludeRedHues() {
        DistinguishableHues distinguishableColors = new DistinguishableHues().withoutRed();
        Stream<Integer> hues = distinguishableColors.create( 320 );

        assertThat( hues ).allMatch( this::isValidHue ).allMatch( this::notRed ).matches( stream -> stream.size() < 320 );
    }

    @Test
    public void canExcludeGreenHues() {
        DistinguishableHues distinguishableColors = new DistinguishableHues().withoutGreen();
        Stream<Integer> hues = distinguishableColors.create( 320 );

        assertThat( hues ).allMatch( this::isValidHue ).allMatch( this::notGreen ).matches( stream -> stream.size() < 320 );
    }

    @Test
    public void canExcludeBlueHues() {
        DistinguishableHues distinguishableColors = new DistinguishableHues().withoutBlue();
        Stream<Integer> hues = distinguishableColors.create( 320 );

        assertThat( hues ).allMatch( this::isValidHue ).allMatch( this::notBlue ).matches( stream -> stream.size() < 320 );
    }

    @Test
    public void canExcludeBothRedAndGreenHues() {
        DistinguishableHues distinguishableColors = new DistinguishableHues().withoutRed().withoutGreen();
        Stream<Integer> hues = distinguishableColors.create( 320 );

        assertThat( hues ).allMatch( this::isValidHue ).allMatch( this::notRed ).allMatch( this::notGreen ).matches( stream -> stream.size() < 320 );
    }

    @Test
    public void canYieldSparseRedBlueAndGreenHues() {
        DistinguishableHues distinguishableColors = new DistinguishableHues().withSparseRedGreenBlue();
        List<Integer> hues = distinguishableColors.create( 55 ).collect( toList() );

        assertThat( hues ).allMatch( this::isValidHue );
        assertThat( hues ).filteredOn( this::isRed ).hasSize( 3 );
        assertThat( hues ).filteredOn( this::isGreen ).hasSize( 3 );
        assertThat( hues ).filteredOn( this::isBlue ).hasSize( 3 );
    }

    private boolean isValidHue( Integer hue ) {
        return 0 <= hue && hue < 360;
    }

    private boolean notRed( Integer hue ) {
        return !isRed( hue );
    }

    private boolean isRed( Integer hue ) {
        Color rgbColor = Color.getHSBColor( hue / 360f, 1f, 1f );
        double weightedRed = rgbColor.getRed() * 0.6;
        return weightedRed >= rgbColor.getGreen() && weightedRed >= rgbColor.getBlue();
    }

    private boolean notGreen( Integer hue ) {
        return !isGreen( hue );
    }

    private boolean isGreen( Integer hue ) {
        Color rgbColor = Color.getHSBColor( hue / 360f, 1f, 1f );
        double weightedGreen = rgbColor.getGreen() * 0.6;
        return weightedGreen >= rgbColor.getRed() && weightedGreen >= rgbColor.getBlue();
    }

    private boolean notBlue( Integer hue ) {
        return !isBlue( hue );
    }

    private boolean isBlue( Integer hue ) {
        Color rgbColor = Color.getHSBColor( hue / 360f, 1f, 1f );
        double weightedBlue = rgbColor.getBlue() * 0.6;
        return weightedBlue >= rgbColor.getRed() && weightedBlue >= rgbColor.getGreen();
    }
}
