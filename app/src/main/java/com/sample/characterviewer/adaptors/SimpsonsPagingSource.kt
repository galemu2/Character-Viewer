package com.sample.characterviewer.adaptors

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sample.characterviewer.BuildConfig
import com.sample.characterviewer.api.SimpsonsService
import com.sample.characterviewer.data.model.RelatedTopic
import com.sample.characterviewer.util.Const.OFFSET_VALUE
import com.sample.characterviewer.util.Const.STARTING_PAGE
import com.sample.characterviewer.util.Const.TAG

class SimpsonsPagingSource(private val api: SimpsonsService, private val chars:String) :
    PagingSource<Int,
        RelatedTopic>
    () {

    override fun getRefreshKey(state: PagingState<Int, RelatedTopic>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition = anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RelatedTopic> {
        Log.d(TAG, "load: Loading data")
        return try {
            val offset = params.key ?: 1
            val result = api.getSimpsonsCharacters(chars = chars).body()?.RelatedTopics?: emptyList()

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "load: ${result?.size}")
            }

            LoadResult.Page(
                data = result,
                prevKey = if (offset ==  STARTING_PAGE) null else offset - OFFSET_VALUE,
                nextKey = if (result.isNullOrEmpty()) null else offset + OFFSET_VALUE
            )
        } catch (e: Exception) {

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "load: ${e.message}")
                Log.d(TAG, "load: ${e.printStackTrace()}")
            }

            LoadResult.Error(e)
        }
    }
}