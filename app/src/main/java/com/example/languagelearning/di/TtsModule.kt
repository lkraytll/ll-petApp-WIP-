package com.example.languagelearning.di

import android.content.Context
import com.example.languagelearning.data.tts.SileroOnnxTtsManager
import com.example.languagelearning.data.tts.TtsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TtsModule {

    @Provides
    @Singleton
    fun provideTtsManager(
        @ApplicationContext context: Context
    ): TtsManager {
        return SileroOnnxTtsManager(context)
    }
}
