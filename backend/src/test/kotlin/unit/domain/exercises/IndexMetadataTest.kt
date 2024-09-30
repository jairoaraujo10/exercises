package unit.domain.exercises

import domain.exercises.IndexMetadata
import domain.exercises.Tag
import domain.users.UserId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class IndexMetadataTest {

    @Test
    fun `valid metadata should be created successfully`() {
        val title = "Article Title"
        val tags = setOf(Tag("Kotlin"), Tag("Programming"))
        val authorId = UserId(123L)
        val creationTimestamp = System.currentTimeMillis()

        val metadata = IndexMetadata(title, tags, authorId, creationTimestamp)

        assertEquals(title, metadata.title)
        assertEquals(tags, metadata.tags)
        assertEquals(authorId, metadata.authorId)
        assertEquals(creationTimestamp, metadata.creationTimestamp)
    }

    @Test
    fun `title should not be blank or whitespace`() {
        val tags = setOf(Tag("Kotlin"), Tag("Programming"))
        val authorId = UserId(123L)
        val creationTimestamp = System.currentTimeMillis()

        val exception = assertThrows<IllegalArgumentException> {
            IndexMetadata(" ", tags, authorId, creationTimestamp)
        }
        assertEquals("Title cannot be blank or whitespace.", exception.message)
    }
}