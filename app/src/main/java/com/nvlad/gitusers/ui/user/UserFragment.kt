package com.nvlad.gitusers.ui.user

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nvlad.gitusers.R
import com.nvlad.gitusers.databinding.UserFragmentBinding
import com.nvlad.gitusers.model.GithubUserDetail
import com.nvlad.gitusers.utils.Extras.EXTRA_LOGIN

class UserFragment: Fragment() {
    private lateinit var binding: UserFragmentBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = UserFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        binding.textLink.setOnClickListener { onClickLink() }
        val login = activity?.intent?.getStringExtra(EXTRA_LOGIN) ?: return
        viewModel.fetchUser(login).observe(viewLifecycleOwner, Observer { updateUI(it) })
    }

    private fun updateUI(user: GithubUserDetail?){
        Glide.with(context!!)
            .load(user!!.avatarURL)
            .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.ic_empty_photo))
            .into(binding.imageUser)
        activity!!.title = user.login
        binding.textName.text = user.login
        binding.textLink.text = user.avatarURL
        binding.textRepos.text = context!!.getString(R.string.repos) + " " + user.publicRepos.toString()
        binding.textGists.text = context!!.getString(R.string.gists) + " " + user.publicGists.toString()
        binding.textFollowers.text = context!!.getString(R.string.followers) + " " + user.followers.toString()
    }

    private fun onClickLink(){
        val link = viewModel.user.value!!.htmlURL
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(i)
    }
}