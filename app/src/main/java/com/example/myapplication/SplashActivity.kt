package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*
import java.lang.Exception

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val animacion: Animation = AnimationUtils.loadAnimation(this, R.anim.anim_arriba)
        val animacion2: Animation = AnimationUtils.loadAnimation(this, R.anim.anim_debajo)

        linear1.animation = animacion
        linear2.animation = animacion2

        val background = object : Thread(){
            override fun run(){
                super.run()

                try{
                    Thread.sleep(4000.toLong())
                    val i = Intent(baseContext, MenuActivity::class.java)
                    startActivity(i)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        background.start()
    }
}