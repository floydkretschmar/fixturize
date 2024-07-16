# Autofixture

[![License](https://img.shields.io/badge/License-Apache%202.0-yellowgreen.svg)](https://github.com/floydkretschmar/autofixture/blob/master/LICENSE.txt)

* [What are test fixtures?](#what-are-test-fixtures)
* [What is fixturize?](#what-is-fixturize)
* [Default value providers](#default-value-providers)
* [Requirements](#requirements)
* [Licensing](#licensing)

## What are test fixtures?

The usage of test fixtures can be seen as a pattern in software development, and more specifically software testing. In
broad terms when setting up a test, you are trying to create a static and predictable enviroment in which you can test
your
concrete implementation. In this scenario a test fixture are all those statically defined externalities, with which you
can
test your concrete implementation.

In the case of unit or integration testing the idea of fixtures is sometimes used to refer to instances of an object
that
have been manually created in a test with static values that can then be used to test unit of code you are trying to
test.
Lets say you have a class `OrderService` with the method `OrderService.send` which takes an `Order` as the parameter and
returns an object of `Parcel`. When writing a test for this service, you will most likely want to create an instance of
both
`Order` and `Parcel` using fixed data. You will want to use `Order` as a parameter to call `OrderService.send` and then
compare the result of that method call against your static expectation of what this `Parcel` result should look like.

To make this example more tangible, lets say the two classes look like this

```java

@Builder
@Value
static class Order {
    String orderNo;
    Instant date;
    String customerName;
    ...
}
```

and this

```java

@Builder
@Value
static class Parcel {
    String orderNo;
    String parcelNo;
    Instant shipmentDate;
    ...
}
```

Then your test for `OrderProcessor.process` would probably look something a little bit like this

```java
static class OrderProcessorTests {
    @Test
    void process_whenCalled_returnCorrectParcel() {
        final String orderNo = "1234";
        final Instant orderDate = Instant.now();
        ...
        final Order orderFixture = Order.build().orderNo(orderNo).date(orderDate).customerName(...).build();
        final Order expectedParcelFixture = Parcel.build().orderNo(orderNo).parcelNo(...).shipmentDate(...).build();
        ....
        assertThat(orderProcessor.process(order)).isEqualTo(expectedParcelFixture);
    }
}
```

As you might notice this is already quite a lot of setup and boilerplate code for just one test. What if you have more
than one test? What if you have more than three attributes per class? What if you want to reuse fixtures across
different
test classes? Fixturize answers all of these questions.

## What is fixturize?

Fixturize is a lean java library that primarily uses
the [java annotation processing api](https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/package-summary.html)
to automatically generate static fixture classes for all your domain objects which you can then directly use in your
tests.

Lets go back to the example from above. How would you use fixturize to automatically generate fixtures for `Order`
and `Parcel`?

### `@Fixture`

By annotating each class with `@Fixture` fixturize will go ahead and scann all fields of the class in question, and
generate
a new fixture class containing a constant for each field defined on the annotated class. For example

```java

@Builder
@Value
@Fixture
static class Order {
    String orderNo;
    Instant date;
    String customerName;
}
```

becomes

```java
static class OrderFixture {
    public static java.lang.String ORDER_NO = "ORDER_NO_VALUE";
    public static Instant DATE = Instant.now();
    public static String CUSTOMER_NAME = "CUSTOMER_NAME_VALUE";
}
```

The value assigned to each constant is decided by a set of factors:

- For many common types such as the primitive types and also common java types default value providers are being
  provided
  (see next section).
- You can annotate each field in your original domain class with `@FixtureConstant` which allows you to define
  both `name`
  and `value` of the corresponding constant
- You can annotate the entire domain class with `@FixtureValueProvider` which allows you to define a custom value
  provider
  for all fields of the same type

### `@FixtureConstructor` and `@FixtureBuilder`

In case you don't just want to generate constants used for creating fixtures of domain classes, but you also want to
automatically
generate methods that allow you to generate instances of you fixtures, you can use the annotations `@FixtureConstructor`
and/or `@FixtureBuilder`. For example, the following annotations

```java

@Builder
@Value
@Fixture
@FixtureBuilder
@FixtureBuilder(usedSetter = {"orderNo"})
static class Order {
    String orderNo;
    Instant date;
    String customerName;
}
```

will generate the following fixture

```java
static class OrderFixture {
    public static java.lang.String ORDER_NO = "ORDER_NO_VALUE";
    public static Instant DATE = Instant.now();
    public static String CUSTOMER_NAME = "CUSTOMER_NAME_VALUE";

    public static Order.OrderBuilder createOrderFixtureWithOrderNoAndDateAndCustomerName() {
        return Order.builder()
                .orderNo(ORDER_NO)
                .date(DATE)
                .customerName(CUSTOMER_NAME);
    }

    public static Order.OrderBuilder createOrderFixtureWithOrderNo() {
        return Order.builder()
                .orderNo(ORDER_NO);
    }
}
```

## Default value providers

| Type                              | Default value                                                                            |
|-----------------------------------|------------------------------------------------------------------------------------------|
| `boolean` and `java.lang.Boolean` | `false`                                                                                  |
| `byte` and `java.lang.Byte`       | 0                                                                                        |
| `char` and `java.lang.Character`  | ' '                                                                                      |
| `double` and `java.lang.Double`   | 0.0                                                                                      |
| `float` and `java.lang.Float`     | 0.0F                                                                                     |
| `int` and `java.lang.Integer`     | 0                                                                                        |
| `long` and `java.lang.Long`       | 0L                                                                                       |
| `short` and `java.lang.Short`     | `Short.valueOf((short)0)`                                                                |
| `java.math.BigDecimal`            | `java.math.BigDecimal.ZERO`                                                              |
| `java.math.BigInteger`            | `java.math.BigInteger.ZERO`                                                              |
| `java.time.Instant`               | `java.time.Instant.now()`                                                                |
| `java.time.Duration`              | `java.time.Duration.ZERO`                                                                |
| `java.time.LocalDate`             | `java.time.LocalDate.now()`                                                              |
| `java.time.LocalDateTime`         | `java.time.LocalDateTime.now()`                                                          |
| `java.time.LocalTime`             | `java.time.LocalTime.now()`                                                              |
| `java.util.Date`                  | `new new java.util.Date()`                                                               |
| `java.util.Collection`            | `java.util.List.of()`                                                                    |
| `java.util.List`                  | `java.util.List.of()`                                                                    |
| `java.util.Map`                   | `java.util.Map.of()`                                                                     |
| `java.util.Set`                   | `java.util.Set.of()`                                                                     |
| `java.util.Queue`                 | `new java.util.PriorityQueue<>()`                                                        |
| `java.util.UUID`                  | `java.util.UUID.fromString(randomUUID)` where `randomUUID` will be an actual UUID string |
| all arrays                        | `mew <ArrayType>[] {}` where `<ArrayType>` is the type of the field                      |
| all enums                         | the first defined enum constant                                                          |
| default fallback                  | `null`                                                                                   |

## Requirements

Fixturize requires Java 17.

## Licensing

Fixturize is licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in
compliance with the License. You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.
