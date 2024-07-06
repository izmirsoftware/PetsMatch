package com.izmirsoftware.petsmatch.di

import android.content.Context
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.repo.FirebaseRepoImpl
import com.izmirsoftware.petsmatch.repo.FirebaseRepoInterFace
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }


    @Provides
    @Singleton
    fun provideGlide(context: Context): RequestManager {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        return Glide.with(context)
            .setDefaultRequestOptions(
                RequestOptions().placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.placeholder)
                    .placeholder(circularProgressDrawable)
            )
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    @Singleton
    fun provideStorage() = Firebase.storage

    @Provides
    @Singleton
    fun provideFirebaseFireStore() = Firebase.firestore


    @Singleton
    @Provides
    fun provideFirebaseRepo(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
    ): FirebaseRepoInterFace {
        return FirebaseRepoImpl(auth, firestore, storage)
    }
}