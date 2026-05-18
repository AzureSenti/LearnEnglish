package com.nhom2.learnenglish.core.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.nhom2.learnenglish.core.data.local.dao.UserDao
import com.nhom2.learnenglish.core.data.local.entity.UserEntity
import com.nhom2.learnenglish.core.util.AppExecutors
import com.nhom2.learnenglish.core.util.SessionManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class UserRepository(
    executors: AppExecutors = AppExecutors.getInstance(),
    private val userDao: UserDao,
    private val sessionManager: SessionManager
    ) : BaseRepository(executors) {

    suspend fun login(email: String, password: String, rememberMe: Boolean = true): UserEntity {
        return suspendCancellableCoroutine { continuation ->
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        if (user != null) {
                            sessionManager.saveAuthToken(user.uid, rememberMe)
                            val userEntity = UserEntity(
                                userId = user.uid,
                                fullName = user.displayName ?: "User",
                                avatarUrl = user.photoUrl?.toString() ?: "",
                                email = user.email ?: email,
                                coins = 0
                            )
                            executors.diskIO.execute {
                                kotlinx.coroutines.runBlocking {
                                    userDao.deleteAll()
                                    userDao.insert(userEntity)
                                }
                            }
                            continuation.resume(userEntity)
                        } else {
                            continuation.resumeWithException(Exception("User is null"))
                        }
                    } else {
                        continuation.resumeWithException(task.exception ?: Exception("Login failed"))
                    }
                }
        }
    }

    suspend fun register(email: String, password: String, username: String, rememberMe: Boolean = true): UserEntity {
        return suspendCancellableCoroutine { continuation ->
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        if (user != null) {
                            sessionManager.saveAuthToken(user.uid, rememberMe)
                            user.updateProfile(
                                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build()
                            )
                            val userEntity = UserEntity(
                                userId = user.uid,
                                fullName = username,
                                avatarUrl = user.photoUrl?.toString() ?: "",
                                email = user.email ?: email,
                                coins = 0
                            )
                            executors.diskIO.execute {
                                kotlinx.coroutines.runBlocking {
                                    userDao.deleteAll()
                                    userDao.insert(userEntity)
                                }
                            }
                            continuation.resume(userEntity)
                        } else {
                            continuation.resumeWithException(Exception("User is null"))
                        }
                    } else {
                        continuation.resumeWithException(task.exception ?: Exception("Register failed"))
                    }
                }
        }
    }
    
    suspend fun resetPassword(email: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(true)
                    } else {
                        continuation.resumeWithException(task.exception ?: Exception("Reset failed"))
                    }
                }
        }
    }

}