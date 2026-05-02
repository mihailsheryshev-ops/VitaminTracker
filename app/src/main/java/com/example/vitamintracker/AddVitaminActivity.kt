package com.example.vitamintracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.UUID

class AddVitaminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val etName = EditText(this).apply {
            hint = "Название витамина (например, Витамин C)"
        }
        val etDose = EditText(this).apply {
            hint = "Ваша доза в мг"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val etNorm = EditText(this).apply {
            hint = "Суточная норма в мг"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val etDescription = EditText(this).apply {
            hint = "Для чего нужен"
        }
        val etExcess = EditText(this).apply {
            hint = "Симптомы избытка"
        }

        val btnSave = Button(this).apply {
            text = "Сохранить"
        }

        layout.addView(etName)
        layout.addView(etDose)
        layout.addView(etNorm)
        layout.addView(etDescription)
        layout.addView(etExcess)
        layout.addView(btnSave)

        setContentView(layout)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val dose = etDose.text.toString().toFloatOrNull()
            val norm = etNorm.text.toString().toFloatOrNull()
            val description = etDescription.text.toString().trim()
            val excess = etExcess.text.toString().trim()

            if (name.isEmpty() || dose == null || norm == null) {
                Toast.makeText(this, "Заполни все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val vitamin = Vitamin(
                id = UUID.randomUUID().toString(),
                name = name,
                doseMg = dose,
                normMg = norm,
                description = description,
                excessSymptoms = excess
            )
            VitaminStorage.add(this, vitamin)
            Toast.makeText(this, "Витамин добавлен!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}