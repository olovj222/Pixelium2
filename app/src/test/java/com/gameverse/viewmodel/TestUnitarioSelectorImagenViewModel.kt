package com.gameverse.viewmodel

import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test

class SelectorImagenViewModelTest {

    private lateinit var viewModel: SelectorImagenViewModel

    @Before
    fun setUp() {
        // Inicializamos el ViewModel antes de cada prueba
        viewModel = SelectorImagenViewModel()
    }

    @Test
    fun `initial state should be null`() {
        // THEN
        viewModel.uriImagen shouldBe null
    }

    @Test
    fun `asignarUriImagen should update uriImagen with valid string`() {
        // GIVEN
        val testUri = "content://media/external/images/media/12345"

        // WHEN
        viewModel.asignarUriImagen(testUri)

        // THEN
        viewModel.uriImagen shouldBe testUri
    }

    @Test
    fun `asignarUriImagen should update uriImagen with null`() {
        // GIVEN
        // Primero asignamos algo para asegurarnos de que cambia
        viewModel.asignarUriImagen("some/uri")

        // WHEN
        viewModel.asignarUriImagen(null)

        // THEN
        viewModel.uriImagen shouldBe null
    }
}