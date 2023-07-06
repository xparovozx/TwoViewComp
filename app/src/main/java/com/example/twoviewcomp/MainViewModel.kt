import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.twoviewcomp.Screen
import com.example.twoviewcomp.User
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    private val _navigateToSecondScreen = mutableStateOf(false)
    val navigateToSecondScreen: MutableState<Boolean> get() = _navigateToSecondScreen

    fun goToSecondScreen() {
        _navigateToSecondScreen.value = true
    }

    private val _currentScreen = mutableStateOf<Screen>(Screen.First)
    val currentScreen: State<Screen> = _currentScreen

    private val _firstNumber = mutableStateOf("")
    val firstNumber: String get() = _firstNumber.value

    private val _secondNumber = mutableStateOf("")
    val secondNumber: String get() = _secondNumber.value

    private val _sum = mutableStateOf(0)
    val sum: Int get() = _sum.value

    private val _users = mutableListOf<User>()
    val users: List<User> get() = _users

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading.value

    fun onFirstNumberChanged(number: String) {
        _firstNumber.value = number
    }

    fun onSecondNumberChanged(number: String) {
        _secondNumber.value = number
    }

    fun fetchData(firstNumber: String, secondNumber: String) {
        viewModelScope.launch {
            _isLoading.value = true

            delay(2000) // Имитация задержки загрузки данных

            val result = withContext(Dispatchers.IO) {
                // Код чтения файла JSON и обработки данных
                try {
                    val jsonString = context.assets.open("name.json").bufferedReader().use { it.readText() }
                    val jsonData = JSONObject(jsonString).getJSONArray("users")

                    val userList = mutableListOf<User>()
                    for (i in 0 until jsonData.length()) {
                        val userObject = jsonData.getJSONObject(i)
                        val name = userObject.getString("name")
                        val age = userObject.getString("age")
                        userList.add(User(name, age))
                    }

                    userList
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                } catch (e: JSONException) {
                    e.printStackTrace()
                    null
                }
            }

            if (result != null) {
                _users.clear()
                _users.addAll(result)

                // Обновление текущего экрана на Screen.Second
                _currentScreen.value = Screen.Second
                goToSecondScreen()
            }

            val sum = (firstNumber.toIntOrNull() ?: 0) + (secondNumber.toIntOrNull() ?: 0)
            _sum.value = sum

            _isLoading.value = false
        }
    }

    fun goBack() {
        _firstNumber.value = ""
        _secondNumber.value = ""
        _sum.value = 0
        _users.clear()
        _currentScreen.value = Screen.First
    }
}