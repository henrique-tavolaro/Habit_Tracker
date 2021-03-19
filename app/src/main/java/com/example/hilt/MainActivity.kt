package com.example.hilt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hilt.db.User
import com.example.hilt.ui.theme.HiltTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val users = viewModel.userList.value

            HiltTheme {
                // A surface container using the 'background' color from the theme

                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                            onClick = {
                                val user = User("José")
                                viewModel.insertUser(user)
                            }) {
                            Text("click here")
                        }
                        Button(modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        onClick = {

                        }) {
                        Text("get users")
                    }

                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(users){data->
                                MyCard(data)
                            }


                    }
                }
            }
        }
    }
}



@Composable
fun MyCard(
    user: User,
) {
    Card {
        user.name?.let { Text(text = it) }
    }
}

private val dataList = listOf(
    MyModel("Alexander"),
    MyModel("Hamilton"),
    MyModel("Agatha")
)

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    HiltTheme {
//        Greeting(dataList)
//    }
//}

data class MyModel(
    val name : String = ""
)