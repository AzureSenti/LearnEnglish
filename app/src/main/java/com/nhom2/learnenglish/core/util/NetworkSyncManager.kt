package com.nhom2.learnenglish.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.nhom2.learnenglish.core.data.local.AppDatabase
import com.nhom2.learnenglish.core.data.repository.SyncRepository
import com.nhom2.learnenglish.core.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Utility class that triggers a background sync whenever the device has network connectivity.
 *
 * Usage:
 *   NetworkSyncManager.syncIfOnline(context)
 *
 * This should be called from onResume() of key activities (MainMenu, Profile, etc.)
 * to ensure data is pushed/pulled whenever the user comes back to the app with connectivity.
 */
object NetworkSyncManager {

    private const val TAG = "NetworkSyncManager"

    @Volatile
    private var isSyncing = false

    /**
     * Check connectivity and trigger a full sync in the background if online.
     * This method is safe to call multiple times — concurrent syncs are prevented.
     */
    fun syncIfOnline(context: Context) {
        val sessionManager = SessionManager(context)

        // Skip for guest users or users not logged in
        if (!sessionManager.isLoggedIn()) {
            return
        }

        // Skip if no network
        if (!isNetworkAvailable(context)) {
            Log.d(TAG, "No network available, skipping sync")
            return
        }

        // Prevent concurrent syncs
        if (isSyncing) {
            Log.d(TAG, "Sync already in progress, skipping")
            return
        }

        isSyncing = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val syncApi = RetrofitClient.getSyncApi()

                val syncRepository = SyncRepository(
                    syncApi = syncApi,
                    wordDao = db.wordDao(),
                    wordSetDao = db.wordSetDao(),
                    wordSetCrossDao = db.wordSetCrossDao(),
                    wordSrsDao = db.wordSrsDao(),
                    grammarProgressDao = db.userGrammarProgressDao(),
                    userWordSetDao = db.userWordSetDao(),
                    userDao = db.userDao(),
                    sessionManager = sessionManager
                )

                val success = syncRepository.performFullSync()
                Log.i(TAG, "Background sync result: $success")
            } catch (e: Exception) {
                Log.e(TAG, "Background sync failed", e)
            } finally {
                isSyncing = false
            }
        }
    }

    /**
     * Check if the device currently has an active internet connection.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
