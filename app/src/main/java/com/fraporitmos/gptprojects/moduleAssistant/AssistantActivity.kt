package com.fraporitmos.gptprojects.moduleAssistant
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fraporitmos.gptprojects.databinding.AssistantActivityBinding
import com.fraporitmos.gptprojects.viewModel.CompletionViewModel
import java.util.*

class AssistantActivity : AppCompatActivity(), TextToSpeech.OnInitListener{
    companion object {
        private const val SPEECH_RECOGNITION = 1
    }
    private var tts : TextToSpeech? = null
    private lateinit var mCompletionViewModel: CompletionViewModel

    private lateinit var  mBinding: AssistantActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = AssistantActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.btnVoice.setOnClickListener {
            askSpeechInput()
        }
        tts = TextToSpeech(this, this)
        setupViewModel()

    }

    private fun setupViewModel() {
        mCompletionViewModel = ViewModelProvider(this)[CompletionViewModel::class.java]
        mCompletionViewModel.observeCompletionLiveData().observe(this) {
            mBinding.pbWaiting.visibility = View.GONE
            mBinding.ltRobot.playAnimation()
            mBinding.cvChatgpt.visibility = View.VISIBLE
            mBinding.tvResponse.visibility = View.VISIBLE
            speak(it.choices[0].message.content)

        }
    }

    private fun askSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Pregunta lo que quieras")
        startActivityForResult(intent, SPEECH_RECOGNITION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == SPEECH_RECOGNITION && resultCode == RESULT_OK){
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = result?.get(0)
            mCompletionViewModel.postCompletionLiveData(text.toString())
            mBinding.pbWaiting.visibility = View.VISIBLE

        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            tts!!.language = Locale("ES")
        }
    }

    override fun onDestroy() {
        if (tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }


    private fun speak(response:String) {
        val listener = object : UtteranceProgressListener() {
            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                val spannableString = SpannableString(response)
                spannableString.setSpan(
                    ForegroundColorSpan(
                        Color.parseColor("#7150F5")
                    ), start, end, 0)
                runOnUiThread {232
                    mBinding.tvResponse.text = spannableString
                }
            }

            override fun onStart(p0: String?) {}

            override fun onDone(utteranceId: String?) {
                runOnUiThread {
                    mBinding.ltRobot.pauseAnimation()
                }
            }
            override fun onError(utteranceId: String?) {
                runOnUiThread { mBinding.tvWelcome.text = "" }
            }
        }
        tts?.setOnUtteranceProgressListener(listener)
        tts?.speak(response, TextToSpeech.QUEUE_FLUSH, null, "id")
    }


}