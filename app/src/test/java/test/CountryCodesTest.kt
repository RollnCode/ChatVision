package test

import org.junit.Assert
import org.junit.Test

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.25
 */
class CountryCodesTest {

    private val codesMap = mutableMapOf("UKR" to "380", "RUS" to "7", "DEU" to "49", "SWE" to "-1", "CAN" to "1", "USA" to "1")

    @Test
    fun checkForExistence() {
        Assert.assertNotNull(codesMap["UKR"])
        Assert.assertNull(codesMap["A"])
    }

    @Test
    fun checkForDuplicate() {
        Assert.assertFalse(codesMap.keys.size == codesMap.values.distinct().size)
        Assert.assertTrue(codesMap.keys.size - codesMap.values.distinct().size == 1)
        Assert.assertTrue(codesMap.values.groupBy { it }.map { it.value.size }.max() == 2)
    }
}