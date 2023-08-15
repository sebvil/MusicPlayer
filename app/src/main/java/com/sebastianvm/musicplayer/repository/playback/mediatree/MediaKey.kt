package com.sebastianvm.musicplayer.repository.playback.mediatree

data class MediaKey(
    val parentType: MediaTree.KeyType,
    val parentId: Long,
    val type: MediaTree.KeyType,
    val itemIndexOrId: Long = 0
) {
    companion object {
        fun fromString(key: String): MediaKey {
            val keyValues = key.split("-")
            return MediaKey(
                parentType = MediaTree.KeyType.valueOf(
                    keyValues.getOrNull(0) ?: MediaTree.KeyType.UNKNOWN.name
                ),
                parentId = keyValues.getOrNull(1)?.toLongOrNull() ?: 0,
                type = MediaTree.KeyType.valueOf(
                    keyValues.getOrNull(2) ?: MediaTree.KeyType.UNKNOWN.name
                ),
                itemIndexOrId = keyValues.getOrNull(3)?.toLongOrNull() ?: 0

            )
        }

        fun fromParent(
            parent: MediaKey,
            keyType: MediaTree.KeyType,
            itemIndexOrId: Long
        ): MediaKey = MediaKey(
            parentType = parent.type,
            parentId = parent.itemIndexOrId,
            type = keyType,
            itemIndexOrId = itemIndexOrId
        )

        fun fromChild(child: MediaKey): MediaKey = MediaKey(
            parentType = MediaTree.KeyType.UNKNOWN,
            parentId = 0,
            type = child.parentType,
            itemIndexOrId = child.parentId
        )
    }

    override fun toString(): String {
        return "${parentType.name}-$parentId-${type.name}-$itemIndexOrId"
    }
}
