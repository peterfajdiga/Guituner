package peterfajdiga.guituner.general;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GeneralTest {
    @Test
    public void ceilPow2() {
        assertEquals(0, General.ceilPow2(0));
        assertEquals(1, General.ceilPow2(1));
        assertEquals(2, General.ceilPow2(2));
        assertEquals(4, General.ceilPow2(3));
        assertEquals(4, General.ceilPow2(4));
        assertEquals(8, General.ceilPow2(5));
        assertEquals(8, General.ceilPow2(8));
        assertEquals(16, General.ceilPow2(9));
        assertEquals(64, General.ceilPow2(64));
        assertEquals(128, General.ceilPow2(125));
        assertEquals(2048, General.ceilPow2(1238));
    }
}
