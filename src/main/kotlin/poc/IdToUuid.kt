package poc

import org.bouncycastle.crypto.engines.AESEngine
import org.bouncycastle.crypto.params.KeyParameter
import java.util.UUID

/**
 * Mapper for ID (Long) to UUID and back.
 *
 * @param keyBytes key (size must be 128/192/256 bits (16,24,32 bytes))
 */
class IdToUuid(keyBytes: ByteArray) {

    private val key = KeyParameter(keyBytes)
    private val ciphering = AESEngine.newInstance().apply { init(true, key) }
    private val deciphering = AESEngine.newInstance().apply { init(false, key) }

    init {
        assert(Long.SIZE_BYTES == 8) // Always true
    }

    fun toUuid(id: Long): UUID {
        val input  = id.toByteArray(16)
        val output = ByteArray(16)

        // cypher
        ciphering.processBlock(input, 0, output, 0)

        // split in two longs to create a UUID

        val mostSigBits = output.toLong()
        val leastSigBits = output.toLong(8)
        return UUID(mostSigBits, leastSigBits)
    }

    fun toId(uuid: UUID): Long {
        val input = uuid.mostSignificantBits.toByteArray().plus(uuid.leastSignificantBits.toByteArray())
        val output = ByteArray(16)

        deciphering.processBlock(input, 0, output, 0)

        return output.toLong()
    }

}
