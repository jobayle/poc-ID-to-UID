# ID to UID

This Proof-Of-Concept tests an ID to UID mapper to mitigate the security issue
of working with sequential IDs, without adding a new index to retrieve elements
from the database and benefit from the optimized index on sequential columns.

The goal is to provide a bijection to convert an ID to a string looking like a
pseudo-generated alphanumeric string (the UID), and convert back to the
original ID, in the least amount of time possible.

### Blowfish encryption

In kotlin, columns of type BIGINT are translated to instances of Long, which
are represented using 8 bytes (64bits).

Blowfish is the preferred cypher algorithm to use in this scenario for the
following reasons:
 * Uses a 64bits block
 * Has the best performances for this class of encryption

Average cipher/decipher time is less than 1µs on my computer
(Intel i7-12700H @ 2.30 GHz)

### Base32

The result of the Blowfish encryption is then encoded using Base32, because
Base32 encodes packets of 5 bytes, the resulting output is made of 13 letters
followed by 3 equal signs for padding.

The mapper also removes and add the padding so the returned UID is only made of
13 letters.

Example: `id=314 -> toUid() -> uid=3ZREIP6QC5QUS -> toId() -> restoredId=314`
