package com.example.vitamintracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var listContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = android.view.ViewGroup.LayoutParams(-1, -1)
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        val title = TextView(this).apply {
            text = "Трекер витаминов"
            textSize = 22f
            setTextColor(Color.BLACK)
            setPadding(32, 48, 32, 16)
        }

        val btnAdd = Button(this).apply {
            text = "+ Добавить витамин"
            val params = LinearLayout.LayoutParams(-1, -2)
            params.setMargins(32, 0, 32, 16)
            layoutParams = params
        }

        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(-1, 0, 1f)
        }

        listContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 0, 32, 32)
        }
        scrollView.addView(listContainer)

        root.addView(title)
        root.addView(btnAdd)
        root.addView(scrollView)
        setContentView(root)

        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddVitaminActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadVitamins()
    }

    private fun loadVitamins() {
        listContainer.removeAllViews()
        val vitamins = VitaminStorage.getAll(this)
        val takenToday = VitaminStorage.getTakenToday(this)

        if (vitamins.isEmpty()) {
            val empty = TextView(this).apply {
                text = "Витамины не добавлены.\nНажми кнопку выше чтобы добавить."
                textSize = 16f
                setTextColor(Color.GRAY)
                gravity = android.view.Gravity.CENTER
                setPadding(0, 64, 0, 0)
            }
            listContainer.addView(empty)
            return
        }

        vitamins.forEach { vitamin ->
            val takenMg = takenToday[vitamin.id] ?: 0
            val normMg = vitamin.normMg.toInt()
            val percent = if (normMg > 0) (takenMg * 100 / normMg) else 0

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 32, 32, 32)
                setBackgroundColor(Color.WHITE)
                val params = LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 0, 0, 24)
                layoutParams = params
            }

            val nameText = TextView(this).apply {
                text = vitamin.name
                textSize = 18f
                setTextColor(Color.BLACK)
            }

            val doseText = TextView(this).apply {
                text = "Доза: ${vitamin.doseMg} мг | Норма: ${vitamin.normMg} мг"
                textSize = 13f
                setTextColor(Color.DKGRAY)
                setPadding(0, 6, 0, 6)
            }

            val takenText = TextView(this).apply {
                text = "Принято сегодня: $takenMg мг из $normMg мг"
                textSize = 14f
                setTextColor(Color.DKGRAY)
                setPadding(0, 4, 0, 4)
            }

            val progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = normMg
                this.progress = takenMg
                layoutParams = LinearLayout.LayoutParams(-1, -2)
            }

            val statusText = TextView(this).apply {
                this.text = when {
                    takenMg == 0 -> "⬜ Ещё не принят"
                    percent < 80 -> "⚠️ Принято частично — $percent%"
                    percent in 80..100 -> "✅ Норма выполнена — $percent%"
                    else -> "🚨 Превышение нормы — $percent%!"
                }
                textSize = 14f
                setTextColor(when {
                    takenMg == 0 -> Color.GRAY
                    percent < 80 -> Color.parseColor("#FF9800")
                    percent in 80..100 -> Color.parseColor("#4CAF50")
                    else -> Color.RED
                })
                setPadding(0, 8, 0, 8)
            }

            val descText = TextView(this).apply {
                this.text = "Для чего: ${vitamin.description}"
                textSize = 13f
                setTextColor(Color.GRAY)
                setPadding(0, 4, 0, 4)
            }

            val excessText = TextView(this).apply {
                this.text = "Симптомы избытка: ${vitamin.excessSymptoms}"
                textSize = 13f
                setTextColor(Color.GRAY)
                setPadding(0, 4, 0, 12)
            }

            val btnTaken = Button(this).apply {
                text = "✓ Принял (${vitamin.doseMg} мг)"
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setTextColor(Color.WHITE)
                val params = LinearLayout.LayoutParams(-1, -2)
                params.setMargins(0, 0, 0, 8)
                layoutParams = params
                setOnClickListener {
                    VitaminStorage.markTaken(this@MainActivity, vitamin.id, vitamin.doseMg)
                    Toast.makeText(this@MainActivity, "${vitamin.name} — доза засчитана!", Toast.LENGTH_SHORT).show()
                    loadVitamins()
                }
            }

            val btnDelete = Button(this).apply {
                this.text = "Удалить"
                setOnClickListener {
                    VitaminStorage.delete(this@MainActivity, vitamin.id)
                    Toast.makeText(this@MainActivity, "Удалено", Toast.LENGTH_SHORT).show()
                    loadVitamins()
                }
            }

            card.addView(nameText)
            card.addView(doseText)
            card.addView(takenText)
            card.addView(progress)
            card.addView(statusText)
            card.addView(descText)
            card.addView(excessText)
            card.addView(btnTaken)
            card.addView(btnDelete)
            listContainer.addView(card)
        }
    }
}