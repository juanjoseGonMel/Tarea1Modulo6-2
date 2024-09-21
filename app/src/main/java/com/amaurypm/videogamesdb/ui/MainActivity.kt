package com.amaurypm.videogamesdb.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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

    private lateinit var gameAdapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        repository = (application as VideoGamesDBApp).repository

        gameAdapter = GameAdapter()

        //Establezco el recyclerview
        binding.rvGames.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = gameAdapter
        }




        /*val game = GameEntity(
            title = "FIFA 23",
            genre = "Deportes",
            developer = "EA Sports",
        )

        lifecycleScope.launch {
            repository.insertGame(game)
        }*/

        updateUI()

    }

    fun click(view: View) {
        //Manejamos el click del floating action button

        val dialog = GameDialog()

        dialog.show(supportFragmentManager, "dialog1")

    }

    private fun updateUI(){
        lifecycleScope.launch {
            games = repository.getAllGames()

            binding.tvSinRegistros.visibility =
                if(games.isNotEmpty()) View.INVISIBLE else View.VISIBLE

            gameAdapter.updateList(games)
        }
    }
}