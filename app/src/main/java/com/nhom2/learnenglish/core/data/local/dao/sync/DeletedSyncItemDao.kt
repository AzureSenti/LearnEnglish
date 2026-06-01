package com.nhom2.learnenglish.core.data.local.dao.sync

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.entity.sync.DeletedSyncItemEntity

@Dao
interface DeletedSyncItemDao {

    @Insert
    fun insert(item: DeletedSyncItemEntity)

    @Query("SELECT * FROM deleted_sync_items")
    fun getAll(): List<DeletedSyncItemEntity>

    @Query("DELETE FROM deleted_sync_items")
    fun deleteAll()
}
