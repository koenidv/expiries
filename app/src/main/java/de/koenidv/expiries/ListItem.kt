package de.koenidv.expiries

abstract class ListItem(
    @androidx.room.Ignore
    val type: Int
) {
    companion object {
        const val TYPE_ARTICLE = 0
        const val TYPE_DIVIDER = 1
    }

    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
}
