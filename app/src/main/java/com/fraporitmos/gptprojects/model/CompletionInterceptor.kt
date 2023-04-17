package com.fraporitmos.gptprojects.model
import com.fraporitmos.gptprojects.*
import com.fraporitmos.gptprojects.service.CompletionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CompletionInterceptor {
    fun postCompletion(prompt: String, callback: (CompletionResponse) -> Unit) {
        val service = RetrofitInstance.getRetroInstance().create(CompletionService::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                var message = Message(
                    content = prompt,
                    role = "user"
                )
                val data = CompletionData(
                    List(1) { message },
                    "gpt-3.5-turbo"
                )
                val response = service.getCompletion(data,"Bearer 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")
                callback(response)
            } catch (e: Exception) {
                (e as? HttpException)?.let {

                }
            }
        }
    }

}