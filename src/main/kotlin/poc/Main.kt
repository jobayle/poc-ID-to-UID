package poc

import org.apache.commons.lang3.RandomStringUtils
import java.time.Instant
import java.util.stream.LongStream
import kotlin.random.Random
import kotlin.time.measureTime

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val rng = Random(Instant.now().epochSecond)
        // Random key, should be stored instead
        val keyMapper = IdToUid(RandomStringUtils.secureStrong().nextAlphanumeric(56).toByteArray())

        LongStream.generate(rng::nextLong)
        LongStream.range(0, 20000).map { rng.nextLong() }
            .forEach { id ->
                lateinit var uid: String
                var restoredId: Long
                val cypherTime = measureTime {
                    uid = keyMapper.toUid(id)
                }
                val decypherTime = measureTime {
                    restoredId = keyMapper.toId(uid)
                }
                println("id=$id, uid=$uid, restoredId=$restoredId (cypherTime=$cypherTime, decypherTime=$decypherTime)")
            }
    }

}
