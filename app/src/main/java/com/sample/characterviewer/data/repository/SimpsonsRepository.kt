package com.sample.characterviewer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sample.characterviewer.adaptors.SimpsonsPagingSource
import com.sample.characterviewer.api.SimpsonsApi
import com.sample.characterviewer.data.model.Characters
import com.sample.characterviewer.data.model.RelatedTopic
import retrofit2.Response

class SimpsonsRepository {

    fun getSimpsonsCharacters(chars: String): Pager<Int, RelatedTopic> {

        return Pager(PagingConfig(pageSize = 20)) {
            SimpsonsPagingSource(
                SimpsonsApi
                    .getInstance(),
                chars = chars
            )
        }
    }

    suspend fun getSelectedCharacter(chars: String): Response<Characters> {
        return SimpsonsApi.getInstance().getSimpsonsCharacters(chars = chars)
    }
}