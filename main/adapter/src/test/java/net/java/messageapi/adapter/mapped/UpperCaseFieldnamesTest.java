package net.java.messageapi.adapter.mapped;

import static org.junit.Assert.*;
import net.java.messageapi.adapter.mapped.Mapping;
import net.java.messageapi.adapter.mapped.MappingBuilder;
import net.sf.twip.TwiP;
import net.sf.twip.Values;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TwiP.class)
public class UpperCaseFieldnamesTest {

    private final Mapping mapping = new MappingBuilder("op").upperCaseFields().build();

    public static final Integer[] LENGTHS = { 1, 2, 3, 4, 5 };

    private String map(String string) {
        return mapping.getMappingForField(string).getAttributeName();
    }

    @Test
    public void shouldUpperCaseAllLowerCase(@Values("LENGTHS") int length) throws Exception {
        String input = "lower".substring(0, length);
        String expected = "LOWER".substring(0, length);
        assertEquals(expected, map(input));
    }

    @Test
    public void shouldLeaveAllUpperCase(@Values("LENGTHS") int length) throws Exception {
        String inout = "UPPER".substring(0, length);
        assertEquals(inout, map(inout));
    }

    @Test
    public void shouldCamelCaseOneOne() throws Exception {
        assertEquals("A_B", map("aB"));
    }

    @Test
    public void shouldCamelCaseTwoOne() throws Exception {
        assertEquals("AB_C", map("abC"));
    }

    @Test
    public void shouldCamelCaseOneTwo() throws Exception {
        assertEquals("A_BC", map("aBC"));
    }

    @Test
    public void shouldCamelCaseTwoTwo() throws Exception {
        assertEquals("AB_CD", map("abCD"));
    }

    @Test
    public void shouldCamelCaseTwice() throws Exception {
        assertEquals("AB_CDEF_GH", map("abCDefGH"));
    }

    @Test
    public void shouldNotCamelCaseFirst() throws Exception {
        assertEquals("AB_CD", map("AbCd"));
    }
}
