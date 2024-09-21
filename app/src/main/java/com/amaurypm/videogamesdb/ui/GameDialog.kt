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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class GameDialog(
    private val newGame: Boolean = true,
    private var game: GameEntity = GameEntity(
        title = "",
        genre = "",
        developer = ""
    ),
    private val updateUI: () -> Unit,
    private val message: (String) -> Unit
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

        //Establecemos en los text input edit text los valores del objeto game
        binding.apply {
            tietTitle.setText(game.title)
            tietGenre.setText(game.genre)
            tietDeveloper.setText(game.developer)
        }

        dialog = if(newGame)
            buildDialog("Guardar", "Cancelar", {
                //Acción de guardar

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
                        val result = async {
                            repository.insertGame(game)
                        }

                        //Con esto nos esperamos a que se termine esta acción antes de ejecutar lo siguiente
                        result.await()

                        //Con esto mandamos la ejecución de message y updateUI al hilo principal
                        withContext(Dispatchers.Main) {
                            message("Juego guardado exitosamente")

                            updateUI()
                        }
                    }



                }catch (e: IOException){

                    message("Error al guardar el juego")

                }

            }, {
                //Acción de cancelar

            })
        else
            buildDialog("Actualizar", "Borrar", {
                //Acción de actualizar

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
                        val result = async {
                            repository.updateGame(game)
                        }

                        result.await()

                        withContext(Dispatchers.Main){
                            message("Juego actualizado exitosamente")

                            updateUI()
                        }
                    }



                }catch (e: IOException){

                    message("Error al actualizar el juego")

                }

            }, {
                //Acción de borrar

                //Almacenamos el contexto en una variable antes de mandar llamar el diálogo nuevo
                val context = requireContext()

                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.confirm))
                    .setMessage(getString(R.string.confirm_game, game.title))
                    //¿Realmente desea eliminar el juego %1$s?
                    .setPositiveButton(getString(R.string.ok)){ _, _ ->
                        try{
                            lifecycleScope.launch(Dispatchers.IO) {

                                val result = async {
                                    repository.deleteGame(game)
                                }

                                result.await()

                                withContext(Dispatchers.Main){

                                    message(context.getString(R.string.game_removed))

                                    updateUI()
                                }
                            }


                        }catch (e: IOException){

                            message("Error al borrar el juego")

                        }
                    }
                    .setNegativeButton("Cancelar"){ dialog, _ ->
                        dialog.dismiss()
                    }
                    .create().show()

            })



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

    private fun buildDialog(
        btn1Text: String,
        btn2Text: String,
        positiveButton: () -> Unit,
        negativeButton: () -> Unit
    ): Dialog =
        builder.setView(binding.root)
            .setTitle(R.string.game)
            .setPositiveButton(btn1Text){ _, _ ->
                //Acción para el botón positivo
                positiveButton()
            }.setNegativeButton(btn2Text){ _, _ ->
                //Acción para el botón negativo
                negativeButton()
            }
            .create()

}