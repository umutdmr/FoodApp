package com.umutdmr.foodapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.umutdmr.foodapp.databinding.FragmentListeBinding
import com.umutdmr.foodapp.databinding.FragmentTarifBinding
import java.lang.Exception

class ListeFragment : Fragment() {

    var foodList = ArrayList<String>()
    var foodIdList = ArrayList<Int>()
    private var _binding: FragmentListeBinding? = null
    private val binding get() = _binding!!
    private lateinit var listAdapter: ListeRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding  = FragmentListeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listAdapter = ListeRecyclerAdapter(foodList, foodIdList)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = listAdapter


        sqlGetData()
    }
    fun sqlGetData() {

        try {

            activity?.let {
                val database = it.openOrCreateDatabase("FoodsDatabase ", Context.MODE_PRIVATE, null)
                val cursor = database.rawQuery("SELECT * FROM yemekler", null)

                val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                val idIndex = cursor.getColumnIndex("id")

                foodList.clear()
                foodIdList.clear()
                while (cursor.moveToNext()) {

                    foodList.add(cursor.getString(yemekIsmiIndex))
                    foodIdList.add(cursor.getInt(idIndex))
                }
                listAdapter.notifyDataSetChanged()
                cursor.close()

            }



        } catch (e: Exception) {

            e.printStackTrace()
        }

    }
    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_food, menu)
        //return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.itemAddFood -> {
                val action = ListeFragmentDirections.actionListeFragmentToTarifFragment()
                findNavController().navigate(action)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }*/

}