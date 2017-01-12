**Q**: Won't using method calls confuse newbies to think it's a synchronous call?<br/>
**A**: Maybe. But a similar argument also holds for remote EJB calls: They could be mistaken for local calls, which has a big impact. But even if you do mistake the sending of a message as a simple method call, all purely functional assumptions remain correct. The advantage OTOH is that, on that level, the reader is confronted only with well known concepts.

**Q**: So it's asynchronous remote calls. What's the difference to @Asynchronous EJBs?<br/>
**A**: The call semantics are completely different. Most notably, an asynchronous EJB can only be called while the receiver is available, only execution is asynchronous; whereas a JMS can be sent completely independently of any receiver. More importantly to enterprise applications, asynchronous EJBs can't guarantee transacted once-and-only-once send and delivery, when JMS can.

**Q**: Why is it separate from the EJB standard?<br/>
**A**: The EJB spec is quite big already. While there are only a few annotations here, their exact semantics take some space to be defined.

**Q**: You can route events as well as method invocations. Isn't it confusing to have two mechanisms for the same thing?<br/>
**A**: There are three different types of messages [cf. Hoope/Woolf: PoEAI] with different semantic coupling between sender and receiver:
* **Command messages** couple the sender to the receiver, as the sender has to assume or make sure that the receiver is in the right state to receive the message.
* **Event messages** OTOH couple the receiver to the sender, as it has to understand in what context the event was produced. The common request-reply pair generally reflects this when the request is a command message and the reply is an event message.
* As it's never useful to couple in both directions, the remaining type of messages is **document messages**, that completely decouple both sides. The semantic coupling is then done in the messaging system, e.g. by complex content based routing often seen in ESBs.
Commands naturally have a verb; events most often as well. But document messages do not. You could put the verb into the message or use a generic method name like 'accept' for document messages, but forcing the syntax of one message type onto the other is more confusing than having two, even when both may result in the same format of the message actually sent.
