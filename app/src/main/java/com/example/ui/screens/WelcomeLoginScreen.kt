package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.auth.AuthState
import com.example.ui.components.JaapLogoGraphic
import com.example.ui.components.LegalTermsDialog
import com.example.ui.viewmodel.JaapViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WelcomeLoginScreen(
    viewModel: JaapViewModel,
    onAuthenticated: (authMethod: String, email: String) -> Unit,
    onContinueAsGuest: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    val authState by viewModel.authState.collectAsState()

    // Mandatory Terms Acceptance Checkbox State (Saved across configuration changes)
    var termsAccepted by rememberSaveable { mutableStateOf(false) }
    var showTermsWarning by remember { mutableStateOf(false) }

    // Dialog state
    var showEmailAuthDialog by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showLegalDialog by remember { mutableStateOf(false) }
    var legalDialogInitialTab by remember { mutableIntStateOf(0) } // 0: Privacy, 1: Terms

    // Email dialog state
    var authTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Register
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var forgotEmailInput by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    // Validation helper
    val validateTermsAndProceed: (() -> Unit) -> Unit = { action ->
        if (!termsAccepted) {
            showTermsWarning = true
            Toast.makeText(
                context,
                "Please accept the Terms & Conditions and Privacy Policy to continue.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            showTermsWarning = false
            action()
        }
    }

    // React to auth state changes
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Authenticated -> {
                showEmailAuthDialog = false
                isSubmitting = false
                onAuthenticated(state.provider, state.user.email ?: "")
            }
            is AuthState.Guest -> {
                showEmailAuthDialog = false
                isSubmitting = false
                onContinueAsGuest()
            }
            is AuthState.Error -> {
                isSubmitting = false
                emailError = state.message
            }
            else -> {}
        }
    }

    // ==========================================
    // 1. FORGOT PASSWORD DIALOG
    // ==========================================
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            containerColor = Color(0xFF140D2E),
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Reset Password",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter your registered email address and we'll send you a password reset link.",
                        color = Color(0xFFB8B0D3),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = forgotEmailInput,
                        onValueChange = { forgotEmailInput = it },
                        placeholder = { Text("your.email@domain.com", color = Color.Gray) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF9D4EDD))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF332A54),
                            focusedContainerColor = Color(0xFF0C0720),
                            unfocusedContainerColor = Color(0xFF0C0720)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_forgot_email")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val email = forgotEmailInput.trim()
                        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.sendPasswordReset(
                            email = email,
                            onSuccess = {
                                Toast.makeText(context, "Password reset link sent to $email. Please check your inbox.", Toast.LENGTH_LONG).show()
                                showForgotPasswordDialog = false
                            },
                            onError = { err ->
                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_send_reset_link")
                ) {
                    Text("Send Link", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel", color = Color(0xFFB8B0D3))
                }
            }
        )
    }

    // ==========================================
    // 2. EMAIL SIGN IN / REGISTER DIALOG
    // ==========================================
    if (showEmailAuthDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isSubmitting) {
                    showEmailAuthDialog = false
                    emailError = null
                }
            },
            containerColor = Color(0xFF140D2E),
            shape = RoundedCornerShape(22.dp),
            title = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (authTab == 0) "Sign In to Naam Jaap" else "Create Devotee Account",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        IconButton(
                            onClick = {
                                showEmailAuthDialog = false
                                emailError = null
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF8B7CB2))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TabRow(
                        selectedTabIndex = authTab,
                        containerColor = Color(0xFF0C0720),
                        contentColor = Color.White,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[authTab]),
                                color = Color(0xFF00E5FF),
                                height = 3.dp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Tab(
                            selected = authTab == 0,
                            onClick = {
                                authTab = 0
                                emailError = null
                            },
                            text = {
                                Text(
                                    "Sign In",
                                    fontWeight = if (authTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (authTab == 0) Color.White else Color(0xFF8B7CB2)
                                )
                            }
                        )
                        Tab(
                            selected = authTab == 1,
                            onClick = {
                                authTab = 1
                                emailError = null
                            },
                            text = {
                                Text(
                                    "Register (Naya)",
                                    fontWeight = if (authTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (authTab == 1) Color.White else Color(0xFF8B7CB2)
                                )
                            }
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (emailError != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = emailError ?: "",
                                color = Color(0xFFFF8B8B),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Email field
                    Text("Email Address", fontSize = 12.sp, color = Color(0xFFB8B0D3))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = {
                            emailInput = it
                            emailError = null
                        },
                        placeholder = { Text("devotee@example.com", color = Color.Gray, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Mail, contentDescription = null, tint = Color(0xFF8B7CB2), modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF332A54),
                            focusedContainerColor = Color(0xFF0C0720),
                            unfocusedContainerColor = Color(0xFF0C0720)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_auth_email")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Password field
                    Text("Password", fontSize = 12.sp, color = Color(0xFFB8B0D3))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            emailError = null
                        },
                        placeholder = { Text("Min. 6 characters", color = Color.Gray, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF8B7CB2), modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Password",
                                    tint = Color(0xFF8B7CB2),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (authTab == 1) ImeAction.Next else ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF332A54),
                            focusedContainerColor = Color(0xFF0C0720),
                            unfocusedContainerColor = Color(0xFF0C0720)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_auth_password")
                    )

                    if (authTab == 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                        // Confirm Password field
                        Text("Confirm Password", fontSize = 12.sp, color = Color(0xFFB8B0D3))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = confirmPasswordInput,
                            onValueChange = {
                                confirmPasswordInput = it
                                emailError = null
                            },
                            placeholder = { Text("Re-enter password", color = Color.Gray, fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF8B7CB2), modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle Confirm Password",
                                        tint = Color(0xFF8B7CB2),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF00E5FF),
                                unfocusedBorderColor = Color(0xFF332A54),
                                focusedContainerColor = Color(0xFF0C0720),
                                unfocusedContainerColor = Color(0xFF0C0720)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_auth_confirm_password")
                        )
                    }

                    if (authTab == 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Forgot Password?",
                                color = Color(0xFF00E5FF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable {
                                        forgotEmailInput = emailInput
                                        showForgotPasswordDialog = true
                                    }
                                    .testTag("btn_forgot_password")
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        keyboardController?.hide()

                        // Action-level protection
                        if (!termsAccepted) {
                            emailError = "Please accept the Terms & Conditions and Privacy Policy to continue."
                            return@Button
                        }

                        val email = emailInput.trim()
                        val pass = passwordInput.trim()
                        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailError = "Please enter a valid email address."
                            return@Button
                        }
                        if (pass.length < 6) {
                            emailError = "Password must be at least 6 characters."
                            return@Button
                        }

                        if (authTab == 1) {
                            // Register flow
                            val confirmPass = confirmPasswordInput.trim()
                            if (pass != confirmPass) {
                                emailError = "Passwords do not match."
                                return@Button
                            }
                            isSubmitting = true
                            viewModel.signUpWithEmail(
                                email = email,
                                password = pass,
                                onLoggedIn = {
                                    isSubmitting = false
                                    showEmailAuthDialog = false
                                },
                                onError = { err ->
                                    isSubmitting = false
                                    emailError = err
                                }
                            )
                        } else {
                            // Sign in flow
                            isSubmitting = true
                            viewModel.signInWithEmail(
                                email = email,
                                password = pass,
                                onLoggedIn = {
                                    isSubmitting = false
                                    showEmailAuthDialog = false
                                },
                                onError = { err ->
                                    isSubmitting = false
                                    emailError = err
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_submit_auth")
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = if (authTab == 0) "Sign In" else "Register (Naya)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        )
    }

    // ==========================================
    // 3. TERMS & PRIVACY DIALOG
    // ==========================================
    if (showLegalDialog) {
        LegalTermsDialog(
            initialTab = legalDialogInitialTab,
            onDismiss = { showLegalDialog = false }
        )
    }

    // ==========================================
    // MAIN REDESIGNED WELCOME SCREEN UI
    // ==========================================
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF02010A),
                        Color(0xFF060317),
                        Color(0xFF0C0624),
                        Color(0xFF050212),
                        Color(0xFF02010A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // ----------------------------------------------------
                // 1. APP LOGO (TRANSPARENT BACKGROUND ON SCREEN CANVAS)
                // ----------------------------------------------------
                JaapLogoGraphic(
                    sizeDp = 136.dp,
                    showCircularBadge = false,
                    animatedGlow = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ----------------------------------------------------
                // 2. TITLE: "Welcome to" & "Naam Jaap"
                // ----------------------------------------------------
                Text(
                    text = "Welcome to",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.3.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Vibrant Gradient "Naam Jaap" Title
                val titleGradient = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF00E5FF), // Bright Cyan
                        Color(0xFF3882FF), // Deep Sky Blue
                        Color(0xFF8B5CF6), // Royal Purple
                        Color(0xFFD946EF)  // Magenta Accent
                    )
                )

                Text(
                    text = "Naam Jaap",
                    style = TextStyle(
                        brush = titleGradient,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ----------------------------------------------------
                // 3. SUBTLE SPIRITUAL LOTUS ORNAMENT DIVIDER
                // ----------------------------------------------------
                SpiritualWelcomeFlourish(
                    tint = Color(0xFF8E7AB5),
                    modifier = Modifier
                        .width(130.dp)
                        .height(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ----------------------------------------------------
                // 4. SUBTITLE
                // ----------------------------------------------------
                Text(
                    text = "Begin your journey of devotion,\none naam at a time.",
                    fontSize = 14.sp,
                    color = Color(0xFFC7BEDD),
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(22.dp))

                // ----------------------------------------------------
                // 5. PREMIUM TRANSLUCENT QUOTE CARD
                // ----------------------------------------------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF130A2E).copy(alpha = 0.85f),
                                    Color(0xFF0B051D).copy(alpha = 0.95f)
                                )
                            )
                        )
                        .border(
                            width = 1.2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2563EB).copy(alpha = 0.7f),
                                    Color(0xFF7C3AED).copy(alpha = 0.6f),
                                    Color(0xFF4C1D95).copy(alpha = 0.4f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Subtle background geometric petals
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val r = size.height * 0.45f
                        for (i in 0 until 8) {
                            val angle = (i * (2 * Math.PI / 8)).toFloat()
                            val px = centerX + (r * 0.5f) * cos(angle)
                            val py = centerY + (r * 0.5f) * sin(angle)
                            drawCircle(
                                color = Color(0xFF8B5CF6).copy(alpha = 0.035f),
                                radius = r * 0.4f,
                                center = Offset(px, py),
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Quote Mark Icon
                        Text(
                            text = "“",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF60A5FA),
                            modifier = Modifier.height(24.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Hindi Quote
                        Text(
                            text = "हर नाम में एक शांति है,\nहर जाप में एक नई शुरुआत।",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFF8FAFC),
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Mini Lotus Divider
                        SpiritualWelcomeFlourish(
                            tint = Color(0xFF818CF8).copy(alpha = 0.75f),
                            modifier = Modifier
                                .width(80.dp)
                                .height(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Author
                        Text(
                            text = "— Naam Jaap",
                            fontSize = 12.sp,
                            color = Color(0xFF93C5FD),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ----------------------------------------------------
            // 6. MANDATORY TERMS & CONDITIONS CHECKBOX
            // ----------------------------------------------------
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Checkbox Row (Full row is accessible and tappable)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (showTermsWarning && !termsAccepted)
                                Color(0xFFEF4444).copy(alpha = 0.12f)
                            else
                                Color(0xFF0F0827).copy(alpha = 0.65f)
                        )
                        .border(
                            width = 1.dp,
                            color = if (showTermsWarning && !termsAccepted)
                                Color(0xFFEF4444).copy(alpha = 0.6f)
                            else if (termsAccepted)
                                Color(0xFF00E5FF).copy(alpha = 0.5f)
                            else
                                Color(0xFF2E2154).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            termsAccepted = !termsAccepted
                            if (termsAccepted) {
                                showTermsWarning = false
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("terms_acceptance_row")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { isChecked ->
                                termsAccepted = isChecked
                                if (isChecked) {
                                    showTermsWarning = false
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF00E5FF),
                                uncheckedColor = Color(0xFF8B7CB2),
                                checkmarkColor = Color(0xFF0C0720)
                            ),
                            modifier = Modifier
                                .size(36.dp)
                                .semantics {
                                    contentDescription = "Accept Terms & Conditions and Privacy Policy"
                                }
                                .testTag("checkbox_terms_accepted")
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Clickable legal text
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "I agree to the ",
                                color = Color(0xFFCFC8E5),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Terms & Conditions",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable {
                                        legalDialogInitialTab = 1
                                        showLegalDialog = true
                                    }
                                    .testTag("link_terms_conditions")
                            )
                            Text(
                                text = " and ",
                                color = Color(0xFFCFC8E5),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Privacy Policy",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable {
                                        legalDialogInitialTab = 0
                                        showLegalDialog = true
                                    }
                                    .testTag("link_privacy_policy")
                            )
                        }
                    }
                }

                // Inline validation error message
                AnimatedVisibility(
                    visible = showTermsWarning && !termsAccepted,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, start = 8.dp, end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFFF8B8B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Please accept the Terms & Conditions and Privacy Policy to continue.",
                            color = Color(0xFFFF8B8B),
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ----------------------------------------------------
                // 7. ACTION BUTTONS (EXACTLY EMAIL & GUEST)
                // ----------------------------------------------------
                // 1. PRIMARY CTA: Continue with Email
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .alpha(if (termsAccepted) 1.0f else 0.65f)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF0072FF), // Vibrant Blue
                                    Color(0xFF3858E9),
                                    Color(0xFF7C3AED), // Vibrant Purple
                                    Color(0xFFA855F7)
                                )
                            )
                        )
                        .clickable {
                            validateTermsAndProceed {
                                emailError = null
                                showEmailAuthDialog = true
                            }
                        }
                        .testTag("btn_email_auth"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mail,
                            contentDescription = "Email",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Continue with Email",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. SECONDARY CTA: Continue as Guest
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .alpha(if (termsAccepted) 1.0f else 0.65f)
                        .background(Color(0xFF0B051E))
                        .border(1.2.dp, Color(0xFF2E2154), RoundedCornerShape(16.dp))
                        .clickable {
                            validateTermsAndProceed {
                                viewModel.continueAsGuest {
                                    onContinueAsGuest()
                                }
                            }
                        }
                        .testTag("btn_guest_signin"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Guest",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Continue as Guest",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. SECURITY FOOTER
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = Color(0xFF7A6B9C),
                        modifier = Modifier.size(12.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Encrypted & Sacred Devotional Platform",
                        color = Color(0xFF8C7BAE),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// SUBTLE SPIRITUAL LOTUS FLOURISH DIVIDER
// ----------------------------------------------------

@Composable
fun SpiritualWelcomeFlourish(
    tint: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerY = h / 2f
        val centerX = w / 2f

        val lineLength = w * 0.34f

        // Left Line & Dot
        drawLine(
            color = tint.copy(alpha = 0.6f),
            start = Offset(0f, centerY),
            end = Offset(lineLength, centerY),
            strokeWidth = 1.dp.toPx()
        )
        drawCircle(
            color = tint,
            radius = 1.8.dp.toPx(),
            center = Offset(lineLength + 4.dp.toPx(), centerY)
        )

        // Center Lotus Petals
        val lotusCenter = Offset(centerX, centerY)
        // Center petal
        drawOval(
            color = tint,
            topLeft = Offset(lotusCenter.x - 2.5.dp.toPx(), centerY - 5.dp.toPx()),
            size = Size(5.dp.toPx(), 8.5.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )
        // Left petal
        drawArc(
            color = tint,
            startAngle = 140f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(lotusCenter.x - 7.5.dp.toPx(), centerY - 4.dp.toPx()),
            size = Size(8.dp.toPx(), 6.5.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )
        // Right petal
        drawArc(
            color = tint,
            startAngle = 300f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(lotusCenter.x - 0.5.dp.toPx(), centerY - 4.dp.toPx()),
            size = Size(8.dp.toPx(), 6.5.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )

        // Right Line & Dot
        val rightStart = w - lineLength
        drawCircle(
            color = tint,
            radius = 1.8.dp.toPx(),
            center = Offset(rightStart - 4.dp.toPx(), centerY)
        )
        drawLine(
            color = tint.copy(alpha = 0.6f),
            start = Offset(rightStart, centerY),
            end = Offset(w, centerY),
            strokeWidth = 1.dp.toPx()
        )
    }
}
