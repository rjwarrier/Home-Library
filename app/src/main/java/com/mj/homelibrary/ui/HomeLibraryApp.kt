package com.mj.homelibrary.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Outbound
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.RunningWithErrors
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
import com.mj.homelibrary.data.BookFormatCode
import com.mj.homelibrary.data.BookSortCode
import com.mj.homelibrary.data.LanguageCode
import com.mj.homelibrary.data.ReadStatusCode
import com.mj.homelibrary.data.entity.BookEntity
import com.mj.homelibrary.data.entity.LoanEntity
import com.mj.homelibrary.data.entity.LocationEntity
import java.time.Year
import kotlin.math.absoluteValue
import kotlin.math.max

private val GridMinCellSize = 148.dp
private val ScreenMaxWidth = 600.dp

private enum class HomeTab(@StringRes val labelRes: Int, @StringRes val titleRes: Int, val icon: ImageVector) {
    Library(R.string.nav_library, R.string.screen_library, Icons.Outlined.Book),
    Shelves(R.string.nav_shelves, R.string.screen_shelves, Icons.Outlined.Place),
    Loans(R.string.nav_loans, R.string.screen_loans, Icons.Outlined.People),
    Stats(R.string.nav_stats, R.string.screen_stats, Icons.Outlined.BarChart),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLibraryApp(viewModel: HomeLibraryViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(HomeTab.Library) }
    var fabExpanded by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    var showAddBook by remember { mutableStateOf(false) }
    var manualEntry by remember { mutableStateOf(false) }
    var scannedIsbn by remember { mutableStateOf("") }
    var selectedBook by remember { mutableStateOf<BookListItem?>(null) }
    var loanBook by remember { mutableStateOf<BookEntity?>(null) }
    var returnLoan by remember { mutableStateOf<LoanEntity?>(null) }

    state.transient.errorRes?.let { errorRes ->
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
                fabExpanded = false
            })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (selectedTab == HomeTab.Library) {
                GranthapuraFabMenu(
                    expanded = fabExpanded,
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
            when (selectedTab) {
                HomeTab.Library -> LibraryScreen(
                    state = state,
                    onQueryChange = viewModel::setQuery,
                    onSortChange = viewModel::setSort,
                    onGridModeChange = viewModel::setGridMode,
                    onLanguageChange = viewModel::setLanguage,
                    onReadStatusChange = viewModel::setReadStatus,
                    onOnLoanChange = viewModel::setOnLoanOnly,
                    onLocationChange = viewModel::setLocation,
                    onBookClick = { selectedBook = it },
                    onScanFirst = { showScanner = true },
                )

                HomeTab.Shelves -> ShelvesScreen(state = state, onBookClick = { selectedBook = it })
                HomeTab.Loans -> LoansScreen(
                    state = state,
                    onReturn = { returnLoan = it },
                    onBookClick = { selectedBook = it },
                )

                HomeTab.Stats -> StatsScreen(
                    state = state,
                    onExportJson = viewModel::exportJson,
                    onExportCsv = viewModel::exportCsv,
                    onImportJson = viewModel::importJson,
                )
            }
        }
    }

    if (showScanner) {
        BarcodeScannerSheet(
            onBarcode = {
                scannedIsbn = it
                manualEntry = false
                showAddBook = true
            },
            onDismiss = { showScanner = false },
        )
    }

    if (showAddBook) {
        AddBookSheet(
            manualEntry = manualEntry,
            initialIsbn = scannedIsbn,
            lookupInProgress = state.transient.lookupInProgress,
            onDismiss = { showAddBook = false },
            onLookup = viewModel::lookupIsbn,
            onSave = { draft -> viewModel.addBook(draft) { showAddBook = false } },
        )
    }

    selectedBook?.let { item ->
        BookDetailSheet(
            item = item,
            loans = state.loans.filter { it.bookId == item.book.id },
            onDismiss = { selectedBook = null },
            onLoan = { loanBook = item.book },
            onReturn = { returnLoan = it },
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
            .height(dimensionResource(R.dimen.bottom_nav_height))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        HomeTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onSelect(tab) },
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(stringResource(tab.labelRes), maxLines = 1) },
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
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 34.sp,
                lineHeight = 38.sp,
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
    onBookClick: (BookListItem) -> Unit,
    onScanFirst: () -> Unit,
) {
    val context = LocalContext.current
    val languageCount = state.allBooks.map { it.languageCode }.distinct().size
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
                onCta = { onQueryChange("") },
            )

            state.filters.gridMode -> LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Adaptive(GridMinCellSize),
                contentPadding = PaddingValues(top = dimensionResource(R.dimen.space_xs), bottom = dimensionResource(R.dimen.space_xl)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_lg)),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                items(state.visibleBooks, key = { it.book.id }) { item ->
                    BookGridCard(item = item, onClick = { onBookClick(item) })
                }
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_xl)),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.visibleBooks, key = { it.book.id }) { item ->
                    BookListRow(item = item, onClick = { onBookClick(item) })
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
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
            )
            Icon(Icons.Outlined.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.space_xs)),
    ) {
        item {
            MorphChip(
                selected = state.filters.readStatusCode == null,
                label = stringResource(R.string.filter_genre),
                trailing = Icons.Outlined.ExpandMore,
                onClick = { onReadStatusChange(null) },
            )
        }
        item {
            MorphChip(
                selected = state.filters.languageCode == LanguageCode.Malayalam.code,
                label = stringResource(LanguageCode.Malayalam.labelRes),
                onClick = {
                    onLanguageChange(
                        if (state.filters.languageCode == LanguageCode.Malayalam.code) null else LanguageCode.Malayalam.code,
                    )
                },
            )
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
            MorphChip(
                selected = state.filters.locationId != null,
                label = stringResource(R.string.filter_location),
                trailing = Icons.Outlined.ExpandMore,
                onClick = { onLocationChange(null) },
            )
        }
    }
}

@Composable
private fun MorphChip(selected: Boolean, label: String, trailing: ImageVector? = null, onClick: () -> Unit) {
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
        shape = RoundedCornerShape(if (selected) 12.dp else 999.dp),
    )
}

@Composable
private fun BookGridCard(item: BookListItem, onClick: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.clickable(onClick = onClick),
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

@Composable
private fun BookListRow(item: BookListItem, onClick: () -> Unit) {
    val context = LocalContext.current
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

@Composable
private fun BookCover(
    book: BookEntity,
    modifier: Modifier = Modifier,
    titleSize: Int = 16,
) {
    val palette = remember(book.id, book.title) { coverPaletteFor(book) }
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
                        fontFamily = if (book.languageCode == LanguageCode.Malayalam.code) FontFamily.Serif else FontFamily.Serif,
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
private fun ShelvesScreen(state: HomeLibraryUiState, onBookClick: (BookListItem) -> Unit) {
    val roomCount = state.locations.map { it.room }.distinct().size
    val unitCount = state.locations.map { it.room to it.unit }.distinct().size
    ContentColumn {
        ScreenHeader(
            titleRes = R.string.screen_shelves,
            meta = stringResource(R.string.shelves_meta, roomCount, unitCount, state.locations.size),
            actions = {
                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                ) {
                    Icon(Icons.Outlined.CheckBoxOutlineBlank, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.action_select))
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
                        RoomSection(room = room, locations = locations, items = state.visibleBooks, onBookClick = onBookClick)
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
) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm))) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.Weekend, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(19.dp))
            Text(room, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            CountChip(stringResource(R.string.book_count, items.count { it.location?.room == room }))
        }
        locations.groupBy { it.unit }.forEach { (unit, unitLocations) ->
            BookcaseCard(unit = unit, locations = unitLocations, items = items, onBookClick = onBookClick)
        }
    }
}

@Composable
private fun BookcaseCard(
    unit: String,
    locations: List<LocationEntity>,
    items: List<BookListItem>,
    onBookClick: (BookListItem) -> Unit,
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
                ShelfRow(location = location, books = shelfBooks, onBookClick = onBookClick)
            }
        }
    }
}

@Composable
private fun ShelfRow(location: LocationEntity, books: List<BookListItem>, onBookClick: (BookListItem) -> Unit) {
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
            Icon(Icons.Outlined.FactCheck, contentDescription = stringResource(R.string.shelf_audit), tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 8.dp).size(18.dp))
        }
        Box(modifier = Modifier.height(74.dp).fillMaxWidth()) {
            HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter), color = MaterialTheme.colorScheme.outlineVariant, thickness = 2.dp)
            Row(
                modifier = Modifier.align(Alignment.BottomStart),
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
    val palette = remember(item.book.id, item.book.title) { coverPaletteFor(item.book) }
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
    onReturn: (LoanEntity) -> Unit,
    onBookClick: (BookListItem) -> Unit,
) {
    val overdueCount = state.activeLoans.count { it.expectedReturnDateEpochMillis?.let { due -> due < System.currentTimeMillis() } == true }
    val borrowers = state.activeLoans.groupBy { it.borrowerName }
    ContentColumn {
        ScreenHeader(
            titleRes = R.string.screen_loans,
            meta = stringResource(R.string.loans_meta, state.activeLoans.size, overdueCount, borrowers.size),
            actions = {
                Button(
                    onClick = {},
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
                            books = state.allBooks,
                            items = state.visibleBooks,
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
    books: List<BookEntity>,
    items: List<BookListItem>,
    onReturn: (LoanEntity) -> Unit,
    onBookClick: (BookListItem) -> Unit,
) {
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
                Icon(Icons.Outlined.Call, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            loans.forEachIndexed { index, loan ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                val book = books.firstOrNull { it.id == loan.bookId } ?: return@forEachIndexed
                val item = items.firstOrNull { it.book.id == book.id } ?: BookListItem(book, null, loan)
                LoanBookRow(item = item, loan = loan, onReturn = onReturn, onBookClick = onBookClick)
            }
        }
    }
}

@Composable
private fun LoanBookRow(
    item: BookListItem,
    loan: LoanEntity,
    onReturn: (LoanEntity) -> Unit,
    onBookClick: (BookListItem) -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
    onExportJson: (Uri) -> Unit,
    onExportCsv: (Uri) -> Unit,
    onImportJson: (Uri) -> Unit,
) {
    val jsonFilename = stringResource(R.string.backup_json_filename)
    val csvFilename = stringResource(R.string.backup_csv_filename)
    val jsonExporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri -> if (uri != null) onExportJson(uri) }
    val csvExporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri -> if (uri != null) onExportCsv(uri) }
    val jsonImporter = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> if (uri != null) onImportJson(uri) }
    val readThisYear = state.stats.finishedBooks
    val reading = state.allBooks.count { it.readStatusCode == ReadStatusCode.Reading.code }
    val unread = state.allBooks.count { it.readStatusCode == ReadStatusCode.Unread.code }
    val goal = 24

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
                    ReadingRingCard(readThisYear, goal, Modifier.weight(1f))
                }
            }
            item {
                BackupCard(
                    onExportJson = { jsonExporter.launch(jsonFilename) },
                    onExportCsv = { csvExporter.launch(csvFilename) },
                    onImportJson = { jsonImporter.launch(arrayOf("application/json", "text/*")) },
                )
            }
        }
    }
}

@Composable
private fun StatsHero(total: Int, onLoan: Int) {
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
                Text(total.toString(), fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 62.sp, lineHeight = 66.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(stringResource(R.string.stats_on_loan), style = MaterialTheme.typography.labelMedium)
                Text(onLoan.toString(), fontFamily = FontFamily.Serif, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun StatTile(@StringRes label: Int, value: Int, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = color)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(stringResource(label).uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value.toString(), fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 30.sp)
        }
    }
}

@Composable
private fun LanguageBarCard(languages: Map<String, Int>) {
    ChartCard(title = R.string.stats_languages) {
        val total = languages.values.sum().coerceAtLeast(1)
        Row(
            modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(8.dp)),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            languages.entries.sortedByDescending { it.value }.forEachIndexed { index, entry ->
                Box(
                    modifier = Modifier
                        .weight(entry.value / total.toFloat())
                        .fillMaxHeight()
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
        Row(
            modifier = Modifier.fillMaxWidth().height(126.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            entries.ifEmpty { listOf(mapOf(stringResource(R.string.not_available) to 1).entries.first()) }.forEachIndexed { index, entry ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Bottom) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((96 * entry.value / maxValue).dp.coerceAtLeast(18.dp))
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
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(104.dp)) {
                drawArc(
                    color = Color.Gray.copy(alpha = 0.25f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 14.dp.toPx()),
                )
                drawArc(
                    color = Color(0xFF6F4E27),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 14.dp.toPx()),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(read.toString(), fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
                Text(stringResource(R.string.stats_goal_count, goal), fontSize = 9.sp)
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
private fun BackupCard(onExportJson: () -> Unit, onExportCsv: () -> Unit, onImportJson: () -> Unit) {
    ChartCard(title = R.string.section_backup_restore) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onExportJson) { Text(stringResource(R.string.action_export_json)) }
            OutlinedButton(onClick = onExportCsv) { Text(stringResource(R.string.action_export_csv)) }
            OutlinedButton(onClick = onImportJson) { Text(stringResource(R.string.action_import_json)) }
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
            Text(title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, textAlign = TextAlign.Center)
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
    onToggle: () -> Unit,
    onScan: () -> Unit,
    onManual: () -> Unit,
) {
    val rotation by animateFloatAsState(if (expanded) 45f else 0f, label = "fabRotation")
    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(spring()) + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FabMenuItem(icon = Icons.Outlined.QrCodeScanner, label = stringResource(R.string.action_scan_isbn), onClick = onScan)
                FabMenuItem(icon = Icons.Outlined.EditNote, label = stringResource(R.string.action_add_manually), onClick = onManual)
            }
        }
        FloatingActionButton(
            onClick = onToggle,
            modifier = Modifier.size(dimensionResource(R.dimen.fab_size)).shadow(10.dp, RoundedCornerShape(if (expanded) 28.dp else 20.dp)),
            shape = RoundedCornerShape(if (expanded) 28.dp else 20.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.action_add_book), modifier = Modifier.rotate(rotation).size(30.dp))
        }
    }
}

@Composable
private fun FabMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh, tonalElevation = 3.dp) {
            Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), style = MaterialTheme.typography.labelLarge)
        }
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
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddBookSheet(
    manualEntry: Boolean,
    initialIsbn: String,
    lookupInProgress: Boolean,
    onDismiss: () -> Unit,
    onLookup: (String, (com.mj.homelibrary.data.remote.BookMetadata?) -> Unit) -> Unit,
    onSave: (BookDraft) -> Unit,
) {
    var draft by remember(initialIsbn, manualEntry) {
        mutableStateOf(
            BookDraft(
                isbn = initialIsbn,
                languageCode = if (manualEntry) LanguageCode.Malayalam.code else LanguageCode.English.code,
            ),
        )
    }
    var showScanner by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                    Text(stringResource(R.string.add_book_title), fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
                    Text(
                        text = stringResource(if (manualEntry) R.string.manual_entry_subtitle else R.string.lookup_entry_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.content_description_close))
                }
            }
            if (lookupInProgress) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text(stringResource(R.string.lookup_in_progress), style = MaterialTheme.typography.bodySmall)
            }
            if (!manualEntry && draft.title.isNotBlank()) {
                MetadataLoadedCard(draft)
            }
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
                    enabled = draft.isbn.isNotBlank() && !lookupInProgress,
                    onClick = {
                        onLookup(draft.isbn) { metadata ->
                            if (metadata != null) draft = draft.applyMetadata(metadata)
                        }
                    },
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
            TextFieldLine(draft.authors, { draft = draft.copy(authors = it) }, R.string.field_authors)
            TextFieldLine(draft.publisher, { draft = draft.copy(publisher = it) }, R.string.field_publisher)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextFieldLine(draft.publishedYear, { draft = draft.copy(publishedYear = it) }, R.string.field_published_year, Modifier.weight(1f))
                TextFieldLine(draft.pageCount, { draft = draft.copy(pageCount = it) }, R.string.field_pages, Modifier.weight(1f))
            }
            TextFieldLine(draft.tags, { draft = draft.copy(tags = it) }, R.string.field_tags)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LanguageCode.entries.forEach { language ->
                    MorphChip(
                        selected = draft.languageCode == language.code,
                        label = stringResource(language.labelRes),
                        onClick = { draft = draft.copy(languageCode = language.code) },
                    )
                }
            }
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
            onBarcode = { draft = draft.copy(isbn = it) },
            onDismiss = { showScanner = false },
        )
    }
}

@Composable
private fun MetadataLoadedCard(draft: BookDraft) {
    Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BookCover(draft.toEntity(), modifier = Modifier.width(52.dp).height(78.dp), titleSize = 8)
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
private fun ShelfLocationField(draft: BookDraft, onDraftChange: (BookDraft) -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Outlined.Place, contentDescription = null)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.section_location), style = MaterialTheme.typography.labelSmall)
                TextFieldLine(draft.room, { onDraftChange(draft.copy(room = it)) }, R.string.field_room)
                TextFieldLine(draft.unit, { onDraftChange(draft.copy(unit = it)) }, R.string.field_unit)
                TextFieldLine(draft.shelf, { onDraftChange(draft.copy(shelf = it)) }, R.string.field_shelf)
            }
            Icon(Icons.Outlined.Edit, contentDescription = null)
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun BookDetailSheet(
    item: BookListItem,
    loans: List<LoanEntity>,
    onDismiss: () -> Unit,
    onLoan: () -> Unit,
    onReturn: (LoanEntity) -> Unit,
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
                    IconButton(onClick = onDismiss, colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.28f), contentColor = Color.White)) {
                        Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.content_description_close))
                    }
                    Row {
                        IconButton(onClick = {}, colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.28f), contentColor = Color.White)) {
                            Icon(Icons.Outlined.MoreVert, contentDescription = null)
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
                Text(item.book.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, lineHeight = 30.sp)
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
                LocationCard(item.location.displayBreadcrumb(context))
                if (item.activeLoan != null) {
                    LoanDetailCard(item = item, loan = item.activeLoan, onReturn = onReturn)
                } else {
                    Button(onClick = onLoan, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(16.dp)) {
                        Icon(Icons.Outlined.Outbound, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.loan_this_book))
                    }
                }
                RatingRow(item.book.rating ?: 0f)
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
private fun LocationCard(breadcrumb: String) {
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
            OutlinedButton(onClick = {}, shape = RoundedCornerShape(11.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
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
private fun RatingRow(rating: Float) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(5) { index ->
            Icon(
                Icons.Outlined.Star,
                contentDescription = null,
                tint = if (rating >= index + 0.5f) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(19.dp),
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
            Text(stringResource(R.string.loan_this_book), fontFamily = FontFamily.Serif, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
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
        title = { Text(stringResource(R.string.mark_returned_title), fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold) },
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

private val CoverPalettes = listOf(
    CoverPalette(Color(0xFF3B2A22), Color(0xFFC9A84C), Color(0xFFF1E4D0)),
    CoverPalette(Color(0xFFA8543A), Color(0xFFF3D9A4), Color(0xFFFFF1E2)),
    CoverPalette(Color(0xFF3D5C33), Color(0xFFC8DCC3), Color(0xFFEFF4EA)),
    CoverPalette(Color(0xFF26333F), Color(0xFF8FB3C9), Color(0xFFE6EEF4)),
    CoverPalette(Color(0xFFB58A2B), Color(0xFF3B2A22), Color(0xFFFFF6E0)),
    CoverPalette(Color(0xFF5A3550), Color(0xFFE0BBD6), Color(0xFFF7E9F4)),
    CoverPalette(Color(0xFF5C5A2E), Color(0xFFE3E0A8), Color(0xFFF5F3E0)),
    CoverPalette(Color(0xFF7A3B2E), Color(0xFFEFC9A8), Color(0xFFFBEDE3)),
)

private fun coverPaletteFor(book: BookEntity): CoverPalette {
    val key = (book.id.takeIf { it > 0 }?.hashCode() ?: book.title.hashCode()).absoluteValue
    return CoverPalettes[key % CoverPalettes.size]
}

@Composable
private fun chartColor(index: Int): Color = when (index % 4) {
    0 -> MaterialTheme.colorScheme.primary
    1 -> MaterialTheme.colorScheme.tertiary
    2 -> MaterialTheme.colorScheme.secondaryContainer
    else -> MaterialTheme.colorScheme.surfaceVariant
}
