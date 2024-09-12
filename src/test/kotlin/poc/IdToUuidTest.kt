package poc

import org.apache.commons.lang3.RandomUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.stream.LongStream
import kotlin.random.Random

class IdToUuidTest {

    fun newRandomKey(): ByteArray = RandomUtils.secureStrong().randomBytes(32)

    // Check different mappers using the same key yield the same results
    @Test
    fun testIsDeterministic() {
        val key = newRandomKey()

        val id = 314L

        val mapper1 = IdToUuid(key)
        val mapper2 = IdToUuid(key)

        val uuid1 = mapper1.toUuid(id)
        val uuid2 = mapper2.toUuid(id)
        assertEquals(uuid1, uuid2)

        val restoredId1 = mapper1.toId(uuid2)
        val restoredId2 = mapper2.toId(uuid1)
        assertEquals(restoredId1, restoredId2)
    }

    // Check cipher/decipher engines are not affected by order
    @Test
    fun testReversedOrder() {
        val mapper = IdToUuid(newRandomKey())

        val ids = listOf(0L, 1L, 2L, 3_333_333L, 4_444_444_444L, 5L)
        val uuids = ids.map(mapper::toUuid)
        val res = uuids.reversed().map(mapper::toId).reversed()

        assertArrayEquals(ids.toLongArray(), res.toLongArray())
    }

    // Heavy load, parallelized test, make sure IdToUid is reusable and re-entrant
    @Test
    fun testHeavyLoadInParallel() {
        val rng = Random(Instant.now().epochSecond)
        val keyMapper = IdToUuid(newRandomKey())

        LongStream.range(0, 20_000)
            .parallel()
            .map { rng.nextLong() }
            .mapToObj { id -> id to keyMapper.toUuid(id) }
            .also { assertTrue(it.isParallel) }
            .forEach { (id, uuid) -> assertEquals(id, keyMapper.toId(uuid)) }
    }
}