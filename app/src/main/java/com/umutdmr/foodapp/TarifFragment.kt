package com.umutdmr.foodapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.umutdmr.foodapp.databinding.FragmentTarifBinding
import java.io.ByteArrayOutputStream
import java.lang.Exception

class TarifFragment : Fragment() {

    private var _binding: FragmentTarifBinding? = null
    private val binding get() = _binding!!
    var secilenGorsel: Uri? = null
    var secilenBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding  = FragmentTarifBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setOnClickListener {
            kaydet(it)
        }
        binding.ivPicture.setOnClickListener{
            gorselSec(it)
        }
        arguments?.let {

            val info = TarifFragmentArgs.fromBundle(it).bilgi
            if(info.equals("menudengeldim")){

                binding.etFoodName.setText("")
                binding.etFoodRecipe.setText("")
                binding.btnSubmit.visibility = View.VISIBLE
                val foodChoosePic = BitmapFactory.decodeResource(context?.resources, R.drawable.gorsel)
                binding.ivPicture.setImageBitmap(foodChoosePic)

            } else {
                binding.btnSubmit.visibility = View.INVISIBLE
                val id  = TarifFragmentArgs.fromBundle(it).id

                context?.let {
                    try {

                        activity?.let {
                            val database = it.openOrCreateDatabase("FoodsDatabase ", Context.MODE_PRIVATE, null)
                            val cursor = database.rawQuery("SELECT * FROM yemekler WHERE id = ?", arrayOf(id.toString()))

                            val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                            val yemekMalzemesiIndex = cursor.getColumnIndex("yemekmalzemesi")
                            val yemekResmiIndex = cursor.getColumnIndex("gorsel")

                            while (cursor.moveToNext()) {

                                binding.etFoodName.setText(cursor.getString(yemekIsmiIndex))
                                binding.etFoodRecipe.setText(cursor.getString(yemekMalzemesiIndex))

                                val byteArray = cursor.getBlob(yemekResmiIndex)
                                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                                binding.ivPicture.setImageBitmap(bitmap )
                            }
                            cursor.close()

                        }



                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                }
            }
        }

    }

    fun kaydet(view: View) {

        val foodName = binding.etFoodName.text.toString()
        val foodRecipe = binding.etFoodRecipe.text.toString()

        if(secilenBitmap != null) {

            val kucukBitmap = createSmallBitmap(secilenBitmap!!, 300)

            val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()

            try {

                context?.let {
                    val database = it.openOrCreateDatabase("FoodsDatabase ", Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS yemekler (id INTEGER PRIMARY KEY , yemekismi VARCHAR, yemekmalzemesi VARCHAR, gorsel BLOB)")

                    val sqlString = "INSERT INTO yemekler(yemekismi, yemekmalzemesi, gorsel) VALUES(?,?,?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1, foodName)
                    statement.bindString(2, foodRecipe)
                    statement.bindBlob(3, byteArray)
                    statement.execute()
                }



            } catch (e: Exception) {

                e.printStackTrace()
            }

            val action = TarifFragmentDirections.actionTarifFragmentToListeFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }

    fun gorselSec(view: View) {

        activity?.let {
            if(ContextCompat.checkSelfPermission(it.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1 )
            } else{
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1){

            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {

            secilenGorsel = data.data
            try {

                context?.let {
                    if(secilenGorsel != null){

                        if(Build.VERSION.SDK_INT >= 28) {

                            val source = ImageDecoder.createSource(it.contentResolver, secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)

                        } else {

                            secilenBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, secilenGorsel!!)
                        }
                        binding.ivPicture.setImageBitmap(secilenBitmap)
                    }
                }
            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun createSmallBitmap(bitmap: Bitmap, maximumRes: Int): Bitmap {

        var width = bitmap.width
        var height = bitmap.height

        var bitmapProportion : Double = width.toDouble() / height.toDouble()

        if(bitmapProportion > 1) {
            width = maximumRes
            height = (width / bitmapProportion).toInt()
        } else {

            height = maximumRes
            width = (height * bitmapProportion).toInt()
        }

        return  Bitmap.createScaledBitmap(bitmap, width, height, true )
    }


}