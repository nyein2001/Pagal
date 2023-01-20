package com.service.mediataggingapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.service.mediataggingapp.R
import com.service.mediataggingapp.model.UserDetailsInfo
import com.service.mediataggingapp.utils.InitTheme
import com.service.mediataggingapp.utils.NetworkCheck.isConnected
import java.util.*

class AuthActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE = 100
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var database: DatabaseReference
    private lateinit var init: InitTheme

    @BindView(R.id.sign_in_with_google)
    lateinit var googleSignInBtn: MaterialCardView

    @BindView(R.id.night_mode_switch)
    lateinit var nightModeSwitch: MaterialCardView

    @BindView(R.id.night_mode_switch_title)
    lateinit var nightModeSwitchTitle: TextView

    @BindView(R.id.night_mode_switch_icon)
    lateinit var nightModeSwitchIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        ButterKnife.bind(this)
        initTheme()
        initComponent()
        signInProcess()
        changeTheme()
    }

    private fun initTheme() {
        init = InitTheme(this)
        if (init.isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            nightModeSwitchIcon.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.mode_day,
                    null
                )
            )
            nightModeSwitchTitle.text = getString(R.string.switch_light)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            nightModeSwitchIcon.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.mode_night,
                    null
                )
            )
            nightModeSwitchTitle.text = getString(R.string.switch_night)
        }
    }

    private fun initComponent() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
    }

    private fun signInProcess() {
        googleSignInBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
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
                Log.d("TAG", "Google sign in failed", e)
                e.printStackTrace()
            }
        }
    }

    private fun loginWithGoogle(tokenId: String?) {
        val credential = GoogleAuthProvider.getCredential(tokenId, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    storeUserDetails(user)
                    goToMainActivity()
                } else {
                    Log.w("NotSuccessError : ", task.exception)
                }
            }
            .addOnFailureListener { e: Exception ->
                Log.d(
                    "onFailure : ",
                    e.message!!
                )
            }
    }

    private fun storeUserDetails(userDetails: FirebaseUser?) {
        val userInfo = UserDetailsInfo(
            userDetails!!.uid, userDetails.displayName,
            userDetails.photoUrl.toString(), userDetails.email,
            Calendar.getInstance().time.toString(), Calendar.getInstance().time.toString()
        )
        database.child("users").child(userDetails.uid).setValue(userInfo)
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
            .setPositiveButton(
                "Yes"
            ) { _: DialogInterface?, _: Int ->
                if (isConnected(this)) {
                    signInProcess()
                } else {
                    showNoInternetDialog()
                }
            }
            .show()
    }

    private fun changeTheme() {
        nightModeSwitch.setOnClickListener {
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                init.setIsNightModeEnabled(true)
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                finish()
                startActivity(intent)
            } else {
                init.setIsNightModeEnabled(false)
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                finish()
                startActivity(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (isConnected(this)) {
            val user = auth.currentUser
            if (user != null) {
                val loginIntent = Intent(this, MainActivity::class.java)
                startActivity(loginIntent)
            }
        } else {
            showNoInternetDialog()
        }
    }
}