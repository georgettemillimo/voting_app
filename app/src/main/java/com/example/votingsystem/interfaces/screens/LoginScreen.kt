package com.example.votingsystem.interfaces.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.votingsystem.R
import com.example.votingsystem.model.LoginState
import com.example.votingsystem.model.LoginViewModel


@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel(),
                onLoginSuccess: () -> Unit
) {


    var admNo by  remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val loginState by viewModel.loginState.collectAsState()



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(

                        Color(0xFFBBDEFB),
                        Color(0xFF191970)


                    )
                )
            )
    ){
        Column( modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Image( painter = painterResource(id = R.drawable.logo),
                contentDescription = "LegsCraft Solutions",
                modifier = Modifier.size(80.dp)
//                    .clip(CircleShape)
//                    .border(2.dp , Color.White, CircleShape)
            )

            Spacer(Modifier.height(14.dp))

            Text("Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF191970))

            Spacer(Modifier.height(10.dp))
            Text("Login to Continue", fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(0.6f))

//============================= Card with Text inputs Starts here++++++++++++++++++++++++++++++

            Spacer(Modifier.height(18.dp))
            Card(modifier = Modifier.fillMaxWidth().padding(24.dp),
                colors = CardDefaults.cardColors(contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp)) {
                Column (
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ){

                    OutlinedTextField(
                        value = admNo,
                        onValueChange = {admNo = it},
                        label = {Text("Username")},
                        placeholder = {Text("Admission Number")},
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Email Icon",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        }, modifier = Modifier.fillMaxWidth().padding(14.dp)

                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = {password = it},
                        label = {Text("Password")},
                        placeholder = {Text("*****")},
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Icon",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                isPasswordVisible = !isPasswordVisible
                            } ) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Password Icon",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        },

                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().padding(14.dp)


                    )

                    Text("Forgot Password?", fontSize = 14.sp, modifier = Modifier.align(Alignment.End).padding(12.dp), color = MaterialTheme.colorScheme.primary)

                    Button(onClick = { viewModel.login(admNo, password)

                    }, Modifier.fillMaxWidth().height(70.dp).padding(12.dp), shape = RectangleShape, enabled = loginState !is LoginState.Loading,
                        colors = ButtonDefaults.buttonColors(Color(0xFF191970), contentColor = Color.White)){
                        if (loginState is LoginState.Loading){
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                        else{
                            Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    when(loginState){
                        is LoginState.Success -> {
                            LaunchedEffect(Unit) { onLoginSuccess() }
                        }
                        is LoginState.Error -> {
                            Text((loginState as LoginState.Error).message, color = Color.Red)
                        }
                        else -> {}
                    }


                }

            }

            Spacer(Modifier.height(16.dp))

            Text("Note: Use Admission Number as username" + "\n" +"and password Provided", fontSize = 12.sp, fontWeight = FontWeight.Normal,
                color = Color.White, textAlign = TextAlign.Center
            )
        }
    }


}




