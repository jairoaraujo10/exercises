package infra.api.routes.response

import domain.exercises.Tag

data class TagView(val value: String) {

    companion object {
        fun from(tag: Tag): TagView {
            return TagView(tag.value)
        }
    }

    fun toTag() : Tag {
        return Tag(value)
    }
}