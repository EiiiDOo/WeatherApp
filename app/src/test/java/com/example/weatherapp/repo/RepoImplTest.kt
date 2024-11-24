package com.example.weatherapp.repo

import com.example.weatherapp.local.FakeLocalDataSourceImplTest
import com.example.weatherapp.data.local.ILocalDataSource
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.remote.FakeRemoteDataSourceImplTest
import com.example.weatherapp.data.remote.IRemotedataSource
import com.example.weatherapp.data.repo.IRepo
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.repo.FakeRepoImplTest.Companion.customSaved
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test


class RepoImplTest {
    companion object {
        val otherCustomSaved = CustomSaved(
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


    lateinit var repo: IRepo
    lateinit var remote: IRemotedataSource
    lateinit var local: ILocalDataSource

    @Before
    fun setup() {
        remote = FakeRemoteDataSourceImplTest()
        local = FakeLocalDataSourceImplTest()
        repo = RepoImpl.getInstance(remote, local)
    }

    @Test
    fun insert_normalTest_returnpositiveNumber() {
        runBlocking {
            val result = repo.insert(customSaved)
            assert(result >= 0)
        }
    }

    @Test
    fun delete_normalTest_returnpositiveNumber() {
        runBlocking {
            val result = repo.delete(customSaved)
            assert(result >= 0)
        }
    }

    @Test
    fun delete_wrongTest_returnnegativeNumber() {
        runBlocking {
            val result = repo.delete(otherCustomSaved)
            assert(result >= 0)
        }
    }

    @Test
    fun serachTest_emptyquery_returnEmptyList() {
        runBlocking {
            val result = repo.search("")
            assert(result.body()!!.isEmpty())
        }
    }
    @Test
    fun serachTest_query_list() {
        runBlocking{
            val result = repo.search("1")
            assert(!result.body()!!.isEmpty())
        }
    }
}