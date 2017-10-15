package org.projectusus.ui.colors;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

public class DistinguishableHues {

    public static final int RED_HUE = 0;
    public static final int GREEN_HUE = 120;
    public static final int BLUE_HUE = 240;

    public static final int SPARSE_DISTANCE = 30;
    private static final int COLOR_RADIUS = 36;
    private static final int HUE_SPACE = 360;

    private final Supplier<Stream<HueProperties>> allHues;
    private final HueProperties allowedProperties;

    public DistinguishableHues() {
        allHues = () -> IntStream.range( 0, HUE_SPACE ).boxed().map( hue -> getProperties( hue ) );
        allowedProperties = HueProperties.allAllowed();
    }

    private HueProperties getProperties( int hue ) {
        return new HueProperties( hue, containsRed( hue ), contains( hue, GREEN_HUE ), contains( hue, BLUE_HUE ), //
                closeToRed( hue ), closeTo( hue, GREEN_HUE ), closeTo( hue, BLUE_HUE ) );
    }

    public Stream<Integer> create( int amount ) {
        validate( amount );

        Stream<HueProperties> hueStream = allHues.get();
        Stream<Integer> matchingHues = hueStream.filter( hueProperty -> hueProperty.matches( allowedProperties ) ).map( HueProperties::getHue );

        int partitionSize = HUE_SPACE / amount;
        List<List<Integer>> partitionedHues = Lists.partition( matchingHues.collect( toList() ), partitionSize );

        return partitionedHues.stream().map( list -> list.get( 0 ) );
    }

    private void validate( int amount ) {
        if( amount < 1 || amount > HUE_SPACE ) {
            throw new IllegalArgumentException( String.format( "Can only create between 1 and %s number of hues", HUE_SPACE ) );
        }
    }

    private boolean containsRed( int hue ) {
        return contains( hue, RED_HUE ) || contains( hue, RED_HUE + HUE_SPACE );
    }

    private boolean contains( int hue, int rgbHue ) {
        return Math.abs( hue - rgbHue ) <= COLOR_RADIUS;
    }

    private boolean closeToRed( int hue ) {
        return closeTo( hue, RED_HUE ) || closeTo( hue, RED_HUE + HUE_SPACE );
    }

    private boolean closeTo( int hue, int rgbHue ) {
        int distance = Math.abs( hue - rgbHue );
        // Allow pristine red/green/blue
        return distance <= SPARSE_DISTANCE && distance > 0;
    }

    public DistinguishableHues withoutRed() {
        allowedProperties.containsRed = false;
        return this;
    }

    public DistinguishableHues withoutGreen() {
        allowedProperties.containsGreen = false;
        return this;
    }

    public DistinguishableHues withoutBlue() {
        allowedProperties.containsBlue = false;
        return this;
    }

    public DistinguishableHues withSparseRedGreenBlue() {
        allowedProperties.closeToRed = false;
        allowedProperties.closeToGreen = false;
        allowedProperties.closeToBlue = false;
        return this;
    }

    static class HueProperties {

        static HueProperties allAllowed() {
            return new HueProperties( null, true, true, true, true, true, true );
        }

        private final Integer hue;

        boolean containsRed;
        boolean containsGreen;
        boolean containsBlue;

        boolean closeToRed;
        boolean closeToGreen;
        boolean closeToBlue;

        public HueProperties( Integer hue, boolean containsRed, boolean containsGreen, boolean containsBlue, boolean closeToRed, boolean closeToGreen, boolean closeToBlue ) {
            this.hue = hue;
            this.containsRed = containsRed;
            this.containsGreen = containsGreen;
            this.containsBlue = containsBlue;
            this.closeToRed = closeToRed;
            this.closeToGreen = closeToGreen;
            this.closeToBlue = closeToBlue;
        }

        public Integer getHue() {
            return hue;
        }

        public boolean matches( HueProperties allowedProperties ) {
            return (allowedProperties.closeToRed || !this.closeToRed) && //
                    (allowedProperties.closeToGreen || !this.closeToGreen) && //
                    (allowedProperties.closeToBlue || !this.closeToBlue) && //
                    (allowedProperties.containsRed || !this.containsRed) && //
                    (allowedProperties.containsGreen || !this.containsGreen) && //
                    (allowedProperties.containsBlue || !this.containsBlue);
        }
    }
}
