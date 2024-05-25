package com.sebastianvm.musicplayer.features.album.list

// class AlbumListStatePreviewParamsProvider : PreviewParameterProvider<AlbumListState> {
//    override val values: Sequence<AlbumListState>
//        get() = sequenceOf(AlbumListState(albumList = (1..10).map {
//            Album(
//                id = it.toLong(),
//                albumName = PreviewUtil.randomString(),
//                year = Random.nextLong(1000L, 9999L).let { n -> if (n % 3L == 0L) n else 0L },
//                artists = PreviewUtil.randomString(),
//                imageUri = ""
//            ).toModelListItemState()
//        }))
// }
//
// @ScreenPreview
// @Composable
// private fun AlbumListScreenPreview(@PreviewParameter(AlbumListStatePreviewParamsProvider::class) state: AlbumListState) {
//    ScreenPreview {
//        AlbumListScreen(
//            state = state,
//            navigateToAlbum = {},
//            openAlbumContextMenu = {},
//            openSortMenu = {},
//            navigateBack = {}
//        )
//    }
// }
