package com.sample.simpsonsviewer.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sample.simpsonsviewer.BuildConfig
import com.sample.simpsonsviewer.api.SimpsonsService
import com.sample.simpsonsviewer.data.model.RelatedTopic
import com.sample.simpsonsviewer.util.Const.TAG

private const val STARTING_PAGE = 1
private const val OFFSET_VALUE = 20

class SimpsonsPagingSource(private val api: SimpsonsService) : PagingSource<Int, RelatedTopic>() {

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
            val result = api.getSimpsonsCharacters().RelatedTopics

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "load: ${result.size}")
            }

            LoadResult.Page(
                data = result,
                prevKey = if (offset == STARTING_PAGE) null else offset - OFFSET_VALUE,
                nextKey = if (result.isEmpty()) null else offset + OFFSET_VALUE
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