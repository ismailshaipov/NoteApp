package com.example.noteapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class BDHelper(context: Context) : SQLiteOpenHelper(
    context,
    "myDB",
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        // Создаем таблицу для заметок при первом запуске
        db?.let { b ->
            b.beginTransaction()
            b.execSQL(
                "CREATE TABLE 'notes' (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title TEXT," +
                        "content TEXT," +
                        "creationTime DATE," +
                        "priority TEXT)"
            )
            b.setTransactionSuccessful()
            b.endTransaction()
        }

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Необходимо реализовать логику обновления базы данных при изменении версии
        // Можно просто удалить старую таблицу и создать новую при изменении структуры
        db.execSQL("DROP TABLE IF EXISTS notes")
        onCreate(db)
    }

    @SuppressLint("SuspiciousIndentation")
    fun insertNote(title: String, content: String, creationTimeMillis: Long, priority: Priority) {
        with(writableDatabase) {
            beginTransaction()
            val values = ContentValues(3)
                values.put("title", title)
                values.put("content", content)
                values.put("creationTime", Instant.ofEpochMilli(creationTimeMillis).toString())
                values.put("priority", priority.name)
            insert("notes", null, values)
            setTransactionSuccessful()
            endTransaction()
        }
    }

    fun updateNote(note: Note) {
        with(writableDatabase) {
            beginTransaction()
            val values = ContentValues().apply {
                put("title", note.title)
                put("content", note.content)
                put("priority", note.priority.name)
            }
            update("notes", values, "id = ?", arrayOf(note.id.toString()))
            setTransactionSuccessful()
            endTransaction()
        }
    }

    fun deleteNote(note: Note) {
        with(writableDatabase) {
            beginTransaction()
            delete("notes", "id = ?", arrayOf(note.id.toString()))
            setTransactionSuccessful()
            endTransaction()
        }
    }

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        with(readableDatabase) {
            query(
                "notes",
                arrayOf("id","title", "content", "creationTime", "priority"),
                null,
                null,
                null,
                null,
                null
            ).let {
                while (it.moveToNext()) {
                    val id = it.getInt(it.getColumnIndexOrThrow("id"))
                    val title = it.getString(it.getColumnIndexOrThrow("title"))
                    val content = it.getString(it.getColumnIndexOrThrow("content"))
                    val creationTime =
                        Instant.parse(it.getString(it.getColumnIndexOrThrow("creationTime")))
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                    val priority =
                        Priority.valueOf(it.getString(it.getColumnIndexOrThrow("priority")))
                    notes.add(Note( id,title, content, creationTime, priority))
                }
                it.close()
            }
        }
        notes.sortWith(compareByDescending<Note> { it.priority }.thenByDescending { it.creationTime })
        return notes
    }

    fun getNoteById(noteId: Long): Note? {
        var note: Note? = null
        with(readableDatabase) {
            query(
                "notes",
                arrayOf("id","title", "content", "creationTime", "priority"),
                "id = ?",
                arrayOf(noteId.toString()),
                null,
                null,
                null
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                    val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
                    val creationTime =
                        LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(cursor.getLong(cursor.getColumnIndexOrThrow("creationTime"))),
                            ZoneId.systemDefault())
                    val priority = Priority.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("priority")))
                    note = Note(id, title, content, creationTime, priority)
                }
            }
        }
        return note
    }
}