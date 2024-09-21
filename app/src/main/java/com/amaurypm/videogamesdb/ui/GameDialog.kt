package com.amaurypm.videogamesdb.ui

import android.app.AlertDialog
import android.app.AlertDialog.Builder
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.amaurypm.videogamesdb.R
import com.amaurypm.videogamesdb.application.VideoGamesDBApp
import com.amaurypm.videogamesdb.data.GameRepository
import com.amaurypm.videogamesdb.data.db.model.GameEntity
import com.amaurypm.videogamesdb.databinding.GameDialogBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class GameDialog(
    private var game: GameEntity = GameEntity(
        title = "",
        genre = "",
        developer = ""
    )
): DialogFragment() {

    private var _binding: GameDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: Dialog

    private var saveButton: Button? = null

    private lateinit var repository: GameRepository

    //Aquí se crea y configura de forma inicial el dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = GameDialogBinding.inflate(requireActivity().layoutInflater)

        //Obtenemos dentro del dialog fragment una instancia al repositorio
        repository = (requireContext().applicationContext as VideoGamesDBApp).repository

        builder = AlertDialog.Builder(requireContext())

        dialog = builder.setView(binding.root)
            .setTitle(getString(R.string.game))
            .setPositiveButton("Guardar", DialogInterface.OnClickListener { _, _ ->
                //Click para el botón positivo

                //Obtenemos los textos ingresados y se los
                //asignamos a nuestro objeto game
                binding.apply {
                    game.apply {
                        title = tietTitle.text.toString()
                        genre = tietGenre.text.toString()
                        developer = tietDeveloper.text.toString()
                    }
                }

                try{

                    lifecycleScope.launch(Dispatchers.IO) {
                        repository.insertGame(game)
                    }

                    Toast.makeText(
                        requireContext(),
                        "Juego guardado exitosamente",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                }catch (e: IOException){
                    Toast.makeText(
                        requireContext(),
                        "Error al guardar el juego",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }


            })
            .setNegativeButton("Cancelar"){ _, _ ->
                //Click para el botón negativo

            }
            .create()

        return dialog
    }

    //Aquí es cuando se destruye
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //Se llama después de que se muestra el diálogo en pantalla
    override fun onStart() {
        super.onStart()

        //Debido a que la clase dialog no me permite referenciarme a sus botones
        val alertDialog = dialog as AlertDialog

        saveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)

        saveButton?.isEnabled = false

        /*binding.tietTitle.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })

        binding.tietGenre.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })

        binding.tietDeveloper.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })*/

        binding.apply {
            setupTextWatcher(
                tietTitle,
                tietGenre,
                tietDeveloper
            )
        }

    }

    private fun validateFields(): Boolean
        = binding.tietTitle.text.toString().isNotEmpty() &&
            binding.tietGenre.text.toString().isNotEmpty() &&
            binding.tietDeveloper.text.toString().isNotEmpty()

    private fun setupTextWatcher(vararg textFields: TextInputEditText){
        val textWatcher = object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        }

        textFields.forEach { textField ->
            textField.addTextChangedListener(textWatcher)
        }
    }

}