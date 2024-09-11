package poc

import org.apache.commons.lang3.RandomUtils
import org.bouncycastle.crypto.engines.BlowfishEngine
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.util.encoders.Base32
import java.nio.ByteBuffer

private fun Long.toByteArray(): ByteArray {
    val buffer = ByteBuffer.allocate(Long.SIZE_BYTES)
    buffer.putLong(this)
    return buffer.array()
}

private fun ByteArray.toLong(): Long {
    val buffer = ByteBuffer.wrap(this)
    return buffer.getLong()
}

/**
 * Mapper for ID (Long) to UID (String) and back.
 *
 * @param keyBytes key (size must be between 32 and 448 bits (4~56 bytes))
 */
class IdToUid(keyBytes: ByteArray) {

    private val key = KeyParameter(keyBytes)
    private val ciphering = BlowfishEngine().apply { init(true, key) }
    private val deciphering = BlowfishEngine().apply { init(false, key) }

    init {
        assert(Long.SIZE_BYTES == 8) // Always true
    }

    fun toUid(id: Long): String {
        val input  = id.toByteArray()
        val output = ByteArray(8)

        // cypher
        ciphering.processBlock(input, 0, output, 0)

        // Base 32 looks like a pseudo-random alphanumeric String (remove padding)
        return Base32.toBase32String(output).substring(0, 13)
    }

    fun toId(uid: String): Long {
        val input = Base32.decode("$uid===") // Add removed padding
        val output = ByteArray(Long.SIZE_BYTES)

        deciphering.processBlock(input, 0, output, 0)

        return output.toLong()
    }

}

