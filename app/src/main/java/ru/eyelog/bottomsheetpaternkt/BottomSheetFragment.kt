package ru.eyelog.bottomsheetpaternkt

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet.view.*

class BottomSheetFragment : BottomSheetDialogFragment(){

    lateinit var bListener : BottomSheetListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.bottom_sheet, container, false)

        rootView.btButtonText.setOnClickListener {
            bListener.onButtonClicked("Button text")
            dismiss()
        }
        rootView.btButtonClose.setOnClickListener {
            bListener.onButtonClicked("Button close")
            dismiss()
        }

        return rootView
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        try {
            bListener = activity as BottomSheetListener
        }catch (e : ClassCastException ){
            throw ClassCastException("Sad think " + activity.toString())
        }

    }

    interface BottomSheetListener{
        fun onButtonClicked (text : String)
    }
}