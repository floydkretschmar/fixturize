# Fixturize

[![License](https://img.shields.io/badge/License-Apache%202.0-yellowgreen.svg)](https://github.com/floydkretschmar/autofixture/blob/master/LICENSE.txt)
[![Latest Version](https://img.shields.io/maven-metadata/v.svg?label=Latest%20Release&maxAge=3600&metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fde%2Ffloydkretschmar%2Ffixturize-core%2Fmaven-metadata.xml)](https://central.sonatype.com/artifact/de.floydkretschmar/fixturize-core)


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
    //...
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
    //...
}
```

Then your test for `OrderProcessor.process` would probably look something a little bit like this

```java
static class OrderProcessorTests {
    @Test
    void process_whenCalled_returnCorrectParcel() {
        final String orderNo = "1234";
        final Instant orderDate = Instant.now();
        //...
        final Order orderFixture = Order.build()
                .orderNo(orderNo)
                .date(orderDate)
                //...
                .build();
        final Order expectedParcelFixture = Parcel.build()
                .orderNo(orderNo)
                //...
                .build();
        //...
        assertThat(orderProcessor.process(order)).isEqualTo(expectedParcelFixture);
    }
}
```

As you might notice this is already quite a lot of setup and boilerplate code for just one test. What if you have more
than one test? What if you have more than three attributes per class? What if you want to reuse fixtures across
different
test classes? Fixturize answers all of these questions.

## What is Fixturize?

Fixturize is a lean java library that primarily uses
the [java annotation processing api](https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/package-summary.html)
to automatically generate static fixture classes for all your domain objects which you can then directly use in your
tests.

Lets go back to the example from above. How would you use Fixturize to automatically generate fixtures for `Order`
and `Parcel`?

### `@Fixture`

By annotating each class with `@Fixture` Fixturize will go ahead and scann all fields of the class in question, and
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
    public static java.time.Instant DATE = java.time.Instant.now();
    public static java.lang.String CUSTOMER_NAME = "CUSTOMER_NAME_VALUE";
}
```

The value assigned to each constant is decided by a set of factors:

- For many common types such as the primitive types and also common java types default value providers are being
  provided (see next section).
- You can annotate each field in your original domain class with `@FixtureConstant` which allows you to define
  both `name`
  and `value` of the corresponding constant
- You can annotate the entire domain class with `@FixtureValueProvider` which allows you to define a custom value
  provider for all fields of the same type

### `@FixtureConstructor` and `@FixtureBuilder`

In case you don't just want to generate constants used for creating fixtures of domain classes, but you also want to
automatically
generate methods that allow you to generate instances of you fixtures, you can use the annotations `@FixtureConstructor`
and/or `@FixtureBuilder`. For example, the following annotations

```java
@Builder
@Value
@Fixture
@FixtureBuilder(methodName = "createOrderFixture")
@FixtureBuilder(methodName = "createOrderFixtureWithOrderNo", usedSetter = {"orderNo"})
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
    public static java.time.Instant DATE = java.time.Instant.now();
    public static java.lang.String CUSTOMER_NAME = "CUSTOMER_NAME_VALUE";

  public static Order.OrderBuilder createOrderFixture() {
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
| `java.util.Collection`            | `java.util.List.of()` filled with one default value                                      |
| `java.util.List`                  | `java.util.List.of()` filled with one default value                                      |
| `java.util.Map`                   | `java.util.Map.of()` filled with one default value                                       |
| `java.util.Set`                   | `java.util.Set.of()` filled with one default value                                       |
| `java.util.Queue`                 | `new java.util.PriorityQueue<>()` filled with one default value                          |
| `java.util.UUID`                  | `java.util.UUID.fromString(randomUUID)` where `randomUUID` will be an actual UUID string |
| all arrays                        | `mew <ArrayType>[] {}` where `<ArrayType>` is the type of the field                      |
| all enums                         | the first defined enum constant                                                          |

If none of these pre-registered value providers provide a valid value for the constants, then the library tries to
generate
valid fallback values based to the declared type of the field. Fixturize uses the following order of fallbacks as
strategies
to create an instance of any declared type for constant generation

1. If the type of constant without registered provider is itself annotated with `@FixtureConstructor`
   or `@FixtureBuilder`
   it will try to use the creation method that uses the most `usedSetters` or `constructorParameters` respectively.
2. If the type of the constant is annotated with any lombok annotations, it will try to use (in order)
    - `@Builder`
    - `@AllArgsConstructor`
    - `@RequiredArgsConstructor`
    - `@NoArgsConstructor`
3. If the type has public constructors defined, it will try to use the one with the most parameters.
4. If the type has a public `builder` method define, it will try to use the corresponding buider class.

For the values needed to parameterize each of these methods, values are generated recursively. If still none of these
strategies
lead to the generation of a valid value, then the default value returned is `null`.

### Default value wildcarding

If you want to use the default value generation logic when defining values using `@FixtureConstant`, you can do so by
using
the wildcard string `${<Qualified classname>}` in your `@FixtureConstant` annotation. For example:

```java
@Builder
@Value
@Fixture
static class Order {
    @FixtureConstant(name = "COMMENTS", value = "java.util.List.of(\"Test\", #{java.lang.String})")
    List<String> comments;
}
```

will create the following fixture

```java
static class OrderFixture {
    public static java.util.List<java.lang.String> COMMENTS = java.util.List.of("Test", "STRING_VALUE");
}
```

## Defining your own value providers

Sometimes you might have a scenario, where you want to define a custom value provider for all constant generations of 
a certain class. Lets say for example, you do not want to use the default value provider for `String` that takes the name
of the field and transforms it into screaming case and attaches `_VALUE` at the end. Instead you want to register your
own value provider, that just uses the field name as is as the value.

Lets start again with the order class

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

If we want to define a custom value provider for all `String` fields of this class, we can use the `@FixtureValueProvider`
annotation.

```java
@Builder
@Value
@Fixture
@FixtureValueProvider(targetType = "java.lang.String", valueProviderCallback = "(field, metadata) => `${field.getSimpleName().toString()}`")
static class Order {
    String orderNo;
    Instant date;
    String customerName;
}
```

As you can see, you can provide custom value provider functions for each target type you want. A value provider receives
the field of type `javax.lang.model.element.Element` and metadata of type `de.floydkretschmar.fixturize.domain.TypeMetadata`
which can both be used to generate a string representation of the value that will be assigned to all constants of the 
specified target type for fixture. So the generated fixture would look as follows:

```java
static class OrderFixture {
    public static java.lang.String ORDER_NO = "orderNo";
    public static java.time.Instant DATE = java.time.Instant.now();
    public static java.lang.String CUSTOMER_NAME = "customerName";
}
```

## Using Fixturize

Just add the dependency with your build tool of choice and configure the annotation processor.

maven:

```maven
<dependency>
    <groupId>de.floydkretschmar</groupId>
    <artifactId>fixturize-core</artifactId>
    <version>1.0.0</version>
</dependency>

<pluginManagement>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.13.0</version>
            <configuration>
                <annotationProcessorPaths>
                    <annotationProcessorPath>
                        <groupId>de.floydkretschmar</groupId>
                        <artifactId>fixturize-core</artifactId>
                        <version>1.0.0</version>
                    </annotationProcessorPath>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</pluginManagement>

```

gradle:

```gradle
dependencies {
    implementation 'de.floydkretschmar:fixturize-core:1.0.0'
    annotationProcessor 'de.floydkretschmar:fixturize-core:1.0.0'
}
```

## Requirements

Java version 17 and later are supported.

## Licensing

Fixturize is licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in
compliance with the License. You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.
