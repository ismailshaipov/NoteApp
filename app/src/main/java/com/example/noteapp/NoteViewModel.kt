package com.example.noteapp

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(private val dbHelper: BDHelper) : ViewModel() {
    private val _notes = mutableStateOf<List<Note>>(emptyList())
    val notes: State<List<Note>> get() = _notes
    //private val _notes = MutableLiveData<List<Note>>(emptyList())
    //val notes: LiveData<List<Note>> get() = _notes


    init {
        CoroutineScope(Dispatchers.IO).launch {
            getAllNotes()
        }
    }

    fun insertNote(title: String, content: String, priority: Priority) {
        val currentTimeMillis = System.currentTimeMillis()
        CoroutineScope(Dispatchers.IO).launch {
            dbHelper.insertNote(title, content, currentTimeMillis,priority)
            getAllNotes()
        }
    }

    fun updateNote(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            dbHelper.updateNote(note)
            getAllNotes()
        }
    }

    fun deleteNote(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            dbHelper.deleteNote(note)
            getAllNotes()
        }
    }

    private suspend fun getAllNotes() {
        withContext(Dispatchers.IO) {
            val notes = dbHelper.getAllNotes()
            withContext(Dispatchers.Main) {
                _notes.value = notes
            }
        }
    }

    fun getNoteById(noteId: Long): Note? {
        return dbHelper.getNoteById(noteId)
    }
}