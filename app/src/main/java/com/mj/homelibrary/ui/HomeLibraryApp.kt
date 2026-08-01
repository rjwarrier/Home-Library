package com.mj.homelibrary.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AssignmentReturn
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Outbound
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.RunningWithErrors
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mj.homelibrary.R
import com.mj.homelibrary.data.AppFontFamily
import com.mj.homelibrary.data.AppearanceSettings
import com.mj.homelibrary.data.BackgroundTintLevel
import com.mj.homelibrary.data.BookFormatCode
import com.mj.homelibrary.data.BookSortCode
import com.mj.homelibrary.data.ColorSource
import com.mj.homelibrary.data.FabPlacement
import com.mj.homelibrary.data.FontScalePreference
import com.mj.homelibrary.data.LanguageCode
import com.mj.homelibrary.data.LibrarySettings
import com.mj.homelibrary.data.ReadStatusCode
import com.mj.homelibrary.data.ThemeColorIntensity
import com.mj.homelibrary.data.ThemePreference
import com.mj.homelibrary.data.entity.BookEntity
import com.mj.homelibrary.data.entity.LoanEntity
import com.mj.homelibrary.data.entity.LocationEntity
import com.mj.homelibrary.data.validIsbnOrNull
import java.time.Year
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.roundToInt

private val GridMinCellSize = 148.dp
private val ScreenMaxWidth = 600.dp

private enum class HomeTab(@StringRes val labelRes: Int, @StringRes val titleRes: Int, val icon: ImageVector) {
    Library(R.string.nav_library, R.string.screen_library, Icons.Outlined.Book),
    Shelves(R.string.nav_shelves, R.string.screen_shelves, Icons.Outlined.Place),
    Loans(R.string.nav_loans, R.string.screen_loans, Icons.Outlined.People),
    Stats(R.string.nav_stats, R.string.screen_stats, Icons.Outlined.BarChart),
    Settings(R.string.nav_settings, R.string.screen_settings, Icons.Outlined.Settings),
}

private enum class SettingsRoute {
    Main,
    Appearance,
    LibraryPreferences,
    DataRecovery,
    PrivacyData,
    HelpAbout,
}

private fun BookEntity.matchesIsbn(isbn: String): Boolean {
    val normalized = isbn.validIsbnOrNull() ?: return false
    return isbn10 == normalized || isbn13 == normalized
}

private fun BookEntity.toListItem(state: HomeLibraryUiState): BookListItem =
    BookListItem(
        book = this,
        location = state.locations.firstOrNull { it.id == locationId },
        activeLoan = state.activeLoans.firstOrNull { it.bookId == id },
    )

private fun List<String>.distinctSorted(): List<String> =
    map { it.trim() }
        .filter(String::isNotBlank)
        .distinctBy { it.lowercase() }
        .sortedBy { it.lowercase() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLibraryApp(startInScanMode: Boolean = false, viewModel: HomeLibraryViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appearanceSettings by viewModel.appearanceSettings.collectAsStateWithLifecycle()
    val librarySettings by viewModel.librarySettings.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(HomeTab.Library) }
    var settingsRoute by remember { mutableStateOf(SettingsRoute.Main) }
    var fabExpanded by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    var showAddBook by remember { mutableStateOf(false) }
    var manualEntry by remember { mutableStateOf(false) }
    var scannedIsbn by remember { mutableStateOf("") }
    var enrichExistingBookId by remember { mutableStateOf<Long?>(null) }
    var selectedBook by remember { mutableStateOf<BookListItem?>(null) }
    var editBook by remember { mutableStateOf<BookListItem?>(null) }
    var moveBook by remember { mutableStateOf<BookListItem?>(null) }
    var deleteBook by remember { mutableStateOf<BookEntity?>(null) }
    var auditShelf by remember { mutableStateOf<LocationEntity?>(null) }
    var showAddShelf by remember { mutableStateOf(false) }
    var showNewLoanPicker by remember { mutableStateOf(false) }
    var loanBook by remember { mutableStateOf<BookEntity?>(null) }
    var returnLoan by remember { mutableStateOf<LoanEntity?>(null) }
    var bulkQueue by remember { mutableStateOf<List<String>>(emptyList()) }
    var bulkIndex by remember { mutableStateOf(0) }
    val haptics = LocalHapticFeedback.current

    fun openBookForIsbn(isbn: String) {
        val existingBook = state.allBooks.firstOrNull { it.matchesIsbn(isbn) }
        if (existingBook != null) {
            editBook = existingBook.toListItem(state)
            enrichExistingBookId = existingBook.id
            selectedBook = null
            showAddBook = false
        } else {
            scannedIsbn = isbn
            manualEntry = false
            showAddBook = true
        }
    }

    fun advanceBulkQueue() {
        if (bulkQueue.isEmpty()) return
        val nextIndex = bulkIndex + 1
        if (nextIndex >= bulkQueue.size) {
            bulkQueue = emptyList()
            bulkIndex = 0
        } else {
            bulkIndex = nextIndex
            openBookForIsbn(bulkQueue[nextIndex])
        }
    }

    LaunchedEffect(startInScanMode) {
        if (startInScanMode) showScanner = true
    }

    BackHandler(enabled = settingsRoute != SettingsRoute.Main) {
        settingsRoute = SettingsRoute.Main
    }
    BackHandler(enabled = settingsRoute == SettingsRoute.Main && fabExpanded) {
        fabExpanded = false
    }

    state.transient.errorRes?.let { errorRes ->
        val failedIsbn = state.transient.failedLookupIsbn
        if (failedIsbn != null && errorRes == R.string.isbn_lookup_failed) {
            LookupFailedDialog(
                isbn = failedIsbn,
                onDismiss = viewModel::clearError,
            )
        } else {
            AlertDialog(
                onDismissRequest = viewModel::clearError,
                confirmButton = {
                    TextButton(onClick = viewModel::clearError) {
                        Text(stringResource(R.string.action_clear))
                    }
                },
                text = { Text(stringResource(errorRes)) },
            )
        }
    }

    state.transient.statusRes?.let { statusRes ->
        val message = stringResource(statusRes)
        LaunchedEffect(statusRes) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearStatus()
        }
    }

    Scaffold(
        bottomBar = {
            GranthapuraNavigationBar(selectedTab = selectedTab, onSelect = {
                selectedTab = it
                if (it != HomeTab.Settings) settingsRoute = SettingsRoute.Main
                fabExpanded = false
            })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButtonPosition = if (appearanceSettings.fabPlacement == FabPlacement.LEFT) FabPosition.Start else FabPosition.End,
        floatingActionButton = {
            AnimatedVisibility(
                visible = selectedTab in listOf(HomeTab.Library, HomeTab.Shelves, HomeTab.Loans),
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
            ) {
                when (selectedTab) {
                    HomeTab.Library -> GranthapuraFabMenu(
                        expanded = fabExpanded,
                        placement = appearanceSettings.fabPlacement,
                        onToggle = { fabExpanded = !fabExpanded },
                        onScan = {
                            fabExpanded = false
                            showScanner = true
                        },
                        onManual = {
                            fabExpanded = false
                            manualEntry = true
                            scannedIsbn = ""
                            showAddBook = true
                        },
                    )

                    HomeTab.Shelves -> ScreenFab(
                        icon = Icons.Outlined.Add,
                        label = stringResource(R.string.action_add_shelf),
                        onClick = { showAddShelf = true },
                    )

                    HomeTab.Loans -> ScreenFab(
                        icon = Icons.Outlined.PersonAdd,
                        label = stringResource(R.string.action_new_loan),
                        onClick = { showNewLoanPicker = true },
                    )

                    else -> Unit
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentAlignment = Alignment.TopCenter,
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn(tween(220, delayMillis = 90)) togetherWith fadeOut(tween(90)) },
                label = "tabContent",
            ) { tab ->
            when (tab) {
                HomeTab.Library -> LibraryScreen(
                    state = state,
                    onQueryChange = viewModel::setQuery,
                    onSortChange = viewModel::setSort,
                    onGridModeChange = viewModel::setGridMode,
                    onLanguageChange = viewModel::setLanguage,
                    onReadStatusChange = viewModel::setReadStatus,
                    onOnLoanChange = viewModel::setOnLoanOnly,
                    onLocationChange = viewModel::setLocation,
                    onTagChange = viewModel::setTag,
                    onClearFilters = viewModel::clearFilters,
                    onBookClick = { selectedBook = it },
                    onScanFirst = { showScanner = true },
                    onSwipeAction = { item ->
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (item.isOnLoan) {
                            item.activeLoan?.let { returnLoan = it }
                        } else {
                            loanBook = item.book
                        }
                    },
                )

                HomeTab.Shelves -> ShelvesScreen(
                    state = state,
                    onBookClick = { selectedBook = it },
                    onAudit = { auditShelf = it },
                    onAddShelf = { showAddShelf = true },
                )
                HomeTab.Loans -> LoansScreen(
                    state = state,
                    onNewLoan = { showNewLoanPicker = true },
                    onReturn = { returnLoan = it },
                    onBookClick = { selectedBook = it },
                )

                HomeTab.Stats -> StatsScreen(
                    state = state,
                    readingGoal = librarySettings.readingGoal,
                )

                HomeTab.Settings -> SettingsScreen(
                    state = state,
                    route = settingsRoute,
                    appearanceSettings = appearanceSettings,
                    librarySettings = librarySettings,
                    onOpenAppearance = { settingsRoute = SettingsRoute.Appearance },
                    onOpenLibraryPreferences = { settingsRoute = SettingsRoute.LibraryPreferences },
                    onOpenDataRecovery = { settingsRoute = SettingsRoute.DataRecovery },
                    onOpenPrivacyData = { settingsRoute = SettingsRoute.PrivacyData },
                    onOpenHelpAbout = { settingsRoute = SettingsRoute.HelpAbout },
                    onBack = { settingsRoute = SettingsRoute.Main },
                    onThemeSelected = viewModel::setThemePreference,
                    onColorSourceSelected = viewModel::setColorSource,
                    onThemeColorIntensitySelected = viewModel::setThemeColorIntensity,
                    onBackgroundTintLevelSelected = viewModel::setBackgroundTintLevel,
                    onFontFamilySelected = viewModel::setAppFontFamily,
                    onFontScaleSelected = viewModel::setFontScalePreference,
                    onContentFontScaleSelected = viewModel::setContentFontScalePreference,
                    onFollowUiFontScaleChanged = viewModel::setFollowUiFontScale,
                    onFabPlacementSelected = viewModel::setFabPlacement,
                    onDefaultGridModeChange = viewModel::setGridMode,
                    onDefaultSortChange = viewModel::setSort,
                    onReadingGoalChange = viewModel::setReadingGoal,
                    onLoanRemindersEnabledChange = viewModel::setLoanRemindersEnabled,
                    onLoanReminderLeadDaysChange = viewModel::setLoanReminderLeadDays,
                    onBackupReminderDaysChange = viewModel::setBackupReminderDays,
                    onExportJson = viewModel::exportJson,
                    onExportCsv = viewModel::exportCsv,
                    onImportJson = viewModel::importJson,
                    onImportCsv = viewModel::importCsv,
                    onExportCompleteBackup = viewModel::exportCompleteBackup,
                    onImportCompleteBackup = viewModel::importCompleteBackup,
                )
            }
            }
        }
    }

    if (showScanner) {
        BarcodeScannerSheet(
            onBarcode = { isbn ->
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                openBookForIsbn(isbn)
                showScanner = false
            },
            onBulkScanned = { isbns ->
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                bulkQueue = isbns
                bulkIndex = 0
                showScanner = false
                openBookForIsbn(isbns.first())
            },
            onDismiss = { showScanner = false },
        )
    }

    if (showAddShelf) {
        AddShelfSheet(
            onDismiss = { showAddShelf = false },
            onSave = { room, unit, shelf ->
                viewModel.addShelf(room, unit, shelf) { showAddShelf = false }
            },
        )
    }

    val authorSuggestions = remember(state.allBooks) { state.allBooks.flatMap { it.authors }.distinctSorted() }
    val tagSuggestions = remember(state.allBooks) { state.allBooks.flatMap { it.tags }.distinctSorted() }
    val bulkProgress = bulkQueue.takeIf { it.isNotEmpty() }?.let { bulkIndex + 1 to it.size }

    if (showAddBook) {
        AddBookSheet(
            manualEntry = manualEntry,
            initialIsbn = scannedIsbn,
            initialDraft = null,
            autoLookup = true,
            fillOnlyEmpty = false,
            authorSuggestions = authorSuggestions,
            tagSuggestions = tagSuggestions,
            lookupInProgress = state.transient.lookupInProgress,
            bulkProgress = bulkProgress,
            onDismiss = { showAddBook = false; advanceBulkQueue() },
            onSkip = { showAddBook = false; advanceBulkQueue() },
            onLookup = viewModel::lookupIsbn,
            onSave = { draft -> viewModel.addBook(draft) { showAddBook = false; advanceBulkQueue() } },
        )
    }

    editBook?.let { item ->
        val enrichFromScan = enrichExistingBookId == item.book.id
        AddBookSheet(
            manualEntry = !enrichFromScan,
            initialIsbn = if (enrichFromScan) item.book.isbn13 ?: item.book.isbn10.orEmpty() else "",
            initialDraft = item.book.toBookDraft().copy(
                room = item.location?.room.orEmpty(),
                unit = item.location?.unit.orEmpty(),
                shelf = item.location?.shelf.orEmpty(),
            ),
            autoLookup = enrichFromScan,
            fillOnlyEmpty = true,
            authorSuggestions = authorSuggestions,
            tagSuggestions = tagSuggestions,
            lookupInProgress = state.transient.lookupInProgress,
            bulkProgress = bulkProgress.takeIf { enrichFromScan },
            onDismiss = {
                editBook = null
                enrichExistingBookId = null
                if (enrichFromScan) advanceBulkQueue()
            },
            onSkip = {
                editBook = null
                enrichExistingBookId = null
                advanceBulkQueue()
            },
            onLookup = viewModel::lookupIsbn,
            onSave = { draft ->
                viewModel.addBook(draft) {
                    editBook = null
                    enrichExistingBookId = null
                    selectedBook = null
                    if (enrichFromScan) advanceBulkQueue()
                }
            },
        )
    }

    selectedBook?.let { item ->
        BookDetailSheet(
            item = item,
            loans = state.loans.filter { it.bookId == item.book.id },
            onDismiss = { selectedBook = null },
            onLoan = { loanBook = item.book },
            onReturn = { returnLoan = it },
            onEdit = { editBook = item },
            onMove = { moveBook = item },
            onDelete = { deleteBook = item.book },
            onRate = { rating -> viewModel.updateBookRating(item.book.id, rating) },
        )
    }

    moveBook?.let { item ->
        MoveBookSheet(
            item = item,
            onDismiss = { moveBook = null },
            onSave = { room, unit, shelf, positionNote ->
                viewModel.moveBook(item.book.id, room, unit, shelf, positionNote) {
                    moveBook = null
                    selectedBook = null
                }
            },
        )
    }

    deleteBook?.let { book ->
        DeleteBookDialog(
            book = book,
            onDismiss = { deleteBook = null },
            onDelete = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.deleteBook(book) {
                    deleteBook = null
                    selectedBook = null
                }
            },
        )
    }

    auditShelf?.let { location ->
        ShelfAuditSheet(
            location = location,
            books = state.allBooks
                .filter { it.locationId == location.id }
                .map { book ->
                    BookListItem(
                        book = book,
                        location = location,
                        activeLoan = state.activeLoans.firstOrNull { it.bookId == book.id },
                    )
                },
            onDismiss = { auditShelf = null },
            onBookClick = {
                selectedBook = it
                auditShelf = null
            },
        )
    }

    if (showNewLoanPicker) {
        NewLoanPickerSheet(
            items = state.allBooks
                .map { book ->
                    BookListItem(
                        book = book,
                        location = state.locations.firstOrNull { it.id == book.locationId },
                        activeLoan = state.activeLoans.firstOrNull { it.bookId == book.id },
                    )
                }
                .filterNot { it.isOnLoan },
            onDismiss = { showNewLoanPicker = false },
            onSelect = {
                loanBook = it.book
                showNewLoanPicker = false
            },
        )
    }

    loanBook?.let { book ->
        LoanBookSheet(
            book = book,
            onDismiss = { loanBook = null },
            onSave = { draft -> viewModel.loanBook(draft) { loanBook = null } },
        )
    }

    returnLoan?.let { loan ->
        val book = state.allBooks.firstOrNull { it.id == loan.bookId }
        val item = state.visibleBooks.firstOrNull { it.book.id == loan.bookId }
        ReturnConfirmationDialog(
            loan = loan,
            book = book,
            location = item?.location,
            onDismiss = { returnLoan = null },
            onReturn = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.markReturned(loan.id)
                returnLoan = null
                selectedBook = null
            },
        )
    }
}

@Composable
private fun GranthapuraNavigationBar(selectedTab: HomeTab, onSelect: (HomeTab) -> Unit) {
    NavigationBar(
        modifier = Modifier
            .height(dimensionResource(R.dimen.bottom_nav_height)),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
    ) {
        HomeTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            val iconScale by animateFloatAsState(
                if (isSelected) 1.15f else 1f,
                animationSpec = ExpressiveSpring,
                label = "navIconScale",
            )
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelect(tab) },
                icon = { Icon(tab.icon, contentDescription = null, modifier = Modifier.scale(iconScale)) },
                label = {
                    Text(
                        text = stringResource(tab.labelRes),
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                alwaysShowLabel = true,
            )
        }
    }
}

@Composable
private fun ContentColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.space_screen))
            .width(ScreenMaxWidth),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
        content = content,
    )
}

@Composable
private fun ScreenHeader(
    @StringRes titleRes: Int,
    meta: String,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.space_lg)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = meta,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 0.72.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.space_xs)),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), content = actions)
    }
}

@Composable
private fun HeaderIconButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    IconButton(
        modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size)),
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}

@Composable
private fun LibraryScreen(
    state: HomeLibraryUiState,
    onQueryChange: (String) -> Unit,
    onSortChange: (BookSortCode) -> Unit,
    onGridModeChange: (Boolean) -> Unit,
    onLanguageChange: (String?) -> Unit,
    onReadStatusChange: (String?) -> Unit,
    onOnLoanChange: (Boolean) -> Unit,
    onLocationChange: (Long?) -> Unit,
    onTagChange: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onBookClick: (BookListItem) -> Unit,
    onScanFirst: () -> Unit,
    onSwipeAction: (BookListItem) -> Unit = {},
) {
    val languageCount = state.stats.languages.size
    ContentColumn {
        ScreenHeader(
            titleRes = R.string.screen_library,
            meta = stringResource(
                R.string.library_meta,
                state.allBooks.size,
                state.activeLoans.size,
                languageCount,
            ),
            actions = {
                HeaderIconButton(
                    icon = if (state.filters.gridMode) Icons.Outlined.ViewList else Icons.Outlined.GridView,
                    contentDescription = stringResource(R.string.content_description_switch_view),
                    onClick = { onGridModeChange(!state.filters.gridMode) },
                )
                SortMenu(selected = state.filters.sort, onSortChange = onSortChange)
            },
        )
        SearchPill(
            query = state.filters.query,
            onQueryChange = onQueryChange,
        )
        FilterStrip(
            state = state,
            onLanguageChange = onLanguageChange,
            onReadStatusChange = onReadStatusChange,
            onOnLoanChange = onOnLoanChange,
            onLocationChange = onLocationChange,
            onTagChange = onTagChange,
        )

        when {
            state.allBooks.isEmpty() -> DesignedEmptyState(
                icon = Icons.Outlined.AutoStories,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                title = stringResource(R.string.empty_library_design_title),
                body = stringResource(R.string.empty_library_design_body),
                cta = stringResource(R.string.empty_library_cta),
                onCta = onScanFirst,
            )

            state.visibleBooks.isEmpty() -> DesignedEmptyState(
                icon = Icons.Outlined.SearchOff,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                title = stringResource(R.string.empty_search_title),
                body = stringResource(R.string.empty_search_body),
                cta = stringResource(R.string.empty_search_cta),
                onCta = onClearFilters,
            )

            state.filters.gridMode -> LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Adaptive(GridMinCellSize),
                contentPadding = PaddingValues(top = dimensionResource(R.dimen.space_xs), bottom = dimensionResource(R.dimen.space_xl)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_lg)),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                items(state.visibleBooks, key = { it.book.id }) { item ->
                    BookGridCard(item = item, modifier = Modifier.animateItem(), onClick = { onBookClick(item) })
                }
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_xl)),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.visibleBooks, key = { it.book.id }) { item ->
                    BookListRow(
                        item = item,
                        modifier = Modifier.animateItem(),
                        onClick = { onBookClick(item) },
                        onSwipeAction = onSwipeAction,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchPill(query: String, onQueryChange: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.search_height)),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
        ) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(stringResource(R.string.search_hint)) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
            )
            AnimatedVisibility(visible = query.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.action_clear), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun SortMenu(selected: BookSortCode, onSortChange: (BookSortCode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        HeaderIconButton(
            icon = Icons.Outlined.Sort,
            contentDescription = stringResource(R.string.content_description_sort),
            onClick = { expanded = true },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            BookSortCode.entries.forEach { sort ->
                DropdownMenuItem(
                    text = { Text(stringResource(sort.labelRes)) },
                    onClick = {
                        onSortChange(sort)
                        expanded = false
                    },
                    leadingIcon = {
                        if (sort == selected) Icon(Icons.Outlined.Check, contentDescription = null)
                    },
                )
            }
        }
    }
}

@Composable
private fun FilterStrip(
    state: HomeLibraryUiState,
    onLanguageChange: (String?) -> Unit,
    onReadStatusChange: (String?) -> Unit,
    onOnLoanChange: (Boolean) -> Unit,
    onLocationChange: (Long?) -> Unit,
    onTagChange: (String?) -> Unit,
) {
    val context = LocalContext.current
    val tags = remember(state.allBooks) { state.allBooks.flatMap { it.tags }.distinct().sortedBy { it.lowercase() } }
    val languages = remember(state.allBooks) { state.allBooks.map { it.languageCode }.distinct().sorted() }
    var genreExpanded by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.space_xs)),
    ) {
        item {
            Box {
                MorphChip(
                    selected = state.filters.tag != null,
                    label = state.filters.tag ?: stringResource(R.string.filter_genre),
                    trailing = Icons.Outlined.ExpandMore,
                    onClick = { genreExpanded = true },
                )
                DropdownMenu(expanded = genreExpanded, onDismissRequest = { genreExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.filter_all_genres)) },
                        onClick = {
                            onTagChange(null)
                            genreExpanded = false
                        },
                    )
                    tags.forEach { tag ->
                        DropdownMenuItem(
                            text = { Text(tag) },
                            onClick = {
                                onTagChange(tag)
                                genreExpanded = false
                            },
                        )
                    }
                }
            }
        }
        if (languages.size > 1) {
            item {
                Box {
                    val selectedLanguage = state.filters.languageCode
                    MorphChip(
                        selected = selectedLanguage != null,
                        label = selectedLanguage?.let { languageLabel(context, it) } ?: stringResource(R.string.filter_language),
                        trailing = Icons.Outlined.ExpandMore,
                        onClick = { languageExpanded = true },
                    )
                    DropdownMenu(expanded = languageExpanded, onDismissRequest = { languageExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.filter_all_languages)) },
                            onClick = {
                                onLanguageChange(null)
                                languageExpanded = false
                            },
                        )
                        languages.forEach { language ->
                            DropdownMenuItem(
                                text = { Text(languageLabel(context, language)) },
                                onClick = {
                                    onLanguageChange(language)
                                    languageExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }
        item {
            MorphChip(
                selected = state.filters.readStatusCode == ReadStatusCode.Reading.code,
                label = stringResource(ReadStatusCode.Reading.labelRes),
                onClick = {
                    onReadStatusChange(
                        if (state.filters.readStatusCode == ReadStatusCode.Reading.code) null else ReadStatusCode.Reading.code,
                    )
                },
            )
        }
        item {
            MorphChip(
                selected = state.filters.onLoanOnly,
                label = stringResource(R.string.filter_on_loan),
                onClick = { onOnLoanChange(!state.filters.onLoanOnly) },
            )
        }
        item {
            Box {
                val selectedLocation = state.locations.firstOrNull { it.id == state.filters.locationId }
                MorphChip(
                    selected = selectedLocation != null,
                    label = selectedLocation?.displayBreadcrumb(context) ?: stringResource(R.string.filter_location),
                    trailing = Icons.Outlined.ExpandMore,
                    onClick = { locationExpanded = true },
                )
                DropdownMenu(expanded = locationExpanded, onDismissRequest = { locationExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.filter_all_locations)) },
                        onClick = {
                            onLocationChange(null)
                            locationExpanded = false
                        },
                    )
                    state.locations.forEach { location ->
                        DropdownMenuItem(
                            text = { Text(location.displayBreadcrumb(context)) },
                            onClick = {
                                onLocationChange(location.id)
                                locationExpanded = false
                            },
                        )
                    }
                }
            }
        }
    }
}

private val ExpressiveSpring = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)

@Composable
private fun rememberPressScale(interactionSource: InteractionSource): androidx.compose.runtime.State<Float> {
    val isPressed by interactionSource.collectIsPressedAsState()
    return animateFloatAsState(if (isPressed) 0.94f else 1f, animationSpec = ExpressiveSpring, label = "pressScale")
}

@Composable
private fun MorphChip(selected: Boolean, label: String, trailing: ImageVector? = null, onClick: () -> Unit) {
    val cornerRadius by animateDpAsState(if (selected) 12.dp else 999.dp, label = "chipMorph")
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, maxLines = 1) },
        leadingIcon = {
            AnimatedVisibility(visible = selected, enter = fadeIn() + slideInVertically { -it / 2 }, exit = fadeOut()) {
                Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(17.dp))
            }
        },
        trailingIcon = trailing?.let {
            { Icon(it, contentDescription = null, modifier = Modifier.size(17.dp)) }
        },
        shape = RoundedCornerShape(cornerRadius),
    )
}

@Composable
private fun BookGridCard(item: BookListItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressScale by rememberPressScale(interactionSource)
    Column(
        modifier = modifier
            .scale(pressScale)
            .clickable(interactionSource = interactionSource, indication = LocalIndication.current, onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box {
            BookCover(
                book = item.book,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.68f)
                    .shadow(6.dp, RoundedCornerShape(16.dp), clip = false),
                titleSize = 16,
            )
            if (item.isOnLoan) {
                LoanBadge(
                    item = item,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                )
            }
        }
        Text(
            text = item.book.title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = item.book.authors.displayAuthors(context),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookListRow(
    item: BookListItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onSwipeAction: ((BookListItem) -> Unit)? = null,
) {
    val context = LocalContext.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) onSwipeAction?.invoke(item)
            false
        },
    )
    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = onSwipeAction != null,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (item.isOnLoan) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    if (item.isOnLoan) Icons.Outlined.AssignmentReturn else Icons.Outlined.Outbound,
                    contentDescription = null,
                    tint = if (item.isOnLoan) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(if (item.isOnLoan) R.string.action_mark_returned else R.string.action_loan_book),
                    color = if (item.isOnLoan) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BookCover(
                    book = item.book,
                    modifier = Modifier
                        .width(54.dp)
                        .height(80.dp),
                    titleSize = 9,
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = item.book.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = item.book.authors.displayAuthors(context),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = item.location.displayBreadcrumb(context),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                StatusPill(item)
            }
        }
    }
}

@Composable
private fun BookCover(
    book: BookEntity,
    modifier: Modifier = Modifier,
    titleSize: Int = 16,
) {
    val palette = coverPaletteFor(book)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(palette.bg),
    ) {
        if (!book.coverImagePath.isNullOrBlank() || !book.coverUrl.isNullOrBlank()) {
            AsyncImage(
                model = book.coverImagePath ?: book.coverUrl,
                contentDescription = stringResource(R.string.content_description_book_cover),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(palette.bg.copy(alpha = 0.96f), palette.bg),
                        ),
                    ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(6.dp)
                        .background(palette.accent.copy(alpha = 0.62f)),
                )
                Box(
                    modifier = Modifier
                        .padding(start = 14.dp, top = 18.dp)
                        .width(24.dp)
                        .height(2.dp)
                        .background(palette.accent),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 14.dp, end = 12.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = book.title,
                        color = palette.fg,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = titleSize.sp,
                        lineHeight = (titleSize * 1.2f).sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = book.authors.firstOrNull().orEmpty().uppercase(),
                        color = palette.fg.copy(alpha = 0.64f),
                        fontSize = max(8, titleSize - 7).sp,
                        letterSpacing = 1.2.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoanBadge(item: BookListItem, modifier: Modifier = Modifier) {
    val bg = if (item.isOverdue) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
    val fg = if (item.isOverdue) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
    val label = if (item.isOverdue) R.string.loan_overdue else R.string.loan_on_loan
    Row(
        modifier = modifier
            .height(24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = if (item.isOverdue) Icons.Outlined.RunningWithErrors else Icons.Outlined.Outbound,
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = fg,
        )
        Text(text = stringResource(label), color = fg, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StatusPill(item: BookListItem) {
    val bg = when {
        item.isOverdue -> MaterialTheme.colorScheme.errorContainer
        item.isOnLoan -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val fg = when {
        item.isOverdue -> MaterialTheme.colorScheme.onErrorContainer
        item.isOnLoan -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val label = when {
        item.isOverdue -> R.string.loan_overdue
        item.isOnLoan -> R.string.loan_on_loan
        else -> ReadStatusCode.fromCode(item.book.readStatusCode).labelRes
    }
    Text(
        text = stringResource(label),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 9.dp, vertical = 5.dp),
        color = fg,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
    )
}

@Composable
private fun ShelvesScreen(
    state: HomeLibraryUiState,
    onBookClick: (BookListItem) -> Unit,
    onAudit: (LocationEntity) -> Unit,
    onAddShelf: () -> Unit,
) {
    val roomCount = state.locations.map { it.room }.distinct().size
    val unitCount = state.locations.map { it.room to it.unit }.distinct().size
    ContentColumn {
        ScreenHeader(
            titleRes = R.string.screen_shelves,
            meta = stringResource(R.string.shelves_meta, roomCount, unitCount, state.locations.size),
            actions = {
                OutlinedButton(
                    onClick = onAddShelf,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.action_add_shelf))
                }
            },
        )
        if (state.locations.isEmpty()) {
            DesignedEmptyState(
                icon = Icons.Outlined.Place,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                title = stringResource(R.string.empty_shelves_title),
                body = stringResource(R.string.empty_shelves_body),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_lg)),
                contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_xl)),
            ) {
                state.locations.groupBy { it.room }.forEach { (room, locations) ->
                    item(key = room) {
                        RoomSection(room = room, locations = locations, items = state.visibleBooks, onBookClick = onBookClick, onAudit = onAudit)
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomSection(
    room: String,
    locations: List<LocationEntity>,
    items: List<BookListItem>,
    onBookClick: (BookListItem) -> Unit,
    onAudit: (LocationEntity) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm))) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.Weekend, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(19.dp))
            Text(room, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            CountChip(stringResource(R.string.book_count, items.count { it.location?.room == room }))
        }
        locations.groupBy { it.unit }.forEach { (unit, unitLocations) ->
            BookcaseCard(unit = unit, locations = unitLocations, items = items, onBookClick = onBookClick, onAudit = onAudit)
        }
    }
}

@Composable
private fun BookcaseCard(
    unit: String,
    locations: List<LocationEntity>,
    items: List<BookListItem>,
    onBookClick: (BookListItem) -> Unit,
    onAudit: (LocationEntity) -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(unit, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Icon(Icons.Outlined.UnfoldMore, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            locations.forEach { location ->
                val shelfBooks = items.filter { it.location?.id == location.id }
                ShelfRow(location = location, books = shelfBooks, onBookClick = onBookClick, onAudit = onAudit)
            }
        }
    }
}

@Composable
private fun ShelfRow(
    location: LocationEntity,
    books: List<BookListItem>,
    onBookClick: (BookListItem) -> Unit,
    onAudit: (LocationEntity) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.shelf_row_label, location.shelf).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 0.8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            CountChip(stringResource(R.string.book_count, books.size), MaterialTheme.colorScheme.tertiaryContainer)
            IconButton(onClick = { onAudit(location) }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Outlined.FactCheck, contentDescription = stringResource(R.string.shelf_audit), tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
        }
        Box(modifier = Modifier.height(74.dp).fillMaxWidth()) {
            HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter), color = MaterialTheme.colorScheme.outlineVariant, thickness = 2.dp)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                books.forEach { item ->
                    Spine(item = item, onClick = { onBookClick(item) })
                }
            }
        }
    }
}

@Composable
private fun Spine(item: BookListItem, onClick: () -> Unit) {
    val palette = coverPaletteFor(item.book)
    val pages = item.book.pageCount ?: 220
    val height = when {
        pages > 450 -> 74.dp
        pages > 260 -> 68.dp
        else -> 62.dp
    }
    val width = when {
        pages > 450 -> 18.dp
        pages > 260 -> 14.dp
        else -> 10.dp
    }
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(palette.bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .width(width * 0.6f)
                .height(2.dp)
                .background(palette.accent),
        )
    }
}

@Composable
private fun LoansScreen(
    state: HomeLibraryUiState,
    onNewLoan: () -> Unit,
    onReturn: (LoanEntity) -> Unit,
    onBookClick: (BookListItem) -> Unit,
) {
    val overdueCount = state.activeLoans.count { it.expectedReturnDateEpochMillis?.let { due -> due < System.currentTimeMillis() } == true }
    val borrowers = state.activeLoans.groupBy { it.borrowerName }
    val bookById = remember(state.allBooks) { state.allBooks.associateBy { it.id } }
    val itemByBookId = remember(state.allBooks, state.locations, state.activeLoans) {
        state.allBooks.associate { it.id to it.toListItem(state) }
    }
    ContentColumn {
        ScreenHeader(
            titleRes = R.string.screen_loans,
            meta = stringResource(R.string.loans_meta, state.activeLoans.size, overdueCount, borrowers.size),
            actions = {
                Button(
                    onClick = onNewLoan,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.action_new_loan))
                }
            },
        )
        if (state.activeLoans.isEmpty()) {
            DesignedEmptyState(
                icon = Icons.Outlined.Handshake,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                title = stringResource(R.string.empty_loans_design_title),
                body = stringResource(R.string.empty_loans_design_body),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
                contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_xl)),
            ) {
                if (overdueCount > 0) {
                    item { OverdueBanner() }
                }
                borrowers.forEach { (borrower, loans) ->
                    item(key = borrower) {
                        BorrowerCard(
                            borrower = borrower,
                            loans = loans,
                            bookById = bookById,
                            itemByBookId = itemByBookId,
                            onReturn = onReturn,
                            onBookClick = onBookClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverdueBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
    ) {
        Icon(Icons.Outlined.RunningWithErrors, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
        Column {
            Text(stringResource(R.string.overdue_banner_title), color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Medium)
            Text(
                stringResource(R.string.overdue_banner_body),
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 19.sp,
            )
        }
    }
}

@Composable
private fun BorrowerCard(
    borrower: String,
    loans: List<LoanEntity>,
    bookById: Map<Long, BookEntity>,
    itemByBookId: Map<Long, BookListItem>,
    onReturn: (LoanEntity) -> Unit,
    onBookClick: (BookListItem) -> Unit,
) {
    val context = LocalContext.current
    val borrowerContact = loans.firstNotNullOfOrNull { it.borrowerContact?.takeIf(String::isNotBlank) }
    ElevatedCard(shape = RoundedCornerShape(20.dp)) {
        Column(
            modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
            ) {
                Avatar(name = borrower)
                Column(modifier = Modifier.weight(1f)) {
                    Text(borrower, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                    Text(stringResource(R.string.loan_books_out, loans.size), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (borrowerContact != null) {
                    IconButton(
                        onClick = {
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$borrowerContact")))
                            }
                        },
                    ) {
                        Icon(Icons.Outlined.Call, contentDescription = stringResource(R.string.action_call_borrower), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            loans.forEachIndexed { index, loan ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                val book = bookById[loan.bookId] ?: return@forEachIndexed
                val item = itemByBookId[book.id] ?: BookListItem(book, null, loan)
                LoanBookRow(item = item, loan = loan, onReturn = onReturn, onBookClick = onBookClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoanBookRow(
    item: BookListItem,
    loan: LoanEntity,
    onReturn: (LoanEntity) -> Unit,
    onBookClick: (BookListItem) -> Unit,
) {
    val context = LocalContext.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) onReturn(loan)
            false
        },
    )
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.AssignmentReturn, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.action_mark_returned), color = MaterialTheme.colorScheme.onSecondaryContainer, style = MaterialTheme.typography.labelLarge)
            }
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onBookClick(item) }
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BookCover(item.book, Modifier.width(38.dp).height(56.dp), titleSize = 8)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(item.book.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                DueChip(item = item, dueText = loan.expectedReturnDateEpochMillis.displayDate(context))
            }
            OutlinedButton(onClick = { onReturn(loan) }, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                Text(stringResource(R.string.action_return))
            }
        }
    }
}

@Composable
private fun DueChip(item: BookListItem, dueText: String) {
    val bg = if (item.isOverdue) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (item.isOverdue) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(bg)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(if (item.isOverdue) Icons.Outlined.RunningWithErrors else Icons.Outlined.Event, contentDescription = null, tint = fg, modifier = Modifier.size(14.dp))
        Text(dueText, color = fg, fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
private fun StatsScreen(
    state: HomeLibraryUiState,
    readingGoal: Int,
) {
    val readThisYear = state.stats.readThisYear
    val reading = state.allBooks.count { it.readStatusCode == ReadStatusCode.Reading.code }
    val unread = state.allBooks.count { it.readStatusCode == ReadStatusCode.Unread.code }

    ContentColumn {
        ScreenHeader(titleRes = R.string.screen_stats, meta = stringResource(R.string.stats_meta))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
            contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_xl)),
        ) {
            item {
                StatsHero(total = state.stats.totalBooks, onLoan = state.stats.activeLoans)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md))) {
                    StatTile(R.string.stats_finished, state.stats.finishedBooks, MaterialTheme.colorScheme.secondaryContainer, Modifier.weight(1f))
                    StatTile(R.string.stats_reading, reading, MaterialTheme.colorScheme.tertiaryContainer, Modifier.weight(1f))
                    StatTile(R.string.stats_unread, unread, MaterialTheme.colorScheme.surfaceVariant, Modifier.weight(1f))
                }
            }
            item { LanguageBarCard(state.stats.languages) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md))) {
                    GenreBarsCard(state.stats.genres, Modifier.weight(1.35f))
                    ReadingRingCard(readThisYear, readingGoal, Modifier.weight(1f))
                }
            }
            item {
                MostBorrowedCard(state.stats.mostBorrowed)
            }
        }
    }
}

@Composable
private fun StatsHero(total: Int, onLoan: Int) {
    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animateIn = true }
    val animatedTotal by animateIntAsState(if (animateIn) total else 0, animationSpec = tween(900), label = "totalBooksCount")
    val animatedOnLoan by animateIntAsState(if (animateIn) onLoan else 0, animationSpec = tween(900), label = "onLoanCount")
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column {
                Text(stringResource(R.string.stats_total_books_eyebrow), style = MaterialTheme.typography.labelSmall, letterSpacing = 1.4.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f))
                Text(animatedTotal.toString(), style = MaterialTheme.typography.displayLarge)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(stringResource(R.string.stats_on_loan), style = MaterialTheme.typography.labelMedium)
                Text(animatedOnLoan.toString(), style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

@Composable
private fun StatTile(@StringRes label: Int, value: Int, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = color)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(stringResource(label).uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value.toString(), style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
private fun LanguageBarCard(languages: Map<String, Int>) {
    ChartCard(title = R.string.stats_languages) {
        val total = languages.values.sum().coerceAtLeast(1)
        var animateIn by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { animateIn = true }
        val growth by animateFloatAsState(if (animateIn) 1f else 0f, animationSpec = tween(700), label = "languageBarGrowth")
        Row(
            modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(8.dp)),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            languages.entries.sortedByDescending { it.value }.forEachIndexed { index, entry ->
                Box(
                    modifier = Modifier
                        .weight(entry.value / total.toFloat())
                        .fillMaxHeight()
                        .scale(scaleX = growth, scaleY = 1f)
                        .background(chartColor(index)),
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        languages.entries.sortedByDescending { it.value }.forEachIndexed { index, entry ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(10.dp).clip(RoundedCornerShape(3.dp)).background(chartColor(index)))
                Text(
                    stringResource(R.string.stats_legend_item, languageLabel(LocalContext.current, entry.key), entry.value),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun GenreBarsCard(genres: Map<String, Int>, modifier: Modifier = Modifier) {
    ChartCard(title = R.string.stats_genres, modifier = modifier) {
        val entries = genres.entries.sortedByDescending { it.value }.take(4)
        val maxValue = entries.maxOfOrNull { it.value }?.coerceAtLeast(1) ?: 1
        var animateIn by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { animateIn = true }
        Row(
            modifier = Modifier.fillMaxWidth().height(126.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            entries.ifEmpty { listOf(mapOf(stringResource(R.string.not_available) to 1).entries.first()) }.forEachIndexed { index, entry ->
                val targetHeight = (96 * entry.value / maxValue).dp.coerceAtLeast(18.dp)
                val animatedHeight by animateDpAsState(
                    if (animateIn) targetHeight else 0.dp,
                    animationSpec = tween(600, delayMillis = index * 60),
                    label = "genreBarHeight",
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Bottom) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(animatedHeight)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                            .background(chartColor(index)),
                    )
                    Text(entry.key, fontSize = 9.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun ReadingRingCard(read: Int, goal: Int, modifier: Modifier = Modifier) {
    ChartCard(title = R.string.stats_read_this_year, modifier = modifier, titleArg = Year.now().value) {
        val progress = (read / goal.toFloat()).coerceIn(0f, 1f)
        var animateIn by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { animateIn = true }
        val animatedProgress by animateFloatAsState(if (animateIn) progress else 0f, animationSpec = tween(800), label = "readingRingProgress")
        val trackColor = MaterialTheme.colorScheme.surfaceVariant
        val progressColor = MaterialTheme.colorScheme.primary
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(104.dp)) {
                drawArc(
                    color = trackColor.copy(alpha = 0.72f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 14.dp.toPx()),
                )
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 14.dp.toPx()),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(read.toString(), style = MaterialTheme.typography.titleLarge)
                Text(stringResource(R.string.stats_goal_count, goal), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun MostBorrowedCard(items: List<BookBorrowStat>) {
    ChartCard(title = R.string.stats_most_borrowed) {
        if (items.isEmpty()) {
            Text(stringResource(R.string.no_borrow_history), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            items.forEachIndexed { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CountChip((index + 1).toString(), MaterialTheme.colorScheme.secondaryContainer)
                    Text(item.title, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(stringResource(R.string.borrow_count, item.borrowCount), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ChartCard(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    titleArg: Any? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(modifier = modifier, shape = RoundedCornerShape(22.dp)) {
        Column(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = if (titleArg == null) stringResource(title) else stringResource(title, titleArg),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
            )
            content()
        }
    }
}

@Composable
private fun SettingsScreen(
    state: HomeLibraryUiState,
    route: SettingsRoute,
    appearanceSettings: AppearanceSettings,
    librarySettings: LibrarySettings,
    onOpenAppearance: () -> Unit,
    onOpenLibraryPreferences: () -> Unit,
    onOpenDataRecovery: () -> Unit,
    onOpenPrivacyData: () -> Unit,
    onOpenHelpAbout: () -> Unit,
    onBack: () -> Unit,
    onThemeSelected: (ThemePreference) -> Unit,
    onColorSourceSelected: (ColorSource) -> Unit,
    onThemeColorIntensitySelected: (ThemeColorIntensity) -> Unit,
    onBackgroundTintLevelSelected: (BackgroundTintLevel) -> Unit,
    onFontFamilySelected: (AppFontFamily) -> Unit,
    onFontScaleSelected: (FontScalePreference) -> Unit,
    onContentFontScaleSelected: (FontScalePreference) -> Unit,
    onFollowUiFontScaleChanged: (Boolean) -> Unit,
    onFabPlacementSelected: (FabPlacement) -> Unit,
    onDefaultGridModeChange: (Boolean) -> Unit,
    onDefaultSortChange: (BookSortCode) -> Unit,
    onReadingGoalChange: (Int) -> Unit,
    onLoanRemindersEnabledChange: (Boolean) -> Unit,
    onLoanReminderLeadDaysChange: (Int) -> Unit,
    onBackupReminderDaysChange: (Int) -> Unit,
    onExportJson: (Uri) -> Unit,
    onExportCsv: (Uri) -> Unit,
    onImportJson: (Uri) -> Unit,
    onImportCsv: (Uri) -> Unit,
    onExportCompleteBackup: (Uri) -> Unit,
    onImportCompleteBackup: (Uri) -> Unit,
) {
    AnimatedContent(
        targetState = route,
        transitionSpec = {
            if (targetState != SettingsRoute.Main) {
                (slideInHorizontally(tween(260)) { it / 3 } + fadeIn()) togetherWith
                    (slideOutHorizontally(tween(260)) { -it / 3 } + fadeOut())
            } else {
                (slideInHorizontally(tween(260)) { -it / 3 } + fadeIn()) togetherWith
                    (slideOutHorizontally(tween(260)) { it / 3 } + fadeOut())
            }
        },
        label = "settingsRoute",
    ) { currentRoute ->
        SettingsRouteContent(
            currentRoute = currentRoute,
            state = state,
            appearanceSettings = appearanceSettings,
            librarySettings = librarySettings,
            onOpenAppearance = onOpenAppearance,
            onOpenLibraryPreferences = onOpenLibraryPreferences,
            onOpenDataRecovery = onOpenDataRecovery,
            onOpenPrivacyData = onOpenPrivacyData,
            onOpenHelpAbout = onOpenHelpAbout,
            onBack = onBack,
            onThemeSelected = onThemeSelected,
            onColorSourceSelected = onColorSourceSelected,
            onThemeColorIntensitySelected = onThemeColorIntensitySelected,
            onBackgroundTintLevelSelected = onBackgroundTintLevelSelected,
            onFontFamilySelected = onFontFamilySelected,
            onFontScaleSelected = onFontScaleSelected,
            onContentFontScaleSelected = onContentFontScaleSelected,
            onFollowUiFontScaleChanged = onFollowUiFontScaleChanged,
            onFabPlacementSelected = onFabPlacementSelected,
            onDefaultGridModeChange = onDefaultGridModeChange,
            onDefaultSortChange = onDefaultSortChange,
            onReadingGoalChange = onReadingGoalChange,
            onLoanRemindersEnabledChange = onLoanRemindersEnabledChange,
            onLoanReminderLeadDaysChange = onLoanReminderLeadDaysChange,
            onBackupReminderDaysChange = onBackupReminderDaysChange,
            onExportJson = onExportJson,
            onExportCsv = onExportCsv,
            onImportJson = onImportJson,
            onImportCsv = onImportCsv,
            onExportCompleteBackup = onExportCompleteBackup,
            onImportCompleteBackup = onImportCompleteBackup,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsRouteContent(
    currentRoute: SettingsRoute,
    state: HomeLibraryUiState,
    appearanceSettings: AppearanceSettings,
    librarySettings: LibrarySettings,
    onOpenAppearance: () -> Unit,
    onOpenLibraryPreferences: () -> Unit,
    onOpenDataRecovery: () -> Unit,
    onOpenPrivacyData: () -> Unit,
    onOpenHelpAbout: () -> Unit,
    onBack: () -> Unit,
    onThemeSelected: (ThemePreference) -> Unit,
    onColorSourceSelected: (ColorSource) -> Unit,
    onThemeColorIntensitySelected: (ThemeColorIntensity) -> Unit,
    onBackgroundTintLevelSelected: (BackgroundTintLevel) -> Unit,
    onFontFamilySelected: (AppFontFamily) -> Unit,
    onFontScaleSelected: (FontScalePreference) -> Unit,
    onContentFontScaleSelected: (FontScalePreference) -> Unit,
    onFollowUiFontScaleChanged: (Boolean) -> Unit,
    onFabPlacementSelected: (FabPlacement) -> Unit,
    onDefaultGridModeChange: (Boolean) -> Unit,
    onDefaultSortChange: (BookSortCode) -> Unit,
    onReadingGoalChange: (Int) -> Unit,
    onLoanRemindersEnabledChange: (Boolean) -> Unit,
    onLoanReminderLeadDaysChange: (Int) -> Unit,
    onBackupReminderDaysChange: (Int) -> Unit,
    onExportJson: (Uri) -> Unit,
    onExportCsv: (Uri) -> Unit,
    onImportJson: (Uri) -> Unit,
    onImportCsv: (Uri) -> Unit,
    onExportCompleteBackup: (Uri) -> Unit,
    onImportCompleteBackup: (Uri) -> Unit,
) {
    when (currentRoute) {
        SettingsRoute.Appearance -> {
            AppearanceSettingsScreen(
                settings = appearanceSettings,
                onBack = onBack,
                onThemeSelected = onThemeSelected,
                onColorSourceSelected = onColorSourceSelected,
                onThemeColorIntensitySelected = onThemeColorIntensitySelected,
                onBackgroundTintLevelSelected = onBackgroundTintLevelSelected,
                onFontFamilySelected = onFontFamilySelected,
                onFontScaleSelected = onFontScaleSelected,
                onContentFontScaleSelected = onContentFontScaleSelected,
                onFollowUiFontScaleChanged = onFollowUiFontScaleChanged,
                onFabPlacementSelected = onFabPlacementSelected,
            )
            return
        }
        SettingsRoute.LibraryPreferences -> {
            LibraryPreferencesScreen(
                settings = librarySettings,
                onBack = onBack,
                onDefaultGridModeChange = onDefaultGridModeChange,
                onDefaultSortChange = onDefaultSortChange,
                onReadingGoalChange = onReadingGoalChange,
            )
            return
        }
        SettingsRoute.DataRecovery -> {
            DataRecoverySettingsScreen(
                settings = librarySettings,
                onBack = onBack,
                onExportJson = onExportJson,
                onExportCsv = onExportCsv,
                onImportJson = onImportJson,
                onImportCsv = onImportCsv,
                onExportCompleteBackup = onExportCompleteBackup,
                onImportCompleteBackup = onImportCompleteBackup,
                onLoanRemindersEnabledChange = onLoanRemindersEnabledChange,
                onLoanReminderLeadDaysChange = onLoanReminderLeadDaysChange,
                onBackupReminderDaysChange = onBackupReminderDaysChange,
            )
            return
        }
        SettingsRoute.PrivacyData -> {
            LocalDataPrivacyScreen(onBack = onBack)
            return
        }
        SettingsRoute.HelpAbout -> {
            HelpAboutScreen(onBack = onBack)
            return
        }
        SettingsRoute.Main -> Unit
    }

    ContentColumn {
        ScreenHeader(
            titleRes = R.string.screen_settings,
            meta = stringResource(R.string.settings_meta, state.allBooks.size, state.locations.size, state.loans.size),
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
            contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_xl)),
        ) {
            item {
                AppearanceEntryCard(onOpenAppearance = onOpenAppearance)
            }
            item {
                SettingsEntryCard(
                    icon = Icons.Outlined.AutoStories,
                    title = stringResource(R.string.settings_library_preferences),
                    body = stringResource(R.string.settings_library_preferences_subtitle),
                    onClick = onOpenLibraryPreferences,
                )
            }
            item {
                SettingsEntryCard(
                    icon = Icons.Outlined.CloudDone,
                    title = stringResource(R.string.settings_data_recovery_title),
                    body = stringResource(R.string.settings_data_recovery_subtitle),
                    onClick = onOpenDataRecovery,
                )
            }
            item {
                SettingsEntryCard(
                    icon = Icons.Outlined.Security,
                    title = stringResource(R.string.settings_privacy_data_title),
                    body = stringResource(R.string.settings_privacy_data_subtitle),
                    onClick = onOpenPrivacyData,
                )
            }
            item {
                SettingsEntryCard(
                    icon = Icons.Outlined.Info,
                    title = stringResource(R.string.settings_help_about),
                    body = stringResource(R.string.settings_help_subtitle),
                    onClick = onOpenHelpAbout,
                )
            }
        }
    }
}

@Composable
private fun AppearanceEntryCard(onOpenAppearance: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressScale by rememberPressScale(interactionSource)
    ElevatedCard(
        onClick = onOpenAppearance,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
        modifier = Modifier.scale(pressScale),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.settings_appearance), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(stringResource(R.string.settings_appearance_subtitle), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 19.sp)
            }
            Icon(Icons.Outlined.ExpandMore, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.rotate(270f))
        }
    }
}

@Composable
private fun SettingsEntryCard(
    icon: ImageVector,
    title: String,
    body: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressScale by rememberPressScale(interactionSource)
    ElevatedCard(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
        modifier = Modifier.scale(pressScale),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 19.sp)
            }
            Icon(Icons.Outlined.ExpandMore, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.rotate(270f))
        }
    }
}

@Composable
private fun CompleteBackupCard(onExport: () -> Unit, onRestore: () -> Unit) {
    ElevatedCard(shape = RoundedCornerShape(22.dp)) {
        Column(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.CloudDone, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.complete_backup_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                    Text(stringResource(R.string.complete_backup_body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 19.sp)
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onExport, shape = RoundedCornerShape(14.dp)) {
                    Text(stringResource(R.string.action_export_complete_backup))
                }
                OutlinedButton(onClick = onRestore, shape = RoundedCornerShape(14.dp)) {
                    Text(stringResource(R.string.action_restore_complete_backup))
                }
            }
        }
    }
}

@Composable
private fun DesignedEmptyState(
    icon: ImageVector,
    containerColor: Color,
    title: String,
    body: String,
    cta: String? = null,
    onCta: (() -> Unit)? = null,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(horizontal = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.empty_icon_container))
                    .clip(RoundedCornerShape(34.dp))
                    .background(containerColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(52.dp), tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Text(body, style = MaterialTheme.typography.bodyMedium, lineHeight = 23.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            if (cta != null && onCta != null) {
                Spacer(Modifier.height(16.dp))
                Button(onClick = onCta, shape = RoundedCornerShape(16.dp), modifier = Modifier.height(48.dp)) {
                    Text(cta)
                }
            }
        }
    }
}

@Composable
private fun GranthapuraFabMenu(
    expanded: Boolean,
    placement: FabPlacement,
    onToggle: () -> Unit,
    onScan: () -> Unit,
    onManual: () -> Unit,
) {
    val rotation by animateFloatAsState(if (expanded) 45f else 0f, label = "fabRotation")
    val alignment = if (placement == FabPlacement.LEFT) Alignment.Start else Alignment.End
    Column(horizontalAlignment = alignment, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(spring()) + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            Column(horizontalAlignment = alignment, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StaggeredFabMenuItem(index = 0, expanded = expanded) {
                    FabMenuItem(icon = Icons.Outlined.QrCodeScanner, label = stringResource(R.string.action_scan_isbn), placement = placement, onClick = onScan)
                }
                StaggeredFabMenuItem(index = 1, expanded = expanded) {
                    FabMenuItem(icon = Icons.Outlined.EditNote, label = stringResource(R.string.action_add_manually), placement = placement, onClick = onManual)
                }
            }
        }
        val fabInteractionSource = remember { MutableInteractionSource() }
        val fabPressScale by rememberPressScale(fabInteractionSource)
        FloatingActionButton(
            onClick = onToggle,
            interactionSource = fabInteractionSource,
            modifier = Modifier
                .size(dimensionResource(R.dimen.fab_size))
                .scale(fabPressScale)
                .shadow(10.dp, RoundedCornerShape(if (expanded) 28.dp else 20.dp)),
            shape = RoundedCornerShape(if (expanded) 28.dp else 20.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.action_add_book), modifier = Modifier.rotate(rotation).size(30.dp))
        }
    }
}

@Composable
private fun ScreenFab(icon: ImageVector, label: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressScale by rememberPressScale(interactionSource)
    FloatingActionButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .size(dimensionResource(R.dimen.fab_size))
            .scale(pressScale)
            .shadow(8.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(28.dp))
    }
}

@Composable
private fun StaggeredFabMenuItem(index: Int, expanded: Boolean, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(expanded) {
        visible = if (expanded) {
            kotlinx.coroutines.delay(index * 45L)
            true
        } else {
            false
        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(180)) + scaleIn(tween(180), initialScale = 0.7f),
        exit = fadeOut(tween(90)),
    ) {
        content()
    }
}

@Composable
private fun FabMenuItem(icon: ImageVector, label: String, placement: FabPlacement, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (placement == FabPlacement.LEFT) {
            FabMenuIcon(icon = icon, label = label, onClick = onClick)
        }
        Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh, tonalElevation = 3.dp) {
            Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), style = MaterialTheme.typography.labelLarge)
        }
        if (placement == FabPlacement.RIGHT) {
            FabMenuIcon(icon = icon, label = label, onClick = onClick)
        }
    }
}

@Composable
private fun FabMenuIcon(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(52.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(17.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        tonalElevation = 4.dp,
    ) {
        Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = label) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddShelfSheet(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit,
) {
    var room by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var shelf by remember { mutableStateOf("") }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.add_shelf_title), style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.add_shelf_body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextFieldLine(room, { room = it }, R.string.field_room)
            TextFieldLine(unit, { unit = it }, R.string.field_unit)
            TextFieldLine(shelf, { shelf = it }, R.string.field_shelf)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(16.dp)) {
                    Text(stringResource(R.string.action_cancel))
                }
                Button(onClick = { onSave(room, unit, shelf) }, modifier = Modifier.weight(1.4f).height(52.dp), shape = RoundedCornerShape(16.dp)) {
                    Text(stringResource(R.string.action_add_shelf))
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddBookSheet(
    manualEntry: Boolean,
    initialIsbn: String,
    initialDraft: BookDraft?,
    autoLookup: Boolean,
    fillOnlyEmpty: Boolean,
    authorSuggestions: List<String>,
    tagSuggestions: List<String>,
    lookupInProgress: Boolean,
    bulkProgress: Pair<Int, Int>? = null,
    onDismiss: () -> Unit,
    onSkip: () -> Unit = onDismiss,
    onLookup: (String, (com.mj.homelibrary.data.remote.BookMetadata?) -> Unit) -> Unit,
    onSave: (BookDraft) -> Unit,
) {
    val editing = initialDraft != null
    var draft by remember(initialIsbn, manualEntry, initialDraft?.id) {
        mutableStateOf(
            initialDraft ?: BookDraft(
                isbn = initialIsbn,
                languageCode = if (manualEntry) LanguageCode.Malayalam.code else LanguageCode.English.code,
            ),
        )
    }
    var showScanner by remember { mutableStateOf(false) }
    var pendingCoverUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val coverPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            pendingCoverUri = uri
        }
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    fun lookupIfValid(isbn: String) {
        val validIsbn = isbn.validIsbnOrNull() ?: return
        onLookup(validIsbn) { metadata ->
            if (metadata != null) {
                draft = if (fillOnlyEmpty) {
                    draft.applyMissingMetadata(metadata, validIsbn)
                } else {
                    draft.copy(isbn = validIsbn).applyMetadata(metadata)
                }
            }
        }
    }
    LaunchedEffect(initialIsbn, autoLookup, fillOnlyEmpty) {
        if (autoLookup && !manualEntry && initialIsbn.validIsbnOrNull() != null) {
            lookupIfValid(initialIsbn)
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 760.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(if (editing) R.string.edit_book_title else R.string.add_book_title), style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = stringResource(
                            when {
                                editing -> R.string.edit_book_subtitle
                                manualEntry -> R.string.manual_entry_subtitle
                                else -> R.string.lookup_entry_subtitle
                            },
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.content_description_close))
                }
            }
            if (bulkProgress != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        stringResource(R.string.bulk_review_body, bulkProgress.first, bulkProgress.second),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    TextButton(onClick = onSkip) {
                        Text(stringResource(R.string.action_skip))
                    }
                }
            }
            if (lookupInProgress) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text(stringResource(R.string.lookup_in_progress), style = MaterialTheme.typography.bodySmall)
            }
            if (!manualEntry && draft.title.isNotBlank()) {
                MetadataLoadedCard(draft)
            }
            CoverEditor(
                draft = draft,
                onDraftChange = { draft = it },
                onPickCover = { coverPicker.launch(arrayOf("image/*")) },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                TextFieldLine(
                    value = draft.isbn,
                    onValueChange = { draft = draft.copy(isbn = it) },
                    label = R.string.field_isbn,
                    modifier = Modifier.weight(1f),
                    monospace = true,
                )
                IconButton(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    onClick = { showScanner = true },
                ) {
                    Icon(Icons.Outlined.QrCodeScanner, contentDescription = stringResource(R.string.action_scan_isbn), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            if (!manualEntry) {
                OutlinedButton(
                    enabled = draft.isbn.validIsbnOrNull() != null && !lookupInProgress,
                    onClick = { lookupIfValid(draft.isbn) },
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.Outlined.CloudDone, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.action_lookup_isbn))
                }
            }
            Text(stringResource(R.string.section_metadata).uppercase(), style = MaterialTheme.typography.labelSmall, letterSpacing = 1.2.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextFieldLine(draft.title, { draft = draft.copy(title = it) }, R.string.field_title)
            TextFieldLine(draft.originalScriptTitle, { draft = draft.copy(originalScriptTitle = it) }, R.string.field_original_title)
            SuggestedTextFieldLine(
                value = draft.authors,
                onValueChange = { draft = draft.copy(authors = it) },
                suggestions = authorSuggestions,
                label = R.string.field_authors,
            )
            TextFieldLine(draft.publisher, { draft = draft.copy(publisher = it) }, R.string.field_publisher)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextFieldLine(draft.publishedYear, { draft = draft.copy(publishedYear = it) }, R.string.field_published_year, Modifier.weight(1f))
                TextFieldLine(draft.pageCount, { draft = draft.copy(pageCount = it) }, R.string.field_pages, Modifier.weight(1f))
            }
            SuggestedTextFieldLine(
                value = draft.tags,
                onValueChange = { draft = draft.copy(tags = it) },
                suggestions = tagSuggestions,
                label = R.string.field_tags,
                commaAppend = true,
            )
            SynopsisField(draft.notes, { draft = draft.copy(notes = it) })
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LanguageCode.entries.forEach { language ->
                    MorphChip(
                        selected = draft.languageCode == language.code,
                        label = stringResource(language.labelRes),
                        onClick = { draft = draft.copy(languageCode = language.code) },
                    )
                }
            }
            RatingEditor(rating = draft.rating, onRatingChange = { draft = draft.copy(rating = it) })
            ShelfLocationField(draft = draft, onDraftChange = { draft = it })
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { onSave(draft) }, shape = RoundedCornerShape(14.dp), modifier = Modifier.height(44.dp)) {
                    Text(stringResource(R.string.action_save_book))
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
    if (showScanner) {
        BarcodeScannerSheet(
            onBarcode = {
                draft = draft.copy(isbn = it)
                lookupIfValid(it)
            },
            onDismiss = { showScanner = false },
        )
    }
    pendingCoverUri?.let { uri ->
        CropCoverSheet(
            imageUri = uri,
            onDismiss = { pendingCoverUri = null },
            onUseOriginal = {
                draft = draft.copy(coverImagePath = uri.toString(), coverUrl = "")
                pendingCoverUri = null
            },
            onUseCropped = { croppedPath ->
                draft = draft.copy(coverImagePath = croppedPath ?: uri.toString(), coverUrl = "")
                pendingCoverUri = null
            },
        )
    }
}

@Composable
private fun CoverEditor(
    draft: BookDraft,
    onDraftChange: (BookDraft) -> Unit,
    onPickCover: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
    ) {
        val previewEntity = remember(draft.id, draft.title, draft.authors, draft.languageCode, draft.coverImagePath, draft.coverUrl, draft.formatCode) {
            draft.toEntity()
        }
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BookCover(previewEntity, modifier = Modifier.width(64.dp).height(96.dp), titleSize = 8)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.section_cover), style = MaterialTheme.typography.labelSmall, letterSpacing = 1.2.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextFieldLine(
                    value = draft.coverUrl,
                    onValueChange = { onDraftChange(draft.copy(coverUrl = it, coverImagePath = "")) },
                    label = R.string.field_cover_url,
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onPickCover, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.action_choose_cover))
                    }
                    TextButton(onClick = { onDraftChange(draft.copy(coverImagePath = "", coverUrl = "")) }) {
                        Text(stringResource(R.string.action_clear_cover))
                    }
                }
            }
        }
    }
}

private val CropFrameWidth = 190.dp
private val CropFrameHeight = 285.dp
private const val CropMaxScale = 4f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CropCoverSheet(
    imageUri: Uri,
    onDismiss: () -> Unit,
    onUseOriginal: () -> Unit,
    onUseCropped: (String?) -> Unit,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    var scale by remember(imageUri) { mutableStateOf(1f) }
    var offset by remember(imageUri) { mutableStateOf(Offset.Zero) }
    val frameWidthPx = with(density) { CropFrameWidth.toPx() }
    val frameHeightPx = with(density) { CropFrameHeight.toPx() }
    val scaleAnim by animateFloatAsState(scale, label = "cropScale")
    val offsetAnim by animateOffsetAsState(offset, label = "cropOffset")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.crop_cover_title), style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.crop_cover_body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(CropFrameWidth)
                        .height(CropFrameHeight)
                        .clip(RoundedCornerShape(16.dp)),
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = stringResource(R.string.content_description_book_cover),
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scaleAnim
                                scaleY = scaleAnim
                                translationX = offsetAnim.x
                                translationY = offsetAnim.y
                            }
                            .pointerInput(imageUri) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    val newScale = (scale * zoom).coerceIn(1f, CropMaxScale)
                                    val maxOffsetX = (frameWidthPx * (newScale - 1f)) / 2f
                                    val maxOffsetY = (frameHeightPx * (newScale - 1f)) / 2f
                                    offset = Offset(
                                        (offset.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX),
                                        (offset.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY),
                                    )
                                    scale = newScale
                                }
                            },
                        contentScale = ContentScale.Crop,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                    )
                }
            }
            if (scale > 1f) {
                TextButton(
                    onClick = { scale = 1f; offset = Offset.Zero },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text(stringResource(R.string.action_reset_crop))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onUseOriginal, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(16.dp)) {
                    Text(stringResource(R.string.action_use_original))
                }
                Button(
                    onClick = {
                        onUseCropped(saveCroppedCover(context, imageUri, scale, offset, frameWidthPx, frameHeightPx))
                    },
                    modifier = Modifier.weight(1.25f).height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(stringResource(R.string.action_use_cropped))
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

private fun saveCroppedCover(
    context: Context,
    uri: Uri,
    userScale: Float,
    userOffsetPx: Offset,
    frameWidthPx: Float,
    frameHeightPx: Float,
): String? =
    runCatching {
        val source = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val imageSource = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(imageSource) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            context.contentResolver.openInputStream(uri)?.use(BitmapFactory::decodeStream)
        } ?: return@runCatching null

        val baseScale = max(frameWidthPx / source.width, frameHeightPx / source.height)
        val totalScale = baseScale * userScale
        val displayedWidth = source.width * totalScale
        val displayedHeight = source.height * totalScale

        val srcWidth = (frameWidthPx / totalScale)
        val srcHeight = (frameHeightPx / totalScale)
        val srcX = ((displayedWidth / 2f - userOffsetPx.x - frameWidthPx / 2f) / totalScale)
            .coerceIn(0f, (source.width - srcWidth).coerceAtLeast(0f))
        val srcY = ((displayedHeight / 2f - userOffsetPx.y - frameHeightPx / 2f) / totalScale)
            .coerceIn(0f, (source.height - srcHeight).coerceAtLeast(0f))

        val cropWidth = srcWidth.roundToInt().coerceIn(1, source.width - srcX.roundToInt())
        val cropHeight = srcHeight.roundToInt().coerceIn(1, source.height - srcY.roundToInt())
        val cropped = Bitmap.createBitmap(source, srcX.roundToInt(), srcY.roundToInt(), cropWidth, cropHeight)
        val directory = File(context.filesDir, "covers").also { it.mkdirs() }
        val file = File(directory, "custom-cover-${System.currentTimeMillis()}.jpg")
        file.outputStream().use { output ->
            cropped.compress(Bitmap.CompressFormat.JPEG, 92, output)
        }
        if (cropped != source) cropped.recycle()
        source.recycle()
        file.absolutePath
    }.getOrNull()

@Composable
private fun MetadataLoadedCard(draft: BookDraft) {
    val previewEntity = remember(draft.id, draft.title, draft.authors, draft.languageCode, draft.coverImagePath, draft.coverUrl, draft.formatCode) {
        draft.toEntity()
    }
    Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BookCover(previewEntity, modifier = Modifier.width(52.dp).height(78.dp), titleSize = 8)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Outlined.CloudDone, contentDescription = null, modifier = Modifier.size(14.dp))
                    Text(stringResource(R.string.metadata_loaded_label), style = MaterialTheme.typography.labelSmall, letterSpacing = 1.1.sp)
                }
                Text(draft.title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(draft.publisher, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f), maxLines = 1)
                Text(stringResource(R.string.metadata_loaded_hint), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun SynopsisField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.field_synopsis)) },
        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
        minLines = 4,
        shape = RoundedCornerShape(14.dp),
        textStyle = MaterialTheme.typography.bodyMedium,
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    )
}

@Composable
private fun ShelfLocationField(draft: BookDraft, onDraftChange: (BookDraft) -> Unit) {
    ElevatedCard(shape = RoundedCornerShape(20.dp)) {
        Column(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.Place, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                Column {
                    Text(stringResource(R.string.section_location), style = MaterialTheme.typography.titleSmall)
                    Text(stringResource(R.string.location_section_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextFieldLine(draft.room, { onDraftChange(draft.copy(room = it)) }, R.string.field_room, Modifier.weight(1f))
                TextFieldLine(draft.unit, { onDraftChange(draft.copy(unit = it)) }, R.string.field_unit, Modifier.weight(1f))
            }
            TextFieldLine(draft.shelf, { onDraftChange(draft.copy(shelf = it)) }, R.string.field_shelf)
            TextFieldLine(draft.positionNote, { onDraftChange(draft.copy(positionNote = it)) }, R.string.field_position_note)
        }
    }
}

@Composable
private fun TextFieldLine(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes label: Int,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    monospace: Boolean = false,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        shape = RoundedCornerShape(14.dp),
        textStyle = if (monospace) MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace) else MaterialTheme.typography.bodyMedium,
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    )
}

@Composable
private fun SuggestedTextFieldLine(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<String>,
    @StringRes label: Int,
    modifier: Modifier = Modifier,
    commaAppend: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    val activeToken = value.substringAfterLast(",").trim()
    val filtered = suggestions
        .filter { suggestion ->
            activeToken.isBlank() || suggestion.contains(activeToken, ignoreCase = true)
        }
        .filterNot { suggestion ->
            value.split(",").map { it.trim().lowercase() }.contains(suggestion.lowercase())
        }
        .take(6)
    Box(modifier = modifier.fillMaxWidth()) {
        TextFieldLine(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = suggestions.isNotEmpty()
            },
            label = label,
            modifier = Modifier.onFocusChanged { focusState ->
                expanded = focusState.isFocused && suggestions.isNotEmpty()
            },
        )
        DropdownMenu(expanded = expanded && filtered.isNotEmpty(), onDismissRequest = { expanded = false }) {
            filtered.forEach { suggestion ->
                DropdownMenuItem(
                    text = { Text(suggestion) },
                    onClick = {
                        onValueChange(if (commaAppend) value.withCommaSuggestion(suggestion) else suggestion)
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun String.withCommaSuggestion(suggestion: String): String {
    val parts = split(",").map { it.trim() }.filter(String::isNotBlank).toMutableList()
    if (parts.isNotEmpty()) {
        parts[parts.lastIndex] = suggestion
    } else {
        parts += suggestion
    }
    return parts.distinctBy { it.lowercase() }.joinToString(", ")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun BookDetailSheet(
    item: BookListItem,
    loans: List<LoanEntity>,
    onDismiss: () -> Unit,
    onLoan: () -> Unit,
    onReturn: (LoanEntity) -> Unit,
    onEdit: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit,
    onRate: (Float) -> Unit,
) {
    val context = LocalContext.current
    val palette = coverPaletteFor(item.book)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 820.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Brush.verticalGradient(listOf(palette.bg.copy(alpha = 0.86f), palette.bg.copy(alpha = 0.45f), MaterialTheme.colorScheme.surface))),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(
                        onClick = onDismiss,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.content_description_close))
                    }
                    Row {
                        IconButton(
                            onClick = onEdit,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        ) {
                            Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.action_edit))
                        }
                        IconButton(
                            onClick = onDelete,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.86f),
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            ),
                        ) {
                            Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.action_delete))
                        }
                    }
                }
                BookCover(
                    book = item.book,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(124.dp)
                        .height(184.dp)
                        .shadow(16.dp, RoundedCornerShape(14.dp)),
                )
            }
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(item.book.title, style = MaterialTheme.typography.headlineSmall)
                item.book.originalScriptTitle?.let {
                    Text(it, style = MaterialTheme.typography.titleMedium, lineHeight = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(item.book.authors.displayAuthors(context), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    MetadataChip(Icons.Outlined.Translate, languageLabel(context, item.book.languageCode))
                    MetadataChip(Icons.Outlined.MenuBook, stringResource(BookFormatCode.fromCode(item.book.formatCode).labelRes))
                    item.book.publishedYear?.let { MetadataChip(Icons.Outlined.CalendarToday, stringResource(R.string.published_year_label, it)) }
                    item.book.pageCount?.let { MetadataChip(Icons.Outlined.Description, stringResource(R.string.pages_label, it)) }
                    MetadataChip(Icons.Outlined.Bookmark, stringResource(ReadStatusCode.fromCode(item.book.readStatusCode).labelRes))
                }
                LocationCard(item.location.displayBreadcrumb(context), onMove = onMove)
                if (item.activeLoan != null) {
                    LoanDetailCard(item = item, loan = item.activeLoan, onReturn = onReturn)
                } else {
                    Button(onClick = onLoan, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(16.dp)) {
                        Icon(Icons.Outlined.Outbound, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.loan_this_book))
                    }
                }
                RatingRow(item.book.rating ?: 0f, onRatingChange = onRate)
                item.book.notes?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, lineHeight = 23.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(stringResource(R.string.loan_history).uppercase(), style = MaterialTheme.typography.labelSmall, letterSpacing = 1.4.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                loans.forEach { loan ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.size(7.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outlineVariant))
                        Text(loan.borrowerName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                        Text(loan.expectedReturnDateEpochMillis.displayDate(context), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataChip(icon: ImageVector, label: String) {
    AssistChip(
        onClick = {},
        label = { Text(label, fontSize = 12.sp, maxLines = 1) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(15.dp)) },
        shape = RoundedCornerShape(9.dp),
    )
}

@Composable
private fun LocationCard(breadcrumb: String, onMove: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Outlined.Place, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.shelved_at), style = MaterialTheme.typography.labelSmall, letterSpacing = 1.2.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(breadcrumb, style = MaterialTheme.typography.labelLarge)
            }
            OutlinedButton(onClick = onMove, shape = RoundedCornerShape(11.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                Text(stringResource(R.string.action_move))
            }
        }
    }
}

@Composable
private fun LoanDetailCard(item: BookListItem, loan: LoanEntity, onReturn: (LoanEntity) -> Unit) {
    val context = LocalContext.current
    val bg = if (item.isOverdue) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
    val fg = if (item.isOverdue) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
    Surface(shape = RoundedCornerShape(18.dp), color = bg, contentColor = fg) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Avatar(loan.borrowerName, size = 38.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(loan.borrowerName, fontWeight = FontWeight.Medium)
                    Text(stringResource(R.string.due_on, loan.expectedReturnDateEpochMillis.displayDate(context)), style = MaterialTheme.typography.labelSmall, color = fg.copy(alpha = 0.8f))
                }
            }
            Button(onClick = { onReturn(loan) }, modifier = Modifier.fillMaxWidth().height(42.dp), shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Outlined.AssignmentReturn, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.action_mark_returned))
            }
        }
    }
}

@Composable
private fun RatingEditor(rating: Float, onRatingChange: (Float) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(stringResource(R.string.rating_label), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        RatingRow(rating = rating, onRatingChange = onRatingChange)
    }
}

@Composable
private fun RatingRow(rating: Float, onRatingChange: ((Float) -> Unit)? = null) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(5) { index ->
            val value = (index + 1).toFloat()
            val filled = rating >= value
            val starScale by animateFloatAsState(if (filled) 1.1f else 1f, animationSpec = ExpressiveSpring, label = "starScale")
            Icon(
                Icons.Outlined.Star,
                contentDescription = null,
                tint = if (filled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .scale(starScale)
                    .then(if (onRatingChange != null) Modifier.clickable { onRatingChange(value) } else Modifier),
            )
        }
        Text(stringResource(R.string.rating_value, rating), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoanBookSheet(book: BookEntity, onDismiss: () -> Unit, onSave: (LoanDraft) -> Unit) {
    var draft by remember { mutableStateOf(LoanDraft(bookId = book.id)) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.loan_this_book), style = MaterialTheme.typography.headlineSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                BookCover(book, Modifier.width(44.dp).height(66.dp), titleSize = 8)
                Column {
                    Text(book.title, fontWeight = FontWeight.Medium)
                    Text(book.authors.displayAuthors(LocalContext.current), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            TextFieldLine(draft.borrowerName, { draft = draft.copy(borrowerName = it) }, R.string.field_borrower_name)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextFieldLine(draft.borrowerContact, { draft = draft.copy(borrowerContact = it) }, R.string.field_borrower_contact, Modifier.weight(1f))
                TextFieldLine(draft.dueDate, { draft = draft.copy(dueDate = it) }, R.string.field_due_date, Modifier.weight(1f), monospace = true)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(16.dp)) {
                    Text(stringResource(R.string.action_cancel))
                }
                Button(onClick = { onSave(draft) }, modifier = Modifier.weight(1.4f).height(52.dp), shape = RoundedCornerShape(16.dp)) {
                    Text(stringResource(R.string.action_loan_book))
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoveBookSheet(
    item: BookListItem,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String?) -> Unit,
) {
    var room by remember(item.book.id) { mutableStateOf(item.location?.room.orEmpty()) }
    var unit by remember(item.book.id) { mutableStateOf(item.location?.unit.orEmpty()) }
    var shelf by remember(item.book.id) { mutableStateOf(item.location?.shelf.orEmpty()) }
    var positionNote by remember(item.book.id) { mutableStateOf(item.book.positionNote.orEmpty()) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.move_book_title), style = MaterialTheme.typography.headlineSmall)
            Text(item.book.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextFieldLine(room, { room = it }, R.string.field_room)
            TextFieldLine(unit, { unit = it }, R.string.field_unit)
            TextFieldLine(shelf, { shelf = it }, R.string.field_shelf)
            TextFieldLine(positionNote, { positionNote = it }, R.string.field_position_note)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(16.dp)) {
                    Text(stringResource(R.string.action_cancel))
                }
                Button(onClick = { onSave(room, unit, shelf, positionNote) }, modifier = Modifier.weight(1.4f).height(52.dp), shape = RoundedCornerShape(16.dp)) {
                    Text(stringResource(R.string.action_move))
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShelfAuditSheet(
    location: LocationEntity,
    books: List<BookListItem>,
    onDismiss: () -> Unit,
    onBookClick: (BookListItem) -> Unit,
) {
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.shelf_audit), style = MaterialTheme.typography.headlineSmall)
            Text(location.displayBreadcrumb(context), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            CountChip(stringResource(R.string.audit_expected_count, books.size), MaterialTheme.colorScheme.tertiaryContainer)
            if (books.isEmpty()) {
                Text(stringResource(R.string.no_books_on_shelf), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                books.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable { onBookClick(item) }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        BookCover(item.book, Modifier.width(38.dp).height(58.dp), titleSize = 7)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.book.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(item.book.authors.displayAuthors(context), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        if (item.isOnLoan) {
                            DueChip(item = item, dueText = stringResource(R.string.loan_on_loan))
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewLoanPickerSheet(
    items: List<BookListItem>,
    onDismiss: () -> Unit,
    onSelect: (BookListItem) -> Unit,
) {
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.action_new_loan), style = MaterialTheme.typography.headlineSmall)
            if (items.isEmpty()) {
                Text(stringResource(R.string.no_available_books), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(items, key = { it.book.id }) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable { onSelect(item) }.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            BookCover(item.book, Modifier.width(40.dp).height(60.dp), titleSize = 7)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.book.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(item.book.authors.displayAuthors(context), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Icon(Icons.Outlined.Outbound, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DeleteBookDialog(
    book: BookEntity,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = { Text(stringResource(R.string.delete_book_title), style = MaterialTheme.typography.headlineSmall) },
        text = { Text(stringResource(R.string.delete_book_body, book.title)) },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
        confirmButton = {
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(stringResource(R.string.action_delete))
            }
        },
    )
}

@Composable
private fun LookupFailedDialog(
    isbn: String,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    fun openSearch(url: String) {
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }.onSuccess {
            onDismiss()
        }.onFailure { error ->
            if (error !is ActivityNotFoundException) throw error
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.SearchOff, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text(stringResource(R.string.lookup_failed_title), style = MaterialTheme.typography.headlineSmall) },
        text = { Text(stringResource(R.string.lookup_failed_body, isbn)) },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_continue_manual)) }
        },
        confirmButton = {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { openSearch("https://www.goodreads.com/search?q=$isbn") }) {
                    Text(stringResource(R.string.action_search_goodreads))
                }
                Button(onClick = { openSearch("https://www.google.com/search?q=$isbn%20book") }, shape = RoundedCornerShape(14.dp)) {
                    Text(stringResource(R.string.action_search_google))
                }
            }
        },
    )
}

@Composable
private fun ReturnConfirmationDialog(
    loan: LoanEntity,
    book: BookEntity?,
    location: LocationEntity?,
    onDismiss: () -> Unit,
    onReturn: () -> Unit,
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.AssignmentReturn, contentDescription = null)
            }
        },
        title = { Text(stringResource(R.string.mark_returned_title), style = MaterialTheme.typography.headlineSmall) },
        text = {
            Text(
                stringResource(R.string.mark_returned_body, location.displayBreadcrumb(context), loan.borrowerName),
                lineHeight = 22.sp,
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_not_yet)) }
        },
        confirmButton = {
            Button(onClick = onReturn, shape = RoundedCornerShape(14.dp)) { Text(stringResource(R.string.action_returned)) }
        },
    )
}

@Composable
private fun CountChip(text: String, color: Color = MaterialTheme.colorScheme.surfaceVariant) {
    Text(
        text = text,
        modifier = Modifier.clip(RoundedCornerShape(7.dp)).background(color).padding(horizontal = 7.dp, vertical = 3.dp),
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
    )
}

@Composable
private fun Avatar(name: String, size: Dp = 40.dp) {
    val initials = name.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").take(2)
    Box(
        modifier = Modifier.size(size).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.SemiBold)
    }
}

private data class CoverPalette(val bg: Color, val accent: Color, val fg: Color)

@Composable
private fun coverPaletteFor(book: BookEntity): CoverPalette {
    val scheme = MaterialTheme.colorScheme
    val palettes = listOf(
        CoverPalette(scheme.primaryContainer, scheme.primary, scheme.onPrimaryContainer),
        CoverPalette(scheme.secondaryContainer, scheme.secondary, scheme.onSecondaryContainer),
        CoverPalette(scheme.tertiaryContainer, scheme.tertiary, scheme.onTertiaryContainer),
        CoverPalette(scheme.surfaceVariant, scheme.primary, scheme.onSurfaceVariant),
        CoverPalette(scheme.surfaceContainerHigh, scheme.tertiary, scheme.onSurface),
        CoverPalette(scheme.primary, scheme.primaryContainer, scheme.onPrimary),
        CoverPalette(scheme.secondary, scheme.secondaryContainer, scheme.onSecondary),
        CoverPalette(scheme.tertiary, scheme.tertiaryContainer, scheme.onTertiary),
    )
    val key = (book.id.takeIf { it > 0 }?.hashCode() ?: book.title.hashCode()).absoluteValue
    return palettes[key % palettes.size]
}

@Composable
private fun chartColor(index: Int): Color = when (index % 4) {
    0 -> MaterialTheme.colorScheme.primary
    1 -> MaterialTheme.colorScheme.tertiary
    2 -> MaterialTheme.colorScheme.secondaryContainer
    else -> MaterialTheme.colorScheme.surfaceVariant
}
