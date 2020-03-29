package com.cn29.aac.repo.github

import com.cn29.aac.AppExecutors
import com.cn29.aac.datasource.github.db.RepoDao
import com.cn29.aac.datasource.github.remote.GithubService
import com.cn29.aac.repo.util.RateLimiter
import com.cn29.aac.util.CoroutinesTestExtension
import com.cn29.aac.util.InstantExecutorExtension
import com.cn29.aac.util.getOrAwaitValue
import com.cn29.aac.util.runBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import retrofit2.Response


@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
class GitRepoRepositoryTest {

    // Set the main coroutines dispatcher for unit testing
    companion object {
        @JvmField
        @RegisterExtension
        var coroutinesRule = CoroutinesTestExtension()
    }


    private lateinit var appExecutors: AppExecutors
    private lateinit var repoDao: RepoDao
    private lateinit var githubService: GithubService
    private lateinit var gitRepoRepository: GitRepoRepository
    private lateinit var rateLimiter: RateLimiter<String>

    @BeforeEach
    fun setUp() {
        appExecutors = mock(AppExecutors::class.java)
        repoDao = mock(RepoDao::class.java)
        githubService = mock(GithubService::class.java)
        rateLimiter = mock(RateLimiter::class.java) as RateLimiter<String>
        gitRepoRepository = GitRepoRepository(appExecutors,
                                              repoDao,
                                              githubService,
                                              coroutinesRule.dispatcher,
                                              rateLimiter)
    }

    @Test
    internal fun `should call network to fetch result and insert to db if the response is not empty`() = coroutinesRule.runBlocking {
        //given
        val owner = "Tom"
        val response = Response.success(listOf(Repo(), Repo()))
        `when`(githubService.getRepo(anyString())).thenReturn(
                response)
        `when`(rateLimiter.shouldFetch(anyString())).thenReturn(true)
        //when
        val orAwaitValue = gitRepoRepository.loadRepo(owner).getOrAwaitValue()
        //then
        verify(githubService).getRepo(owner)
        verify(repoDao).insertRepos(anyList())
    }

}