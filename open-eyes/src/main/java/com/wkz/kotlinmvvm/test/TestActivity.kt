package com.wkz.kotlinmvvm.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.mvvm.viewmodel.activity.OpenEyesHomeActivity
import kotlinx.android.synthetic.main.activity_test.*
import okhttp3.MediaType
import okhttp3.RequestBody


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // {
        //    "userId": "32834",
        //    "page": 0,
        //    "size": 10,
        //    "classType": "e35d6e7951ec4b1e94672e63677e402f",
        //    "rebateStatus": "PENDING"
        //}
        mTvPostJson.setOnClickListener {
            val param = "{\n" +
                    "    \"userId\": \"32834\",\n" +
                    "    \"page\": 0,\n" +
                    "    \"size\": 10,\n" +
                    "    \"classType\": \"e35d6e7951ec4b1e94672e63677e402f\",\n" +
                    "    \"rebateStatus\": \"PENDING\"\n" +
                    "}"
            val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), param)
            TestModel().queryRebateListByUserId(body)
        }
        mTvPostForm.setOnClickListener {
            TestModel().queryGoodsClass("233")
        }
        mTvPostMultipart.setOnClickListener {

        }
        mTvGoToHome.setOnClickListener {
            startActivity(Intent(this@TestActivity, OpenEyesHomeActivity::class.java))
        }
    }
}
