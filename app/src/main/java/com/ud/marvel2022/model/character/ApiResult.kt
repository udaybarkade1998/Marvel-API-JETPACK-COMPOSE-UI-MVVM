package com.ud.marvel2022.model.character


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull

@Parcelize
data class ApiResult(

    val id: Int,

    val comics: Comics?,

    val description: String,

    val name: String,

    val thumbnail: Thumbnail
) : Parcelable