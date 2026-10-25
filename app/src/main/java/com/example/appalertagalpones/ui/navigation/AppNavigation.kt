package com.example.appalertagalpones.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.alertagalpones.ui.screen.AccionScreen
import com.example.alertagalpones.ui.screen.AjustesScreen
import com.example.alertagalpones.ui.screen.AlertasScreen
import com.example.alertagalpones.ui.screen.ContactosScreen
import com.example.alertagalpones.ui.screen.DetalleScreen
import com.example.alertagalpones.ui.screen.GalponesScreen
import com.example.alertagalpones.ui.screen.LoginScreen
import com.example.alertagalpones.ui.screen.PanelScreen
import com.example.alertagalpones.ui.screen.PerfilScreen
import com.example.alertagalpones.ui.screen.SimuladorScreen
import com.example.appalertagalpones.data.model.Rol
import com.example.appalertagalpones.ui.viewmodel.AccionViewModel
import com.example.appalertagalpones.ui.viewmodel.AjustesViewModel
import com.example.appalertagalpones.ui.viewmodel.AlertasViewModel
import com.example.appalertagalpones.ui.viewmodel.DetalleViewModel
import com.example.appalertagalpones.ui.viewmodel.GalponesViewModel
import com.example.appalertagalpones.ui.viewmodel.PanelViewModel
import com.example.appalertagalpones.ui.viewmodel.SesionViewModel
import com.example.appalertagalpones.ui.viewmodel.SimuladorViewModel
import kotlinx.coroutines.launch

private data class Destino(val ruta: String, val etiqueta: String, val icono: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(factory: ViewModelProvider.Factory) {
    // ViewModels compartidos entre pantallas (Guía 11): se crean una vez aquí y se entregan a cada destino.
    val sesionVM: SesionViewModel = viewModel(factory = factory)
    val galponesVM: GalponesViewModel = viewModel(factory = factory)
    val alertasVM: AlertasViewModel = viewModel(factory = factory)
    val detalleVM: DetalleViewModel = viewModel(factory = factory)
    val accionVM: AccionViewModel = viewModel(factory = factory)
    val simuladorVM: SimuladorViewModel = viewModel(factory = factory)
    val ajustesVM: AjustesViewModel = viewModel(factory = factory)
    val panelVM: PanelViewModel = viewModel(factory = factory)

    val cargando by sesionVM.cargando.collectAsStateWithLifecycle()
    val usuario by sesionVM.usuario.collectAsStateWithLifecycle()

    if (cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // La sesión manda: sin usuario → login; con usuario estando en login → galpones.
    LaunchedEffect(usuario?.usuario) {
        val actual = navController.currentDestination?.route ?: return@LaunchedEffect
        if (usuario == null && actual != AppRoutes.LOGIN) {
            navController.navigate(AppRoutes.LOGIN) { popUpTo(0) }
        } else if (usuario != null && actual == AppRoutes.LOGIN) {
            navController.navigate(AppRoutes.GALPONES) { popUpTo(AppRoutes.LOGIN) { inclusive = true } }
        }
    }

    fun irA(ruta: String) {
        navController.navigate(ruta) {
            popUpTo(AppRoutes.GALPONES) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val mostrarMarco = usuario != null && rutaActual != null && rutaActual != AppRoutes.LOGIN
    val inferiores = listOf(
        Destino(AppRoutes.GALPONES, "Galpones", Icons.Default.Home),
        Destino(AppRoutes.ALERTAS, "Alertas", Icons.Default.Notifications),
        Destino(AppRoutes.PERFIL, "Perfil", Icons.Default.Person)
    )
    val lateral = buildList {
        add(Destino(AppRoutes.GALPONES, "Galpones", Icons.Default.Home))
        add(Destino(AppRoutes.ALERTAS, "Alertas y eventos", Icons.Default.Notifications))
        if (usuario?.rol != Rol.OPERARIO) add(Destino(AppRoutes.PANEL, "Panel general", Icons.Default.Warning))
        add(Destino(AppRoutes.SIMULADOR, "Simulador de lecturas", Icons.Default.Build))
        add(Destino(AppRoutes.CONTACTOS, "Contactos", Icons.Default.Call))
        add(Destino(AppRoutes.AJUSTES, "Umbrales", Icons.Default.Settings))
        add(Destino(AppRoutes.PERFIL, "Perfil", Icons.Default.Person))
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = mostrarMarco,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.padding(24.dp)) {
                    Text("Alerta Galpones", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "${usuario?.nombre ?: ""} · ${usuario?.rol?.etiqueta ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                lateral.forEach { destino ->
                    NavigationDrawerItem(
                        selected = rutaActual == destino.ruta,
                        onClick = {
                            irA(destino.ruta)
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(destino.icono, contentDescription = null) },
                        label = { Text(destino.etiqueta) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (mostrarMarco) {
                    TopAppBar(
                        title = { Text(AppRoutes.titulo(rutaActual)) },
                        navigationIcon = {
                            if (rutaActual in AppRoutes.principales) {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                                }
                            } else {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                                }
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (mostrarMarco && inferiores.any { it.ruta == rutaActual }) {
                    NavigationBar {
                        inferiores.forEach { destino ->
                            NavigationBarItem(
                                selected = rutaActual == destino.ruta,
                                onClick = { irA(destino.ruta) },
                                icon = { Icon(destino.icono, contentDescription = destino.etiqueta) },
                                label = { Text(destino.etiqueta) }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = if (usuario != null) AppRoutes.GALPONES else AppRoutes.LOGIN,
                modifier = Modifier.padding(paddingValues),
                enterTransition = { fadeIn(tween(250)) },
                exitTransition = { fadeOut(tween(200)) },
                popEnterTransition = { fadeIn(tween(250)) },
                popExitTransition = { fadeOut(tween(200)) }
            ) {
                composable(AppRoutes.LOGIN) {
                    LoginScreen(viewModel = sesionVM)
                }
                composable(AppRoutes.GALPONES) {
                    GalponesScreen(
                        viewModel = galponesVM,
                        onGalponClick = { id ->
                            detalleVM.seleccionarGalpon(id)
                            navController.navigate(AppRoutes.DETALLE)
                        }
                    )
                }
                composable(AppRoutes.DETALLE) {
                    DetalleScreen(
                        viewModel = detalleVM,
                        onRegistrarAccion = { id ->
                            accionVM.prepararFormulario(id)
                            navController.navigate(AppRoutes.ACCION)
                        }
                    )
                }
                composable(AppRoutes.ACCION) {
                    AccionScreen(
                        viewModel = accionVM,
                        onGuardado = { navController.popBackStack() }
                    )
                }
                composable(AppRoutes.ALERTAS) { AlertasScreen(viewModel = alertasVM) }
                composable(AppRoutes.PANEL) { PanelScreen(viewModel = panelVM) }
                composable(AppRoutes.SIMULADOR) { SimuladorScreen(viewModel = simuladorVM) }
                composable(AppRoutes.CONTACTOS) { ContactosScreen() }
                composable(AppRoutes.AJUSTES) { AjustesScreen(viewModel = ajustesVM) }
                composable(AppRoutes.PERFIL) {
                    PerfilScreen(usuario = usuario, onCerrarSesion = sesionVM::cerrarSesion)
                }
            }
        }
    }
}
