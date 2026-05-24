package com.nhom2.learnenglish.core.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.nhom2.learnenglish.core.data.local.dao.UserDao
import com.nhom2.learnenglish.core.data.local.entity.UserEntity
import com.nhom2.learnenglish.core.network.Auth.AuthApi
import com.nhom2.learnenglish.core.network.Auth.LoginRequest
import com.nhom2.learnenglish.core.util.AppExecutors
import com.nhom2.learnenglish.core.util.SessionManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class UserRepository @JvmOverloads constructor(
    executors: AppExecutors = AppExecutors.getInstance(),
    private val userDao: UserDao,
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
    ) : BaseRepository(executors) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        const val LOCAL_USER_ID = -1L
    }

    suspend fun login(username: String, password: String, isRememberMe: Boolean = false) : UserEntity {
        val user = signInWithFirebase(username, password)
        val token = fetchIdToken(user)

        sessionManager.createLoginSession(token, user.uid.hashCode().toLong(), isRememberMe)

        val userEntity = UserEntity(
            userId = user.uid,
            fullName = user.displayName ?: username,
            avatarUrl = user.photoUrl?.toString(),
            email = user.email,
            coins = 0
        )

        userDao.deleteAll()
        userDao.insert(userEntity)

        return userEntity
    }

    suspend fun register(email: String, password: String, fullName: String, isRememberMe: Boolean = false): UserEntity {
        val user = registerWithFirebase(email, password)
        updateFirebaseProfile(user, fullName)
        val token = fetchIdToken(user)

        sessionManager.createLoginSession(token, user.uid.hashCode().toLong(), isRememberMe)

        val userEntity = UserEntity(
            userId = user.uid,
            fullName = fullName,
            email = user.email,
            coins = 0
        )

        userDao.deleteAll()
        userDao.insert(userEntity)

        return userEntity
    }

    suspend fun resetPassword(email: String) {
        suspendCancellableCoroutine<Unit> { continuation ->
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(task.exception ?: Exception("Không thể gửi email đặt lại mật khẩu"))
                    }
                }
        }
    }

    private suspend fun signInWithFirebase(email: String, password: String): FirebaseUser {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = firebaseAuth.currentUser
                        if (currentUser != null) {
                            continuation.resume(currentUser)
                        } else {
                            continuation.resumeWithException(Exception("Không thể xác thực người dùng."))
                        }
                    } else {
                        continuation.resumeWithException(task.exception ?: Exception("Đăng nhập thất bại."))
                    }
                }
        }
    }

    private suspend fun registerWithFirebase(email: String, password: String): FirebaseUser {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = firebaseAuth.currentUser
                        if (currentUser != null) {
                            continuation.resume(currentUser)
                        } else {
                            continuation.resumeWithException(Exception("Không thể tạo tài khoản mới."))
                        }
                    } else {
                        continuation.resumeWithException(task.exception ?: Exception("Đăng ký thất bại."))
                    }
                }
        }
    }

    private suspend fun updateFirebaseProfile(user: FirebaseUser, fullName: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullName)
            .build()

        suspendCancellableCoroutine<Unit> { continuation ->
            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(task.exception ?: Exception("Không thể cập nhật hồ sơ."))
                    }
                }
        }
    }

    private suspend fun fetchIdToken(user: FirebaseUser): String {
        return suspendCancellableCoroutine { continuation ->
            user.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(task.result?.token.orEmpty())
                    } else {
                        continuation.resumeWithException(task.exception ?: Exception("Không thể lấy token."))
                    }
                }
        }
    }

    fun ensureLocalUserExists(): UserEntity {
        var localUser = userDao.getById(LOCAL_USER_ID)

        if (localUser == null) {
            localUser = UserEntity(
                id = LOCAL_USER_ID,
                userId = LOCAL_USER_ID.toString(),
                fullName = "Local User",
                coins = 0,
            )
            userDao.insert(localUser)
        }
        return localUser
    }
}