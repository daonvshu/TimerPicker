package com.daonvshu.customtimerpicker

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.lljjcoder.style.citypickerview.widget.CanShow
import com.lljjcoder.style.citypickerview.widget.wheel.OnWheelChangedListener
import com.lljjcoder.style.citypickerview.widget.wheel.WheelView
import com.lljjcoder.style.citypickerview.widget.wheel.adapters.ArrayWheelAdapter
import com.lljjcoder.utils.utils
import kotlinx.android.synthetic.main.pop_timer_picker.view.*
import java.util.*
import kotlin.collections.ArrayList

class CustomTimerPickerView(private val context: Context) : CanShow, OnWheelChangedListener {

    private lateinit var popWindow: PopupWindow
    private lateinit var popView: View
    private var selectCallback: ((Long, Long)->Unit)? = null

    private val layoutInflater = LayoutInflater.from(context)

    private fun initView() {
        popView = layoutInflater.inflate(R.layout.pop_timer_picker, null)
        initWheelView(popView.id_st_year)
        initWheelView(popView.id_st_month)
        initWheelView(popView.id_et_year)
        initWheelView(popView.id_et_month)

        popView.tv_cancel.setOnClickListener {
            hide()
        }
        popView.tv_confirm.setOnClickListener {
            selectCallback?.invoke(getTime(popView.id_st_year, popView.id_st_month),
                getTime(popView.id_et_year, popView.id_et_month, true) - 1)
            hide()
        }

        popWindow = PopupWindow(popView, -1, -2)
        popWindow.animationStyle = R.style.AnimBottom
        popWindow.setBackgroundDrawable(ColorDrawable())
        popWindow.isTouchable = true
        popWindow.isOutsideTouchable = false
        popWindow.isFocusable = true
        popWindow.setOnDismissListener {
            utils.setBackgroundAlpha(context, 1.0f)
        }

        utils.setBackgroundAlpha(context, 0.5f)

        setupData()
    }

    private fun initWheelView(view: WheelView) {
        view.addChangingListener(this)
        view.visibleItems = 5
        view.isCyclic = false
        view.setDrawShadows(true)
    }

    private val yearData = ArrayList<DateData>()
    private val monthData = ArrayList<DateData>()

    private fun setupData() {
        val calendar = Calendar.getInstance()

        val yearCur = calendar.get(Calendar.YEAR)
        for (year in yearCur - 50..yearCur) {
            yearData.add(DateData(year, "${year}年"))
        }
        val yearAdapter = ArrayWheelAdapter(context, yearData)
        popView.id_st_year.viewAdapter = yearAdapter
        popView.id_et_year.viewAdapter = yearAdapter

        for (month in 1..12) {
            monthData.add(DateData(month, "${month}月"))
        }
        val monthAdapter = ArrayWheelAdapter(context, monthData)
        popView.id_st_month.viewAdapter = monthAdapter
        popView.id_et_month.viewAdapter = monthAdapter

        popView.id_st_year.currentItem = yearData.size - 1
        popView.id_st_month.currentItem = calendar.get(Calendar.MONTH)
    }

    override fun hide() {
        if (isShow) {
            popWindow.dismiss()
        }
    }

    override fun isShow(): Boolean {
        return popWindow.isShowing
    }

    override fun onChanged(p0: WheelView?, p1: Int, p2: Int) {
        when (p0) {
            popView.id_st_year, popView.id_st_month -> timeLimitCheck(true)
            popView.id_et_year, popView.id_et_month -> timeLimitCheck(false)
        }
    }

    private fun timeLimitCheck(stMoved: Boolean) {
        val st = getTime(popView.id_st_year, popView.id_st_month)
        val et = getTime(popView.id_et_year, popView.id_et_month)
        if (et < st) {
            if (stMoved) {
                popView.id_et_year.syncItem(popView.id_st_year)
                popView.id_et_month.syncItem(popView.id_st_month)
            } else {
                popView.id_st_year.syncItem(popView.id_et_year)
                popView.id_st_month.syncItem(popView.id_et_month)
            }
        }
    }

    private fun getTime(yearView: WheelView, monthView: WheelView, nextMonth: Boolean = false) : Long {
        val calendar = Calendar.getInstance()
        calendar.set(yearView.value(yearData), monthView.value(monthData) - 1, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        if (nextMonth) {
            calendar.add(Calendar.MONTH, 1)
        }
        return calendar.time.time
    }

    fun showPicker(selectCallback: ((Long, Long)->Unit)? = null) : CustomTimerPickerView {
        this.selectCallback = selectCallback
        initView()
        if (!isShow) {
            popWindow.showAtLocation(popView, 80, 0, 0)
        }
        return this
    }

    fun setTime(st: Long, et: Long) {
        if (st == 0L || et == 0L) {
            return
        }
        val calendar = Calendar.getInstance()
        val yearCur = calendar.get(Calendar.YEAR)

        calendar.time = Date(st)
        val yearSt = calendar.get(Calendar.YEAR)
        if (yearSt in yearCur - 50..yearCur) {
            popView.id_st_year.currentItem = yearSt - (yearCur - 50)
        }
        popView.id_st_month.currentItem = calendar.get(Calendar.MONTH)

        calendar.time = Date(et)
        val yearEt = calendar.get(Calendar.YEAR)
        if (yearEt in yearCur - 50..yearCur) {
            popView.id_et_year.currentItem = yearEt - (yearCur - 50)
        }
        popView.id_et_month.currentItem = calendar.get(Calendar.MONTH)
    }
}

data class DateData(val value: Int, val name: String = "$value") {

    override fun toString(): String {
        return name
    }
}

fun WheelView.value(source: ArrayList<DateData>): Int {
    return source[this.currentItem].value
}

fun WheelView.syncItem(view: WheelView) {
    this.currentItem = view.currentItem
}