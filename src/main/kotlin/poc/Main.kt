package poc

import org.apache.commons.lang3.RandomUtils
import java.time.Instant
import java.util.UUID
import java.util.stream.LongStream
import kotlin.random.Random
import kotlin.time.measureTime

object Main {

    fun newRandomKey(): ByteArray = RandomUtils.secureStrong().randomBytes(32)

    @JvmStatic
    fun main(args: Array<String>) {
        val rng = Random(Instant.now().epochSecond)
        // Random key, should be stored instead
        val keyMapper = IdToUuid(newRandomKey())

        LongStream.generate(rng::nextLong)
        LongStream.range(0, 20000)
            .parallel()
            .map { rng.nextLong() }
            .forEach { id ->
                lateinit var uuid: UUID
                var restoredId: Long
                val cypherTime = measureTime {
                    uuid = keyMapper.toUuid(id)
                }
                val decipherTime = measureTime {
                    restoredId = keyMapper.toId(uuid)
                }
                println("id=$id, uuid=$uuid, restoredId=$restoredId (cipherTime=$cypherTime, decipherTime=$decipherTime)")
                assert(id == restoredId) { "$id != $restoredId" }
            }
    }

}
