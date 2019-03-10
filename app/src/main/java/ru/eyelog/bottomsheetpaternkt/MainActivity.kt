package ru.eyelog.bottomsheetpaternkt

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomSheetFragment.BottomSheetListener {

//    lateinit var textView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomSheet = BottomSheetFragment()

        button.setOnClickListener {
            bottomSheet.show(supportFragmentManager, "bottomSheet")
        }
    }

    override fun onButtonClicked(text: String) {
        textView.setText(text)
    }
}
