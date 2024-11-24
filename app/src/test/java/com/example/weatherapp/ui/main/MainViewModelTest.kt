package com.example.weatherapp.ui.main

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.model.StateGeneric
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.model.pojo.OsmResponseItem
import com.example.weatherapp.model.pojo.WeatherData
import com.example.weatherapp.model.pojo.WeatherForecastFiveDays
import com.example.weatherapp.repo.FakeRepoImplTest
import com.example.weatherapp.data.repo.IRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainViewModelTest {
    lateinit var repo: IRepo
    lateinit var viewModel: MainViewModel
    lateinit var customSaved: CustomSaved
    lateinit var otherCustomSaved: CustomSaved

    @Before
    fun setup() {
        repo = FakeRepoImplTest()
        viewModel = MainViewModel(repo)
        customSaved = CustomSaved(
            "",
            1.10,
            10L,
            "q",
            "q",
            1,
            1.0,
            1,
            1,
            1,
            false,
            true,
            1.0, 1.0, emptyList()

        )
        otherCustomSaved = CustomSaved(
            "",
            0.0,
            10L,
            "q",
            "q",
            1,
            1.0,
            1,
            1,
            1,
            false,
            true,
            1.0, 1.0, emptyList()

        )
    }

    @Test
    fun delete_equality_moreThanZero() {
        runTest {
            //when
            val res = viewModel.deleteWeatherData(customSaved)
            //then
            assert(res > 0)
        }
    }

    @Test
    fun delete_Notequality_moreThanZero() {
        runTest {
            //when
            val res = viewModel.deleteWeatherData(otherCustomSaved)
            //then
            assert(res <= 0)
        }
    }

    @Test
    fun searchTest_normalInput_NOtEmpty() {
        runTest {
            //when
            lateinit var res: List<OsmResponseItem>
            val job = launch {
                viewModel.search("q").collect {
                    when (it) {
                        is StateGeneric.Success -> res = it.data
                        is StateGeneric.Error -> res = emptyList()
                        is StateGeneric.Loading -> res = emptyList()
                    }
                }
            }
            job.join()
            //then
            assert(res.isEmpty() == false)
        }
    }

    @Test
    fun searchTest_wrongInput_empty() {
        runTest {
            //when
            lateinit var res: List<OsmResponseItem>
            val job = launch {
                viewModel.search("").collect {
                    when (it) {
                        is StateGeneric.Success -> res = it.data
                        is StateGeneric.Error -> res = emptyList()
                        is StateGeneric.Loading -> res = emptyList()
                    }
                }
            }
            job.join()
            //then
            assert(res.isEmpty() == true)
        }
    }

    @Test
    fun getWeatherByLongitudeAndLatitude_normalInput_success() {
        //when
        lateinit var res: StateGeneric<WeatherData?>
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.getWeatherByLongitudeAndLatitude(0.0, 0.0, "en", false)

            viewModel.responseOfWeather.collect {
                res = it
                //then
                assert(res is StateGeneric.Success)
            }
        }


    }

    @Test
    fun getHomeWeatherData_normalInput_success() {
        //when
        GlobalScope.launch(Dispatchers.IO) {
            //when
            viewModel.getHomeWeatherData()
            //then
            viewModel.homeLonLat.collect {
                assert(it.get("lat") == 1.1)
                viewModel.homeData.collect {
                    assert(it is StateGeneric.Success)
                }
            }

        }
    }
}