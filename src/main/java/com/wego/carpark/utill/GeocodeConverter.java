package com.wego.carpark.utill;

import org.locationtech.proj4j.*;

public class GeocodeConverter {

    private static final String SVY21_EPSG_CODE = "EPSG:3414";
    private static final String WGS84_EPSG_CODE = "EPSG:4326";

    // Create a CRSFactory
    private static final CRSFactory crsFactory = new CRSFactory();

    // Create CoordinateReferenceSystem objects for SVY21 and WGS84
    private static final CoordinateReferenceSystem svy21Crs = crsFactory.createFromName(SVY21_EPSG_CODE);
    private static final CoordinateReferenceSystem wgs84Crs = crsFactory.createFromName(WGS84_EPSG_CODE);

    private static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

    // Create a CoordinateTransform for SVY21 to WGS84
    private static final CoordinateTransform transform = ctFactory.createTransform(svy21Crs, wgs84Crs);


    /**
     * Convert SVY21 coordinates to WGS84.
     *
     * @param svy21X SVY21 X coordinate
     * @param svy21Y SVY21 Y coordinate
     * @return Array containing WGS84 latitude and longitude
     */
    public static double[] convertSVY21ToWGS84(double svy21X, double svy21Y) {

        // Transform SVY21 coordinates to WGS84
        ProjCoordinate svy21Point = new ProjCoordinate(svy21X, svy21Y);
        ProjCoordinate wgs84Point = new ProjCoordinate();
        transform.transform(svy21Point, wgs84Point);

        // Return WGS84 latitude and longitude
        return new double[]{wgs84Point.y, wgs84Point.x};
    }

}

