package com.nvlad.gitusers.ui.userslist

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.security.ProviderInstaller
import com.nvlad.gitusers.R
import com.nvlad.gitusers.databinding.UsersListFragmentBinding
import com.nvlad.gitusers.model.GithubUser
import javax.net.ssl.SSLContext
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.GooglePlayServicesRepairableException
import android.app.Activity
import android.os.Build
import android.util.Log


class UsersListFragment: Fragment() {
    private lateinit var binding: UsersListFragmentBinding
    private lateinit var viewModel: UsersListViewModel
    private lateinit var adapter: UsersListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = UsersListFragmentBinding.inflate(inflater)
        adapter = UsersListAdapter(ArrayList(), activity){prefetchUsers()}
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerUsers.layoutManager = layoutManager
        binding.recyclerUsers.adapter = adapter
        binding.layoutRoot.setOnRefreshListener { refresh() }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UsersListViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        viewModel.users.observe(viewLifecycleOwner, Observer { updateUsers(it) })
    }

    private fun updateUsers(users: List<GithubUser>?){
        binding.layoutRoot.isRefreshing = false
        if (users == null){
            Toast.makeText(context, R.string.no_connection, Toast.LENGTH_SHORT).show()
            Handler().postDelayed({refresh()},2000L)
            return
        }
        adapter.users = users
        adapter.notifyDataSetChanged()
    }

    private fun refresh(){
        viewModel.updateUsers()
    }

    private fun prefetchUsers(){
        viewModel.fetchNew()
    }
}