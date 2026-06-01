package com.nhom2.learnenglish.core.data.local.dao.word

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.dao.BaseDao
import com.nhom2.learnenglish.core.data.local.entity.word.UserWordSetCrossRef

@Dao
interface UserWordSetDao : BaseDao<UserWordSetCrossRef> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun unlockSet(crossRef: UserWordSetCrossRef)

    @Query("SELECT EXISTS(SELECT 1 FROM user_word_set_cross_ref WHERE user_id = :userId AND set_id = :setId)")
    fun isSetUnlocked(userId: Long, setId: String): Boolean

    // --- Sync queries ---

    @Query("SELECT set_id FROM user_word_set_cross_ref WHERE user_id = :userId")
    fun getUnlockedSetIds(userId: Long): List<String>

    @Query("DELETE FROM user_word_set_cross_ref WHERE user_id = :userId")
    fun deleteAllForUser(userId: Long)


}