package com.example.calorietracker.presentation.diary

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calorietracker.R
import com.example.calorietracker.data.Graph
import com.example.calorietracker.data.remote.dto.DayDiaryDto
import com.example.calorietracker.data.remote.dto.EntryDto
import com.example.calorietracker.databinding.ActivityMainBinding
import com.example.calorietracker.databinding.DialogEditEntryBinding
import com.example.calorietracker.databinding.DialogGoalBinding
import com.example.calorietracker.presentation.add.AddEntryActivity
import com.example.calorietracker.presentation.auth.AuthActivity
import com.example.calorietracker.presentation.profile.ProfileActivity
import com.example.calorietracker.presentation.stats.StatsActivity
import com.example.calorietracker.util.DateUtil
import com.example.calorietracker.util.Resource
import com.example.calorietracker.util.formatNumber
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: DiaryViewModel by viewModels {
        viewModelFactory { initializer { DiaryViewModel(Graph.repository) } }
    }
    private lateinit var adapter: EntryAdapter

    /** Текущая дневная норма из последнего ответа сервера — для подстановки в диалог. */
    private var currentGoal: Int = DEFAULT_GOAL

    /** Признак, что экран уже показывался: первый onResume пропускается. */
    private var resumedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = EntryAdapter(
            onEdit = { entry -> showEditDialog(entry) },
            onDelete = { entry -> confirmDelete(entry) }
        )
        binding.entriesList.layoutManager = LinearLayoutManager(this)
        binding.entriesList.adapter = adapter

        binding.toolbar.setTitle(R.string.app_name)
        // Наполнение меню панели приложения: без inflateMenu кнопки в шапке
        // не отображаются (панель не назначена support action bar'ом).
        binding.toolbar.inflateMenu(R.menu.menu_main)
        // Никнейм авторизованного пользователя — рядом с кнопкой личного кабинета.
        binding.toolbar.subtitle = Graph.session.login

        binding.prevDayButton.setOnClickListener { viewModel.previousDay() }
        binding.nextDayButton.setOnClickListener { viewModel.nextDay() }
        binding.retryButton.setOnClickListener { viewModel.load() }
        binding.fabAdd.setOnClickListener { startActivity(Intent(this, AddEntryActivity::class.java)) }

        // Нажатие на карточку сводки открывает диалог изменения дневной нормы
        binding.summaryCard.setOnClickListener { showGoalDialog() }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_stats -> startActivity(Intent(this, StatsActivity::class.java))
                R.id.action_profile -> startActivity(Intent(this, ProfileActivity::class.java))
                R.id.action_logout -> confirmLogout()
            }
            true
        }

        viewModel.date.observe(this) { date ->
            binding.dateText.text = DateUtil.display(date)
        }
        viewModel.state.observe(this) { state -> render(state) }
        viewModel.goalResult.observe(this) { state -> renderGoalResult(state) }
        viewModel.entryResult.observe(this) { state -> renderEntryResult(state) }
    }

    /**
     * При возвращении на дневник (после добавления записи на экране добавления,
     * изменения нормы в профиле и т.п.) дневник перезагружается, поэтому список
     * записей и шкала калорий обновляются сразу. Первый показ экрана пропускается:
     * начальная загрузка уже выполнена в ViewModel при его создании.
     */
    override fun onResume() {
        super.onResume()
        if (resumedOnce) {
            viewModel.load()
        }
        resumedOnce = true
    }

    /** Диалог изменения дневной нормы калорий. */
    private fun showGoalDialog() {
        val dialogBinding = DialogGoalBinding.inflate(layoutInflater)
        dialogBinding.goalInput.setText(currentGoal.toString())
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.goal_dialog_title)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.cancel, null)
            .show()
        // Кнопка «Сохранить» закрывает диалог только при корректном значении
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val goal = dialogBinding.goalInput.text?.toString()?.trim()?.toIntOrNull()
            if (goal == null || goal < 800 || goal > 10000) {
                dialogBinding.goalLayout.error = getString(R.string.goal_error)
            } else {
                dialogBinding.goalLayout.error = null
                viewModel.updateGoal(goal)
                dialog.dismiss()
            }
        }
    }

    /** Диалог редактирования записи: исправление названия и калорийности. */
    private fun showEditDialog(entry: EntryDto) {
        val dialogBinding = DialogEditEntryBinding.inflate(layoutInflater)
        dialogBinding.nameInput.setText(entry.foodName)
        dialogBinding.caloriesInput.setText(formatNumber(entry.calories).replace(',', '.'))
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.edit_entry_title)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.cancel, null)
            .show()
        // Кнопка «Сохранить» закрывает диалог только при корректных данных
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = dialogBinding.nameInput.text?.toString()?.trim().orEmpty()
            val caloriesText = dialogBinding.caloriesInput.text?.toString()
                ?.replace(',', '.')?.trim().orEmpty()
            val calories = caloriesText.toDoubleOrNull()

            var valid = true
            if (name.isEmpty() || name.length > 100) {
                dialogBinding.nameLayout.error = getString(R.string.entry_name_error)
                valid = false
            } else {
                dialogBinding.nameLayout.error = null
            }
            if (calories == null || calories < 0.0 || calories > 10000.0) {
                dialogBinding.caloriesLayout.error = getString(R.string.entry_calories_error)
                valid = false
            } else {
                dialogBinding.caloriesLayout.error = null
            }
            if (valid) {
                viewModel.updateEntry(entry.id, name, calories!!)
                dialog.dismiss()
            }
        }
    }

    /** Подтверждение выхода из учётной записи с очисткой сеанса. */
    private fun confirmLogout() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_message)
            .setPositiveButton(R.string.logout_short) { _, _ -> logout() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun logout() {
        Graph.session.clear()
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun render(state: Resource<DayDiaryDto>) {
        when (state) {
            is Resource.Loading -> {
                binding.progress.isVisible = true
                binding.retryButton.isVisible = false
            }
            is Resource.Success -> {
                binding.progress.isVisible = false
                binding.retryButton.isVisible = false
                showDiary(state.data)
            }
            is Resource.Error -> {
                binding.progress.isVisible = false
                binding.entriesList.isVisible = false
                binding.emptyText.isVisible = true
                binding.emptyText.text = state.message
                binding.retryButton.isVisible = true
            }
        }
    }

    private fun showDiary(diary: DayDiaryDto) {
        val totals = diary.totals
        if (totals != null) {
            currentGoal = totals.goal
            binding.summaryText.text = getString(
                R.string.summary_format,
                formatNumber(totals.calories),
                totals.goal
            )
            binding.goalProgress.progress = totals.percentOfGoal.toInt().coerceIn(0, 100)
            binding.remainingText.text = if (totals.calories <= totals.goal) {
                getString(R.string.remaining_format, formatNumber(totals.remaining))
            } else {
                getString(
                    R.string.overage_format,
                    formatNumber(totals.calories - totals.goal)
                )
            }
            binding.summaryCard.isVisible = true
        } else {
            binding.summaryCard.isVisible = false
        }

        binding.entriesList.isVisible = diary.entries.isNotEmpty()
        binding.emptyText.isVisible = diary.entries.isEmpty()
        binding.emptyText.text = getString(R.string.empty_diary)
        binding.retryButton.isVisible = false
        adapter.submitList(diary.entries)
    }

    private fun renderGoalResult(state: Resource<Unit>?) {
        when (state) {
            is Resource.Loading -> Unit
            is Resource.Success -> {
                Toast.makeText(this, R.string.goal_saved, Toast.LENGTH_SHORT).show()
                viewModel.resetGoalResult()
            }
            is Resource.Error -> {
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetGoalResult()
            }
            null -> Unit
        }
    }

    private fun renderEntryResult(state: Resource<Unit>?) {
        when (state) {
            is Resource.Loading -> Unit
            is Resource.Success -> {
                Toast.makeText(this, R.string.entry_updated, Toast.LENGTH_SHORT).show()
                viewModel.resetEntryResult()
            }
            is Resource.Error -> {
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetEntryResult()
            }
            null -> Unit
        }
    }

    private fun confirmDelete(entry: EntryDto) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_entry_title)
            .setMessage(getString(R.string.delete_entry_message, entry.foodName))
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteEntry(entry.id) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private companion object {
        const val DEFAULT_GOAL = 2000
    }
}
