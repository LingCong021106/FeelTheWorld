package com.example.feeltheworld

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.TranslationRequest
import com.aallam.openai.api.audio.Voice
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatMessage
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.feeltheworld.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.Locale
import java.util.Objects
import java.util.concurrent.Flow


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityMainBinding
    private val client = OkHttpClient()
    lateinit var messageRecycleView: RecyclerView
    lateinit var messageAdapter: MessageAdapter
    lateinit var messageList: ArrayList<MessageModal>
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private var tts: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //declaration
        messageRecycleView = binding.recycleMessage
        messageList = ArrayList()
        messageAdapter = MessageAdapter(messageList)
        val layoutManager = LinearLayoutManager(applicationContext)
        messageRecycleView.layoutManager = layoutManager
        messageRecycleView.adapter = messageAdapter
        tts = TextToSpeech(this, this)

        binding.queryLayout.setOnClickListener{
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )

            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to Text")

            try{
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception){
                Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun getResponse(query: String){
        val apiKey = "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"
        val openAI = OpenAI(apiKey)
        val model = ModelId("gpt-3.5-turbo")

        val chatCompletionRequest = ChatCompletionRequest(
            model = model,
            messages = listOf(
                ChatMessage (
                    role = ChatRole.System,
                    content = "You are a helpful assistant!"
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = "$query"
                )
            )
        )

        val response = openAI.chatCompletion(chatCompletionRequest)
        val message = response.choices.first().message.content

        messageList.add(MessageModal(message!!, "bot"))
        messageAdapter.notifyDataSetChanged()
        textToSpeech(message)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE_SPEECH_INPUT){
            if(resultCode == RESULT_OK && data != null){
                val res: ArrayList<String> = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
                val response = Objects.requireNonNull(res)[0]
                messageList.add(MessageModal(response, "user"))
                messageAdapter.notifyDataSetChanged()
                CoroutineScope(Dispatchers.Main).launch {
                    getResponse(response)
                }
            }
        }
    }

    private fun textToSpeech(text: String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    public override fun onDestroy() {
        super.onDestroy()
        tts!!.stop()
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("tts", "The Language not supported!")
            }else{
                firstCall()
            }
        }
    }

    private fun firstCall(){
        val message = "Hello! How can I assist you today?"
        messageList.add(MessageModal(message!!, "bot"))
        messageAdapter.notifyDataSetChanged()
        textToSpeech(message)
    }
}