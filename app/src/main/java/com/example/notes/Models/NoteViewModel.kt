package com.example.notes.Models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.notes.Database.NoteDatabase
import com.example.notes.Database.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : NoteRepository

    val allnotes : LiveData<List<Note>>

    init {
        val dao = NoteDatabase.getDatabase(application).getNoteDatabase()
        repository = NoteRepository(dao)
        allnotes = repository.allNotes
    }

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {

        repository.delete(note)
    }

    fun insertNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {

        repository.insert(note)
    }

    fun updatyNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.updaty(note)
    }
}