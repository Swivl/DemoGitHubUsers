package com.nvlad.gitusers.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nvlad.gitusers.model.GithubUser
import com.nvlad.gitusers.model.GithubUserDetail
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class UsersRepository {

    private val githubAPI: GithubAPI
    private val githubDetailAPI: GithubDetailAPI

    init {
        val gson = GsonBuilder().registerTypeAdapter(GithubUser::class.java, GithubUserDeserializer()).create()
        val gson2 = GsonBuilder().registerTypeAdapter(GithubUserDetail::class.java, GithubUserDetailDeserializer()).create()

        val retrofit = Retrofit.Builder()
            .baseUrl(GithubAPI.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        githubAPI = retrofit.create(GithubAPI::class.java)

        val retrofit2 = Retrofit.Builder()
            .baseUrl(GithubDetailAPI.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(gson2))
            .build()

        githubDetailAPI = retrofit2.create(GithubDetailAPI::class.java)
    }

    fun getUsers(): LiveData<List<GithubUser>> {
        val usersData = MutableLiveData<List<GithubUser>>()
        githubAPI.getUsers().enqueue(object : Callback<List<GithubUser>> {
            override fun onResponse(call: Call<List<GithubUser>>, response: Response<List<GithubUser>>) {
                usersData.value = response.body()
            }

            override fun onFailure(call: Call<List<GithubUser>>, t: Throwable) {}
        })
        return usersData
    }

    fun getUsers(since: Int, completion: (List<GithubUser>?) -> (Unit)){
        githubAPI.getUsers(since).enqueue(object : Callback<List<GithubUser>> {
            override fun onResponse(call: Call<List<GithubUser>>, response: Response<List<GithubUser>>) {
                completion(response.body())
            }

            override fun onFailure(call: Call<List<GithubUser>>, t: Throwable) {}
        })
    }

    fun getUserDetail(login: String): LiveData<GithubUserDetail>{
        val data = MutableLiveData<GithubUserDetail>()
        githubDetailAPI.getUserDetail(login).enqueue(object : Callback<GithubUserDetail> {
            override fun onResponse(call: Call<GithubUserDetail>, response: Response<GithubUserDetail>) {
                data.value = response.body()
            }

            override fun onFailure(call: Call<GithubUserDetail>, t: Throwable) {}
        })
        return data
    }

    companion object {

        private var usersRepository: UsersRepository? = null

        val instance: UsersRepository
            get() {
                if (usersRepository == null) {
                    usersRepository = UsersRepository()
                }
                return usersRepository!!
            }
    }
}