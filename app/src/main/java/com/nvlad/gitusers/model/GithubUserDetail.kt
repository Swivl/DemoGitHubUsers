package com.nvlad.gitusers.model

data class GithubUserDetail(var login: String, var avatarURL: String, var htmlURL: String,
                            var publicRepos: Int, var publicGists: Int, var followers: Int)