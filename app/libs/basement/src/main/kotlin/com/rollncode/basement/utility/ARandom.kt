package com.rollncode.basement.utility

import java.util.*

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.09.27
 */
object ARandom {

    private val r by lazy { Random(System.currentTimeMillis()) }
    private val sb by lazy { StringBuilder() }
    private val alphabet by lazy {
        val list = mutableListOf<Char>()

        var i = 'a'
        while (i++ < 'z') {
            list += i
        }
        i = 'A'
        while (i++ < 'Z') {
            list += i
        }
        list.toTypedArray()
    }
    private val avatars by lazy {
        arrayOf(
                "https://cdn0.iconfinder.com/data/icons/user-pictures/100/matureman2-256.png",
                "https://polymath.com/wp-content/uploads/2015/11/flat-faces-icons-circle-6.png",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/63/Creative-Tail-People-businness-man.svg/1024px-Creative-Tail-People-businness-man.svg.png",
                "https://image.flaticon.com/icons/svg/190/190673.svg",
                "https://www.shareicon.net/data/2016/09/05/825176_people_512x512.png",
                "https://cdn.pixabay.com/photo/2017/03/01/22/18/avatar-2109804_960_720.png"
               )
    }
    private val backgrounds by lazy {
        arrayOf(
                "https://weinspiredesign.com/wp-content/uploads/2017/06/spiritual.png",
                "https://i.ytimg.com/vi/QX4j_zHAlw8/maxresdefault.jpg",
                "http://cdn.mos.cms.futurecdn.net/cb08aa1c246ead664f25c45a58a41f0d.jpg",
                "https://i.ytimg.com/vi/xC5n8f0fTeE/maxresdefault.jpg",
                "http://newsroom.unfccc.int/media/777629/flickr_projoshua-mayer_cpforets.jpg"
               )
    }

    private fun toString(action: StringBuilder.() -> Unit): String {
        sb.delete(0, sb.length).action()
        return sb.toString()
    }

    fun getString(length: Int = 8) = toString {
        var i = 0
        while (i++ < length) {
            sb.append(alphabet[nextInt(alphabet.size)])
        }
    }

    fun getImageUri(avatar: Boolean = true, seed: Any? = null): String {
        val values = if (avatar) avatars else backgrounds
        if (seed != null) {
            return values[Math.abs(seed.hashCode()) % values.size]
        }
        return values[nextInt(values.size)]
    }

    fun nextInt(max: Int)
            = r.nextInt(max)

    fun getStrings(size: Int = Int.MIN_VALUE): Array<String> {
        val s = if (size == Int.MIN_VALUE) 1 + nextInt(0xF) else size
        val strings = mutableListOf<String>()

        while (strings.size < s) {
            strings += getString()
        }
        return strings.toTypedArray()
    }
}