package peterfajdiga.guituner.general;

import org.junit.Assert;
import org.junit.Test;

public class GeneralTest {
    @Test
    public void ceilPow2() {
        Assert.assertEquals(0, General.ceilPow2(0));
        Assert.assertEquals(1, General.ceilPow2(1));
        Assert.assertEquals(2, General.ceilPow2(2));
        Assert.assertEquals(4, General.ceilPow2(3));
        Assert.assertEquals(4, General.ceilPow2(4));
        Assert.assertEquals(8, General.ceilPow2(5));
        Assert.assertEquals(8, General.ceilPow2(8));
        Assert.assertEquals(16, General.ceilPow2(9));
        Assert.assertEquals(64, General.ceilPow2(64));
        Assert.assertEquals(128, General.ceilPow2(125));
        Assert.assertEquals(2048, General.ceilPow2(1238));
    }
}
