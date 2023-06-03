package com.sample.simpsonsviewer.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sample.simpsonsviewer.BuildConfig
import com.sample.simpsonsviewer.data.SimpsonsRepository
import com.sample.simpsonsviewer.util.Const.TAG

class SimpsonsViewModel(private var repository: SimpsonsRepository = SimpsonsRepository()) :
    ViewModel() {

    var simpsonsCharacters = repository.getSimpsonsCharacters().flow.cachedIn(viewModelScope)
    init {
        if(BuildConfig.DEBUG){
            Log.d(TAG, "item: $simpsonsCharacters")
        }
    }
//    var simpsonsCharacters: MutableLiveData<Resource<Characters>> = MutableLiveData()

//    init {
//        getSimpsonsCharacters()
//    }
//    fun getSimpsonsCharacters() {
//        viewModelScope.launch {
//            simpsonsCharacters.postValue(Resource.Loading())
//            val response = repository.getSimpsonsCharacters()
//            simpsonsCharacters.postValue(handleSimpsonsApiResponse(response))
//        }
//    }
//
//    private fun handleSimpsonsApiResponse(response: Response<Characters>): Resource<Characters> {
//        if (response.isSuccessful) {
//            response.body()?.let { characters ->
//                Log.d("TAG", "Success: ${characters.RelatedTopics.size}")
//                return Resource.Success(characters)
//            }
//        }
//        Log.d("TAG", "ERROR.  ${response.message()}")
//        return Resource.Error(response.message())
//    }
}