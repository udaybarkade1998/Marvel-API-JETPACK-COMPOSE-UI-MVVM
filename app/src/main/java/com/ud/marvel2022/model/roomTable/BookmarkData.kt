package com.ud.marvel2022.model.roomTable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//class used to store list of bookmarks i.e BookmarkTable

@Parcelize
data class BookmarkData(
    val bookmarks: MutableList<BookmarksTable>?
):Parcelable