package com.demo.cryptoappv2

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.demo.cryptoappv2.api.ApiFactory
import com.demo.cryptoappv2.database.AppDatabase
import com.demo.cryptoappv2.pojo.CoinPriceInfo
import com.demo.cryptoappv2.pojo.CoinPriceInfoRawData
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CoinViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val compositeDisposable = CompositeDisposable()

    val priceList = db.coinPriceInfoDao().getPriceList()

    init {
        loadData()
    }

    fun getDetailInfo(fSym: String): LiveData<CoinPriceInfo> {
        return db.coinPriceInfoDao().getPriceInfoAboutCoin(fSym)
    }

    private fun loadData() {
        val disposable = ApiFactory.apiService.getTopCoinsInfo()
            .map { it.data?.map{ it1 -> it1.coinInfo?.name }?.joinToString(",") }
            .flatMap { ApiFactory.apiService.getFullPriceList(fSyms = it) }
            .map { getPriceListFromRawData(it) }
            .delaySubscription(30, TimeUnit.SECONDS) // Таймер на повторение загрузки (сначала числовое значение, потом минуты/секунды/милисекунды/дни и тд)
            .repeat() // Повторение загрузки бесконечное кол-во раз с максимально быстрой скоростью (но если упадёт ошибка, загрузка прекратится)
            .retry() // Повторяет попытку, если упадёт ошибка (например, если отключить интернет)
            .subscribeOn(Schedulers.io())
            .subscribe({
                it?.let { it1 -> db.coinPriceInfoDao().insertPriceList(it1) }
                Log.d("TEST_OF_LOADING_DATA", it.toString())
            }, {
                Log.d("TEST_OF_LOADING_DATA", "Failure: ${it.message}")
            })
        compositeDisposable.add(disposable)
    }

    private fun getPriceListFromRawData(coinPriceInfoRawData: CoinPriceInfoRawData): List<CoinPriceInfo>? {
        val result = ArrayList<CoinPriceInfo>()
        val jsonObject = coinPriceInfoRawData.coinPriceInfoJsonObject ?: return result
        val coinKeySet = jsonObject.keySet()
        for (key in coinKeySet) {
            val currentJson = jsonObject.getAsJsonObject(key)
            val currentKeySet = currentJson.keySet()
            for (currentKey in currentKeySet) {
                val priceInfo = Gson().fromJson(
                    currentJson.getAsJsonObject(currentKey),
                    CoinPriceInfo::class.java
                )
                result.add(priceInfo)
            }
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}