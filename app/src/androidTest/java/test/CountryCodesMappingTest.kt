package test

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.LineNumberReader

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.25
 */
@RunWith(AndroidJUnit4::class)
class CountryCodesMappingTest {

    @Test
    fun checkForFileMapping() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val input = LineNumberReader(appContext.assets.open("country_codes.txt").reader())
        val countryMap = input.readLines().sorted().associate { Pair(it.substringBefore("/").trim(), it.substringAfter("/").trim()) }
        input.close()
        Assert.assertTrue(countryMap.isNotEmpty())
    }
}