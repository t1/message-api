package net.java.messageapi.adapter;

import static org.junit.Assert.*;

import org.junit.Test;

public class UpperCaseFieldnamesTest {

    private final Mapping mapping = new MappingBuilder("op").upperCaseFields().build();

    private String map(String string) {
        return mapping.getMappingForField(string).getAttributeName();
    }

    @Test
    public void shouldUpperCaseAllLowerCase1() throws Exception {
        shouldUpperCaseAllLowerCase(1);
    }

    @Test
    public void shouldUpperCaseAllLowerCase2() throws Exception {
        shouldUpperCaseAllLowerCase(2);
    }

    @Test
    public void shouldUpperCaseAllLowerCase3() throws Exception {
        shouldUpperCaseAllLowerCase(3);
    }

    @Test
    public void shouldUpperCaseAllLowerCase4() throws Exception {
        shouldUpperCaseAllLowerCase(4);
    }

    @Test
    public void shouldUpperCaseAllLowerCase5() throws Exception {
        shouldUpperCaseAllLowerCase(5);
    }

    private void shouldUpperCaseAllLowerCase(int length) throws Exception {
        String input = "lower".substring(0, length);
        String expected = "LOWER".substring(0, length);
        assertEquals(expected, map(input));
    }

    @Test
    public void shouldLeaveAllUpperCase1() throws Exception {
        shouldLeaveAllUpperCase(1);
    }

    @Test
    public void shouldLeaveAllUpperCase2() throws Exception {
        shouldLeaveAllUpperCase(2);
    }

    @Test
    public void shouldLeaveAllUpperCase3() throws Exception {
        shouldLeaveAllUpperCase(3);
    }

    @Test
    public void shouldLeaveAllUpperCase4() throws Exception {
        shouldLeaveAllUpperCase(4);
    }

    @Test
    public void shouldLeaveAllUpperCase5() throws Exception {
        shouldLeaveAllUpperCase(5);
    }

    private void shouldLeaveAllUpperCase(int length) throws Exception {
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
