package com.sample.simpsonsviewer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sample.simpsonsviewer.adaptors.SimpsonsPagingSource
import com.sample.simpsonsviewer.api.SimpsonsApi
import com.sample.simpsonsviewer.data.model.Characters
import com.sample.simpsonsviewer.data.model.RelatedTopic
import retrofit2.Response

class SimpsonsRepository {

    fun getSimpsonsCharacters(): Pager<Int, RelatedTopic> {

        return Pager(PagingConfig(pageSize = 20)) {
            SimpsonsPagingSource(
                SimpsonsApi
                    .getInstance()
            )
        }
    }

    suspend fun getSelectedCharacter():Response<Characters> {
        return SimpsonsApi.getInstance().getSimpsonsCharacters()
    }
}