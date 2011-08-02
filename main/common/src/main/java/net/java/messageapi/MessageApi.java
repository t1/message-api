package net.java.messageapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import org.jboss.seam.solder.serviceHandler.ServiceHandlerType;

/**
 * Annotate an interface to declare that it is normally called asynchronously via a messaging
 * system. I.e., all methods within this interface have to return <code>void</code> and declare no
 * exceptions.
 * <p>
 * On the sender side the interface is implemented probably by a dynamic proxy that adapts calls to
 * a concrete messaging infrastructure like JMS; on the receiver side a messaging gateway like an
 * MDB adapts incoming messages to the business code that implements directly the message API
 * interface.
 * <p>
 * By completely hiding the messaging technology, it is possible to switch e.g. from JMS to ESB or
 * any other infrastructure without touching the business code. An even greater advantage is that
 * there is not need to create the same boiler plate code every time a message needs to be sent; a
 * procedure that can be quite error prone with e.g. the quite complex JMS API. This obviously also
 * makes testing much easier. But probably the biggest advantage is that the compiler enforces that
 * all messages conform to a common schema agreed upon by sender and receiver. Whenever the schema
 * is changed in a breaking way, a new version of the API has to be built for the client requiring
 * these changes, while an arbitrary number of versions of the receiver can be deployed at once.
 * <h3>Sender</h3>
 * The sender can safely assume that nothing "remote" is happening <i>inside</i> the call, i.e.:
 * <ul>
 * <li>The call can be transactional: If the sender runs within a transaction, nothing will be
 * visible to the outside world until the transaction is committed, and if the transaction fails
 * even after the call was made, no message will be sent. So it's safe to throw an exception with
 * regard to this call.
 * <li>There are no remoting problems: The messaging system guarantees the delivery of the call
 * after it has committed to handle it. Only local problems can occur: A failing database
 * connection, an out of memory situation, etc. are the only reasons for the call to fail; and then
 * the sender will generally have some problem itself.
 * </ul>
 * Additional meta data, like the queue name that the message has to be sent to, has to be retrieved
 * from a messaging registry. The coordinates for this lookup are the fully qualified name of the
 * interface and the version of the artifact the interface is packaged in.
 * <h3>Receiver</h3>
 * The receiver can safely assume that nothing "remote" is happening <i>outside</i> the call, i.e.:
 * <ul>
 * <li>The handling of the call can be transactional: If the receiver requires a transaction, then
 * the message will not be consumed until the transaction is committed, and if the transaction fails
 * even after the call was handled, everything in this transaction will be undone... and the message
 * will eventually be delivered again.
 * <li>There are no remoting problems: The receiver can safely throw an exception without affecting
 * the sender of the message.
 * </ul>
 * Additional meta data, like the queue name that the message has to be fetched, has to be retrieved
 * by some other means, ideally from a messaging registry. The coordinates for this lookup are the
 * fully qualified name of the interface and the version of the artifact the interface is packaged
 * in.
 * 
 * @author Rüdiger zu Dohna
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@ServiceHandlerType(MessageApiSendDelegater.class)
public @interface MessageApi {
    // empty
}
