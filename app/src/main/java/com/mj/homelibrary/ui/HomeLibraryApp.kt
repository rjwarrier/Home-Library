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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AssignmentReturn
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Sell
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
import androidx.compose.material.icons.outlined.Payments
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
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.mj.homelibrary.data.entity.BorrowerEntity
import com.mj.homelibrary.data.entity.LoanEntity
import com.mj.homelibrary.data.entity.LocationEntity
import com.mj.homelibrary.data.validIsbnOrNull
import com.mj.homelibrary.ui.theme.ExpressiveMotion
import com.mj.homelibrary.ui.theme.expressiveClickable
import com.mj.homelibrary.ui.theme.expressivePressScale
import com.mj.homelibrary.ui.theme.m3DialogEnterTransition
import com.mj.homelibrary.ui.theme.m3DialogExitTransition
import com.mj.homelibrary.ui.theme.m3TabTransition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    GenreManagement,
    OcrLanguages,
    DataRecovery,
    PrivacyData,
    HelpAbout,
}

private fun BookEntity.toListItem(state: HomeLibraryUiState): BookListItem =
    state.itemByBookId[id] ?: BookListItem(book = this, location = null, activeLoan = null)

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
    var cloneBook by remember { mutableStateOf<BookListItem?>(null) }
    var moveBook by remember { mutableStateOf<BookListItem?>(null) }
    var deleteBook by remember { mutableStateOf<BookEntity?>(null) }
    var auditShelf by remember { mutableStateOf<LocationEntity?>(null) }
    var showAddShelf by remember { mutableStateOf(false) }
    var showNewLoanFlow by remember { mutableStateOf(false) }
    var loanFlowBook by remember { mutableStateOf<BookEntity?>(null) }
    var showAddPerson by remember { mutableStateOf(false) }
    var returnLoan by remember { mutableStateOf<LoanEntity?>(null) }
    var bulkQueue by remember { mutableStateOf<List<String>>(emptyList()) }
    var bulkIndex by remember { mutableStateOf(0) }
    var showBulkStatusSheet by remember { mutableStateOf(false) }
    var showBulkMoveSheet by remember { mutableStateOf(false) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    fun openBookForIsbn(isbn: String) {
        val existingBook = isbn.validIsbnOrNull()?.let(state.bookByIsbn::get)
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
                enter = m3DialogEnterTransition(),
                exit = m3DialogExitTransition(),
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

                    HomeTab.Loans -> LoansFabMenu(
                        expanded = fabExpanded,
                        placement = appearanceSettings.fabPlacement,
                        onToggle = { fabExpanded = !fabExpanded },
                        onAddPerson = {
                            fabExpanded = false
                            showAddPerson = true
                        },
                        onAddLoan = {
                            fabExpanded = false
                            showNewLoanFlow = true
                        },
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
                transitionSpec = {
                    m3TabTransition(isForward = targetState.ordinal > initialState.ordinal)
                },
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
                    onMainGenreChange = viewModel::setMainGenreFilter,
                    onSubGenreChange = viewModel::setSubGenreFilter,
                    onClearFilters = viewModel::clearFilters,
                    onBookClick = { selectedBook = it },
                    onScanFirst = { showScanner = true },
                    onSwipeAction = { item ->
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (item.isOnLoan) {
                            item.activeLoan?.let { returnLoan = it }
                        } else {
                            loanFlowBook = item.book
                        }
                    },
                    selectionMode = state.transient.selectionMode,
                    selectedBookIds = state.transient.selectedBookIds,
                    onEnterSelectionMode = { viewModel.toggleSelectionMode(true) },
                    onEnterSelection = { bookId ->
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.toggleSelectBook(bookId)
                    },
                    onToggleSelectBook = viewModel::toggleSelectBook,
                    onSelectAllVisible = { viewModel.selectAllVisible(state.visibleBooks.map { it.book.id }) },
                    onClearSelection = viewModel::clearSelection,
                    onBulkSetStatus = { showBulkStatusSheet = true },
                    onBulkMove = { showBulkMoveSheet = true },
                    onBulkDelete = { showBulkDeleteDialog = true },
                )

                HomeTab.Shelves -> ShelvesScreen(
                    state = state,
                    onBookClick = { selectedBook = it },
                    onAudit = { auditShelf = it },
                    onAddShelf = { showAddShelf = true },
                )
                HomeTab.Loans -> LoansScreen(
                    state = state,
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
                    onOpenGenreManagement = { settingsRoute = SettingsRoute.GenreManagement },
                    onOpenOcrLanguages = { settingsRoute = SettingsRoute.OcrLanguages },
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
                    onPrimaryLanguageChange = viewModel::setPrimaryLanguage,
                    onAddMainGenre = viewModel::addMainGenre,
                    onRemoveMainGenre = viewModel::removeMainGenre,
                    onAddSubGenre = viewModel::addSubGenre,
                    onRemoveSubGenre = viewModel::removeSubGenre,
                    onLoanRemindersEnabledChange = viewModel::setLoanRemindersEnabled,
                    onLoanReminderLeadDaysChange = viewModel::setLoanReminderLeadDays,
                    onBackupReminderDaysChange = viewModel::setBackupReminderDays,
                    onExportJson = viewModel::exportJson,
                    onExportCsv = viewModel::exportCsv,
                    onExportHtmlCatalog = viewModel::exportHtmlCatalog,
                    onExportPdfCatalog = viewModel::exportPdfCatalog,
                    onExportCsvTemplate = viewModel::exportCsvTemplate,
                    onImportJson = viewModel::importJson,
                    onImportCsv = viewModel::importCsv,
                    onExportCompleteBackup = viewModel::exportCompleteBackup,
                    onImportCompleteBackup = viewModel::importCompleteBackup,
                )
            }
            }
            AnimatedVisibility(
                visible = fabExpanded,
                enter = fadeIn(tween(180)),
                exit = fadeOut(tween(140)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.52f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { fabExpanded = false },
                        ),
                )
            }
        }
    }

    var ocrDraftInitial by remember { mutableStateOf<BookDraft?>(null) }

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
            onOcrResult = { ocr ->
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                showScanner = false
                manualEntry = true
                scannedIsbn = ocr.rawIsbn.orEmpty()
                ocrDraftInitial = BookDraft(
                    title = ocr.title,
                    authors = ocr.authors.joinToString(", "),
                    publisher = ocr.publisher.orEmpty(),
                    publishedYear = ocr.year?.toString().orEmpty(),
                    isbn = ocr.rawIsbn.orEmpty(),
                    languageCode = LanguageCode.English.code,
                )
                showAddBook = true
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
    val seriesSuggestions = remember(state.allBooks) { state.allBooks.mapNotNull { it.seriesName }.distinctSorted() }
    val bulkProgress = bulkQueue.takeIf { it.isNotEmpty() }?.let { bulkIndex + 1 to it.size }

    if (showAddBook) {
        AddBookSheet(
            manualEntry = manualEntry,
            initialIsbn = scannedIsbn,
            initialDraft = ocrDraftInitial,
            autoLookup = true,
            fillOnlyEmpty = false,
            authorSuggestions = authorSuggestions,
            tagSuggestions = tagSuggestions,
            seriesSuggestions = seriesSuggestions,
            locations = state.locations,
            mainGenreOptions = librarySettings.mainGenres,
            subGenresByMainGenre = librarySettings.subGenresByMainGenre,
            primaryLanguage = librarySettings.primaryLanguage,
            lookupInProgress = state.transient.lookupInProgress,
            savingInProgress = state.transient.savingBookInProgress,
            bulkProgress = bulkProgress,
            onDismiss = { showAddBook = false; ocrDraftInitial = null; advanceBulkQueue() },
            onSkip = { showAddBook = false; ocrDraftInitial = null; advanceBulkQueue() },
            onLookup = viewModel::lookupIsbn,
            onSilentLookup = viewModel::lookupIsbnSilently,
            onFindCoverCandidates = viewModel::findCoverCandidates,
            onSearchByTitleAndAuthor = viewModel::searchBooksByTitleAndAuthor,
            onSave = { draft -> viewModel.addBook(draft) { showAddBook = false; ocrDraftInitial = null; advanceBulkQueue() } },
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
            seriesSuggestions = seriesSuggestions,
            locations = state.locations,
            mainGenreOptions = librarySettings.mainGenres,
            subGenresByMainGenre = librarySettings.subGenresByMainGenre,
            primaryLanguage = librarySettings.primaryLanguage,
            lookupInProgress = state.transient.lookupInProgress,
            savingInProgress = state.transient.savingBookInProgress,
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
            onSilentLookup = viewModel::lookupIsbnSilently,
            onFindCoverCandidates = viewModel::findCoverCandidates,
            onSearchByTitleAndAuthor = viewModel::searchBooksByTitleAndAuthor,
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

    cloneBook?.let { item ->
        AddBookSheet(
            manualEntry = true,
            initialIsbn = "",
            initialDraft = item.book.toBookDraft().copy(
                id = 0L,
                isbn = "",
                coverImagePath = "",
                readStatusCode = ReadStatusCode.Unread.code,
                rating = 0f,
                positionNote = "",
                purchaseDate = "",
                cost = "",
                signedCopy = false,
                room = item.location?.room.orEmpty(),
                unit = item.location?.unit.orEmpty(),
                shelf = item.location?.shelf.orEmpty(),
            ),
            autoLookup = false,
            fillOnlyEmpty = false,
            authorSuggestions = authorSuggestions,
            tagSuggestions = tagSuggestions,
            seriesSuggestions = seriesSuggestions,
            locations = state.locations,
            mainGenreOptions = librarySettings.mainGenres,
            subGenresByMainGenre = librarySettings.subGenresByMainGenre,
            primaryLanguage = librarySettings.primaryLanguage,
            lookupInProgress = state.transient.lookupInProgress,
            savingInProgress = state.transient.savingBookInProgress,
            onDismiss = { cloneBook = null },
            onSkip = { cloneBook = null },
            onLookup = viewModel::lookupIsbn,
            onSilentLookup = viewModel::lookupIsbnSilently,
            onFindCoverCandidates = viewModel::findCoverCandidates,
            onSearchByTitleAndAuthor = viewModel::searchBooksByTitleAndAuthor,
            onSave = { draft ->
                viewModel.addBook(draft) {
                    cloneBook = null
                }
            },
        )
    }

    selectedBook?.let { item ->
        BookDetailSheet(
            item = item,
            loans = state.loansByBookId[item.book.id].orEmpty(),
            onDismiss = { selectedBook = null },
            onLoan = { loanFlowBook = item.book },
            onReturn = { returnLoan = it },
            onEdit = { editBook = item },
            onClone = { cloneBook = item },
            onMove = { moveBook = item },
            onDelete = { deleteBook = item.book },
            onRate = { rating -> viewModel.updateBookRating(item.book.id, rating) },
        )
    }

    moveBook?.let { item ->
        MoveBookSheet(
            item = item,
            locations = state.locations,
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

    if (showBulkStatusSheet) {
        BulkStatusSheet(
            selectedCount = state.transient.selectedBookIds.size,
            onDismiss = { showBulkStatusSheet = false },
            onSelectStatus = { code ->
                viewModel.bulkSetReadStatus(code)
                showBulkStatusSheet = false
            },
        )
    }

    if (showBulkMoveSheet) {
        BulkMoveSheet(
            selectedCount = state.transient.selectedBookIds.size,
            locations = state.locations,
            onDismiss = { showBulkMoveSheet = false },
            onSelectLocation = { locationId ->
                viewModel.bulkSetLocation(locationId)
                showBulkMoveSheet = false
            },
        )
    }

    if (showBulkDeleteDialog) {
        BulkDeleteDialog(
            selectedCount = state.transient.selectedBookIds.size,
            onDismiss = { showBulkDeleteDialog = false },
            onDelete = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.bulkDeleteSelected()
                showBulkDeleteDialog = false
            },
        )
    }

    auditShelf?.let { location ->
        ShelfAuditSheet(
            location = location,
            books = state.itemsByLocationId[location.id].orEmpty(),
            onDismiss = { auditShelf = null },
            onBookClick = {
                selectedBook = it
                auditShelf = null
            },
        )
    }

    if (showAddPerson) {
        AddPersonSheet(
            onDismiss = { showAddPerson = false },
            onSave = { name, phone, relation ->
                viewModel.addBorrower(name, phone, relation) { showAddPerson = false }
            },
        )
    }

    if (showNewLoanFlow || loanFlowBook != null) {
        val availableBooks = remember(state.allItems) {
            state.allItems.filterNot { it.isOnLoan }
        }
        NewLoanFlowSheet(
            preselectedBook = loanFlowBook,
            availableBooks = availableBooks,
            borrowers = state.borrowers,
            onDismiss = { showNewLoanFlow = false; loanFlowBook = null },
            onAddPersonRequest = { showAddPerson = true },
            onSave = { draft ->
                viewModel.loanBook(draft) {
                    showNewLoanFlow = false
                    loanFlowBook = null
                }
            },
        )
    }

    returnLoan?.let { loan ->
        val item = state.itemByBookId[loan.bookId]
        ReturnConfirmationDialog(
            loan = loan,
            book = item?.book,
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
private fun BulkSelectionHeader(
    selectedCount: Int,
    onClose: () -> Unit,
    onSelectAll: () -> Unit,
    onSetStatus: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.space_lg)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HeaderIconButton(
                icon = Icons.Outlined.Close,
                contentDescription = stringResource(R.string.bulk_action_clear_selection),
                onClick = onClose,
            )
            Text(
                text = stringResource(R.string.bulk_selected_count, selectedCount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            HeaderIconButton(
                icon = Icons.Outlined.DoneAll,
                contentDescription = stringResource(R.string.bulk_action_select_all),
                onClick = onSelectAll,
            )
            HeaderIconButton(
                icon = Icons.Outlined.Bookmark,
                contentDescription = stringResource(R.string.bulk_action_set_status),
                onClick = onSetStatus,
            )
            HeaderIconButton(
                icon = Icons.Outlined.Place,
                contentDescription = stringResource(R.string.bulk_action_move),
                onClick = onMove,
            )
            HeaderIconButton(
                icon = Icons.Outlined.Delete,
                contentDescription = stringResource(R.string.bulk_action_delete),
                onClick = onDelete,
            )
        }
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
    onMainGenreChange: (String?) -> Unit,
    onSubGenreChange: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onBookClick: (BookListItem) -> Unit,
    onScanFirst: () -> Unit,
    onSwipeAction: (BookListItem) -> Unit = {},
    selectionMode: Boolean = false,
    selectedBookIds: Set<Long> = emptySet(),
    onEnterSelectionMode: () -> Unit = {},
    onEnterSelection: (Long) -> Unit = {},
    onToggleSelectBook: (Long) -> Unit = {},
    onSelectAllVisible: () -> Unit = {},
    onClearSelection: () -> Unit = {},
    onBulkSetStatus: () -> Unit = {},
    onBulkMove: () -> Unit = {},
    onBulkDelete: () -> Unit = {},
) {
    val languageCount = state.stats.languages.size
    ContentColumn {
        if (selectionMode) {
            BulkSelectionHeader(
                selectedCount = selectedBookIds.size,
                onClose = onClearSelection,
                onSelectAll = onSelectAllVisible,
                onSetStatus = onBulkSetStatus,
                onMove = onBulkMove,
                onDelete = onBulkDelete,
            )
        } else {
            ScreenHeader(
                titleRes = R.string.screen_library,
                meta = stringResource(
                    R.string.library_meta,
                    state.allBooks.size,
                    state.activeLoans.size,
                    languageCount,
                ),
                actions = {
                    if (state.allBooks.isNotEmpty()) {
                        HeaderIconButton(
                            icon = Icons.Outlined.CheckBoxOutlineBlank,
                            contentDescription = stringResource(R.string.content_description_enter_selection_mode),
                            onClick = onEnterSelectionMode,
                        )
                    }
                    HeaderIconButton(
                        icon = if (state.filters.gridMode) Icons.Outlined.ViewList else Icons.Outlined.GridView,
                        contentDescription = stringResource(R.string.content_description_switch_view),
                        onClick = { onGridModeChange(!state.filters.gridMode) },
                    )
                    SortMenu(selected = state.filters.sort, onSortChange = onSortChange)
                },
            )
        }
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
            onMainGenreChange = onMainGenreChange,
            onSubGenreChange = onSubGenreChange,
            onClearFilters = onClearFilters,
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
                    BookGridCard(
                        item = item,
                        modifier = Modifier.animateItem(),
                        selectionMode = selectionMode,
                        selected = item.book.id in selectedBookIds,
                        onClick = { if (selectionMode) onToggleSelectBook(item.book.id) else onBookClick(item) },
                        onLongClick = { onEnterSelection(item.book.id) },
                    )
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
                        selectionMode = selectionMode,
                        selected = item.book.id in selectedBookIds,
                        onClick = { if (selectionMode) onToggleSelectBook(item.book.id) else onBookClick(item) },
                        onLongClick = { onEnterSelection(item.book.id) },
                        onSwipeAction = if (selectionMode) null else onSwipeAction,
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
            .heightIn(min = dimensionResource(R.dimen.search_height)),
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
                IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size))) {
                    Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.action_clear), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun SortMenu(selected: BookSortCode, onSortChange: (BookSortCode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val dismissKeyboard = rememberDismissKeyboard()
    Box {
        HeaderIconButton(
            icon = Icons.Outlined.Sort,
            contentDescription = stringResource(R.string.content_description_sort),
            onClick = { dismissKeyboard(); expanded = true },
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
    onMainGenreChange: (String?) -> Unit,
    onSubGenreChange: (String?) -> Unit,
    onClearFilters: () -> Unit,
) {
    val context = LocalContext.current
    val tags = remember(state.allBooks) { state.allBooks.flatMap { it.tags }.distinct().sortedBy { it.lowercase() } }
    val mainGenres = remember(state.allBooks) { state.allBooks.mapNotNull { it.mainGenre }.distinct().sortedBy { it.lowercase() } }
    val subGenres = remember(state.allBooks) { state.allBooks.flatMap { it.subGenres }.distinct().sortedBy { it.lowercase() } }
    val languages = remember(state.allBooks) { state.allBooks.map { it.languageCode }.distinct().sorted() }
    var tagsExpanded by remember { mutableStateOf(false) }
    var mainGenreExpanded by remember { mutableStateOf(false) }
    var subGenreExpanded by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }
    val dismissKeyboard = rememberDismissKeyboard()
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.space_xs)),
    ) {
        if (state.filters.hasActiveFilters) {
            item {
                ClearFiltersChip(onClick = onClearFilters)
            }
        }
        if (mainGenres.isNotEmpty()) {
            item {
                Box {
                    MorphChip(
                        selected = state.filters.mainGenre != null,
                        label = state.filters.mainGenre ?: stringResource(R.string.filter_main_genre),
                        trailing = Icons.Outlined.ExpandMore,
                        onClick = { dismissKeyboard(); mainGenreExpanded = true },
                    )
                    DropdownMenu(expanded = mainGenreExpanded, onDismissRequest = { mainGenreExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.filter_all_main_genres)) },
                            onClick = {
                                onMainGenreChange(null)
                                mainGenreExpanded = false
                            },
                        )
                        mainGenres.forEach { genre ->
                            DropdownMenuItem(
                                text = { Text(genre) },
                                onClick = {
                                    onMainGenreChange(genre)
                                    mainGenreExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }
        if (subGenres.isNotEmpty()) {
            item {
                Box {
                    MorphChip(
                        selected = state.filters.subGenre != null,
                        label = state.filters.subGenre ?: stringResource(R.string.filter_sub_genre),
                        trailing = Icons.Outlined.ExpandMore,
                        onClick = { dismissKeyboard(); subGenreExpanded = true },
                    )
                    DropdownMenu(expanded = subGenreExpanded, onDismissRequest = { subGenreExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.filter_all_sub_genres)) },
                            onClick = {
                                onSubGenreChange(null)
                                subGenreExpanded = false
                            },
                        )
                        subGenres.forEach { genre ->
                            DropdownMenuItem(
                                text = { Text(genre) },
                                onClick = {
                                    onSubGenreChange(genre)
                                    subGenreExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }
        item {
            Box {
                MorphChip(
                    selected = state.filters.tag != null,
                    label = state.filters.tag ?: stringResource(R.string.filter_tags),
                    trailing = Icons.Outlined.ExpandMore,
                    onClick = { dismissKeyboard(); tagsExpanded = true },
                )
                DropdownMenu(expanded = tagsExpanded, onDismissRequest = { tagsExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.filter_all_tags)) },
                        onClick = {
                            onTagChange(null)
                            tagsExpanded = false
                        },
                    )
                    tags.forEach { tag ->
                        DropdownMenuItem(
                            text = { Text(tag) },
                            onClick = {
                                onTagChange(tag)
                                tagsExpanded = false
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
                        onClick = { dismissKeyboard(); languageExpanded = true },
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
                    onClick = { dismissKeyboard(); locationExpanded = true },
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
private fun rememberDismissKeyboard(): () -> Unit {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    return remember(focusManager, keyboardController) {
        {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
        }
    }
}

@Composable
private fun rememberPressScale(interactionSource: InteractionSource): Float {
    val isPressed by interactionSource.collectIsPressedAsState()
    return expressivePressScale(isPressed, pressedScale = 0.94f)
}

@Composable
private fun ClearFiltersChip(onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(stringResource(R.string.action_clear_filters), maxLines = 1) },
        leadingIcon = { Icon(Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(17.dp)) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            labelColor = MaterialTheme.colorScheme.onErrorContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onErrorContainer,
        ),
        border = null,
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_control)),
    )
}

@Composable
private fun MorphChip(selected: Boolean, label: String, trailing: ImageVector? = null, onClick: () -> Unit) {
    val cornerRadius by animateDpAsState(
        if (selected) dimensionResource(R.dimen.corner_control) else 999.dp,
        animationSpec = ExpressiveMotion.MorphDpSpring,
        label = "chipMorph",
    )
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
private fun SelectionMarker(selected: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .border(
                width = 1.5.dp,
                color = if (selected) Color.Transparent else MaterialTheme.colorScheme.outline,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(
                Icons.Outlined.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookGridCard(
    item: BookListItem,
    modifier: Modifier = Modifier,
    selectionMode: Boolean = false,
    selected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressScale = rememberPressScale(interactionSource)
    Column(
        modifier = modifier
            .scale(pressScale)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick,
                onLongClick = onLongClick,
                onLongClickLabel = stringResource(R.string.content_description_enter_selection),
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box {
            BookCover(
                book = item.book,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.68f)
                    .shadow(6.dp, RoundedCornerShape(dimensionResource(R.dimen.corner_card)), clip = false),
                titleSize = 16,
            )
            if (item.isOnLoan && !selectionMode) {
                LoanBadge(
                    item = item,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                )
            }
            if (selectionMode) {
                SelectionMarker(
                    selected = selected,
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
        item.book.originalScriptTitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = item.book.authors.displayAuthors(context),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun BookListRow(
    item: BookListItem,
    modifier: Modifier = Modifier,
    selectionMode: Boolean = false,
    selected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
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
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_lg)))
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
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                    onLongClickLabel = stringResource(R.string.content_description_enter_selection),
                ),
            shape = RoundedCornerShape(dimensionResource(R.dimen.corner_lg)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (selectionMode) {
                    SelectionMarker(selected = selected)
                }
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
                    item.book.originalScriptTitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
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
            .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_card)))
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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = titleSize.sp,
                        lineHeight = (titleSize * 1.2f).sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = book.authors.firstOrNull().orEmpty().uppercase(),
                        color = palette.fg.copy(alpha = 0.64f),
                        fontSize = max(8, titleSize - 7).sp,
                        fontWeight = FontWeight.Medium,
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
            .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_sm)))
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
        Text(text = stringResource(label), color = fg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
            .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_sm)))
            .background(bg)
            .padding(horizontal = 9.dp, vertical = 5.dp),
        color = fg,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
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
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_control)),
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
                        RoomSection(
                            room = room,
                            locations = locations,
                            itemsByLocationId = state.visibleItemsByLocationId,
                            onBookClick = onBookClick,
                            onAudit = onAudit,
                            modifier = Modifier.animateItem(),
                        )
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
    itemsByLocationId: Map<Long, List<BookListItem>>,
    onBookClick: (BookListItem) -> Unit,
    onAudit: (LocationEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.Weekend, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(19.dp))
            Text(room, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            CountChip(stringResource(R.string.book_count, locations.sumOf { itemsByLocationId[it.id].orEmpty().size }))
        }
        locations.groupBy { it.unit }.forEach { (unit, unitLocations) ->
            BookcaseCard(
                unit = unit,
                locations = unitLocations,
                itemsByLocationId = itemsByLocationId,
                onBookClick = onBookClick,
                onAudit = onAudit,
            )
        }
    }
}

@Composable
private fun BookcaseCard(
    unit: String,
    locations: List<LocationEntity>,
    itemsByLocationId: Map<Long, List<BookListItem>>,
    onBookClick: (BookListItem) -> Unit,
    onAudit: (LocationEntity) -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_prominent)),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(dimensionResource(R.dimen.corner_prominent)))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(unit, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Icon(Icons.Outlined.UnfoldMore, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            locations.forEach { location ->
                val shelfBooks = itemsByLocationId[location.id].orEmpty()
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
            IconButton(onClick = { onAudit(location) }, modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size))) {
                Icon(Icons.Outlined.FactCheck, contentDescription = stringResource(R.string.shelf_audit), tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
        }
        Box(modifier = Modifier.height(74.dp).fillMaxWidth()) {
            HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter), color = MaterialTheme.colorScheme.outlineVariant, thickness = 2.dp)
            LazyRow(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                items(books, key = { it.book.id }) { item ->
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
            .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_xs)))
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
    onReturn: (LoanEntity) -> Unit,
    onBookClick: (BookListItem) -> Unit,
) {
    val overdueCount = state.activeLoans.count { it.expectedReturnDateEpochMillis?.let { due -> due < System.currentTimeMillis() } == true }
    val borrowers = state.activeLoans.groupBy { it.borrowerName }
    val bookById = remember(state.allBooks) { state.allBooks.associateBy { it.id } }
    val itemByBookId = state.itemByBookId
    ContentColumn {
        ScreenHeader(
            titleRes = R.string.screen_loans,
            meta = stringResource(R.string.loans_meta, state.activeLoans.size, overdueCount, borrowers.size),
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
                    item(key = "overdue_banner") { OverdueBanner(modifier = Modifier.animateItem()) }
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
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverdueBanner(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_prominent)))
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
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val borrowerContact = loans.firstNotNullOfOrNull { it.borrowerContact?.takeIf(String::isNotBlank) }
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_prominent)),
    ) {
        Column(
            modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(dimensionResource(R.dimen.corner_prominent))),
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
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_md)))
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
            OutlinedButton(onClick = { onReturn(loan) }, shape = RoundedCornerShape(dimensionResource(R.dimen.corner_control)), contentPadding = PaddingValues(horizontal = 12.dp)) {
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
            .clip(CircleShape)
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
    ContentColumn {
        ScreenHeader(titleRes = R.string.screen_stats, meta = stringResource(R.string.stats_meta))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_2xl)),
        ) {
            item {
                AnalyticsOverviewGrid(stats = state.stats)
            }
            item {
                ReadingRingCard(read = state.stats.readThisYear, goal = readingGoal)
            }
            item {
                ReadingStatusSegmentedCard(stats = state.stats)
            }
            if (state.stats.genres.isNotEmpty()) {
                item {
                    GenreBarsCard(genres = state.stats.genres)
                }
            }
            if (state.stats.topAuthors.isNotEmpty()) {
                item {
                    TopAuthorsLeaderboardCard(authors = state.stats.topAuthors)
                }
            }
            if (state.stats.formatsBreakdown.isNotEmpty()) {
                item {
                    FormatDistributionCard(formats = state.stats.formatsBreakdown)
                }
            }
            if (state.stats.decadesBreakdown.isNotEmpty()) {
                item {
                    PublicationDecadesCard(decades = state.stats.decadesBreakdown)
                }
            }
            if (state.stats.languages.isNotEmpty()) {
                item { LanguageBarCard(languages = state.stats.languages) }
            }
            item { MostBorrowedCard(items = state.stats.mostBorrowed) }
        }
    }
}

/**
 * True once this call site has been composed for at least one frame.
 */
@Composable
private fun rememberEntryAnimationTrigger(): Boolean {
    var triggered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { triggered = true }
    return triggered
}

@Composable
private fun AnalyticsOverviewGrid(stats: LibraryStats) {
    val animateIn = rememberEntryAnimationTrigger()
    val animatedTotal by animateIntAsState(
        if (animateIn) stats.totalBooks else 0,
        animationSpec = tween(750, easing = ExpressiveMotion.EmphasizedDecelerate),
        label = "totalBooksCount"
    )
    val animatedPages by animateIntAsState(
        if (animateIn) stats.totalPages else 0,
        animationSpec = tween(750, easing = ExpressiveMotion.EmphasizedDecelerate),
        label = "totalPagesCount"
    )

    val total = stats.totalBooks.coerceAtLeast(1)
    val finishedPct = (stats.finishedBooks * 100) / total
    val readingPct = (stats.readingBooks * 100) / total
    val unreadPct = (stats.unreadBooks * 100) / total

    val subtitle = when {
        stats.genres.isNotEmpty() -> stringResource(R.string.stats_hero_subtitle_genres, stats.totalBooks, stats.genres.size)
        stats.topAuthors.isNotEmpty() -> stringResource(R.string.stats_hero_subtitle_authors, stats.totalBooks, stats.topAuthors.size)
        else -> stringResource(R.string.stats_hero_subtitle_single, stats.totalBooks)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Informative Collection Pulse Hero Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Header row: Eyebrow + dynamic status badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(28.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.AutoStories,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.stats_total_books_eyebrow),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        )
                    }

                    // Dynamic status badges (active loans / signed copies)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (stats.activeLoans > 0) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                ) {
                                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiary))
                                    Text(
                                        text = stringResource(R.string.stats_active_loans_badge, stats.activeLoans),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                        if (stats.signedCount > 0) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Icon(
                                        Icons.Outlined.Draw,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp),
                                    )
                                    Text(
                                        text = stringResource(R.string.stats_signed_editions, stats.signedCount),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                    }
                }

                // Main body: Split Primary Metric + Informative Reading Snapshot Panel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    // Left Column: Big bold figure & dynamic context
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = animatedTotal.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.80f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    // Right Column: Reading Pulse Snapshot Panel
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                        modifier = Modifier.weight(1.15f),
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(R.string.stats_hero_reading_pulse),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = "$finishedPct% read",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }

                            // Inline Micro Segmented Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                                horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                            ) {
                                if (stats.finishedBooks > 0) {
                                    Box(modifier = Modifier.weight(stats.finishedBooks / total.toFloat()).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
                                }
                                if (stats.readingBooks > 0) {
                                    Box(modifier = Modifier.weight(stats.readingBooks / total.toFloat()).fillMaxHeight().background(MaterialTheme.colorScheme.tertiary))
                                }
                                if (stats.unreadBooks > 0) {
                                    Box(modifier = Modifier.weight(stats.unreadBooks / total.toFloat()).fillMaxHeight().background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)))
                                }
                                if (stats.abandonedBooks > 0) {
                                    Box(modifier = Modifier.weight(stats.abandonedBooks / total.toFloat()).fillMaxHeight().background(MaterialTheme.colorScheme.error))
                                }
                            }

                            // 3 Compact status indicators
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                MiniPulseMetric(
                                    label = stringResource(ReadStatusCode.Finished.labelRes),
                                    count = stats.finishedBooks,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                MiniPulseMetric(
                                    label = stringResource(ReadStatusCode.Reading.labelRes),
                                    count = stats.readingBooks,
                                    color = MaterialTheme.colorScheme.tertiary,
                                )
                                MiniPulseMetric(
                                    label = stringResource(ReadStatusCode.Unread.labelRes),
                                    count = stats.unreadBooks,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2x2 Balanced KPI Tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MetricTile(
                icon = Icons.Outlined.Description,
                label = stringResource(R.string.stats_total_pages),
                value = if (animatedPages > 0) String.format("%,d", animatedPages) else "—",
                badgeBg = MaterialTheme.colorScheme.tertiaryContainer,
                badgeFg = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f),
            )
            MetricTile(
                icon = Icons.Outlined.Payments,
                label = stringResource(R.string.stats_library_value),
                value = if (stats.totalLibraryValue > 0.0) stats.totalLibraryValue.displayCost() else "—",
                badgeBg = MaterialTheme.colorScheme.secondaryContainer,
                badgeFg = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MetricTile(
                icon = Icons.Outlined.Star,
                label = stringResource(R.string.stats_avg_rating),
                value = if (stats.averageRating > 0f) stringResource(R.string.stats_avg_rating_value, stats.averageRating) else "—",
                badgeBg = MaterialTheme.colorScheme.primaryContainer,
                badgeFg = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f),
            )
            MetricTile(
                icon = Icons.Outlined.Handshake,
                label = stringResource(R.string.stats_on_loan),
                value = stats.activeLoans.toString(),
                badgeBg = MaterialTheme.colorScheme.surfaceContainerHigh,
                badgeFg = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MiniPulseMetric(
    label: String,
    count: Int,
    color: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun MetricTile(
    icon: ImageVector,
    label: String,
    value: String,
    badgeBg: Color,
    badgeFg: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = badgeBg,
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = badgeFg, modifier = Modifier.size(20.dp))
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun ReadingStatusSegmentedCard(stats: LibraryStats) {
    val total = stats.totalBooks.coerceAtLeast(1)
    val finishedPct = (stats.finishedBooks * 100) / total
    val readingPct = (stats.readingBooks * 100) / total
    val unreadPct = (stats.unreadBooks * 100) / total
    val abandonedPct = (stats.abandonedBooks * 100) / total

    ChartCard(
        title = R.string.stats_reading_status_breakdown,
        icon = Icons.Outlined.Bookmark,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Visual Segmented Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (stats.finishedBooks > 0) {
                    Box(modifier = Modifier.weight(stats.finishedBooks / total.toFloat()).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
                }
                if (stats.readingBooks > 0) {
                    Box(modifier = Modifier.weight(stats.readingBooks / total.toFloat()).fillMaxHeight().background(MaterialTheme.colorScheme.tertiary))
                }
                if (stats.unreadBooks > 0) {
                    Box(modifier = Modifier.weight(stats.unreadBooks / total.toFloat()).fillMaxHeight().background(MaterialTheme.colorScheme.secondary))
                }
                if (stats.abandonedBooks > 0) {
                    Box(modifier = Modifier.weight(stats.abandonedBooks / total.toFloat()).fillMaxHeight().background(MaterialTheme.colorScheme.error))
                }
            }

            // Clean 2x2 Status Legend
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusLegendItem(
                        color = MaterialTheme.colorScheme.primary,
                        label = stringResource(ReadStatusCode.Finished.labelRes),
                        count = stats.finishedBooks,
                        pct = finishedPct,
                        modifier = Modifier.weight(1f),
                    )
                    StatusLegendItem(
                        color = MaterialTheme.colorScheme.tertiary,
                        label = stringResource(ReadStatusCode.Reading.labelRes),
                        count = stats.readingBooks,
                        pct = readingPct,
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusLegendItem(
                        color = MaterialTheme.colorScheme.secondary,
                        label = stringResource(ReadStatusCode.Unread.labelRes),
                        count = stats.unreadBooks,
                        pct = unreadPct,
                        modifier = Modifier.weight(1f),
                    )
                    if (stats.abandonedBooks > 0) {
                        StatusLegendItem(
                            color = MaterialTheme.colorScheme.error,
                            label = stringResource(ReadStatusCode.Abandoned.labelRes),
                            count = stats.abandonedBooks,
                            pct = abandonedPct,
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusLegendItem(
    color: Color,
    label: String,
    count: Int,
    pct: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
                Text(
                    text = "$count ($pct%)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun TopAuthorsLeaderboardCard(authors: Map<String, Int>) {
    ChartCard(
        title = R.string.stats_most_collected_authors,
        icon = Icons.Outlined.Draw,
    ) {
        val maxCount = authors.values.maxOrNull()?.coerceAtLeast(1) ?: 1
        val animateIn = rememberEntryAnimationTrigger()
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            authors.entries.take(5).forEachIndexed { index, (author, count) ->
                val progress = count / maxCount.toFloat()
                val animatedProgress by animateFloatAsState(
                    if (animateIn) progress else 0f,
                    animationSpec = ExpressiveMotion.SoftSpring,
                    label = "authorProgress_$index",
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = when (index) {
                            0 -> MaterialTheme.colorScheme.primaryContainer
                            1 -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surfaceContainerHigh
                        },
                        modifier = Modifier.size(28.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = when (index) {
                                    0 -> MaterialTheme.colorScheme.onPrimaryContainer
                                    1 -> MaterialTheme.colorScheme.onSecondaryContainer
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = author,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                            Text(
                                text = stringResource(R.string.stats_books_count_format, count),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedProgress)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(chartColor(index)),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormatDistributionCard(formats: Map<String, Int>) {
    ChartCard(
        title = R.string.stats_bindings_formats,
        icon = Icons.Outlined.Book,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            formats.forEach { (code, count) ->
                val label = BookFormatCode.fromCode(code).labelRes
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(label),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                        ) {
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PublicationDecadesCard(decades: Map<String, Int>) {
    val sorted = decades.entries.sortedBy { it.key }
    val maxCount = sorted.maxOfOrNull { it.value }?.coerceAtLeast(1) ?: 1
    val animateIn = rememberEntryAnimationTrigger()

    ChartCard(
        title = R.string.stats_publication_timeline,
        icon = Icons.Outlined.CalendarToday,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                sorted.forEachIndexed { index, entry ->
                    val targetHeight = (72 * entry.value / maxCount).dp.coerceAtLeast(14.dp)
                    val animatedHeight by animateDpAsState(
                        if (animateIn) targetHeight else 0.dp,
                        animationSpec = ExpressiveMotion.MorphDpSpring,
                        label = "decadeBarHeight_$index",
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        Text(
                            text = entry.value.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(animatedHeight)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = entry.key,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageBarCard(languages: Map<String, Int>) {
    val total = languages.values.sum().coerceAtLeast(1)
    val animateIn = rememberEntryAnimationTrigger()
    val growth by animateFloatAsState(
        if (animateIn) 1f else 0f,
        animationSpec = ExpressiveMotion.SoftSpring,
        label = "languageBarGrowth"
    )

    ChartCard(
        title = R.string.stats_languages,
        icon = Icons.Outlined.Translate,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
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

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                languages.entries.sortedByDescending { it.value }.forEachIndexed { index, entry ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(chartColor(index)))
                            Text(
                                text = languageLabel(LocalContext.current, entry.key),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "(${entry.value})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GenreBarsCard(genres: Map<String, Int>, modifier: Modifier = Modifier) {
    ChartCard(
        title = R.string.stats_genres,
        icon = Icons.Outlined.Category,
        modifier = modifier,
    ) {
        val entries = genres.entries.sortedByDescending { it.value }.take(5)
        val maxValue = entries.maxOfOrNull { it.value }?.coerceAtLeast(1) ?: 1
        val animateIn = rememberEntryAnimationTrigger()

        if (entries.isEmpty()) {
            Text(
                stringResource(R.string.not_available),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                entries.forEachIndexed { index, entry ->
                    val progress = entry.value / maxValue.toFloat()
                    val animatedProgress by animateFloatAsState(
                        if (animateIn) progress else 0f,
                        animationSpec = ExpressiveMotion.SoftSpring,
                        label = "genreProgress_$index",
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = entry.key,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                            Text(
                                text = stringResource(R.string.stats_books_count_format, entry.value),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedProgress)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(chartColor(index)),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingRingCard(read: Int, goal: Int, modifier: Modifier = Modifier) {
    ChartCard(
        title = R.string.stats_read_this_year,
        icon = Icons.Outlined.EmojiEvents,
        titleArg = java.time.Year.now().value,
        modifier = modifier,
    ) {
        val targetProgress = (read / goal.toFloat()).coerceIn(0f, 1f)
        val animateIn = rememberEntryAnimationTrigger()
        val animatedProgress by animateFloatAsState(
            if (animateIn) targetProgress else 0f,
            animationSpec = ExpressiveMotion.SoftSpring,
            label = "readingRingProgress"
        )
        val trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        val progressColor = MaterialTheme.colorScheme.primary
        val percentage = (targetProgress * 100).toInt()

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                    val strokeWidth = 12.dp.toPx()
                    drawArc(
                        color = trackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                    )
                    if (animatedProgress > 0f) {
                        drawArc(
                            color = progressColor,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(R.string.stats_goal_count, goal),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = "$read of $goal books completed",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
                val remaining = (goal - read).coerceAtLeast(0)
                Text(
                    text = if (remaining == 0) "Goal achieved for this year!" else "$remaining more to reach your annual goal",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun MostBorrowedCard(items: List<BookBorrowStat>) {
    ChartCard(
        title = R.string.stats_most_borrowed,
        icon = Icons.Outlined.Outbound,
    ) {
        if (items.isEmpty()) {
            Text(
                stringResource(R.string.no_borrow_history),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items.forEachIndexed { index, item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (index == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.size(28.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (index == 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        ) {
                            Text(
                                text = stringResource(R.string.borrow_count, item.borrowCount),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartCard(
    @StringRes title: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    titleArg: Any? = null,
    badgeText: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.size(38.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                Text(
                    text = if (titleArg == null) stringResource(title) else stringResource(title, titleArg),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                if (badgeText != null) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }
            }
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
    onOpenGenreManagement: () -> Unit,
    onOpenOcrLanguages: () -> Unit,
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
    onPrimaryLanguageChange: (String) -> Unit,
    onAddMainGenre: (String) -> Unit,
    onRemoveMainGenre: (String) -> Unit,
    onAddSubGenre: (String, String) -> Unit,
    onRemoveSubGenre: (String, String) -> Unit,
    onLoanRemindersEnabledChange: (Boolean) -> Unit,
    onLoanReminderLeadDaysChange: (Int) -> Unit,
    onBackupReminderDaysChange: (Int) -> Unit,
    onExportJson: (Uri) -> Unit,
    onExportCsv: (Uri) -> Unit,
    onExportHtmlCatalog: (Uri) -> Unit,
    onExportPdfCatalog: (Uri) -> Unit,
    onExportCsvTemplate: (Uri) -> Unit,
    onImportJson: (Uri) -> Unit,
    onImportCsv: (Uri) -> Unit,
    onExportCompleteBackup: (Uri) -> Unit,
    onImportCompleteBackup: (Uri) -> Unit,
) {
    AnimatedContent(
        targetState = route,
        transitionSpec = {
            if (targetState != SettingsRoute.Main) {
                (slideInHorizontally(tween(ExpressiveMotion.DurationMedium, easing = ExpressiveMotion.EmphasizedDecelerate)) { it / 3 } +
                    fadeIn(tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedDecelerate)) +
                    scaleIn(tween(ExpressiveMotion.DurationMedium, easing = ExpressiveMotion.EmphasizedDecelerate), initialScale = 0.95f)) togetherWith
                    (slideOutHorizontally(tween(ExpressiveMotion.DurationMedium, easing = ExpressiveMotion.EmphasizedAccelerate)) { -it / 3 } +
                        fadeOut(tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedAccelerate)) +
                        scaleOut(tween(ExpressiveMotion.DurationMedium, easing = ExpressiveMotion.EmphasizedAccelerate), targetScale = 0.95f))
            } else {
                (slideInHorizontally(tween(ExpressiveMotion.DurationMedium, easing = ExpressiveMotion.EmphasizedDecelerate)) { -it / 3 } +
                    fadeIn(tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedDecelerate)) +
                    scaleIn(tween(ExpressiveMotion.DurationMedium, easing = ExpressiveMotion.EmphasizedDecelerate), initialScale = 0.95f)) togetherWith
                    (slideOutHorizontally(tween(ExpressiveMotion.DurationMedium, easing = ExpressiveMotion.EmphasizedAccelerate)) { it / 3 } +
                        fadeOut(tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedAccelerate)) +
                        scaleOut(tween(ExpressiveMotion.DurationMedium, easing = ExpressiveMotion.EmphasizedAccelerate), targetScale = 0.95f))
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
            onOpenGenreManagement = onOpenGenreManagement,
            onOpenOcrLanguages = onOpenOcrLanguages,
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
            onPrimaryLanguageChange = onPrimaryLanguageChange,
            onAddMainGenre = onAddMainGenre,
            onRemoveMainGenre = onRemoveMainGenre,
            onAddSubGenre = onAddSubGenre,
            onRemoveSubGenre = onRemoveSubGenre,
            onLoanRemindersEnabledChange = onLoanRemindersEnabledChange,
            onLoanReminderLeadDaysChange = onLoanReminderLeadDaysChange,
            onBackupReminderDaysChange = onBackupReminderDaysChange,
            onExportJson = onExportJson,
            onExportCsv = onExportCsv,
            onExportHtmlCatalog = onExportHtmlCatalog,
            onExportPdfCatalog = onExportPdfCatalog,
            onExportCsvTemplate = onExportCsvTemplate,
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
    onOpenGenreManagement: () -> Unit,
    onOpenOcrLanguages: () -> Unit,
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
    onPrimaryLanguageChange: (String) -> Unit,
    onAddMainGenre: (String) -> Unit,
    onRemoveMainGenre: (String) -> Unit,
    onAddSubGenre: (String, String) -> Unit,
    onRemoveSubGenre: (String, String) -> Unit,
    onLoanRemindersEnabledChange: (Boolean) -> Unit,
    onLoanReminderLeadDaysChange: (Int) -> Unit,
    onBackupReminderDaysChange: (Int) -> Unit,
    onExportJson: (Uri) -> Unit,
    onExportCsv: (Uri) -> Unit,
    onExportHtmlCatalog: (Uri) -> Unit,
    onExportPdfCatalog: (Uri) -> Unit,
    onExportCsvTemplate: (Uri) -> Unit,
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
                onPrimaryLanguageChange = onPrimaryLanguageChange,
            )
            return
        }
        SettingsRoute.GenreManagement -> {
            GenreManagementScreen(
                settings = librarySettings,
                onBack = onBack,
                onAddMainGenre = onAddMainGenre,
                onRemoveMainGenre = onRemoveMainGenre,
                onAddSubGenre = onAddSubGenre,
                onRemoveSubGenre = onRemoveSubGenre,
            )
            return
        }
        SettingsRoute.OcrLanguages -> {
            OcrLanguagePacksScreen(onBack = onBack)
            return
        }
        SettingsRoute.DataRecovery -> {
            DataRecoverySettingsScreen(
                settings = librarySettings,
                onBack = onBack,
                onExportJson = onExportJson,
                onExportCsv = onExportCsv,
                onExportHtmlCatalog = onExportHtmlCatalog,
                onExportPdfCatalog = onExportPdfCatalog,
                onExportCsvTemplate = onExportCsvTemplate,
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
                    icon = Icons.Outlined.Category,
                    title = stringResource(R.string.settings_genre_management_title),
                    body = stringResource(R.string.settings_genre_management_subtitle),
                    onClick = onOpenGenreManagement,
                )
            }
            item {
                SettingsEntryCard(
                    icon = Icons.Outlined.Translate,
                    title = stringResource(R.string.settings_ocr_language_packs_title),
                    body = stringResource(R.string.settings_ocr_language_packs_desc),
                    onClick = onOpenOcrLanguages,
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
    androidx.compose.material3.Surface(
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_prominent)),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .expressiveClickable(onClick = onOpenAppearance),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_md)))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.settings_appearance), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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
    androidx.compose.material3.Surface(
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_prominent)),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .expressiveClickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_md)))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 19.sp)
            }
            Icon(Icons.Outlined.ExpandMore, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.rotate(270f))
        }
    }
}

@Composable
private fun CompleteBackupCard(onExport: () -> Unit, onRestore: () -> Unit) {
    ElevatedCard(shape = RoundedCornerShape(dimensionResource(R.dimen.corner_xl))) {
        Column(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(dimensionResource(R.dimen.corner_xl)))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(dimensionResource(R.dimen.corner_md))).background(MaterialTheme.colorScheme.primaryContainer),
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
                Button(onClick = onExport, shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md))) {
                    Text(stringResource(R.string.action_export_complete_backup))
                }
                OutlinedButton(onClick = onRestore, shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md))) {
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
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_sheet)))
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
                Button(onClick = onCta, shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card)), modifier = Modifier.height(48.dp)) {
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
    val rotation by animateFloatAsState(
        if (expanded) 45f else 0f,
        animationSpec = ExpressiveMotion.ExpressiveSpring,
        label = "fabRotation"
    )
    val alignment = if (placement == FabPlacement.LEFT) Alignment.Start else Alignment.End
    Column(horizontalAlignment = alignment, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AnimatedVisibility(
            visible = expanded,
            enter = m3DialogEnterTransition(),
            exit = m3DialogExitTransition(),
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
        val fabPressScale = rememberPressScale(fabInteractionSource)
        val fabCornerRadius by animateDpAsState(
            if (expanded) dimensionResource(R.dimen.corner_2xl) else dimensionResource(R.dimen.corner_prominent),
            animationSpec = ExpressiveMotion.MorphDpSpring,
            label = "fabCornerMorph",
        )
        FloatingActionButton(
            onClick = onToggle,
            interactionSource = fabInteractionSource,
            modifier = Modifier
                .size(dimensionResource(R.dimen.fab_size))
                .scale(fabPressScale)
                .shadow(10.dp, RoundedCornerShape(fabCornerRadius)),
            shape = RoundedCornerShape(fabCornerRadius),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.action_add_book), modifier = Modifier.rotate(rotation).size(30.dp))
        }
    }
}

@Composable
private fun LoansFabMenu(
    expanded: Boolean,
    placement: FabPlacement,
    onToggle: () -> Unit,
    onAddPerson: () -> Unit,
    onAddLoan: () -> Unit,
) {
    val rotation by animateFloatAsState(
        if (expanded) 45f else 0f,
        animationSpec = ExpressiveMotion.ExpressiveSpring,
        label = "loansFabRotation"
    )
    val alignment = if (placement == FabPlacement.LEFT) Alignment.Start else Alignment.End
    Column(horizontalAlignment = alignment, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AnimatedVisibility(
            visible = expanded,
            enter = m3DialogEnterTransition(),
            exit = m3DialogExitTransition(),
        ) {
            Column(horizontalAlignment = alignment, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StaggeredFabMenuItem(index = 0, expanded = expanded) {
                    FabMenuItem(icon = Icons.Outlined.PersonAdd, label = stringResource(R.string.action_add_person), placement = placement, onClick = onAddPerson)
                }
                StaggeredFabMenuItem(index = 1, expanded = expanded) {
                    FabMenuItem(icon = Icons.Outlined.Outbound, label = stringResource(R.string.action_new_loan), placement = placement, onClick = onAddLoan)
                }
            }
        }
        val fabInteractionSource = remember { MutableInteractionSource() }
        val fabPressScale = rememberPressScale(fabInteractionSource)
        val fabCornerRadius by animateDpAsState(
            if (expanded) dimensionResource(R.dimen.corner_2xl) else dimensionResource(R.dimen.corner_prominent),
            animationSpec = ExpressiveMotion.MorphDpSpring,
            label = "fabCornerMorph",
        )
        FloatingActionButton(
            onClick = onToggle,
            interactionSource = fabInteractionSource,
            modifier = Modifier
                .size(dimensionResource(R.dimen.fab_size))
                .scale(fabPressScale)
                .shadow(10.dp, RoundedCornerShape(fabCornerRadius)),
            shape = RoundedCornerShape(fabCornerRadius),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.action_new_loan), modifier = Modifier.rotate(rotation).size(30.dp))
        }
    }
}

@Composable
private fun ScreenFab(icon: ImageVector, label: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressScale = rememberPressScale(interactionSource)
    FloatingActionButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .size(dimensionResource(R.dimen.fab_size))
            .scale(pressScale)
            .shadow(8.dp, RoundedCornerShape(dimensionResource(R.dimen.corner_xl))),
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_xl)),
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
        enter = fadeIn(tween(180, easing = ExpressiveMotion.EmphasizedDecelerate)) + scaleIn(spring(dampingRatio = 0.65f, stiffness = 450f), initialScale = 0.7f),
        exit = fadeOut(tween(100, easing = ExpressiveMotion.EmphasizedAccelerate)) + scaleOut(tween(100), targetScale = 0.8f),
    ) {
        content()
    }
}

@Composable
private fun FabMenuItem(icon: ImageVector, label: String, placement: FabPlacement, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressScale = rememberPressScale(interactionSource)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .scale(pressScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
    ) {
        if (placement == FabPlacement.LEFT) {
            FabMenuButton(icon = icon, label = label, onClick = onClick, interactionSource = interactionSource)
        }
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            shadowElevation = 8.dp,
            tonalElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            ),
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        if (placement == FabPlacement.RIGHT) {
            FabMenuButton(icon = icon, label = label, onClick = onClick, interactionSource = interactionSource)
        }
    }
}

@Composable
private fun FabMenuButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    SmallFloatingActionButton(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_prominent)),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp,
            hoveredElevation = 8.dp,
            focusedElevation = 8.dp,
        ),
        modifier = Modifier.size(52.dp),
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(26.dp))
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
        shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.corner_sheet), topEnd = dimensionResource(R.dimen.corner_sheet)),
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
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card))) {
                    Text(stringResource(R.string.action_cancel))
                }
                Button(onClick = { onSave(room, unit, shelf) }, modifier = Modifier.weight(1.4f).height(52.dp), shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card))) {
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
    seriesSuggestions: List<String>,
    locations: List<LocationEntity>,
    mainGenreOptions: List<String>,
    subGenresByMainGenre: Map<String, List<String>>,
    primaryLanguage: String,
    lookupInProgress: Boolean,
    savingInProgress: Boolean,
    bulkProgress: Pair<Int, Int>? = null,
    onDismiss: () -> Unit,
    onSkip: () -> Unit = onDismiss,
    onLookup: (String, (com.mj.homelibrary.data.remote.BookMetadata?) -> Unit) -> Unit,
    onSilentLookup: (String, (com.mj.homelibrary.data.remote.BookMetadata?) -> Unit) -> Unit,
    onFindCoverCandidates: (String, String, List<String>, (List<String>) -> Unit) -> Unit,
    onSearchByTitleAndAuthor: (String, List<String>, (List<com.mj.homelibrary.data.remote.BookMetadata>) -> Unit) -> Unit = { _, _, cb -> cb(emptyList()) },
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
    var coverCandidates by remember { mutableStateOf<List<String>>(emptyList()) }
    var findCoversInProgress by remember { mutableStateOf(false) }
    var showCoverPicker by remember { mutableStateOf(false) }
    var autoLookupFailed by remember { mutableStateOf(false) }
    var titleMatches by remember { mutableStateOf<List<com.mj.homelibrary.data.remote.BookMetadata>>(emptyList()) }
    var showTitleMatchPicker by remember { mutableStateOf(false) }
    var titleSearchInProgress by remember { mutableStateOf(false) }
    var noTitleMatchesFound by remember { mutableStateOf(false) }
    val context = LocalContext.current
    fun subGenreOptionsFor(mainGenre: String): List<String> =
        subGenresByMainGenre.entries
            .firstOrNull { it.key.equals(mainGenre, ignoreCase = true) }
            ?.value
            .orEmpty()
    val coverPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            pendingCoverUri = uri
        }
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    fun applyLookupResult(validIsbn: String, metadata: com.mj.homelibrary.data.remote.BookMetadata?) {
        if (metadata != null) {
            autoLookupFailed = false
            draft = if (fillOnlyEmpty) {
                draft.applyMissingMetadata(metadata, validIsbn)
            } else {
                draft.copy(isbn = validIsbn).applyMetadata(metadata)
            }
        }
    }
    fun lookupIfValid(isbn: String) {
        val validIsbn = isbn.validIsbnOrNull() ?: return
        onLookup(validIsbn) { metadata -> applyLookupResult(validIsbn, metadata) }
    }
    LaunchedEffect(initialIsbn, autoLookup, fillOnlyEmpty) {
        val validIsbn = initialIsbn.validIsbnOrNull()
        if (autoLookup && !manualEntry && validIsbn != null) {
            onSilentLookup(validIsbn) { metadata ->
                applyLookupResult(validIsbn, metadata)
                if (metadata == null) autoLookupFailed = true
            }
        }
    }
    val editorContent: @Composable (Modifier) -> Unit = { containerModifier ->
        Column(
            modifier = containerModifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(if (editing) R.string.edit_book_title else R.string.add_book_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
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
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_md)))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        stringResource(R.string.bulk_review_body, bulkProgress.first, bulkProgress.second),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    TextButton(onClick = onSkip) {
                        Text(stringResource(R.string.action_skip), fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (lookupInProgress || titleSearchInProgress) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text(
                    if (titleSearchInProgress) stringResource(R.string.autofill_searching) else stringResource(R.string.lookup_in_progress),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (noTitleMatchesFound) {
                Surface(
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            stringResource(R.string.autofill_no_results),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = { noTitleMatchesFound = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
            if (autoLookupFailed && draft.title.isBlank()) {
                AutoLookupFailedBanner(isbn = draft.isbn, onDismiss = { autoLookupFailed = false })
            }
            if (!manualEntry && draft.title.isNotBlank()) {
                MetadataLoadedCard(draft)
            }
            CoverEditor(
                draft = draft,
                onDraftChange = { draft = it },
                onPickCover = { coverPicker.launch(arrayOf("image/*")) },
                onFindCovers = if (draft.isbn.validIsbnOrNull() != null || draft.title.isNotBlank()) {
                    {
                        findCoversInProgress = true
                        onFindCoverCandidates(draft.isbn, draft.title, draft.authors.split(",").map(String::trim)) { results ->
                            findCoversInProgress = false
                            coverCandidates = results
                            showCoverPicker = true
                        }
                    }
                } else null,
                findCoversInProgress = findCoversInProgress,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                val isValidIsbn = draft.isbn.validIsbnOrNull() != null
                TextFieldLine(
                    value = draft.isbn,
                    onValueChange = { draft = draft.copy(isbn = it) },
                    label = R.string.field_isbn,
                    modifier = Modifier.weight(1f),
                    monospace = true,
                )
                if (isValidIsbn) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Outlined.Check,
                            contentDescription = stringResource(R.string.isbn_valid_badge),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                IconButton(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_card)))
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
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
                ) {
                    Icon(Icons.Outlined.CloudDone, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.action_lookup_isbn), fontWeight = FontWeight.SemiBold)
                }
            }
            Text(stringResource(R.string.section_metadata).uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, color = MaterialTheme.colorScheme.primary)
            TextFieldLine(draft.title, { draft = draft.copy(title = it) }, R.string.field_title)
            SuggestedTextFieldLine(
                value = draft.authors,
                onValueChange = { draft = draft.copy(authors = it) },
                suggestions = authorSuggestions,
                label = R.string.field_authors,
            )

            // Online Title/Author Autofill Button
            if (draft.title.isNotBlank() && !lookupInProgress && !titleSearchInProgress) {
                OutlinedButton(
                    onClick = {
                        titleSearchInProgress = true
                        noTitleMatchesFound = false
                        val authorsList = draft.authors.split(",").map(String::trim).filter(String::isNotBlank)
                        onSearchByTitleAndAuthor(draft.title, authorsList) { results ->
                            titleSearchInProgress = false
                            if (results.isEmpty()) {
                                noTitleMatchesFound = true
                            } else {
                                titleMatches = results
                                showTitleMatchPicker = true
                            }
                        }
                    },
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Outlined.AutoStories, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.action_autofill_title_author), fontWeight = FontWeight.SemiBold)
                }
            }

            SuggestedTextFieldLine(
                value = draft.seriesName,
                onValueChange = { draft = draft.copy(seriesName = it) },
                suggestions = seriesSuggestions,
                label = R.string.field_series_name,
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
            if (draft.languageCode != primaryLanguage) {
                TextFieldLineText(
                    value = draft.originalScriptTitle,
                    onValueChange = { draft = draft.copy(originalScriptTitle = it) },
                    label = stringResource(R.string.field_title_in_language, languageLabel(LocalContext.current, draft.languageCode)),
                )
            }
            RatingEditor(rating = draft.rating, onRatingChange = { draft = draft.copy(rating = it) })
            Text(stringResource(R.string.section_classification).uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, color = MaterialTheme.colorScheme.primary)
            val availableSubGenres = subGenreOptionsFor(draft.mainGenre)
            val selectedSubGenre = draft.subGenres.firstOrNull { selected ->
                availableSubGenres.any { it.equals(selected, ignoreCase = true) }
            }.orEmpty()
            DropdownSelectField(
                value = draft.mainGenre,
                onValueChange = { selectedMainGenre ->
                    val nextSubGenres = subGenreOptionsFor(selectedMainGenre)
                    draft = draft.copy(
                        mainGenre = selectedMainGenre,
                        subGenres = draft.subGenres.filter { current ->
                            nextSubGenres.any { it.equals(current, ignoreCase = true) }
                        }.take(1),
                    )
                },
                options = mainGenreOptions,
                label = R.string.field_main_genre,
            )
            DropdownSelectField(
                value = selectedSubGenre,
                onValueChange = { selected -> draft = draft.copy(subGenres = listOf(selected)) },
                options = availableSubGenres,
                label = R.string.field_sub_genre,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MorphChip(
                    selected = draft.bookType == "new",
                    label = stringResource(R.string.book_type_new),
                    onClick = { draft = draft.copy(bookType = "new") },
                )
                MorphChip(
                    selected = draft.bookType == "used",
                    label = stringResource(R.string.book_type_used),
                    onClick = { draft = draft.copy(bookType = "used") },
                )
            }
            TextFieldLine(draft.edition, { draft = draft.copy(edition = it) }, R.string.field_edition)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(stringResource(R.string.field_signed_copy), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Switch(checked = draft.signedCopy, onCheckedChange = { draft = draft.copy(signedCopy = it) })
            }
            Text(stringResource(R.string.section_ownership).uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                TextFieldLine(
                    value = draft.purchaseDate,
                    onValueChange = { draft = draft.copy(purchaseDate = it) },
                    label = R.string.field_purchase_date,
                    modifier = Modifier.weight(1f),
                    monospace = true,
                    placeholder = R.string.date_format_hint,
                )
                IconButton(
                    onClick = {
                        val cal = java.util.Calendar.getInstance()
                        android.app.DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                draft = draft.copy(purchaseDate = String.format(java.util.Locale.US, "%04d-%02d-%02d", y, m + 1, d))
                            },
                            cal.get(java.util.Calendar.YEAR),
                            cal.get(java.util.Calendar.MONTH),
                            cal.get(java.util.Calendar.DAY_OF_MONTH),
                        ).show()
                    },
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        Icons.Outlined.CalendarToday,
                        contentDescription = stringResource(R.string.action_pick_date),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                TextFieldLine(
                    value = draft.cost,
                    onValueChange = { draft = draft.copy(cost = it) },
                    label = R.string.field_cost,
                    modifier = Modifier.weight(1f),
                )
            }
            ShelfLocationField(draft = draft, locations = locations, onDraftChange = { draft = it })
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss, enabled = !savingInProgress) { Text(stringResource(R.string.action_cancel)) }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { onSave(draft) },
                    enabled = !savingInProgress,
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
                    modifier = Modifier.height(44.dp),
                ) {
                    if (savingInProgress) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.action_saving), fontWeight = FontWeight.Bold)
                    } else {
                        Text(stringResource(R.string.action_save_book), fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
    if (editing) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
                editorContent(
                    Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing),
                )
            }
        }
    } else {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.corner_sheet), topEnd = dimensionResource(R.dimen.corner_sheet)),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            editorContent(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 760.dp),
            )
        }
    }
    if (showScanner) {
        BarcodeScannerSheet(
            onBarcode = {
                draft = draft.copy(isbn = it)
                lookupIfValid(it)
                showScanner = false
            },
            onOcrResult = { ocr ->
                draft = draft.copy(
                    title = ocr.title.ifBlank { draft.title },
                    authors = if (ocr.authors.isNotEmpty()) ocr.authors.joinToString(", ") else draft.authors,
                    publisher = ocr.publisher ?: draft.publisher,
                    publishedYear = ocr.year?.toString() ?: draft.publishedYear,
                    isbn = ocr.rawIsbn ?: draft.isbn,
                )
                showScanner = false
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

    if (showCoverPicker) {
        CoverPickerSheet(
            candidates = coverCandidates,
            onDismiss = { showCoverPicker = false },
            onSelect = { url ->
                draft = draft.copy(coverUrl = url, coverImagePath = "")
                showCoverPicker = false
            },
        )
    }

    if (showTitleMatchPicker) {
        TitleMatchPickerSheet(
            matches = titleMatches,
            onDismiss = { showTitleMatchPicker = false },
            onSelect = { metadata ->
                showTitleMatchPicker = false
                draft = draft.copy(
                    title = metadata.title.ifBlank { draft.title },
                    subtitle = metadata.subtitle ?: draft.subtitle,
                    authors = if (metadata.authors.isNotEmpty()) metadata.authors.joinToString(", ") else draft.authors,
                    publisher = metadata.publisher ?: draft.publisher,
                    publishedYear = metadata.publishedYear?.toString() ?: draft.publishedYear,
                    pageCount = metadata.pageCount?.toString() ?: draft.pageCount,
                    notes = metadata.notes ?: draft.notes,
                    coverUrl = metadata.coverUrl ?: draft.coverUrl,
                    isbn = metadata.isbn13 ?: metadata.isbn10 ?: draft.isbn,
                    tags = if (metadata.tags.isNotEmpty()) metadata.tags.joinToString(", ") else draft.tags,
                )
            },
        )
    }
}

@Composable
private fun CoverEditor(
    draft: BookDraft,
    onDraftChange: (BookDraft) -> Unit,
    onPickCover: () -> Unit,
    onFindCovers: (() -> Unit)? = null,
    findCoversInProgress: Boolean = false,
) {
    Surface(
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_lg)),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(dimensionResource(R.dimen.corner_lg))),
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
                    OutlinedButton(onClick = onPickCover, shape = RoundedCornerShape(dimensionResource(R.dimen.corner_control)), contentPadding = PaddingValues(horizontal = 12.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.action_choose_cover))
                    }
                    TextButton(onClick = { onDraftChange(draft.copy(coverImagePath = "", coverUrl = "")) }) {
                        Text(stringResource(R.string.action_clear_cover))
                    }
                    if (onFindCovers != null) {
                        OutlinedButton(
                            onClick = onFindCovers,
                            enabled = !findCoversInProgress,
                            shape = RoundedCornerShape(dimensionResource(R.dimen.corner_control)),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                        ) {
                            if (findCoversInProgress) {
                                androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(17.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Outlined.Search, contentDescription = null, modifier = Modifier.size(17.dp))
                            }
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.action_find_covers))
                        }
                    }
                }
            }
        }
    }
}

private val CropFrameWidth = 190.dp
private val CropFrameHeight = 285.dp
private const val CropMaxScale = 4f
private const val CoverOutputWidthPx = 800
private const val CoverOutputHeightPx = 1200
private const val CoverDecodeMaxDimensionPx = 2400

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
    val coroutineScope = rememberCoroutineScope()
    var scale by remember(imageUri) { mutableStateOf(1f) }
    var offset by remember(imageUri) { mutableStateOf(Offset.Zero) }
    var cropInProgress by remember(imageUri) { mutableStateOf(false) }
    val frameWidthPx = with(density) { CropFrameWidth.toPx() }
    val frameHeightPx = with(density) { CropFrameHeight.toPx() }
    val scaleAnim by animateFloatAsState(scale, label = "cropScale")
    val offsetAnim by animateOffsetAsState(offset, label = "cropOffset")

    ModalBottomSheet(
        onDismissRequest = { if (!cropInProgress) onDismiss() },
        shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.corner_sheet), topEnd = dimensionResource(R.dimen.corner_sheet)),
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
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_prominent)))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(CropFrameWidth)
                        .height(CropFrameHeight)
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_card))),
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
                            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), RoundedCornerShape(dimensionResource(R.dimen.corner_card))),
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
                OutlinedButton(
                    onClick = onUseOriginal,
                    enabled = !cropInProgress,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card)),
                ) {
                    Text(stringResource(R.string.action_use_original))
                }
                Button(
                    onClick = {
                        if (!cropInProgress) {
                            cropInProgress = true
                            coroutineScope.launch {
                                val croppedPath = saveCroppedCover(context, imageUri, scale, offset, frameWidthPx, frameHeightPx)
                                cropInProgress = false
                                onUseCropped(croppedPath)
                            }
                        }
                    },
                    enabled = !cropInProgress,
                    modifier = Modifier.weight(1.25f).height(52.dp),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card)),
                ) {
                    if (cropInProgress) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(stringResource(R.string.action_use_cropped))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

private suspend fun saveCroppedCover(
    context: Context,
    uri: Uri,
    userScale: Float,
    userOffsetPx: Offset,
    frameWidthPx: Float,
    frameHeightPx: Float,
): String? = withContext(Dispatchers.IO) {
    runCatching {
        val source = decodeCoverBitmap(context, uri) ?: return@runCatching null
        var cropped: Bitmap? = null
        var outputBitmap: Bitmap? = null
        try {
            val baseScale = max(frameWidthPx / source.width, frameHeightPx / source.height)
            val totalScale = baseScale * userScale
            val displayedWidth = source.width * totalScale
            val displayedHeight = source.height * totalScale

            val srcWidth = frameWidthPx / totalScale
            val srcHeight = frameHeightPx / totalScale
            val srcX = ((displayedWidth / 2f - userOffsetPx.x - frameWidthPx / 2f) / totalScale)
                .coerceIn(0f, (source.width - srcWidth).coerceAtLeast(0f))
            val srcY = ((displayedHeight / 2f - userOffsetPx.y - frameHeightPx / 2f) / totalScale)
                .coerceIn(0f, (source.height - srcHeight).coerceAtLeast(0f))

            val srcXInt = srcX.roundToInt().coerceIn(0, source.width - 1)
            val srcYInt = srcY.roundToInt().coerceIn(0, source.height - 1)
            val cropWidth = srcWidth.roundToInt().coerceIn(1, source.width - srcXInt)
            val cropHeight = srcHeight.roundToInt().coerceIn(1, source.height - srcYInt)
            val croppedBitmap = Bitmap.createBitmap(source, srcXInt, srcYInt, cropWidth, cropHeight)
            cropped = croppedBitmap
            val resizedBitmap = Bitmap.createScaledBitmap(croppedBitmap, CoverOutputWidthPx, CoverOutputHeightPx, true)
            outputBitmap = resizedBitmap

            val directory = File(context.filesDir, "covers").also { it.mkdirs() }
            val file = File(directory, "custom-cover-${System.currentTimeMillis()}.jpg")
            try {
                file.outputStream().use { output ->
                    check(resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, output))
                }
                file.absolutePath
            } catch (error: Throwable) {
                file.delete()
                throw error
            }
        } finally {
            outputBitmap?.takeIf { it !== cropped && !it.isRecycled }?.recycle()
            cropped?.takeIf { it !== source && !it.isRecycled }?.recycle()
            if (!source.isRecycled) source.recycle()
        }
    }.getOrNull()
}

private fun decodeCoverBitmap(context: Context, uri: Uri): Bitmap? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val imageSource = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(imageSource) { decoder, info, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val sourceWidth = info.size.width
            val sourceHeight = info.size.height
            val largestDimension = max(sourceWidth, sourceHeight)
            if (largestDimension > CoverDecodeMaxDimensionPx) {
                val sampleScale = CoverDecodeMaxDimensionPx / largestDimension.toFloat()
                decoder.setTargetSize(
                    (sourceWidth * sampleScale).roundToInt().coerceAtLeast(1),
                    (sourceHeight * sampleScale).roundToInt().coerceAtLeast(1),
                )
            }
        }
    } else {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sampleSize = 1
        while (max(bounds.outWidth, bounds.outHeight) / sampleSize > CoverDecodeMaxDimensionPx) {
            sampleSize *= 2
        }
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoverPickerSheet(
    candidates: List<String>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.corner_sheet), topEnd = dimensionResource(R.dimen.corner_sheet)),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.cover_picker_title), style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.cover_picker_body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (candidates.isEmpty()) {
                Text(stringResource(R.string.cover_picker_empty), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.heightIn(max = 420.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(candidates, key = { it }) { url ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val pressScale = rememberPressScale(interactionSource)
                        AsyncImage(
                            model = url,
                            contentDescription = stringResource(R.string.content_description_book_cover),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.68f)
                                .scale(pressScale)
                                .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_control)))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(dimensionResource(R.dimen.corner_control)))
                                .clickable(interactionSource = interactionSource, indication = LocalIndication.current) { onSelect(url) },
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TitleMatchPickerSheet(
    matches: List<com.mj.homelibrary.data.remote.BookMetadata>,
    onDismiss: () -> Unit,
    onSelect: (com.mj.homelibrary.data.remote.BookMetadata) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.corner_sheet), topEnd = dimensionResource(R.dimen.corner_sheet)),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(stringResource(R.string.autofill_select_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                stringResource(R.string.autofill_subtitle, matches.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 460.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(matches) { item ->
                    Surface(
                        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card)),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(item) },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (!item.coverUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = item.coverUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .width(44.dp)
                                        .aspectRatio(0.68f)
                                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_sm))),
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .width(44.dp)
                                        .aspectRatio(0.68f)
                                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_sm)))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        Icons.Outlined.Book,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                if (item.authors.isNotEmpty()) {
                                    Text(
                                        text = item.authors.joinToString(", "),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                val metaParts = listOfNotNull(
                                    item.publisher?.take(25),
                                    item.publishedYear?.toString(),
                                    item.pageCount?.let { "$it p." },
                                    item.isbn13 ?: item.isbn10,
                                )
                                if (metaParts.isNotEmpty()) {
                                    Text(
                                        text = metaParts.joinToString(" · "),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            Icon(
                                Icons.Outlined.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MetadataLoadedCard(draft: BookDraft) {
    val previewEntity = remember(draft.id, draft.title, draft.authors, draft.languageCode, draft.coverImagePath, draft.coverUrl, draft.formatCode) {
        draft.toEntity()
    }
    Surface(shape = RoundedCornerShape(dimensionResource(R.dimen.corner_lg)), color = MaterialTheme.colorScheme.secondaryContainer) {
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
private fun AutoLookupFailedBanner(isbn: String, onDismiss: () -> Unit) {
    Surface(shape = RoundedCornerShape(dimensionResource(R.dimen.corner_lg)), color = MaterialTheme.colorScheme.tertiaryContainer) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.SearchOff, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    stringResource(R.string.auto_lookup_failed_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Text(
                    stringResource(R.string.auto_lookup_failed_body, isbn),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f),
                )
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size))) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.content_description_close),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(18.dp),
                )
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
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
        textStyle = MaterialTheme.typography.bodyMedium,
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    )
}

@Composable
private fun ShelfLocationField(draft: BookDraft, locations: List<LocationEntity>, onDraftChange: (BookDraft) -> Unit) {
    ElevatedCard(shape = RoundedCornerShape(dimensionResource(R.dimen.corner_prominent))) {
        Column(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(dimensionResource(R.dimen.corner_prominent)))
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
            LocationDropdownFields(
                room = draft.room,
                unit = draft.unit,
                shelf = draft.shelf,
                locations = locations,
                onRoomChange = { onDraftChange(draft.copy(room = it, unit = "", shelf = "")) },
                onUnitChange = { onDraftChange(draft.copy(unit = it, shelf = "")) },
                onShelfChange = { onDraftChange(draft.copy(shelf = it)) },
            )
            TextFieldLine(draft.positionNote, { onDraftChange(draft.copy(positionNote = it)) }, R.string.field_position_note)
        }
    }
}

@Composable
private fun LocationDropdownFields(
    room: String,
    unit: String,
    shelf: String,
    locations: List<LocationEntity>,
    onRoomChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onShelfChange: (String) -> Unit,
) {
    val roomOptions = remember(locations) { locations.map { it.room }.distinctSorted() }
    val unitOptions = remember(locations, room) {
        locations.filter { it.room.equals(room, ignoreCase = true) }.map { it.unit }.distinctSorted()
    }
    val shelfOptions = remember(locations, room, unit) {
        locations.filter { it.room.equals(room, ignoreCase = true) && it.unit.equals(unit, ignoreCase = true) }
            .map { it.shelf }
            .distinctSorted()
    }
    if (locations.isEmpty()) {
        Text(
            stringResource(R.string.location_no_shelves_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DropdownSelectField(
            value = room,
            onValueChange = onRoomChange,
            options = roomOptions,
            label = R.string.field_room,
            modifier = Modifier.weight(1f),
        )
        DropdownSelectField(
            value = unit,
            onValueChange = onUnitChange,
            options = unitOptions,
            label = R.string.field_unit,
            modifier = Modifier.weight(1f),
        )
    }
    DropdownSelectField(
        value = shelf,
        onValueChange = onShelfChange,
        options = shelfOptions,
        label = R.string.field_shelf,
    )
}

@Composable
private fun DropdownSelectField(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    @StringRes label: Int,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val enabled = options.isNotEmpty()
    val dismissKeyboard = rememberDismissKeyboard()
    Box(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Surface(
                shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(dimensionResource(R.dimen.corner_md)))
                    .clickable(enabled = enabled) { dismissKeyboard(); expanded = true },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = value.ifBlank { stringResource(R.string.field_select_placeholder) },
                        color = if (value.isBlank()) {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(Icons.Outlined.ExpandMore, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ScrollableDropdownMenuItems(
                options = options,
                onOptionSelected = { option ->
                    onValueChange(option)
                    expanded = false
                },
            )
        }
    }
}

@Composable
private fun ScrollableDropdownMenuItems(
    options: List<String>,
    onOptionSelected: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    val showScrollbar = scrollState.maxValue > 0
    val thumbColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
    Box {
        Column(
            modifier = Modifier
                .heightIn(max = 320.dp)
                .verticalScroll(scrollState),
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onOptionSelected(option) },
                )
            }
        }
        if (showScrollbar) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(6.dp)
                    .padding(vertical = 8.dp, horizontal = 1.dp)
                    .drawBehind {
                        val scrollableDistance = scrollState.maxValue.toFloat().coerceAtLeast(1f)
                        val thumbHeight = (size.height * size.height / (size.height + scrollableDistance)).coerceAtLeast(28.dp.toPx())
                        val scrollFraction = (scrollState.value / scrollableDistance).coerceIn(0f, 1f)
                        val thumbTop = (size.height - thumbHeight) * scrollFraction
                        drawRoundRect(
                            color = trackColor,
                            topLeft = Offset(size.width * 0.25f, 0f),
                            size = Size(size.width * 0.5f, size.height),
                            cornerRadius = CornerRadius(size.width, size.width),
                        )
                        drawRoundRect(
                            color = thumbColor,
                            topLeft = Offset(0f, thumbTop),
                            size = Size(size.width, thumbHeight),
                            cornerRadius = CornerRadius(size.width, size.width),
                        )
                    },
            )
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
    @StringRes placeholder: Int? = null,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label), maxLines = 1, overflow = TextOverflow.Ellipsis) },
        placeholder = placeholder?.let { { Text(stringResource(it)) } },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
        textStyle = if (monospace) MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace) else MaterialTheme.typography.bodyMedium,
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    )
}

@Composable
private fun TextFieldLineText(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        singleLine = true,
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
        textStyle = MaterialTheme.typography.bodyMedium,
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
    var focused by remember { mutableStateOf(false) }
    val activeToken = value.substringAfterLast(",").trim()
    val filtered = suggestions
        .filter { suggestion ->
            activeToken.isBlank() || suggestion.contains(activeToken, ignoreCase = true)
        }
        .filterNot { suggestion ->
            value.split(",").map { it.trim().lowercase() }.contains(suggestion.lowercase())
        }
        .take(6)
    Column(modifier = modifier.fillMaxWidth()) {
        TextFieldLine(
            value = value,
            onValueChange = onValueChange,
            label = label,
            modifier = Modifier.onFocusChanged { focusState -> focused = focusState.isFocused },
        )
        AnimatedVisibility(visible = focused && filtered.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            ) {
                Column {
                    filtered.forEach { suggestion ->
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onValueChange(if (commaAppend) value.withCommaSuggestion(suggestion) else suggestion)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        )
                    }
                }
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
    onClone: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit,
    onRate: (Float) -> Unit,
) {
    val context = LocalContext.current
    val palette = coverPaletteFor(item.book)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.corner_sheet), topEnd = dimensionResource(R.dimen.corner_sheet)),
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
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {
                                onDismiss()
                                onClone()
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        ) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = stringResource(R.string.action_clone_book))
                        }
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
                        .shadow(16.dp, RoundedCornerShape(dimensionResource(R.dimen.corner_md))),
                )
            }
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(item.book.title, style = MaterialTheme.typography.headlineSmall)
                item.book.originalScriptTitle?.let {
                    Text(it, style = MaterialTheme.typography.titleMedium, lineHeight = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                item.book.seriesName?.let {
                    Text(
                        stringResource(R.string.series_label, it),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(item.book.authors.displayAuthors(context), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    MetadataChip(Icons.Outlined.Translate, languageLabel(context, item.book.languageCode))
                    MetadataChip(Icons.Outlined.MenuBook, stringResource(BookFormatCode.fromCode(item.book.formatCode).labelRes))
                    item.book.publishedYear?.let { MetadataChip(Icons.Outlined.CalendarToday, stringResource(R.string.published_year_label, it)) }
                    item.book.pageCount?.let { MetadataChip(Icons.Outlined.Description, stringResource(R.string.pages_label, it)) }
                    MetadataChip(Icons.Outlined.Bookmark, stringResource(ReadStatusCode.fromCode(item.book.readStatusCode).labelRes))
                    item.book.purchaseDateEpochMillis?.let {
                        MetadataChip(Icons.Outlined.CalendarToday, stringResource(R.string.purchased_on_label, it.displayDate(context)))
                    }
                    item.book.cost?.let { MetadataChip(Icons.Outlined.Payments, it.displayCost()) }
                    item.book.mainGenre?.let { MetadataChip(Icons.Outlined.Category, it) }
                    item.book.subGenres.forEach { MetadataChip(Icons.Outlined.Category, it) }
                    item.book.bookType?.let {
                        MetadataChip(
                            Icons.Outlined.Sell,
                            stringResource(if (it == "new") R.string.book_type_new else R.string.book_type_used),
                        )
                    }
                    item.book.edition?.let { MetadataChip(Icons.Outlined.MenuBook, it) }
                    if (item.book.signedCopy) {
                        MetadataChip(Icons.Outlined.Draw, stringResource(R.string.field_signed_copy))
                    }
                }
                LocationCard(item.location.displayBreadcrumb(context), onMove = onMove)
                if (item.activeLoan != null) {
                    LoanDetailCard(item = item, loan = item.activeLoan, onReturn = onReturn)
                } else {
                    Button(onClick = onLoan, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card))) {
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
        label = { Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, maxLines = 1) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(15.dp)) },
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_control)),
    )
}

@Composable
private fun LocationCard(breadcrumb: String, onMove: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_lg)),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(dimensionResource(R.dimen.corner_lg))),
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
            OutlinedButton(onClick = onMove, shape = RoundedCornerShape(dimensionResource(R.dimen.corner_control)), contentPadding = PaddingValues(horizontal = 12.dp)) {
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
    Surface(shape = RoundedCornerShape(dimensionResource(R.dimen.corner_lg)), color = bg, contentColor = fg) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Avatar(loan.borrowerName, size = 38.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(loan.borrowerName, fontWeight = FontWeight.Medium)
                    Text(stringResource(R.string.due_on, loan.expectedReturnDateEpochMillis.displayDate(context)), style = MaterialTheme.typography.labelSmall, color = fg.copy(alpha = 0.8f))
                }
            }
            Button(onClick = { onReturn(loan) }, modifier = Modifier.fillMaxWidth().height(42.dp), shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md))) {
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
        Box(modifier = Modifier.padding(vertical = 4.dp)) {
            RatingRow(rating = rating, onRatingChange = onRatingChange, starSize = 32.dp)
        }
    }
}

@Composable
private fun RatingRow(rating: Float, onRatingChange: ((Float) -> Unit)? = null, starSize: Dp = 24.dp) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(5) { index ->
            val value = (index + 1).toFloat()
            val filled = rating >= value
            val starScale by animateFloatAsState(
                if (filled) 1.15f else 1f,
                animationSpec = ExpressiveMotion.ExpressiveSpring,
                label = "starScale"
            )
            val starTint by androidx.compose.animation.animateColorAsState(
                targetValue = if (filled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant,
                animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedDecelerate),
                label = "starTint"
            )
            Icon(
                Icons.Outlined.Star,
                contentDescription = null,
                tint = starTint,
                modifier = Modifier
                    .size(starSize)
                    .scale(starScale)
                    .then(
                        if (onRatingChange != null) {
                            Modifier.expressiveClickable(pressedScale = 0.88f) { onRatingChange(value) }
                        } else {
                            Modifier
                        }
                    ),
            )
        }
        Text(stringResource(R.string.rating_value, rating), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddPersonSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, relation: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("") }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.corner_sheet), topEnd = dimensionResource(R.dimen.corner_sheet)),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.add_person_title), style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.add_person_body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextFieldLine(name, { name = it }, R.string.field_person_name)
            TextFieldLine(phone, { phone = it }, R.string.field_person_phone)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    stringResource(R.string.relation_family),
                    stringResource(R.string.relation_friend),
                    stringResource(R.string.relation_colleague),
                    stringResource(R.string.relation_neighbor),
                ).forEach { option ->
                    MorphChip(selected = relation == option, label = option, onClick = { relation = option })
                }
            }
            TextFieldLine(relation, { relation = it }, R.string.field_person_relation)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card))) {
                    Text(stringResource(R.string.action_cancel))
                }
                Button(
                    onClick = { onSave(name, phone, relation) },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.weight(1.4f).height(52.dp),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card)),
                ) {
                    Text(stringResource(R.string.action_save_person))
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
    locations: List<LocationEntity>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String?) -> Unit,
) {
    var room by remember(item.book.id) { mutableStateOf(item.location?.room.orEmpty()) }
    var unit by remember(item.book.id) { mutableStateOf(item.location?.unit.orEmpty()) }
    var shelf by remember(item.book.id) { mutableStateOf(item.location?.shelf.orEmpty()) }
    var positionNote by remember(item.book.id) { mutableStateOf(item.book.positionNote.orEmpty()) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.corner_sheet), topEnd = dimensionResource(R.dimen.corner_sheet)),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.move_book_title), style = MaterialTheme.typography.headlineSmall)
            Text(item.book.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LocationDropdownFields(
                room = room,
                unit = unit,
                shelf = shelf,
                locations = locations,
                onRoomChange = { room = it; unit = ""; shelf = "" },
                onUnitChange = { unit = it; shelf = "" },
                onShelfChange = { shelf = it },
            )
            TextFieldLine(positionNote, { positionNote = it }, R.string.field_position_note)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card))) {
                    Text(stringResource(R.string.action_cancel))
                }
                Button(onClick = { onSave(room, unit, shelf, positionNote) }, modifier = Modifier.weight(1.4f).height(52.dp), shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card))) {
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
        shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.corner_sheet), topEnd = dimensionResource(R.dimen.corner_sheet)),
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
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(dimensionResource(R.dimen.corner_md))).clickable { onBookClick(item) }.padding(vertical = 8.dp),
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

private enum class LoanFlowStep { SelectBorrower, SelectBook, Confirm }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewLoanFlowSheet(
    preselectedBook: BookEntity?,
    availableBooks: List<BookListItem>,
    borrowers: List<BorrowerEntity>,
    onDismiss: () -> Unit,
    onAddPersonRequest: () -> Unit,
    onSave: (LoanDraft) -> Unit,
) {
    var step by remember { mutableStateOf(LoanFlowStep.SelectBorrower) }
    var selectedBorrower by remember { mutableStateOf<BorrowerEntity?>(null) }
    var selectedBook by remember(preselectedBook) { mutableStateOf(preselectedBook) }
    var dueDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.corner_sheet), topEnd = dimensionResource(R.dimen.corner_sheet)),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .heightIn(max = 640.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (step) {
                LoanFlowStep.SelectBorrower -> {
                    Text(stringResource(R.string.new_loan_step_borrower_title), style = MaterialTheme.typography.headlineSmall)
                    if (borrowers.isEmpty()) {
                        Text(
                            stringResource(R.string.new_loan_no_people_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(onClick = onAddPersonRequest, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card))) {
                            Icon(Icons.Outlined.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.action_add_person))
                        }
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 380.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(borrowers, key = { it.id }) { borrower ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_card)))
                                        .clickable {
                                            selectedBorrower = borrower
                                            step = if (selectedBook != null) LoanFlowStep.Confirm else LoanFlowStep.SelectBook
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Avatar(borrower.name, size = 40.dp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(borrower.name, fontWeight = FontWeight.Medium)
                                        val subtitle = listOfNotNull(borrower.relation, borrower.phone).joinToString(" · ")
                                        if (subtitle.isNotBlank()) {
                                            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                        TextButton(onClick = onAddPersonRequest) {
                            Icon(Icons.Outlined.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.action_add_person))
                        }
                    }
                }

                LoanFlowStep.SelectBook -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { step = LoanFlowStep.SelectBorrower }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.action_back))
                        }
                        Text(stringResource(R.string.new_loan_step_book_title), style = MaterialTheme.typography.headlineSmall)
                    }
                    var query by remember { mutableStateOf("") }
                    TextFieldLine(query, { query = it }, R.string.new_loan_book_search_hint)
                    val filtered = remember(query, availableBooks) {
                        if (query.isBlank()) {
                            availableBooks
                        } else {
                            availableBooks.filter { item ->
                                item.book.title.contains(query, ignoreCase = true) ||
                                    item.book.authors.any { it.contains(query, ignoreCase = true) }
                            }
                        }
                    }
                    if (filtered.isEmpty()) {
                        Text(stringResource(R.string.no_available_books), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 340.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filtered, key = { it.book.id }) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_card)))
                                        .clickable {
                                            selectedBook = item.book
                                            step = LoanFlowStep.Confirm
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    BookCover(item.book, Modifier.width(40.dp).height(60.dp), titleSize = 7)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.book.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(
                                            item.book.authors.displayAuthors(context),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                LoanFlowStep.Confirm -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (preselectedBook == null) {
                            IconButton(onClick = { step = LoanFlowStep.SelectBook }) {
                                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.action_back))
                            }
                        }
                        Text(stringResource(R.string.loan_this_book), style = MaterialTheme.typography.headlineSmall)
                    }
                    selectedBook?.let { book ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            BookCover(book, Modifier.width(44.dp).height(66.dp), titleSize = 8)
                            Column {
                                Text(book.title, fontWeight = FontWeight.Medium)
                                Text(book.authors.displayAuthors(context), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    selectedBorrower?.let { borrower ->
                        Surface(shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)), color = MaterialTheme.colorScheme.secondaryContainer) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Avatar(borrower.name, size = 34.dp)
                                Text(borrower.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                                TextButton(onClick = { step = LoanFlowStep.SelectBorrower }) {
                                    Text(stringResource(R.string.action_change))
                                }
                            }
                        }
                    }
                    TextFieldLine(dueDate, { dueDate = it }, R.string.field_due_date, monospace = true, placeholder = R.string.date_format_hint)
                    TextFieldLine(notes, { notes = it }, R.string.field_notes)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card))) {
                            Text(stringResource(R.string.action_cancel))
                        }
                        Button(
                            onClick = {
                                val borrower = selectedBorrower
                                val book = selectedBook
                                if (borrower != null && book != null) {
                                    onSave(
                                        LoanDraft(
                                            bookId = book.id,
                                            borrowerId = borrower.id,
                                            borrowerName = borrower.name,
                                            borrowerContact = borrower.phone.orEmpty(),
                                            dueDate = dueDate,
                                            notes = notes,
                                        ),
                                    )
                                }
                            },
                            enabled = selectedBorrower != null && selectedBook != null,
                            modifier = Modifier.weight(1.4f).height(52.dp),
                            shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card)),
                        ) {
                            Text(stringResource(R.string.action_loan_book))
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
                shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
            ) {
                Text(stringResource(R.string.action_delete))
            }
        },
    )
}

@Composable
private fun BulkDeleteDialog(
    selectedCount: Int,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = { Text(stringResource(R.string.bulk_delete_title, selectedCount), style = MaterialTheme.typography.headlineSmall) },
        text = { Text(stringResource(R.string.bulk_delete_body)) },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
        confirmButton = {
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError),
                shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
            ) {
                Text(stringResource(R.string.action_delete))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BulkStatusSheet(
    selectedCount: Int,
    onDismiss: () -> Unit,
    onSelectStatus: (String) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.corner_sheet), topEnd = dimensionResource(R.dimen.corner_sheet)),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(R.string.bulk_status_title, selectedCount),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            ReadStatusCode.entries.forEach { status ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_md)))
                        .clickable { onSelectStatus(status.code) }
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(Icons.Outlined.Bookmark, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(stringResource(status.labelRes), style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BulkMoveSheet(
    selectedCount: Int,
    locations: List<LocationEntity>,
    onDismiss: () -> Unit,
    onSelectLocation: (Long?) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.corner_sheet), topEnd = dimensionResource(R.dimen.corner_sheet)),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 480.dp)
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.bulk_move_title, selectedCount),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_md)))
                        .clickable { onSelectLocation(null) }
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(Icons.Outlined.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(stringResource(R.string.bulk_move_unassign), style = MaterialTheme.typography.bodyLarge)
                }
            }
            items(locations, key = { it.id }) { location ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_md)))
                        .clickable { onSelectLocation(location.id) }
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(Icons.Outlined.Place, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${location.room} › ${location.unit} › ${location.shelf}",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            item { Spacer(Modifier.height(12.dp)) }
        }
    }
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
                Button(onClick = { openSearch("https://www.google.com/search?q=$isbn%20book") }, shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md))) {
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
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(dimensionResource(R.dimen.corner_lg))).background(MaterialTheme.colorScheme.secondaryContainer),
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
            Button(onClick = onReturn, shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md))) { Text(stringResource(R.string.action_returned)) }
        },
    )
}

@Composable
private fun CountChip(text: String, color: Color = MaterialTheme.colorScheme.surfaceVariant) {
    Text(
        text = text,
        modifier = Modifier.clip(CircleShape).background(color).padding(horizontal = 7.dp, vertical = 3.dp),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
    )
}

@Composable
private fun Avatar(name: String, size: Dp = 40.dp) {
    val initials = name.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").take(2)
    Box(
        modifier = Modifier.size(size).clip(RoundedCornerShape(dimensionResource(R.dimen.corner_md))).background(MaterialTheme.colorScheme.primaryContainer),
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
private fun chartColor(index: Int): Color = when (index % 5) {
    0 -> MaterialTheme.colorScheme.primary
    1 -> MaterialTheme.colorScheme.tertiary
    2 -> MaterialTheme.colorScheme.secondary
    3 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
    else -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.65f)
}
