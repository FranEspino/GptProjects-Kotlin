package com.fraporitmos.gptprojects.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fraporitmos.gptprojects.CompletionResponse
import com.fraporitmos.gptprojects.model.CompletionInterceptor

class CompletionViewModel: ViewModel() {
    private  var interceptor: CompletionInterceptor = CompletionInterceptor()
    private  var completionLiveData: MutableLiveData<CompletionResponse> = MutableLiveData()

    fun observeCompletionLiveData(): MutableLiveData<CompletionResponse> {
        return completionLiveData
    }

    fun postCompletionLiveData(promt:String) {
        interceptor.postCompletion(promt){
            completionLiveData.postValue(it)
        }
    }

}