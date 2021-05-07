# MessageApi

# Status and Future

The MessageApi is already reality: Version 1.0 is in production use internally at 1&1 since August 2010; by November 2010 we have sent or received over 250.000 messages. The work required to release it open source on java.net resulted in version 1.1 released in December 2010.

The project has been quite dormant for the past years, as I'm not using a lot of JMS any more, and there have been no feature requests.

## About

[![Join the chat at https://gitter.im/t1/message-api](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/t1/message-api?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

The EJB standard took a giant step between version 2.1 and 3.0 - the same step (in our eyes) is still missing in the JMS standard (even in 2.0): To provide a simple to use, plain business interface solution to remote calls... only asynchronous in this case. Even Spring has not a solution satisfying us, so we have started this project.

We want to evolve it into a standard in some way (in some fits of megalomania I still think it maybe even become part of JMS 3.0 and/or Java EE 8 one day ;-). As a first step we make it publicly available so everybody can take a look at what we're up to. We welcome any feedback - from feature requests to patches, from downloads to success stories. Yes, downloading is feedback, too: Having lots of users and still no comments could mean that the project is just perfect, couldn't it? ;-)

Issue 6.2011 of the (German) Java Magazin contained my article on this subject. I've translated it into English to share it with the broader community. You can find it [http://java.net/projects/messageapi/downloads/download/MessageAPI.pdf here].

## Motivation

Business code should concentrate on the message that is to be sent or what it has to do when a message is received. What technology is used, where the message is going to or coming from, how the message is transformed, etc. has to be provided by a configurable adapter layer and injected into the business code.

Moving from JMS 1.1 to the MessageApi means:

* You use a simple business API without any boiler plate code and no snake pits to fall into.
* Your code is easy to unit-test.
* Your calls are type-safe.
* You messages are versioned, i.e. they can evolve.

## Solution

The idea is actually quite simple: You add the @MessageApi annotation to an interface that has only methods that return void and don't declare any exceptions - remember: they are going the be sent asynchronously. An annotation processor converts all methods in that interface into POJOs, e.g. it transforms a method <tt>public void createCustomer(String firstName, String lastName)</tt> into a <tt>CreateCustomer</tt> POJO with the two parameters as properties (i.e. it has a <tt>getFirstName()</tt> method). This POJO is immutable, contains hashCode, equals, and toString methods, and is properly annotated for JAXB. You then package the interface and the generated POJOs into an API jar to be called by clients and implemented by services.

At runtime, the container injects a proxy for that interface into the client business code. So when you call e.g. the createCustomer method, the proxy converts the call into an instance of the POJO, marshalls it to XML using JAXB, creates a text message containing it, and sends it to the configured queue. The business code can rely on two things: The call is asynchronous (i.e. only a little bit of some technical stuff actually happens within the method call) and the call is transactional (i.e. if the whole transaction fails for any reason, no message is sent either).

On the receiver side, an MDB receives the message, unmarshalls the POJO, and calls the corresponding method in your business code that implements the messaging interface. The assumptions the business code can make are mirrored: The message was asynchronous (i.e. the calling business code has probably long finished) and the call is transactional (i.e. if the whole transaction fails for any reason, the message is not consumed).

We have chosen to send XML messages instead of binary, serialized java objects to ease the tooling in the messaging system. But in order to communicate with 'legacy' JMS participants, you can configure the MessageApi adapter to send or receive mapped messages instead, and how to exactly map the message fields.


## How It Feels

A simple business interface to create a customer could look like this:

```java
@MessageApi
public interface CustomerService {
    public void createCustomer(String firstName, String lastName);
}
```

You'd package this interface into an API Jar and some sender business code would call it like this:

```java
private CustomerService customerService;

public void businessMethod() {
    ...
    customerService.createCustomer("Walter", "Smith");
    ...
}
```

The receiver would simply implement the interface:

```java
public class MyCustomerService implements CustomerService {
    public void createCustomer(String firstName, String lastName) {
        ...
    }
}
```

## Getting Started

You can profit or participate in a variety of ways:

* You can get started with the [[Demo|Chat-Demo]]
* You can [http://java.net/projects/messageapi/downloads download] the current release version
* You can subscribe to one of this project's mailing lists
* You can submit a project issue, or query existing issues
* You can access the Subversion repository
* You can supply patches to the mailing lists or to the issues
* Or you can even request membership to this project


## More

[Proposal for a new Chapter in the JMS spec](doc/PROPOSAL.mediawiki)

[Status and TODOs](doc/STATUS.mediawiki)
