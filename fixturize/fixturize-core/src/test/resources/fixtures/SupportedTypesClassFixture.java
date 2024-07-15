package de.floydkretschmar.fixturize.mocks;

public class SupportedTypesClassFixture {
    public static boolean BOOLEAN_FIELD = false;
    public static byte BYTE_FIELD = 0;
    public static char CHAR_FIELD = ' ';
    public static double DOUBLE_FIELD = 0.0;
    public static float FLOAT_FIELD = 0.0F;
    public static int INT_FIELD = 0;
    public static long LONG_FIELD = 0L;
    public static short SHORT_FIELD = Short.valueOf((short)0);
    public static java.lang.Boolean BOOLEAN_CLASS_FIELD = false;
    public static java.lang.Byte BYTE_CLASS_FIELD = 0;
    public static java.lang.Character CHAR_CLASS_FIELD = ' ';
    public static java.lang.Double DOUBLE_CLASS_FIELD = 0.0;
    public static java.lang.Float FLOAT_CLASS_FIELD = 0.0F;
    public static java.lang.Integer INT_CLASS_FIELD = 0;
    public static java.lang.Long LONG_CLASS_FIELD = 0L;
    public static java.lang.Short SHORT_CLASS_FIELD = Short.valueOf((short)0);
    public static java.lang.String STRING_FIELD = "STRING_FIELD_VALUE";
    public static java.time.Instant INSTANT_FIELD = java.time.Instant.now();
    public static java.time.Duration DURATION_FIELD = java.time.Duration.ZERO;
    public static java.time.LocalDate LOCAL_DATE_FIELD = java.time.LocalDate.now();
    public static java.time.LocalDateTime LOCAL_DATE_TIME_FIELD = java.time.LocalDateTime.now();
    public static java.time.LocalTime LOCAL_TIME_FIELD = java.time.LocalTime.now();
    public static java.util.UUID UUID_FIELD = java.util.UUID.fromString("6b21f215-bf9e-445a-9dd2-5808a3a98d52");
    public static java.util.Date DATE_FIELD = new java.util.Date();
    public static java.util.Collection<java.lang.String> COLLECTION_FIELD = java.util.List.of();
    public static java.util.List<java.lang.String> LIST_FIELD = java.util.List.of();
    public static java.util.Map<java.lang.String,java.lang.String> MAP_FIELD = java.util.Map.of();
    public static java.util.Set<java.lang.String> SET_FIELD = java.util.Set.of();
    public static java.util.Queue<java.lang.String> QUEUE_FIELD = new java.util.PriorityQueue<>();


}
