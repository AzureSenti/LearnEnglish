package com.nhom2.learnenglish.core.data.local.entity.sync

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "deleted_sync_items")
data class DeletedSyncItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "item_type")
    val itemType: String, // "WORD_SET", "CROSS_REF"

    @ColumnInfo(name = "primary_id")
    val primaryId: String, // setId or wordId

    @ColumnInfo(name = "secondary_id")
    val secondaryId: String? = null // setId for cross-ref
)
