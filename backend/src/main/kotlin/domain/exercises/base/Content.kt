package domain.exercises.base

data class Content(
    val description: String,
    val possibleAnswers: List<String>,
    val correctAnswerIndex: Int
) {
    init {
        require(description.isNotBlank()) {
            "Description cannot be blank or whitespace."
        }
        require(possibleAnswers.size >= 2) {
            "There must be at least two possible answers."
        }
        require(possibleAnswers.none { it.isBlank() }) {
            "Answer text cannot be blank or whitespace."
        }
        require(possibleAnswers.size == possibleAnswers.distinct().size) {
            "Possible answers must not contain duplicates."
        }
        require(correctAnswerIndex in possibleAnswers.map { possibleAnswers.indexOf(it) }) {
            "Correct answer id must be present in possibleAnswers."
        }
    }
}