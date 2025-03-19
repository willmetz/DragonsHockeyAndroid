package com.slapshotapps.dragonshockey.di

import javax.inject.Qualifier


@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GameID