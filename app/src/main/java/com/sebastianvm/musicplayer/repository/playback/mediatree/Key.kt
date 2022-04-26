package com.sebastianvm.musicplayer.repository.playback.mediatree

data class Key(val type: MediaTree.KeyType, val id: Long, val index: Int = 0) {
    companion object {
        fun fromString(key: String): Key {
            val keyValues = key.split("-")
            return Key(
                type = MediaTree.KeyType.valueOf(
                    keyValues.getOrNull(0) ?: MediaTree.KeyType.UNKNOWN.name
                ),
                id = keyValues.getOrNull(1)?.toLongOrNull() ?: 0,
                index = keyValues.getOrNull(2)?.toIntOrNull() ?: 0

            )
        }
    }

    override fun toString(): String {
        return "${type.name}-$id-$index"
    }
}