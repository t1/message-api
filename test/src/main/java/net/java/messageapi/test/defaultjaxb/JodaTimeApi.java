package net.java.messageapi.test.defaultjaxb;

import net.java.messageapi.MessageApi;

import org.joda.time.Instant;
import org.joda.time.LocalDate;

/**
 * This api is in a separate package, so the EclipseLink JAXB provider doesn't complain... it seems to have troubles
 * with the DateTimeZone class having no default constructor.
 */
@MessageApi
public interface JodaTimeApi {
    public void instantCall(Instant instantName);

    public void localDateCall(LocalDate date, boolean flag);
}
