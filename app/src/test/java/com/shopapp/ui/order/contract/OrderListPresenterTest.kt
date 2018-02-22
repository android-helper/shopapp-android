package com.shopapp.ui.order.contract

import com.shopapp.gateway.entity.Error
import com.shopapp.gateway.entity.Order
import com.shopapp.domain.interactor.order.OrderListUseCase
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.inOrder
import com.shopapp.ext.mock
import com.shopapp.ui.order.list.contract.OrderListPresenter
import com.shopapp.ui.order.list.contract.OrderListView
import com.shopapp.util.RxImmediateSchedulerRule
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class OrderListPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Mock
    private lateinit var view: OrderListView

    @Mock
    private lateinit var useCase: OrderListUseCase

    @Mock
    private lateinit var orderList: List<Order>

    private lateinit var presenter: OrderListPresenter


    @Before
    fun setUpTest() {
        MockitoAnnotations.initMocks(this)
        presenter = OrderListPresenter(useCase)
        presenter.attachView(view)
        useCase.mock()
    }

    @After
    fun tearDown() {
        presenter.detachView(false)
    }

    @Test
    fun shouldShowContentOnSingleSuccess() {
        given(useCase.buildUseCaseSingle(any())).willReturn(Single.just(orderList))
        presenter.getOrders(1, null)

        val inOrder = inOrder(view, useCase)
        inOrder.verify(useCase).execute(any(), any(), eq(OrderListUseCase.Params(1, null)))
        inOrder.verify(view).showContent(orderList)
    }

    @Test
    fun shouldShowMessageOnUseCaseNonCriticalError() {
        given(useCase.buildUseCaseSingle(any())).willReturn(Single.error(Error.NonCritical("ErrorMessage")))
        presenter.getOrders(1, null)

        val inOrder = inOrder(view, useCase)
        inOrder.verify(useCase).execute(any(), any(), eq(OrderListUseCase.Params(1, null)))
        inOrder.verify(view).showMessage("ErrorMessage")
    }

    @Test
    fun shouldShowErrorOnUseCaseContentError() {
        given(useCase.buildUseCaseSingle(any())).willReturn(Single.error(Error.Content(false)))
        presenter.getOrders(1, null)

        val inOrder = inOrder(view, useCase)
        inOrder.verify(useCase).execute(any(), any(), eq(OrderListUseCase.Params(1, null)))
        inOrder.verify(view).showError(false)
    }

}
