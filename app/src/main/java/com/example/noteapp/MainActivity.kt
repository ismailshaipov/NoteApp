package com.example.noteapp
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.noteapp.ui.theme.NoteAppTheme
import java.text.DateFormat
import java.time.LocalDateTime
import java.util.Date


class MainActivity : ComponentActivity() {
    lateinit var dbBDHelper: BDHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dbHelper = BDHelper(applicationContext)
        setContent {
            NoteAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "noteList") {
                        composable("noteList") {
                            NoteListScreen(
                                viewModel = NoteViewModel(dbHelper),
                                onAddNoteClick = {
                                    navController.navigate("addNote")
                                },

                                onEditClick = { selectedNote ->
                                    navController.navigate("edit/${selectedNote.id}")
                                },
                                onNoteClick = { selectedNote ->
                                    navController.navigate("noteDetail/${selectedNote.id}")
                                }
                            )
                        }

                        composable("addNote") {
                            NoteAddScreen(
                                viewModel = NoteViewModel(dbHelper),
                                onNavigationUp = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(
                            route = "edit/{noteId}",
                            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0
                            NoteEditScreen(
                                viewModel = NoteViewModel(dbHelper),
                                noteId = noteId,
                                onNavigationUp = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(
                            route = "noteDetail/{noteId}",
                            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0
                            val viewModel = NoteViewModel(dbHelper)
                            viewModel.getNoteById(noteId)?.let {
                                NoteDetailScreen(note = it) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onNoteClick: (Note) -> Unit,
    onEditClick: (Note) -> Unit,
    onDeleteClick: (Note) -> Unit
) {

    val formattedDateTime =  getFormattedDateTime(note.creationTime)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onNoteClick(note) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column (
            modifier = Modifier
                .padding(16.dp)
        ){
            Text(text = note.title, style = MaterialTheme.typography.headlineMedium)
            Text(text = note.content, style = MaterialTheme.typography.bodyLarge)

            // Отображаем дату и время
            Text(
                text = formattedDateTime,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                PriorityIcon(priority = note.priority)
                Row(modifier = Modifier.padding(8.dp)) {
                    IconButton(onClick = { onEditClick(note) }) {
                        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_edit_24), contentDescription = "Edit Note")
                    }
                    IconButton(onClick = { onDeleteClick(note) }) {
                        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_delete_24), contentDescription = "Delete Note")
                    }
                }
            }
        }
    }
}

@Composable
fun getFormattedDateTime(creationTime: LocalDateTime): String {
    val currentDate = Date(creationTime)
    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, LocalContext.current.resources.configuration.locales[0])
    return dateFormat.format(currentDate)
}

fun Date(creationTime: LocalDateTime): Date {
    TODO("Not yet implemented")
}

/*@Composable
fun NoteItem(
    note: Note,
    onNoteClick: (Note) -> Unit,
    onEditClick: (Note) -> Unit,
    onDeleteClick: (Note) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onNoteClick(note) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column (
            modifier = Modifier
                .padding(16.dp)
        ){
            Text(text = note.title, style = MaterialTheme.typography.headlineMedium)
            Text(text = note.content, style = MaterialTheme.typography.bodyLarge)
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            val formattedDateTime = note.creationTime.format(formatter)
            Text(
                text = formattedDateTime.toString(),
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                PriorityIcon(priority = note.priority)
                Row(modifier = Modifier.padding(8.dp)) {
                    IconButton(onClick = { onEditClick(note) }) {
                        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_edit_24), contentDescription = "Edit Note")
                    }
                    IconButton(onClick = { onDeleteClick(note) }) {
                        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_delete_24), contentDescription = "Delete Note")
                    }
                }

            }
        }
    }
}*/
@Composable
fun PriorityIcon(priority: Priority) {
    val color = when (priority) {
        Priority.HIGH -> Color.Red
        Priority.MEDIUM -> Color.Yellow
        Priority.LOW -> Color.Green
        Priority.NONE -> Color.White
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color, shape = MaterialTheme.shapes.medium)
    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    viewModel: NoteViewModel,
    onNoteClick: (Note) -> Unit,
    onEditClick: (Note) -> Unit,
    onAddNoteClick: () -> Unit
) {
    //val notes by remember { viewModel.notes}
    val notes: List<Note> = viewModel.notes.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note'S") },
                actions = {
                    IconButton(onClick = { onAddNoteClick() }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
                    }
                }
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(top = 60.dp)) {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    onNoteClick = { onNoteClick(note) },
                    onEditClick = { onEditClick(note) },
                    onDeleteClick = { viewModel.deleteNote(note) }
                )
            }
        }
    }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAddScreen(
    viewModel: NoteViewModel,
    onNavigationUp: () -> Unit
) {
    // State для хранения данных
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.NONE) }
    val context = LocalContext.current


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.add_note)) },
                navigationIcon = {
                    IconButton(onClick = { onNavigationUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Проверка, что поле "Title" не пустое
                        if (title.isNotBlank()) {
                            // Создаем новую заметку
                            viewModel.insertNote(title, content, priority)

                            // Возвращаемся на начальный экран
                            onNavigationUp()
                        } else {
                            val message = context.getString(R.string.toast_message)
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save"
                        )
                    }
                }
            )
        }
    ) {
        // Column для размещения компонентов внутри экрана
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)
        ) {
            // TextField для ввода заголовка
            TextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                label = { Text(stringResource(id = R.string.title)) }
            )

            // TextField для ввода содержимого
            TextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .padding(8.dp),
                label = { Text(stringResource(id = R.string.content)) },
                textStyle = TextStyle.Default.copy(
                    lineHeight = 20.sp,
                    fontSize = 16.sp
                ),
                maxLines = Int.MAX_VALUE
            )

            // PriorityDropdown для выбора приоритета
            PrioritySelector(
                selectedPriority = priority,
                onPrioritySelected = { newPriority ->
                    priority = newPriority
                }
            )
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    viewModel: NoteViewModel,
    noteId: Long,
    onNavigationUp: () -> Unit
) {
    // Загрузите существующую заметку по noteId
    val existingNote = viewModel.getNoteById(noteId)

    // State для хранения данных
    var title by remember { mutableStateOf(existingNote?.title.orEmpty()) }
    var content by remember { mutableStateOf(existingNote?.content.orEmpty()) }
    var priority by remember { mutableStateOf(existingNote?.priority ?: Priority.NONE) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.edit_note)) },
                navigationIcon = {
                    IconButton(onClick = { onNavigationUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Обновляем существующую заметку
                        existingNote?.let {
                            viewModel.updateNote(
                                it.copy(
                                    title = title,
                                    content = content,
                                    priority = priority
                                )
                            )
                        }

                        // Возвращаемся на начальный экран
                        onNavigationUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)
        ) {
            // TextField для ввода заголовка
            TextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                label = { Text(stringResource(id = R.string.title)) }
            )

            // TextField для ввода содержимого
            TextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .padding(8.dp),
                label = { Text(stringResource(id = R.string.content)) },
                textStyle = TextStyle.Default.copy(
                    lineHeight = 20.sp,
                    fontSize = 16.sp
                ),
                maxLines = Int.MAX_VALUE
            )

            // PriorityDropdown для выбора приоритета
            PrioritySelector(
                selectedPriority = priority,
                onPrioritySelected = { newPriority ->
                    priority = newPriority
                }
            )
        }
    }
}

@Composable
fun PrioritySelector(
    selectedPriority: Priority,
    onPrioritySelected: (Priority) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PriorityCircle(
            color = Color.Red,
            isSelected = selectedPriority == Priority.HIGH,
            onClick = { onPrioritySelected(if (selectedPriority == Priority.HIGH) Priority.NONE else Priority.HIGH) }
        )
        PriorityCircle(
            color = Color.Yellow,
            isSelected = selectedPriority == Priority.MEDIUM,
            onClick = { onPrioritySelected(if (selectedPriority == Priority.MEDIUM) Priority.NONE else Priority.MEDIUM) }
        )
        PriorityCircle(
            color = Color.Green,
            isSelected = selectedPriority == Priority.LOW,
            onClick = { onPrioritySelected(if (selectedPriority == Priority.LOW) Priority.NONE else Priority.LOW) }
        )
    }
}

@Composable
fun PriorityCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(color = color, shape = CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White
            )
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    note: Note,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.detail_note)) },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, top = 60.dp)
        ) {
            Text(text = note.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = note.content, style = MaterialTheme.typography.bodyLarge)
        }
    }
}









