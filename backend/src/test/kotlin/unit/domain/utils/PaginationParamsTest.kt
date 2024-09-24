package unit.domain.utils

import domain.utils.PaginationParams
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PaginationParamsTest {

    @Test
    fun `should create PaginationParams with valid limit and offset`() {
        val params = PaginationParams(10, 5)
        assertEquals(10, params.limit)
        assertEquals(5, params.offset)
    }

    @Test
    fun `should throw exception when limit is negative`() {
        val exception = assertThrows<IllegalArgumentException> {
            PaginationParams(-1, 5)
        }
        assertEquals("Limit cannot be negative", exception.message)
    }

    @Test
    fun `should throw exception when limit exceeds maximum allowed value`() {
        val exception = assertThrows<IllegalArgumentException> {
            PaginationParams(101, 5)
        }
        assertEquals("Limit exceeds maximum allowed value", exception.message)
    }

    @Test
    fun `should throw exception when offset is negative`() {
        val exception = assertThrows<IllegalArgumentException> {
            PaginationParams(10, -1)
        }
        assertEquals("Offset cannot be negative", exception.message)
    }

    @Test
    fun `should default limit to 50 and offset to 0 when null values are provided`() {
        val params = PaginationParams(null, null)
        assertEquals(50, params.limit)
        assertEquals(0, params.offset)
    }

    @Test
    fun `should use default limit when null is provided and offset is valid`() {
        val params = PaginationParams(null, 10)
        assertEquals(50, params.limit)
        assertEquals(10, params.offset)
    }

    @Test
    fun `should use default offset when null is provided and limit is valid`() {
        val params = PaginationParams(10, null)
        assertEquals(10, params.limit)
        assertEquals(0, params.offset)
    }

    @Test
    fun `should handle nullable values and respect limit constraints`() {
        val params = PaginationParams(null, null)
        assertTrue(params.limit in 0..100)
        assertTrue(params.offset >= 0)
    }
}