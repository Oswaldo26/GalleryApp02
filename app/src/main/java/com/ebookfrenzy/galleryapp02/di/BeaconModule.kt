package com.ebookfrenzy.galleryapp02.di


import android.content.Context
import com.ebookfrenzy.galleryapp02.beacon.BeaconScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BeaconModule {

    @Provides
    @Singleton
    fun provideBeaconScanner(@ApplicationContext context: Context): BeaconScanner {
        return BeaconScanner(context)
    }
}
