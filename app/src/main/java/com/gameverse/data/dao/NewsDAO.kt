package com.gameverse.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gameverse.data.model.NewsItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(newsItems: List<NewsItem>)

    @Query("SELECT * FROM news")
    fun getNews(): Flow<List<NewsItem>>

    @Query("SELECT COUNT(*) FROM news")
    suspend fun count(): Int
}
