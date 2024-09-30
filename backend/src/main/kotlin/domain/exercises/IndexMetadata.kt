package domain.exercises

import domain.users.UserId

data class IndexMetadata(
    val title: String,
    val tags: Set<Tag>,
    val authorId: UserId,
    val creationTimestamp: Long
) {
    init {
        require(title.isNotBlank()) {
            "Title cannot be blank or whitespace."
        }
    }
}