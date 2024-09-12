package poc

import java.nio.ByteBuffer

fun Long.toByteArray(size: Int = Long.SIZE_BYTES): ByteArray {
    val buffer = ByteBuffer.allocate(size)
    buffer.putLong(this)
    return buffer.array()
}

fun ByteArray.toLong(shift: Int = 0): Long {
    val buffer = ByteBuffer.wrap(this)
    buffer.position(shift)
    return buffer.getLong()
}
