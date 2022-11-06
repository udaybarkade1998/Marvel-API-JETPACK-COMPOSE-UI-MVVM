package com.ud.marvel2022.model.roomTable

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "bookmark")
data class BookmarksTable(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int
):Parcelable