package com.marknkamau.justjava.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.marknjunge.core.data.model.Resource
import com.marknjunge.core.data.model.User
import com.marknjunge.core.data.repository.UsersRepository
import com.marknkamau.justjava.utils.SampleData
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class ProfileViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var usersRepository: UsersRepository

    private lateinit var viewModel: ProfileViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = ProfileViewModel(usersRepository)
    }

    @ExperimentalCoroutinesApi
    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `can get current user`() {
        val resource = Resource.Success(SampleData.user)
        coEvery { usersRepository.getCurrentUser() } returns flow { emit(resource) }

        val observer = spyk<Observer<Resource<User>>>()
        viewModel.user.observeForever(observer)
        viewModel.getCurrentUser()

        verify { observer.onChanged(resource) }
    }

    @Test
    fun `can update user`() {
        val resource = Resource.Success(Unit)
        coEvery { usersRepository.updateUser("", "", "", "") } returns resource

        val observer = spyk<Observer<Resource<Unit>>>()
        viewModel.updateUser("", "", "", "").observeForever(observer)

        verify { observer.onChanged(resource) }
    }
}