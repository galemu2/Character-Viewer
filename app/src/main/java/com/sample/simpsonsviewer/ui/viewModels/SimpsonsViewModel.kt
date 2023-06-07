package com.sample.simpsonsviewer.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sample.simpsonsviewer.data.model.Characters
import com.sample.simpsonsviewer.data.model.RelatedTopic
import com.sample.simpsonsviewer.data.repository.SimpsonsRepository
import com.sample.simpsonsviewer.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class SimpsonsViewModel(private val repository: SimpsonsRepository = SimpsonsRepository()) :
    ViewModel() {


    val networkState:MutableLiveData<Boolean> = MutableLiveData(false)

    var simpsonsCharacters = repository.getSimpsonsCharacters().flow.cachedIn(viewModelScope)

    var searchedCharacters: MutableLiveData<Resource<List<RelatedTopic>>> = MutableLiveData()

    fun getSimpsonsCharacters(query: String) {
        viewModelScope.launch {
            searchedCharacters.postValue(Resource.Loading())
            val response = repository.getSelectedCharacter()
            searchedCharacters.postValue(handleSimpsonsApiResponse(response, query))
        }
    }


    private fun handleSimpsonsApiResponse(response: Response<Characters>, query: String):
            Resource<List<RelatedTopic>> {
        if (response.isSuccessful) {
            response.body()?.let {

                val data = it.RelatedTopics.filter { relatedTopic ->
                    val index = relatedTopic.Text.indexOf("-")
                    val topic = relatedTopic.Text.substring(0, index)
                    topic.contains(query, ignoreCase = true)
                }

                return Resource.Success(data = data)
            }
        }
        return Resource.Error(response.message())
    }
}