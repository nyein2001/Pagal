package com.service.mediataggingapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.service.mediataggingapp.R
import com.service.mediataggingapp.utils.NetworkCheck.isConnected


class AuthActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null
    private var googleSignInClient: GoogleSignInClient? = null

    companion object {
        private const val REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        initComponent()
        if (firebaseUser == null) {
            Handler().postDelayed({
                if (isConnected(this)) {
                    loginProcess()
                } else {
                    showNoInternetDialog()
                }
            }, 300)
        } else {
            val loginIntent = Intent(this, MainActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun initComponent() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        firebaseUser = auth!!.currentUser
    }

    private fun loginProcess() {
        findViewById<View>(R.id.sign_in_with_google).setOnClickListener {
            val signInIntent = googleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, REQUEST_CODE)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("onActivityResult : ", account.id!!)
                loginWithGoogle(account.idToken)
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }

    private fun loginWithGoogle(tokenId: String?) {
        Log.d("LoginWithGoogle : ", tokenId!!)
        val credential = GoogleAuthProvider.getCredential(tokenId, null)
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    goToMainActivity()
                } else {
                    Log.d("NotSuccessError : ", task.toString())
                }
            }
            .addOnFailureListener { e: Exception ->
                Log.d(
                    "onFailure : ",
                    e.message!!
                )
            }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.no_internet_title))
            .setMessage(R.string.no_internet_message)
            .setCancelable(false)
            .setPositiveButton("Yes"
            ) { _: DialogInterface?, _: Int ->
                if (isConnected(this)) {
                    loginProcess()
                } else {
                    showNoInternetDialog()
                }
            }
            .show()
    }
}