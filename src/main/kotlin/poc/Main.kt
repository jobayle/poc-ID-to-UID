package poc

import org.apache.commons.lang3.RandomStringUtils
import java.time.Instant
import java.util.stream.LongStream
import kotlin.random.Random
import kotlin.time.measureTime

object Main {

    fun newRandomKey(): ByteArray = RandomStringUtils.secureStrong().nextAlphanumeric(56).toByteArray()

    @JvmStatic
    fun main(args: Array<String>) {
        val rng = Random(Instant.now().epochSecond)
        // Random key, should be stored instead
        val keyMapper = IdToUid(newRandomKey())

        LongStream.generate(rng::nextLong)
        LongStream.range(0, 20000)
            .parallel()
            .map { rng.nextLong() }
            .forEach { id ->
                lateinit var uid: String
                var restoredId: Long
                val cypherTime = measureTime {
                    uid = keyMapper.toUid(id)
                }
                val decipherTime = measureTime {
                    restoredId = keyMapper.toId(uid)
                }
                println("id=$id, uid=$uid, restoredId=$restoredId (cipherTime=$cypherTime, decipherTime=$decipherTime)")
                assert(id == restoredId) { "$id != $restoredId" }
            }
    }

}
