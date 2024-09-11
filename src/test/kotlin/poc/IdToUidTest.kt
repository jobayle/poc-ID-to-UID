package poc

import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.stream.LongStream
import kotlin.random.Random

class IdToUidTest {

    fun newRandomKey(): ByteArray = RandomStringUtils.secureStrong().nextAlphanumeric(56).toByteArray()

    // Check cipher/decipher engines are not affected by order
    @Test
    fun testReversedOrder() {
        val mapper = IdToUid(newRandomKey())

        val ids = listOf(0L, 1L, 2L, 3_333_333L, 4_444_444_444L, 5L)
        val uids = ids.map(mapper::toUid)
        val res = uids.reversed().map(mapper::toId).reversed()

        println(ids)
        println(res)

        assertArrayEquals(ids.toLongArray(), res.toLongArray())
    }

    // Heavy load, parallelized test, make sure IdToUid is reusable and re-entrant
    @Test
    fun testHeavyLoadInParallel() {
        val rng = Random(Instant.now().epochSecond)
        val keyMapper = IdToUid(newRandomKey())

        LongStream.range(0, 20_000)
            .parallel()
            .map { rng.nextLong() }
            .mapToObj { id -> id to keyMapper.toUid(id) }
            .also { assertTrue(it.isParallel) }
            .forEach { (id, uid) -> assertEquals(id, keyMapper.toId(uid)) }
    }

}
