package com.example.project250228.Schedule

import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import kotlinx.coroutines.*
import android.util.Log

class GetSchedule {


//...在你的Activity或Fragment中

    fun fetchTimeTable(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient().newBuilder().followRedirects(true).build()

                // 1. 登入
                val formBody = FormBody.Builder()
                    .add("username", username)
                    .add("password", password)
                    .build()

                val loginRequest = Request.Builder()
                    .url("你的登入網址") // 請替換成正確的登入網址
                    .post(formBody)
                    .build()

                val loginResponse = client.newCall(loginRequest).execute()
                val cookies = loginResponse.headers("Set-Cookie")

                if (loginResponse.isSuccessful) {
                    // 2. 取得課表
                    val timetableRequest = Request.Builder()
                        .url("https://eclass2.nttu.edu.tw/dashboard/myTimeTable")
                        .header("Cookie", cookies.joinToString("; "))
                        .build()

                    val timetableResponse = client.newCall(timetableRequest).execute()
                    val timetableHtml = timetableResponse.body?.string()

                    if (timetableHtml != null) {
                        parseTimeTable(timetableHtml)
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.e("TimeTable", "取得課表 HTML 失敗")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.e("TimeTable", "登入失敗")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("TimeTable", "發生錯誤：${e.message}")
                }
            }
        }
    }

    fun parseTimeTable(html: String) {
        val document: Document = Jsoup.parse(html)
        val table: Element? = document.selectFirst("table#myTimeTable")

        if (table != null) {
            val rows: Elements = table.select("tr")

            CoroutineScope(Dispatchers.Main).launch {
                for (row in rows) {
                    val columns: Elements = row.select("td")
                    val rowData = mutableListOf<String>()

                    for (column in columns) {
                        rowData.add(column.text())
                    }
                    Log.d("TimeTable", rowData.toString())
                    // 在這裡您可以將 rowData 用於您的應用程式邏輯，例如顯示在 UI 上
                }
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                Log.e("TimeTable", "找不到課表")
            }
        }
    }
}