package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.JaapViewModel

@Composable
fun DevoteeAuthDialog(
    viewModel: JaapViewModel,
    initialTab: Int = 1, // 0: Sign In, 1: Register
    onDismissRequest: () -> Unit,
    onAuthSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var authTab by remember { mutableIntStateOf(initialTab) }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    var termsAccepted by rememberSaveable { mutableStateOf(true) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotEmailInput by remember { mutableStateOf("") }

    var showLegalDialog by remember { mutableStateOf(false) }
    var legalDialogInitialTab by remember { mutableIntStateOf(0) }

    if (showLegalDialog) {
        LegalTermsDialog(
            initialTab = legalDialogInitialTab,
            onDismiss = { showLegalDialog = false }
        )
    }

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
                        modifier = Modifier.fillMaxWidth().testTag("input_forgot_email")
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

    AlertDialog(
        onDismissRequest = {
            if (!isSubmitting) {
                onDismissRequest()
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
                            if (!isSubmitting) onDismissRequest()
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
                        .testTag("input_auth_password")
                )

                if (authTab == 1) {
                    Spacer(modifier = Modifier.height(10.dp))
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

                Spacer(modifier = Modifier.height(10.dp))

                // Terms agreement row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF00E5FF),
                            uncheckedColor = Color(0xFF8B7CB2),
                            checkmarkColor = Color(0xFF0C0720)
                        ),
                        modifier = Modifier
                            .size(28.dp)
                            .semantics {
                                contentDescription = "Accept Terms & Conditions and Privacy Policy"
                            }
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    val authTermsAnnotated = remember {
                        buildAnnotatedString {
                            append("I agree to ")
                            pushStringAnnotation(tag = "TERMS", annotation = "terms")
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("Terms")
                            }
                            pop()
                            append(" & ")
                            pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("Privacy Policy")
                            }
                            pop()
                        }
                    }

                    ClickableText(
                        text = authTermsAnnotated,
                        style = TextStyle(
                            color = Color(0xFFCFC8E5),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        ),
                        modifier = Modifier.weight(1f),
                        onClick = { offset ->
                            val termsAnnotation = authTermsAnnotated.getStringAnnotations(
                                tag = "TERMS",
                                start = offset,
                                end = offset
                            ).firstOrNull()

                            val privacyAnnotation = authTermsAnnotated.getStringAnnotations(
                                tag = "PRIVACY",
                                start = offset,
                                end = offset
                            ).firstOrNull()

                            when {
                                termsAnnotation != null -> {
                                    legalDialogInitialTab = 1
                                    showLegalDialog = true
                                }
                                privacyAnnotation != null -> {
                                    legalDialogInitialTab = 0
                                    showLegalDialog = true
                                }
                                else -> {
                                    termsAccepted = !termsAccepted
                                }
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    keyboardController?.hide()

                    if (!termsAccepted) {
                        emailError = "Please accept the Terms & Conditions and Privacy Policy."
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
                                Toast.makeText(context, "Account created! Your Jaap progress has been safely synced.", Toast.LENGTH_LONG).show()
                                onAuthSuccess()
                                onDismissRequest()
                            },
                            onError = { err ->
                                isSubmitting = false
                                emailError = err
                            }
                        )
                    } else {
                        isSubmitting = true
                        viewModel.signInWithEmail(
                            email = email,
                            password = pass,
                            onLoggedIn = {
                                isSubmitting = false
                                Toast.makeText(context, "Signed in successfully! Your Jaap data is synced.", Toast.LENGTH_SHORT).show()
                                onAuthSuccess()
                                onDismissRequest()
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
