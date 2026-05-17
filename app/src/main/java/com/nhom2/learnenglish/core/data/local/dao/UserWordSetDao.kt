package com.nhom2.learnenglish.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.entity.UserWordSetCrossRef

@Dao
interface UserWordSetDao : BaseDao<UserWordSetCrossRef> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockSet(crossRef: UserWordSetCrossRef)

    @Query("SELECT EXISTS(SELECT 1 FROM user_word_set_cross_ref WHERE user_id = :userId AND set_id = :setId)")
    suspend fun isSetUnlocked(userId: Long, setId: Long): Boolean

}