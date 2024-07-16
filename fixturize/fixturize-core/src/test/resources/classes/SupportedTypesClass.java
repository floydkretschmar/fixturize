package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import lombok.Builder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

@Builder
@Fixture
public class SupportedTypesClass {
    private boolean booleanField;
    private byte byteField;
    private char charField;
    private double doubleField;
    private float floatField;
    private int intField;
    private long longField;
    private short shortField;

    private Boolean booleanClassField;
    private Byte byteClassField;
    private Character charClassField;
    private Double doubleClassField;
    private Float floatClassField;
    private Integer intClassField;
    private Long longClassField;
    private Short shortClassField;

    private String stringField;

    private BigDecimal bigDecimalField;
    private BigInteger bigIntegerField;

    private Instant instantField;
    private Duration durationField;
    private LocalDate localDateField;
    private LocalDateTime localDateTimeField;
    private LocalTime localTimeField;

    private UUID uuidField;
    private Date dateField;

    private Collection<String> collectionField;
    private List<String> listField;
    private Map<String, String> mapField;
    private Set<String> setField;
    private Queue<String> queueField;
}
