package com.example.notes

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.notes.Database.NoteDatabase
import com.example.notes.Models.Note
import com.example.notes.Models.NoteViewModel
import com.example.notes.databinding.FragmentAddBinding
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private lateinit var note: Note
    private lateinit var old_note : Note
    private var isUpdate = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddBinding.inflate(inflater)

        val sharedPreferences = context?.getSharedPreferences("Notes", AppCompatActivity.MODE_PRIVATE)
        if(sharedPreferences?.getBoolean("update", false) == true){
            old_note = Note(
                sharedPreferences.getInt("id", 0),
                sharedPreferences.getString("title", ""),
                sharedPreferences.getString("note", ""),
                sharedPreferences.getString("date", "")
            )
            binding.etTitle.setText(old_note.title)
            binding.etNote.setText(old_note.note)
            isUpdate = true
        }

        binding.imgCheck.setOnClickListener{
            val title = binding.etTitle.text.toString()
            val note_desc = binding.etNote.text.toString()

            if (title.isNotEmpty() || note_desc.isNotEmpty()){

                val formatter = SimpleDateFormat("EEE, d MMM yyy HH:mm a")

                if(isUpdate) {
                    note = Note(
                        old_note.id,title,note_desc,formatter.format(Date())
                    )
                }else{
                    note = Note(
                        null,title,note_desc,formatter.format(Date())
                    )
                }

                val intent = Intent()
                intent.putExtra("note",note)
                activity?.setResult(Activity.RESULT_OK,intent)
                activity?.finish()
            }else{
                Toast.makeText(context,"Please enter some data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

        binding.imgBackArrow.setOnClickListener{
            activity?.onBackPressed()
        }
        return binding.root
    }
}