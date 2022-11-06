package com.ud.marvel2022.model

import android.os.Parcelable
import com.ud.marvel2022.model.roomTable.BookmarksTable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BookmarkData(
    val bookmarks: MutableList<BookmarksTable>?
):Parcelable