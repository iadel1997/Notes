package com.example.notes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.Adapter.NotesAdapter
import com.example.notes.Database.NoteDatabase
import com.example.notes.Models.Note
import com.example.notes.Models.NoteViewModel
import com.example.notes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NotesAdapter.NotesitemclickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: NoteDatabase
    private lateinit var viewModel: NoteViewModel // Brj3 lha
    private lateinit var adapter: NotesAdapter
    private lateinit var selectedNote : Note

    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor;

    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result -> if (result.resultCode == Activity.RESULT_OK){
            val note = result.data?.getSerializableExtra("note") as? Note
        if (note != null) {
            viewModel.updatyNote(note)
        }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        
        initUI()
        // Brj3 lha
        viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(NoteViewModel::class.java)

        viewModel.allnotes.observe(this) { list ->
            list?.let {
                adapter.updateList(list)
            }

        }

        database = NoteDatabase.getDatabase(this)

        sharedPreferences = getSharedPreferences("Notes", MODE_PRIVATE)
        editor = sharedPreferences.edit()

    }

    private fun initUI() {

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =  StaggeredGridLayoutManager(2, LinearLayout.VERTICAL )
        adapter = NotesAdapter(this,this)
        binding.recyclerView.adapter = adapter

        val getConstant = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result -> if(result.resultCode == Activity.RESULT_OK){

                val note = result.data?.getSerializableExtra("note") as? Note
            if (note != null){
                viewModel.insertNote(note)
            }
            }
        }

        binding.fbAddNote.setOnClickListener(){
            editor.putBoolean("update", false)
            editor.apply()
            val intent = Intent(this,AddNote::class.java)
            getConstant.launch(intent)
        }

        binding.SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText !=  null){
                    adapter.filterList(newText)
                }
                return true
            }


        })
    }

    override fun onitemCliced(note: Note) {
        editor.putBoolean("update", true)
        editor.putInt("id", note.id!!)
        editor.putString("title", note.title!!)
        editor.putString("note", note.note!!)
        editor.putString("date", note.date!!)
        editor.apply()
        val intent = Intent(this@MainActivity,AddNote::class.java)
        updateNote.launch(intent)
    }

    override fun onLongItemClicked(note: Note, cardView: CardView) {
        selectedNote = note
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: CardView) {
        val popup =  PopupMenu(this, cardView)
        popup.setOnMenuItemClickListener (this@MainActivity)
        popup.inflate(R.menu.pup_up_menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delet_note){
            viewModel.deleteNote(selectedNote)
            return true
        }
        else if (item?.itemId == R.id.share_note){
             sendEmail(selectedNote.title!!, selectedNote.note!!)
            return true
        }
        return false
    }

    @SuppressLint("IntentReset")
    private fun sendEmail(title: String, note: String) {
        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        mIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        mIntent.putExtra(Intent.EXTRA_TEXT, note)

        try {
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }
}