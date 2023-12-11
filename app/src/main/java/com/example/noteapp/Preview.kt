package com.example.noteapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

class Preview {
    @Composable
    @Preview(showBackground = true)
    fun NoteListScreenPreview() {
        val context = LocalContext.current
        val viewModel = NoteViewModel(dbHelper = BDHelper(context))
        NoteListScreen(
            viewModel = viewModel,
            onNoteClick = { /* обработчик нажатия на заметку */ },
            onEditClick = {},
            onAddNoteClick = { /* обработчик нажатия на добавление заметки */ }
        )
    }


    @Preview(showBackground = true)
    @Composable
    fun NoteItemPreview() {
        val note = Note(
            id = 1,
            title = "Sample Title",
            content = "Sample Content",
            priority = Priority.HIGH
        )

        NoteItem(
            note = note,
            onNoteClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }

    @Composable
    @Preview
    fun NoteAddScreenPreview() {
        NoteAddScreen(
            viewModel = NoteViewModel(dbHelper = BDHelper(LocalContext.current)),
            onNavigationUp = {}
        )
    }



    @Preview(showBackground = true)
    @Composable
    fun NoteDetailPreview() {
        val note = Note(
            id = 1,
            title = "Sample Title",
            content = "Sample Content",
            priority = Priority.HIGH
        )

        NoteDetailScreen(
            note = note,
            onBackClick = {}
        )
    }
}