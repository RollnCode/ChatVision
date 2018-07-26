package test

import com.rollncode.basement.interfaces.ObjectsReceiver
import com.rollncode.basement.utility.ReceiverBus
import org.junit.Assert
import org.junit.Test
import java.util.*

class ReceiverBusTest {

    private val eventA = 0xA
    private val eventB = 0xB

    private val paramsA = arrayOf(0, "string", true, -1L, {})
    private val paramsB = arrayOf("string2", 2, {}, +5.0)

    @Test
    fun subscribeEventATest() {
        val receiver = Receiver()

        ReceiverBus.subscribe(receiver, eventA)

        ReceiverBus.notify(eventA, *paramsA)
        Assert.assertTrue(receiver.event == eventA)
        Assert.assertTrue(Arrays.equals(receiver.objects, paramsA))

        ReceiverBus.notify(eventB, *paramsB)
        Assert.assertFalse(receiver.event == eventB)
        Assert.assertFalse(Arrays.equals(receiver.objects, paramsB))
    }

    @Test
    fun subscribeEventBTest() {
        val receiver = Receiver()

        ReceiverBus.subscribe(receiver, eventB)

        ReceiverBus.notify(eventB, *paramsB)
        Assert.assertTrue(receiver.event == eventB)
        Assert.assertTrue(Arrays.equals(receiver.objects, paramsB))

        ReceiverBus.notify(eventA, *paramsA)
        Assert.assertFalse(receiver.event == eventA)
        Assert.assertFalse(Arrays.equals(receiver.objects, paramsA))
    }

    @Test
    fun subscribeEventABTest() {
        val receiver = Receiver()

        ReceiverBus.subscribe(receiver, eventA, eventB)

        ReceiverBus.notify(eventA, *paramsA)
        Assert.assertTrue(receiver.event == eventA)
        Assert.assertTrue(Arrays.equals(receiver.objects, paramsA))

        ReceiverBus.notify(eventB, *paramsB)
        Assert.assertTrue(receiver.event == eventB)
        Assert.assertTrue(Arrays.equals(receiver.objects, paramsB))
    }

    @Test
    fun unSubscribeTest() {
        val receiver = Receiver()

        ReceiverBus.subscribe(receiver, eventA, eventB)

        ReceiverBus.notify(eventA, *paramsA)
        Assert.assertTrue(receiver.event == eventA)
        Assert.assertTrue(Arrays.equals(receiver.objects, paramsA))

        ReceiverBus.notify(eventB, *paramsB)
        Assert.assertTrue(receiver.event == eventB)
        Assert.assertTrue(Arrays.equals(receiver.objects, paramsB))

        ReceiverBus.unSubscribe(receiver, eventA, eventB)

        receiver.event = 0
        receiver.objects = arrayOf()

        ReceiverBus.notify(eventA, *paramsA)
        Assert.assertFalse(receiver.event == eventA)
        Assert.assertFalse(Arrays.equals(receiver.objects, paramsA))

        ReceiverBus.notify(eventB, *paramsB)
        Assert.assertFalse(receiver.event == eventB)
        Assert.assertFalse(Arrays.equals(receiver.objects, paramsB))
    }
}

private class Receiver : ObjectsReceiver {

    var event = 0
    var objects = arrayOf<Any>()

    override fun onObjectsReceive(event: Int, vararg objects: Any) {
        this.event = event
        @Suppress("UNCHECKED_CAST")
        this.objects = objects as Array<Any>
    }
}