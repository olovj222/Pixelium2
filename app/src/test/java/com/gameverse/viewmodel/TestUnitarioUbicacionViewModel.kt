package com.gameverse.viewmodel

import android.app.Application
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher // Importante
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UbicacionViewModelTest {

    private lateinit var viewModel: UbicacionViewModel

    // Usamos UnconfinedTestDispatcher para que las corutinas se ejecuten "ansiosamente" (inmediatamente)
    // Esto suele simplificar mucho las pruebas de StateFlow/MutableState
    private val testDispatcher = UnconfinedTestDispatcher()

    private val application: Application = mockk(relaxed = true)
    private val addressProvider: AddressProvider = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // ¡PASAMOS EL TEST DISPATCHER AL VIEWMODEL!
        viewModel = UbicacionViewModel(application, addressProvider, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `actualizarUbicacion should update lat, lon and address`() = runTest {
        // GIVEN
        val lat = -33.4489
        val lon = -70.6693
        val expectedAddress = "Calle Falsa 123, Santiago"

        // Enseñamos al mock a responder (con un pequeño delay simulado si quieres probar asincronía real,
        // pero con Unconfined es instantáneo si no hay delay).
        coEvery { addressProvider.getAddress(lat, lon) } returns expectedAddress

        // WHEN
        viewModel.actualizarUbicacion(lat, lon)

        // Con UnconfinedTestDispatcher, el código dentro de launch se ejecuta hasta el primer punto de suspensión.
        // Si getAddress es suspendida pero el mock responde inmediato, puede que termine todo.
        // Aún así, advanceUntilIdle() es buena práctica para asegurar que todo terminó.
        // Nota: Como estamos dentro de runTest, usamos su scheduler implícito o el del dispatcher.

        // Si usaste StandardTestDispatcher arriba, NECESITAS esta línea.
        // Con Unconfined a veces no, pero no hace daño.
        // testDispatcher.scheduler.advanceUntilIdle()

        // THEN
        viewModel.latitud shouldBe lat
        viewModel.longitud shouldBe lon
        viewModel.direccion shouldBe expectedAddress
    }
}