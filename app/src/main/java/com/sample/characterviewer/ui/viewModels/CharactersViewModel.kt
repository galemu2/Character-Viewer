package com.sample.characterviewer.ui.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.cachedIn
import com.sample.characterviewer.R
import com.sample.characterviewer.data.model.Characters
import com.sample.characterviewer.data.model.RelatedTopic
import com.sample.characterviewer.data.repository.SimpsonsRepository
import com.sample.characterviewer.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class CharactersViewModel(
    application: Application,
    private val repository: SimpsonsRepository =
        SimpsonsRepository()
) :
    ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return if (modelClass.isAssignableFrom(CharactersViewModel::class.java)) {
                    val application = checkNotNull(extras[APPLICATION_KEY])
                    return CharactersViewModel(application) as T
                } else super.create(modelClass, extras)
            }
        }
    }

    private val chars: String = application.getString(R.string.API_VALUE)

    val networkState: MutableLiveData<Boolean> = MutableLiveData(false)

    var simpsonsCharacters = repository.getSimpsonsCharacters(
        chars = chars
    ).flow.cachedIn(viewModelScope)

    var searchedCharacters: MutableLiveData<Resource<List<RelatedTopic>>> = MutableLiveData()

    fun getSimpsonsCharacters(query: String) {
        viewModelScope.launch {
            searchedCharacters.postValue(Resource.Loading())
            val response = repository.getSelectedCharacter(chars)
            searchedCharacters.postValue(handleSimpsonsApiResponse(response, query))
        }
    }


    private fun handleSimpsonsApiResponse(response: Response<Characters>, query: String):
            Resource<List<RelatedTopic>> {
        if (response.isSuccessful) {
            response.body()?.let {

                val data = it.RelatedTopics.filter { relatedTopic ->
                    relatedTopic.Text.contains(query, ignoreCase = true)
                }

                return Resource.Success(data = data)
            }
        }
        return Resource.Error(response.message())
    }
}

