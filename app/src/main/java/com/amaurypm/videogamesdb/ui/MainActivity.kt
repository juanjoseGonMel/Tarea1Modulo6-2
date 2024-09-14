package com.amaurypm.videogamesdb.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.amaurypm.videogamesdb.application.VideoGamesDBApp
import com.amaurypm.videogamesdb.data.GameRepository
import com.amaurypm.videogamesdb.data.db.model.GameEntity
import com.amaurypm.videogamesdb.databinding.ActivityMainBinding
import com.amaurypm.videogamesdb.util.Constants
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var games: MutableList<GameEntity> = mutableListOf()
    private lateinit var repository: GameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        repository = (application as VideoGamesDBApp).repository

        /*val game = GameEntity(
            title = "",
            genre = "",
            developer = "",
        )

        lifecycleScope.launch {
            repository.insertGame(game)
        }*/

        updateUI()

    }

    fun click(view: View) {
        //Manejamos el click del floating action button
    }

    private fun updateUI(){
        lifecycleScope.launch {
            games = repository.getAllGames()

            binding.tvSinRegistros.visibility =
                if(games.isNotEmpty()) View.INVISIBLE else View.VISIBLE
        }
    }
}