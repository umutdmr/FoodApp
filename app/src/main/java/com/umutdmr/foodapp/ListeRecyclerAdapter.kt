package com.umutdmr.foodapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlin.contracts.contract

class ListeRecyclerAdapter(val foodNameList: ArrayList<String>, val idList:ArrayList<Int>): RecyclerView.Adapter<ListeRecyclerAdapter.FoodVH>() {

    class FoodVH(itemView: View): RecyclerView.ViewHolder(itemView){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)
        return FoodVH(itemView)
    }

    override fun onBindViewHolder(holder: FoodVH, position: Int) {

        holder.itemView.findViewById<TextView>(R.id.tvFoodNameRecyclerView).text = foodNameList.get(position)
        holder.itemView.setOnClickListener{
            val action = ListeFragmentDirections.actionListeFragmentToTarifFragment("recyclerdangeldim", idList.get(position)  )
            Navigation.findNavController(it ).navigate(action)
        }

        /*holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, IntroductionActivity::class.java)
            intent.putExtra("superHeroName", foodNameList.get(position))
            intent.putExtra("superHeroPic", foodNameList.get(position))
            /*val singletonClass = SingletonClass.Hero
            singletonClass.pic = superHeroPics.get(position)*/
            holder.itemView.context.startActivity(intent)
        }*/
    }

    override fun getItemCount(): Int {
        return foodNameList.size
    }
}

