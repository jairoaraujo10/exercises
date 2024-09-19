package domain.utils

data class PaginationParams(val limit: Int, val offset: Int) {
    init {
        require(limit >= 0) { "Limit cannot be negative" }
        require(limit <= 100) { "Limit exceeds maximum allowed value" }
        require(offset >= 0) { "Offset cannot be negative" }
    }
    constructor(limit: Int?, offset: Int?) : this(limit ?: 50, offset ?: 0)
}