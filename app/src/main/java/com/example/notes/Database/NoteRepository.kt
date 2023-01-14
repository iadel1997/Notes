package com.example.notes.Database

import androidx.lifecycle.LiveData
import com.example.notes.Models.Note

class NoteRepository(private val noteDao : NoteDao) {

    val allNotes : LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note){
        noteDao.insert(note)
    }

    suspend fun delete(note: Note){
        noteDao.delete(note)
    }

    suspend fun updaty(note: Note){
        noteDao.update(note.id, note.title, note.note)
    }


}