package com.ud.marvel2022.model.roomTable

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "marvel_characters")
@Parcelize
data class CharacterInfoTable(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "comics")
    val comics: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "thumbnail")
    val thumbnail: String
) : Parcelable
