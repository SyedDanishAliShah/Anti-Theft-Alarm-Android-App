package com.example.antitheftalarmapp.adapterclasses

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.antitheftalarmapp.AntiPocketDetectAlarmActivity
import com.example.antitheftalarmapp.ChargerDetectAlarmActivity
import com.example.antitheftalarmapp.FullBatteryDetectAlarmActivity
import com.example.antitheftalarmapp.IntruderAlertActivity
import com.example.antitheftalarmapp.MotionDetectionAlarmActivity
import com.example.antitheftalarmapp.R
import com.example.antitheftalarmapp.WifiChangeDetectAlarmActivity
import com.example.antitheftalarmapp.dataclasses.CardData

class CardAdapter(private val cardDataList: List<CardData>) :
    RecyclerView.Adapter<CardAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cardDataList[position]
    }


    override fun getItemCount(): Int = 1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardViewIntruder: ImageView = itemView.findViewById(R.id.cardView)
        val cardViewMotionDetectionAlarm : ImageView = itemView.findViewById(R.id.separate_alarm_card)
        val cardViewAntiPocketDetect : ImageView = itemView.findViewById(R.id.separate_alarm_card_1)
        val cardViewChargerDetect : ImageView = itemView.findViewById(R.id.separate_alarm_card_2)
        val cardViewWifiState : ImageView = itemView.findViewById(R.id.separate_alarm_card_3)
        val cardViewFullBattery : ImageView = itemView.findViewById(R.id.separate_alarm_card_4)


        init {
            cardViewIntruder.setOnClickListener {
                    val context = itemView.context
                    val intent = Intent(context, IntruderAlertActivity::class.java)
                    context.startActivity(intent)
            }

            cardViewMotionDetectionAlarm.setOnClickListener {
                    val context = itemView.context
                    val intent = Intent(context, MotionDetectionAlarmActivity::class.java)
                    context.startActivity(intent)
            }

            cardViewAntiPocketDetect.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, AntiPocketDetectAlarmActivity::class.java)
                context.startActivity(intent)
            }

            cardViewChargerDetect.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ChargerDetectAlarmActivity::class.java)
                context.startActivity(intent)
            }

            cardViewWifiState.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, WifiChangeDetectAlarmActivity::class.java)
                context.startActivity(intent)
            }

            cardViewFullBattery.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, FullBatteryDetectAlarmActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
}
