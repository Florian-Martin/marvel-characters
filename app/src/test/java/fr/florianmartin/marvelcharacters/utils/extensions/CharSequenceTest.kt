package fr.florianmartin.marvelcharacters.utils.extensions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.security.MessageDigest

class CharSequenceTest {

    private lateinit var loggerMock: Logger

    @Before
    fun setUp() {
        loggerMock = mock(Logger::class.java)
    }

    @Test
    fun `hash returns wrong hashed input`() {
        val input = "abcd"
        val wrongHash = "sd54fqsdf"
        assertNotEquals(wrongHash, input.hash(MessageDigest.getInstance("MD5")))
    }

    @Test
    fun `formatDate returns correctly formatted date`() {
        val input = "2024-04-01T12:00:00+0000"
        val expected = "2024-04-01 at 12:00:00"
        assertEquals(expected, input.formatDate(loggerMock))
    }

    @Test
    fun `formatDate returns null when date format is invalid`() {
        val input = "01-02-1995:15:15:15"
        assertNull(input.formatDate(loggerMock))
    }

    @Test
    fun `parseEmoji returns correct emoji when if code is valid`() {
        val input = "U+1F525"
        val expected = String(Character.toChars(0x1F525))
        assertEquals(expected, input.parseEmoji(loggerMock))
    }

    @Test
    fun `parseEmoji returns null if code is invalid`() {
        val input = "abc+1F525"
        assertNull(input.parseEmoji(loggerMock))
    }

    @Test
    fun `httpToHttps replaces http with https`() {
        val input = "http://olweb.com"
        val expected = "https://olweb.com"
        assertEquals(expected, input.httpToHttps())
    }

    @Test
    fun `httpToHttps returns correct value if https is present in input`() {
        val input = "https://olweb.com"
        assertEquals(input, input.httpToHttps())
    }
}