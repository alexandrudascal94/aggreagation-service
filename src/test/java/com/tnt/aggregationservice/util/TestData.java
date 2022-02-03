package com.tnt.aggregationservice.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestData {
    public static final String AGGREGATION_ID_1 = "000000001";
    public static final String AGGREGATION_ID_2 = "000000002";
    public static final String AGGREGATION_ID_3 = "000000003";
    public static final String AGGREGATION_ID_4 = "000000004";
    public static final String AGGREGATION_ID_5 = "000000005";
    public static final String AGGREGATION_ID_6 = "000000006";

    public static final String NL = "NL";
    public static final String CA = "CA";
    public static final String DE = "DE";
    public static final String FR = "FR";
    public static final String RU = "RU";
    public static final String IT = "IT";

    public static final double NL_PRICING = 10.01d;
    public static final double CA_PRICING = 15.09d;
    public static final double DE_PRICING = 15.09d;
    public static final double FR_PRICING = 15.09d;
    public static final double RU_PRICING = 15.09d;
    public static final double IT_PRICING = 15.09d;


    public static final List<String> SHIPMENT_ID_1 = List.of("box", "pallet", "box", "envelope");
    public static final List<String> SHIPMENT_ID_2 = List.of("pallet", "box", "envelope");
    public static final List<String> SHIPMENT_ID_3 = List.of("pallet", "pallet", "envelope");
    public static final List<String> SHIPMENT_ID_4 = List.of("pallet", "envelope", "envelope");
    public static final List<String> SHIPMENT_ID_5 = List.of("pallet", "envelope");
    public static final List<String> SHIPMENT_ID_6 = List.of("box", "envelope");

    public static final String TRACK_ID_1 = "NEW";
    public static final String TRACK_ID_2 = "COLLECTING";
    public static final String TRACK_ID_3 = "COLLECTED";
    public static final String TRACK_ID_4 = "IN TRANSIT";
    public static final String TRACK_ID_5 = "DELIVERING";
    public static final String TRACK_ID_6 = "NEW";

    private static  final HashMap<String, Double> pricesMap = new HashMap<>();
    private static  final HashMap<String, List<String>> shipmentMap = new HashMap<>();
    private static  final HashMap<String, String> trackMap = new HashMap<>();

    static {
        pricesMap.put(NL, NL_PRICING);
        pricesMap.put(FR, FR_PRICING);
        pricesMap.put(DE, DE_PRICING);
        pricesMap.put(CA, CA_PRICING);
        pricesMap.put(RU, RU_PRICING);
        pricesMap.put(IT, IT_PRICING);

        shipmentMap.put(AGGREGATION_ID_1, SHIPMENT_ID_1);
        shipmentMap.put(AGGREGATION_ID_2, SHIPMENT_ID_2);
        shipmentMap.put(AGGREGATION_ID_3, SHIPMENT_ID_3);
        shipmentMap.put(AGGREGATION_ID_4, SHIPMENT_ID_4);
        shipmentMap.put(AGGREGATION_ID_5, SHIPMENT_ID_5);
        shipmentMap.put(AGGREGATION_ID_6, SHIPMENT_ID_6);

        trackMap.put(AGGREGATION_ID_1, TRACK_ID_1);
        trackMap.put(AGGREGATION_ID_2, TRACK_ID_2);
        trackMap.put(AGGREGATION_ID_3, TRACK_ID_3);
        trackMap.put(AGGREGATION_ID_4, TRACK_ID_4);
        trackMap.put(AGGREGATION_ID_5, TRACK_ID_5);
        trackMap.put(AGGREGATION_ID_6, TRACK_ID_6);
    }

    public static Map<String, Double> getPricingResponse(List<String> keys) {
        var response = new HashMap<String, Double>();
        keys.forEach(k -> response.put(k, pricesMap.get(k)));
        return response;
    }

    public static Map<String, List<String>> getShipmentsResponse(List<String> trackIds) {
        var response = new HashMap<String, List<String>>();
        trackIds.forEach(k -> response.put(k, shipmentMap.get(k)));
        return response;
    }

    public static Map<String, String> getTrackResponse(List<String> shipmentIds) {
        var response = new HashMap<String, String>();
        shipmentIds.forEach(k -> response.put(k, trackMap.get(k)));
        return response;
    }
}
