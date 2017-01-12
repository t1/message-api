This proposal builds on the ''AtInject'' proposal for JMS 2.0 and adds an abstraction layer on-top, that allows clients to easily send and receive messages in a type-safe way.

# Message Body & Properties
## Default: XML message

Simply by annotating it as ''MessageApi'', an interface can be used as the business contract for an asynchronous service.

```java
@MessageApi
public interface TradeService {
    public void priceUpdate(String symbol, BigInteger price, Timestamp timeStamp);
}
```

Note that all methods in such an interface must return ''void'' and not declare any exceptions, as the execution will be done asynchronously.

The sender can simply inject a stub that forwards all calls to the service using JMS.

```java
    ...
    @Inject TradeService tradeService;
    ...
    {
        tradeService.priceUpdate("ORCL", 26.5, now);
    }
```

The receiver just implements the annotated business interface.

```java
public class TradeServiceImpl implements TradeService {
    public void priceUpdate(String symbol, BigInteger price, Timestamp timeStamp) {
        ...
    }
}
```

The default message format is xml, e.g.:

```xml
<?xml version='1.0' encoding='UTF-8'?>
<priceUpdate>
    <symbol>ORCL</symbol>
    <price>26.5</price>
    <timeStamp>2011-09-10-15-34-00</timeStamp>
</priceUpdate>
```

The message payload is generated with JAXB, so objects passed in may have to be annotated accordingly. Note that the root element is the name of the method.

::TODO: Add sample code for the Timestamp class annotated to be formatted as seen above.

## Message Property & Message Selector

By default, all parameters are placed into the body. You can annotate parameters as ''JmsProperty'', so they will additionally be put into a property of the message.

```java
@MessageApi
public interface TradeService {
    public void priceUpdate(@JmsProperty String symbol, BigInteger price, Timestamp timeStamp);
}
```

The receiver can then use a message selector to only receive message with that property:

```java
@JmsMessageSelector("symbol = 'ORCL'")
public class TradeServiceImpl implements TradeService {
    public void priceUpdate(String symbol, BigInteger price, Timestamp timeStamp) {
        ...
    }
}
```

A ''JmsProperty'' annotation can not only appear directly on the parameter, but just as well on any direct or indirect property of a parameter object.

## Message Property only in the header

By using the ''onlyHeader'' attribute of the ''JmsProperty'' annotation, the field will be removed from the payload.

```java
@MessageApi
public interface TradeService {
    public void priceUpdate(@JmsProperty(onlyHeader=true) String symbol, BigInteger price, Timestamp timeStamp);
}
```

While the ''symbol'' parameter is a message property, the message body looks like this:

```xml
<?xml version='1.0' encoding='UTF-8'?>
<priceUpdate>
    <price>26.5</price>
    <timeStamp>2011-09-10-15-34-00</timeStamp>
</priceUpdate>
```

## Object Message

If you want the message payload to be serialized objects instead of an xml string, you can annotate the interface:

```java
@MessageApi
@JmsPayload(format = SERIALIZED)
public interface TradeService {
    public void priceUpdate(String symbol, BigInteger price, Timestamp timeStamp);
}
```

You can place the same annotation at the methods as well.

```java
@MessageApi
public interface TradeService {
    @JmsPayload(format = SERIALIZED)
    public void priceUpdate(String symbol, BigInteger price, Timestamp timeStamp);
}
```

## Map Message

If you want the message payload to be a map instead of an xml string, you can annotate the interface:

```java
@MessageApi
@JmsPayload(format = MAPPED)
public interface TradeService {
    public void priceUpdate(String symbol, BigInteger price, Timestamp timeStamp);
}
```

You can place the same annotation at the methods as well.

```java
@MessageApi
public interface TradeService {
@JmsPayload(format = MAPPED)
    public void priceUpdate(String symbol, BigInteger price, Timestamp timeStamp);
}
```

The mapped message then looks like this:

{|- border="1"
|SYMBOL
|ORCL
|-
|PRICE
|26.5
|-
|TIME_STAMP
|2011-09-10-15-34-00
|}

Note that the default naming scheme for the field names is to make them upper case with underscores.

::TODO: Custom map field name schema and/or custom map field names

::TODO: Custom message marshaller/unmarshaller

# Delivery
## Static Destination Name

The default destination name is the fully qualified name of the interface. You can change that on the ''MessageApi'' annotation.

```java
@MessageApi(destinationName = "Trades")
public interface TradeService {
    public void priceUpdate(String symbol, BigInteger price, Timestamp timeStamp);
}
```

## Dynamic Destination Name

By annotating a parameter as ''JmsDestination'', it is used for the destination name at run time.

```java
@MessageApi
public interface TradeService {
    public void priceUpdate(@JmsDestination String symbol, BigInteger price, Timestamp timeStamp);
}
```

As with static destination names, if a destination does not exist, normally an exception is thrown. By qualifying the ''JmsDestination'' annotation with ''createMissing = true'', missing destinations are created.

::TODO: As a queue or as a topic?!?

```java
@MessageApi
public interface TradeService {
    public void priceUpdate(@JmsDestination(createMissing = true) String symbol, BigInteger price, Timestamp timeStamp);
}
```

On the receivers side, the symbol argument is the name of the destination that was used to deliver the message. This can be different from the destination that the sender used, if the message was routed to a different destination for some reason.

::TODO: maybe this contradicts 3.4.1?

## Delivery Mode NON_PERSISTENT

The sender can statically set the delivery mode with a ''JmsNonPersistent'' annotation:

```java
    @Inject @JmsNonPersistent TradeService tradeService;
```

## Bean Managed Acknowledgement

By default the acknowledgement of messages is done automatically with the transaction.

::TODO

# Special Message Header Fields
## Message-ID & Timestamp

The sender can read the id of a message directly after it was sent from a ''JmsMessageId'' helper:

```java
    ...
    @Inject JmsMessageId messageId;
    @Inject TradeService tradeService;
    ...
    {
        tradeService.priceUpdate("ORCL", 26.5, now);
        String id = messageId.get();
    }
```

If the ''JmsMessageId'' is not injected into the sender, then a message id may not be generated by the JMS provider for performance reasons (see 3.4.3)

The receiver can, too, read the id of the message just received via ''JmsMessageId'':

```java
public class TradeServiceImpl implements TradeService {
    @Inject JmsMessageId messageId;
    
    public void priceUpdate(String symbol, BigInteger price, Timestamp timeStamp) {
        String id = messageId.get();
        ...
    }
}
```

''JmsTimestamp'' is analogous.

## Correlation-Id, Reply-To & Message-Type

The interface can include a ''JmsCorrelationId'', which then is added as a header field. To remove it from the payload, add a ''onlyHeader'' attribute to the annotation.

```java
@MessageApi
public interface TradeService {
    public void priceUpdate(@JmsCorrelationId(onlyHeader = true) String correlationId, String symbol, BigInteger price, Timestamp timeStamp);
}
```

The correlation id can be a ''JmsMessageId'', a custom String, or a provider native byte''''.

''JmsReplyTo'' and ''JmsMessageType'' are analogous.

## Redelivered

The receiver can read the redelivered flag of the message just received via ''JmsRedelivered'':

```java
public class TradeServiceImpl implements TradeService {
    @Inject JmsRedelivered redelivered;
    
    public void priceUpdate(String symbol, BigInteger price, Timestamp timeStamp) {
        if (redelivered.get()) {
            ...
        }
    }
}
```

## Static Time-To-Live & Expiration

The sender can specify a constant time-to-live for all messages sent to a service by using the ''JmsTimeToLive'' annotation:

```java
    @Inject @JmsTimeToLive(1000) TradeService tradeService;
```

The default time-to-live is zero, i.e. to never expire.

It can also read the calculated expiration just after the send:

```java
    @Inject JmsExpiration jmsExpiration;
    @Inject TradeService tradeService;
    ...
    {
        tradeService.priceUpdate("ORCL", 26.5, now);
        long expiration = jmsExpiration.get();
    }
```

The receiver can use the same annotation:

```java
public class TradeServiceImpl implements TradeService {
    @Inject JmsExpiration jmsExpiration;
    public void priceUpdate(String symbol, BigInteger price, Timestamp timeStamp) {
        long expiration = jmsExpiration.get();
        ...
    }
}
```

## Dynamic Time-To-Live

The sender can dynamically specify a time-to-live just before it sends a message, with the help of a ''JmsDynamicTimeToLive'' object:

```java
    @Inject TradeService tradeService;
    @Inject JmsDynamicTimeToLive timeToLive;
    ...
    {
        timeToLive.set(1000);
        tradeService.priceUpdate("ORCL", 26.5, now);
    }
```

You can reset the time-to-live back to the default (static) time-to-live by calling ''JmsDynamicTimeToLive#reset()''.

::TODO: Alternative syntax (no reset required):
```java
    @Inject TradeService tradeService;
    ...
    {
        withTimeToLive(1000).on(tradeService).priceUpdate("ORCL", 26.5, now);
    }
```

If both a dynamic and a static time-to-live are specified, the dynamic value is used.

The sender as well as the receiver can read the calculated expiration just as with a static time-to-live.

## Static Priority

The priority of messages can be specified by annotating the methods of the interface as ''JmsPriority'':

```java
@MessageApi
public interface TradeService {
    @JmsPriority(3)
    public void priceUpdate(String symbol, BigInteger price, Timestamp timeStamp);
}
```

The same annotation can be placed at the interface as well. The method annotation overrides the interface annotation.

::TODO: Maybe better just at the injection point... the receiver is not interested, is it?

## Dynamic Priority

The sender can dynamically specify the priority just before it sends a message, with the help of a ''JmsDynamicPriority'' object:

```java
    @Inject TradeService tradeService;
    @Inject JmsDynamicPriority priority;
    ...
    {
        priority.set(7);
        tradeService.priceUpdate("ORCL", 26.5, now);
    }
```

You can reset the priority back to the default (static) priority by calling ''JmsDynamicPriority#reset()''.

::TODO: Alternative syntax (no reset required):
```java
    @Inject TradeService tradeService;
    ...
    {
        withPriority(7).on(tradeService).priceUpdate("ORCL", 26.5, now);
    }
```

If both a dynamic and a static priority are specified, the dynamic value is used.

The sender as well as the receiver can read the priority analogous to the ''JmsExpiration'' above.
