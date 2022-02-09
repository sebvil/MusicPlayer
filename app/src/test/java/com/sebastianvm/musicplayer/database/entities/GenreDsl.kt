package com.sebastianvm.musicplayer.database.entities


fun genre(init: Genre.() -> Genre): Genre = newGenre().init()

private fun newGenre(): Genre =
    Genre(genreName = "")

