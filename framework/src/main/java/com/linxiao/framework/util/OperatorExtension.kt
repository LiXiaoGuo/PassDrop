/**
 * Created by Extends on 2018/1/12.
 */

/**
 * 支持负数取值
 * eg:list(-1),即取最后一个值
 */
operator fun <E> List<E>.invoke(i: Int):E{
    return when (i) {
        in 0 until size -> this[i]
        in (-size+1) until 0 -> this[size+i]
        else -> throw error("list index out of range,size = $size ,index = $i")
    }
}

/**
 * 支持负数取值
 * eg:array(-1),即取最后一个值
 */
operator fun <E> Array<E>.invoke(i: Int):E{
    return when (i) {
        in 0 until size -> this[i]
        in (-size+1) until 0 -> this[size+i]
        else -> throw error("Array index out of range,size = $size ,index = $i")
    }
}

/**
 * 支持负数取值
 * eg:charSequence(-1),即取最后一个值
 */
operator fun CharSequence.invoke(i: Int):Char{
    return when (i) {
        in 0 until length -> this[i]
        in (-length+1) until 0 -> this[length+i]
        else -> throw error("CharSequence index out of range,length = $length ,index = $i")
    }
}

/**
 * 获取list指定的范围
 * 支持负数取值
 * 支持step步长
 * eg:
 * var list = listOf(1,2,3,4,5,6,7)
 * list[-1..4],即[7,1,2,3,4,5]
 */
operator fun <E> List<E>.get(range:IntRange) = range.map { invoke(it) }

/**
 * 获取array指定的范围
 * 支持负数取值
 * 支持step步长
 * eg:
 * var array = arrayOf(1,2,3,4,5,6,7)
 * array[-1..4],即[7,1,2,3,4,5]
 */
operator fun <E> Array<E>.get(range:IntRange) = range.map { invoke(it) }

/**
 * 截取字符串
 * end支持负数取值
 * start不支持负数取值
 */
operator fun CharSequence.get(start: Int,end:Int):String{
    val i = when (end) {
        in 0 until length -> end
        in (-length+1) until 0 -> length+end
        else -> throw error("CharSequence index out of range,length = $length ,end = $end")
    }
    return substring(start,i)

}

/**
 * 获取list指定的范围
 * 支持负数取值
 * 支持倒序取值
 * 支持step步长
 * eg:
 * var list = listOf(1,2,3,4,5,6,7)
 * list[4 downTo -1],即[5, 4, 3, 2, 1, 7]
 */
operator fun <E> List<E>.get(range:IntProgression) = range.map { invoke(it) }

/**
 * 获取array指定的范围
 * 支持负数取值
 * 支持倒序取值
 * 支持step步长
 * eg:
 * var array = listOf(1,2,3,4,5,6,7)
 * array[4 downTo -1],即[5, 4, 3, 2, 1, 7]
 */
operator fun <E> Array<E>.get(range:IntProgression) = range.map { invoke(it) }

/**
 * 给list分组，以自定义数量一组
 */
fun <E> List<E>.grouping(group:Int):List<List<E>>
        = (0 until size step group).map { get(it..(if(it+group<count()) it+group-1 else count()-1))}

/**
 * 给Array分组，以自定义数量一组
 */
fun <E> Array<E>.grouping(group:Int):List<List<E>>
        = (0 until size step group).map { get(it..(if(it+group<count()) it+group-1 else count()-1)) }

