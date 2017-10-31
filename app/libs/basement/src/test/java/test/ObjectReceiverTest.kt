package test

import com.rollncode.basement.interfaces.*
import com.rollncode.basement.utility.*
import org.junit.*

@Suppress("ReplaceCallWithComparison")
class ObjectReceiverTest {

    private val receiver = object : ObjectsReceiver {

        private var receiverString = "receiver.toString"
        private var receiverHash = 0xA

        override fun onObjectsReceive(event: Int, vararg objects: Any) {
            receiverString = objects[0] as String
            receiverHash = objects[1] as Int
        }

        override fun toString() = receiverString
        override fun hashCode() = receiverHash
    }

    @Test
    fun convertObjectReceiverToWReceiverTest() {
        val reference = WReceiver(receiver)

        Assert.assertFalse(reference.equals(null))
        Assert.assertTrue(reference.equals(receiver))
        Assert.assertTrue(receiver.hashCode() == reference.hashCode())
        Assert.assertTrue(receiver.toString() == reference.toString())
    }

    @Test
    fun onObjectsReceiveTest() {
        val reference = WReceiver(receiver)

        receiver.onObjectsReceive(0, "testString", 0xB)
        Assert.assertFalse(reference.equals(receiver))
        Assert.assertFalse(receiver.hashCode() == reference.hashCode())
        Assert.assertFalse(receiver.toString() == reference.toString())
    }

    @Test
    fun onWReceiverObjectsReceiveTest() {
        val reference = WReceiver(receiver)

        reference.onObjectsReceive(0, "testString", 0xB)
        Assert.assertFalse(reference.equals(receiver))
        Assert.assertFalse(receiver.hashCode() == reference.hashCode())
        Assert.assertFalse(receiver.toString() == reference.toString())
    }
}