package com.example.ermayuni.sqlliteexample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ermayuni.sqlliteexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var studentDBHelper: StudentDBHelper
    private val listData: ArrayList<StudentModel> = arrayListOf()
    private lateinit var studentAdapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentDBHelper = StudentDBHelper(this)
        studentAdapter = StudentAdapter(listData)

        initializeRecyclerList()
        loadAllData()

        binding.btnCari.setOnClickListener {
            val nama = binding.etNama.text.toString()
            if (nama.isEmpty()) {
                Toast.makeText(this, "Silakan masukkan nama terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val students = studentDBHelper.searchStudentByName(nama)
            listData.clear()
            listData.addAll(students)
            studentAdapter.notifyDataSetChanged()
            binding.tvHasilPencarian.text = "Ditemukan ${students.size} mahasiswa"
        }

        binding.btnTambah.setOnClickListener {
            startActivity(Intent(this, CreateActivity::class.java))
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            loadAllData()
        }
    }

    private fun loadAllData() {
        val students = studentDBHelper.readStudents()
        listData.clear()
        listData.addAll(students)
        studentAdapter.notifyDataSetChanged()
        binding.tvHasilPencarian.text = "Total ${students.size} mahasiswa"
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        loadAllData()
    }

    override fun onDestroy() {
        studentDBHelper.close()
        super.onDestroy()
    }

    private fun initializeRecyclerList() {
        binding.rvStudents.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = studentAdapter
        }

        studentAdapter.setOnItemClickCallback(object : StudentAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StudentModel) {
                updateStudent(data)
            }
        })
    }

    private fun updateStudent(data: StudentModel) {
        val moveWithObjectIntent = Intent(this, UpdateActivity::class.java)
        moveWithObjectIntent.putExtra(UpdateActivity.EXTRA_STUDENT, data)
        startActivity(moveWithObjectIntent)
    }
}
