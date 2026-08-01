package com.mj.homelibrary.ui

import androidx.annotation.StringRes
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

private val GridMinCellSize = 148.dp

private enum class HomeTab(@StringRes val labelRes: Int, val icon: ImageVector) {
    Library(R.string.nav_library, Icons.Outlined.Book),
    Shelves(R.string.nav_shelves, Icons.Outlined.Place),
    Loans(R.string.nav_loans, Icons.Outlined.People),
    Stats(R.string.nav_stats, Icons.Outlined.BarChart),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLibraryApp(viewModel: HomeLibraryViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(HomeTab.Library) }
    var showAddBook by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<BookListItem?>(null) }
    var loanBook by remember { mutableStateOf<BookEntity?>(null) }

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
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { showAddBook = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.action_add_book),
                        )
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                HomeTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(stringResource(tab.labelRes)) },
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (selectedTab == HomeTab.Library) {
                FloatingActionButton(onClick = { showAddBook = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(R.string.action_add_book),
                    )
                }
            }
        },
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (selectedTab) {
                HomeTab.Library -> LibraryScreen(
                    state = state,
                    onQueryChange = viewModel::setQuery,
                    onSortChange = viewModel::setSort,
                    onGridModeChange = viewModel::setGridMode,
                    onLanguageChange = viewModel::setLanguage,
                    onReadStatusChange = viewModel::setReadStatus,
                    onBookClick = { selectedBook = it },
                )

                HomeTab.Shelves -> ShelvesScreen(state = state, onBookClick = { selectedBook = it })
                HomeTab.Loans -> LoansScreen(
                    state = state,
                    onReturn = viewModel::markReturned,
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

    if (showAddBook) {
        AddBookDialog(
            lookupInProgress = state.transient.lookupInProgress,
            onDismiss = { showAddBook = false },
            onLookup = viewModel::lookupIsbn,
            onSave = { draft -> viewModel.addBook(draft) { showAddBook = false } },
        )
    }

    selectedBook?.let { item ->
        BookDetailDialog(
            item = item,
            loans = state.loans.filter { it.bookId == item.book.id },
            onDismiss = { selectedBook = null },
            onLoan = { loanBook = item.book },
            onReturn = viewModel::markReturned,
        )
    }

    loanBook?.let { book ->
        LoanBookDialog(
            book = book,
            onDismiss = { loanBook = null },
            onSave = { draft -> viewModel.loanBook(draft) { loanBook = null } },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScreen(
    state: HomeLibraryUiState,
    onQueryChange: (String) -> Unit,
    onSortChange: (BookSortCode) -> Unit,
    onGridModeChange: (Boolean) -> Unit,
    onLanguageChange: (String?) -> Unit,
    onReadStatusChange: (String?) -> Unit,
    onBookClick: (BookListItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.space_lg)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = state.filters.query,
                onValueChange = onQueryChange,
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null)
                },
                label = { Text(stringResource(R.string.search_books)) },
                placeholder = { Text(stringResource(R.string.search_hint)) },
                singleLine = true,
            )
            SortMenu(selected = state.filters.sort, onSortChange = onSortChange)
            IconButton(onClick = { onGridModeChange(!state.filters.gridMode) }) {
                Icon(
                    imageVector = if (state.filters.gridMode) Icons.Outlined.ViewList else Icons.Outlined.GridView,
                    contentDescription = stringResource(R.string.content_description_switch_view),
                )
            }
        }

        LibraryFiltersRow(
            filters = state.filters,
            onLanguageChange = onLanguageChange,
            onReadStatusChange = onReadStatusChange,
        )

        if (state.visibleBooks.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.empty_library_title),
                body = stringResource(R.string.empty_library_body),
            )
        } else if (state.filters.gridMode) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Adaptive(GridMinCellSize),
                contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_xl)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
            ) {
                items(state.visibleBooks, key = { it.book.id }) { item ->
                    BookGridCard(item = item, onClick = { onBookClick(item) })
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
                contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_xl)),
            ) {
                items(state.visibleBooks, key = { it.book.id }) { item ->
                    BookListRow(item = item, onClick = { onBookClick(item) })
                }
            }
        }
    }
}

@Composable
private fun SortMenu(selected: BookSortCode, onSortChange: (BookSortCode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Outlined.Sort,
                contentDescription = stringResource(R.string.content_description_sort),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            BookSortCode.entries.forEach { sort ->
                DropdownMenuItem(
                    text = { Text(stringResource(sort.labelRes)) },
                    onClick = {
                        onSortChange(sort)
                        expanded = false
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = selected == sort,
                            enter = fadeIn(spring()),
                            exit = fadeOut(spring()),
                        ) {
                            AssistChip(onClick = {}, label = { Text(stringResource(sort.labelRes)) })
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun LibraryFiltersRow(
    filters: LibraryFilters,
    onLanguageChange: (String?) -> Unit,
    onReadStatusChange: (String?) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.space_xs)),
    ) {
        item {
            FilterChip(
                selected = filters.languageCode == null,
                onClick = { onLanguageChange(null) },
                label = { Text(stringResource(R.string.filter_all_languages)) },
            )
        }
        items(LanguageCode.entries) { language ->
            FilterChip(
                selected = filters.languageCode == language.code,
                onClick = { onLanguageChange(language.code) },
                label = { Text(stringResource(language.labelRes)) },
            )
        }
        item {
            Spacer(Modifier.width(dimensionResource(R.dimen.space_md)))
        }
        item {
            FilterChip(
                selected = filters.readStatusCode == null,
                onClick = { onReadStatusChange(null) },
                label = { Text(stringResource(R.string.filter_all_statuses)) },
            )
        }
        items(ReadStatusCode.entries) { status ->
            FilterChip(
                selected = filters.readStatusCode == status.code,
                onClick = { onReadStatusChange(status.code) },
                label = { Text(stringResource(status.labelRes)) },
            )
        }
    }
}

@Composable
private fun BookGridCard(item: BookListItem, onClick: () -> Unit) {
    val context = LocalContext.current
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner)),
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.space_sm)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
        ) {
            BookCover(
                book = item.book,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.68f),
            )
            Text(
                text = item.book.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.book.authors.displayAuthors(context),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            LoanStatusChip(item)
        }
    }
}

@Composable
private fun BookListRow(item: BookListItem, onClick: () -> Unit) {
    val context = LocalContext.current
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.space_md)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BookCover(
                book = item.book,
                modifier = Modifier.size(dimensionResource(R.dimen.cover_list_size)),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.book.authors.displayAuthors(context),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.location.displayBreadcrumb(context),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            LoanStatusChip(item)
        }
    }
}

@Composable
private fun BookCover(book: BookEntity, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(dimensionResource(R.dimen.card_corner)))
            .background(MaterialTheme.colorScheme.tertiaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (!book.coverUrl.isNullOrBlank() || !book.coverImagePath.isNullOrBlank()) {
            AsyncImage(
                model = book.coverImagePath ?: book.coverUrl,
                contentDescription = stringResource(R.string.content_description_book_cover),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = book.title.firstOrNull()?.uppercase().orEmpty(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}

@Composable
private fun LoanStatusChip(item: BookListItem) {
    val label = when {
        item.isOverdue -> R.string.loan_overdue
        item.isOnLoan -> R.string.loan_on_loan
        else -> R.string.loan_available
    }
    AssistChip(onClick = {}, label = { Text(stringResource(label)) })
}

@Composable
private fun ShelvesScreen(
    state: HomeLibraryUiState,
    onBookClick: (BookListItem) -> Unit,
) {
    if (state.locations.isEmpty()) {
        EmptyState(
            title = stringResource(R.string.empty_shelves_title),
            body = stringResource(R.string.empty_shelves_body),
        )
        return
    }

    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.space_lg)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_xl)),
    ) {
        state.locations.groupBy { it.room }.forEach { (room, roomLocations) ->
            item {
                Text(room, style = MaterialTheme.typography.titleLarge)
            }
            roomLocations.groupBy { it.unit }.forEach { (unit, unitLocations) ->
                item {
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                items(unitLocations, key = { it.id }) { location ->
                    val books = state.visibleBooks.filter { it.location?.id == location.id }
                    ElevatedCard(shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner))) {
                        Column(
                            modifier = Modifier.padding(dimensionResource(R.dimen.space_md)),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(location.shelf, style = MaterialTheme.typography.titleMedium)
                                AssistChip(
                                    onClick = {},
                                    label = { Text(stringResource(R.string.audit_expected_count, books.size)) },
                                )
                            }
                            Text(
                                text = stringResource(R.string.shelf_audit),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            books.forEach { item ->
                                TextButton(onClick = { onBookClick(item) }) {
                                    Text(
                                        text = item.book.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            if (books.isEmpty()) {
                                Text(
                                    text = location.displayBreadcrumb(context),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoansScreen(
    state: HomeLibraryUiState,
    onReturn: (Long) -> Unit,
    onBookClick: (BookListItem) -> Unit,
) {
    if (state.activeLoans.isEmpty()) {
        EmptyState(
            title = stringResource(R.string.empty_loans_title),
            body = stringResource(R.string.empty_loans_body),
        )
        return
    }

    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.space_lg)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_xl)),
    ) {
        items(state.activeLoans, key = { it.id }) { loan ->
            val item = state.visibleBooks.firstOrNull { it.book.id == loan.bookId }
                ?: state.allBooks.firstOrNull { it.id == loan.bookId }?.let {
                    BookListItem(book = it, location = null, activeLoan = loan)
                }
            if (item != null) {
                ElevatedCard(
                    onClick = { onBookClick(item) },
                    shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner)),
                ) {
                    Column(
                        modifier = Modifier.padding(dimensionResource(R.dimen.space_md)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
                    ) {
                        Text(item.book.title, style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.loaned_to, loan.borrowerName))
                        Text(stringResource(R.string.due_on, loan.expectedReturnDateEpochMillis.displayDate(context)))
                        OutlinedButton(onClick = { onReturn(loan.id) }) {
                            Text(stringResource(R.string.action_mark_returned))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsScreen(
    state: HomeLibraryUiState,
    onExportJson: (android.net.Uri) -> Unit,
    onExportCsv: (android.net.Uri) -> Unit,
    onImportJson: (android.net.Uri) -> Unit,
) {
    val jsonFilename = stringResource(R.string.backup_json_filename)
    val csvFilename = stringResource(R.string.backup_csv_filename)
    val jsonExporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) onExportJson(uri)
    }
    val csvExporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) onExportCsv(uri)
    }
    val jsonImporter = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) onImportJson(uri)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.space_lg)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_xl)),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
            ) {
                StatCard(label = R.string.stats_total_books, value = state.stats.totalBooks, modifier = Modifier.weight(1f))
                StatCard(label = R.string.stats_on_loan, value = state.stats.activeLoans, modifier = Modifier.weight(1f))
                StatCard(label = R.string.stats_finished, value = state.stats.finishedBooks, modifier = Modifier.weight(1f))
            }
        }
        item {
            BreakdownCard(title = R.string.stats_languages, entries = state.stats.languages)
        }
        item {
            BreakdownCard(title = R.string.stats_genres, entries = state.stats.genres)
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner))) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.space_md)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
                ) {
                    Text(stringResource(R.string.section_backup_restore), style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm))) {
                        OutlinedButton(onClick = { jsonExporter.launch(jsonFilename) }) {
                            Text(stringResource(R.string.action_export_json))
                        }
                        OutlinedButton(onClick = { csvExporter.launch(csvFilename) }) {
                            Text(stringResource(R.string.action_export_csv))
                        }
                    }
                    OutlinedButton(onClick = { jsonImporter.launch(arrayOf("application/json", "text/*")) }) {
                        Text(stringResource(R.string.action_import_json))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(@StringRes label: Int, value: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.space_md)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_xs)),
        ) {
            Text(stringResource(label), style = MaterialTheme.typography.labelMedium)
            Text(stringResource(R.string.stat_count, value), style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun BreakdownCard(@StringRes title: Int, entries: Map<String, Int>) {
    val context = LocalContext.current
    ElevatedCard(shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner))) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.space_md)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
        ) {
            Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
            entries.ifEmpty { mapOf(context.getString(R.string.not_available) to 0) }.forEach { (key, count) ->
                val label = LanguageCode.entries.firstOrNull { it.code == key }?.let { context.getString(it.labelRes) } ?: key
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(label, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(stringResource(R.string.stat_count, count))
                }
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, body: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.space_xl)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm)),
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddBookDialog(
    lookupInProgress: Boolean,
    onDismiss: () -> Unit,
    onLookup: (String, (com.mj.homelibrary.data.remote.BookMetadata?) -> Unit) -> Unit,
    onSave: (BookDraft) -> Unit,
) {
    var draft by remember { mutableStateOf(BookDraft()) }
    var showScanner by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onSave(draft) }) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        title = { Text(stringResource(R.string.add_book_title)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
            ) {
                if (lookupInProgress) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text(stringResource(R.string.lookup_in_progress), style = MaterialTheme.typography.bodySmall)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm))) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = draft.isbn,
                        onValueChange = { draft = draft.copy(isbn = it) },
                        label = { Text(stringResource(R.string.field_isbn)) },
                        singleLine = true,
                    )
                    IconButton(onClick = { showScanner = true }) {
                        Icon(
                            imageVector = Icons.Outlined.QrCodeScanner,
                            contentDescription = stringResource(R.string.action_scan_isbn),
                        )
                    }
                }
                OutlinedButton(
                    enabled = draft.isbn.isNotBlank() && !lookupInProgress,
                    onClick = {
                        onLookup(draft.isbn) { metadata ->
                            if (metadata != null) draft = draft.applyMetadata(metadata)
                        }
                    },
                ) {
                    Text(stringResource(R.string.action_lookup_isbn))
                }
                Text(stringResource(R.string.section_metadata), style = MaterialTheme.typography.titleSmall)
                TextFieldLine(draft.title, { draft = draft.copy(title = it) }, R.string.field_title)
                TextFieldLine(draft.subtitle, { draft = draft.copy(subtitle = it) }, R.string.field_subtitle)
                TextFieldLine(draft.authors, { draft = draft.copy(authors = it) }, R.string.field_authors)
                TextFieldLine(draft.originalScriptTitle, { draft = draft.copy(originalScriptTitle = it) }, R.string.field_original_title)
                TextFieldLine(draft.tags, { draft = draft.copy(tags = it) }, R.string.field_tags)
                TextFieldLine(draft.publisher, { draft = draft.copy(publisher = it) }, R.string.field_publisher)
                Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm))) {
                    TextFieldLine(
                        value = draft.publishedYear,
                        onValueChange = { draft = draft.copy(publishedYear = it) },
                        label = R.string.field_published_year,
                        modifier = Modifier.weight(1f),
                    )
                    TextFieldLine(
                        value = draft.pageCount,
                        onValueChange = { draft = draft.copy(pageCount = it) },
                        label = R.string.field_pages,
                        modifier = Modifier.weight(1f),
                    )
                }

                Text(stringResource(R.string.field_language), style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm))) {
                    LanguageCode.entries.forEach { language ->
                        FilterChip(
                            selected = draft.languageCode == language.code,
                            onClick = { draft = draft.copy(languageCode = language.code) },
                            label = { Text(stringResource(language.labelRes)) },
                        )
                    }
                }

                Text(stringResource(R.string.section_reading), style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm))) {
                    ReadStatusCode.entries.forEach { status ->
                        FilterChip(
                            selected = draft.readStatusCode == status.code,
                            onClick = { draft = draft.copy(readStatusCode = status.code) },
                            label = { Text(stringResource(status.labelRes)) },
                        )
                    }
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_sm))) {
                    BookFormatCode.entries.forEach { format ->
                        FilterChip(
                            selected = draft.formatCode == format.code,
                            onClick = { draft = draft.copy(formatCode = format.code) },
                            label = { Text(stringResource(format.labelRes)) },
                        )
                    }
                }
                Text(stringResource(R.string.rating_label), style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = draft.rating,
                    onValueChange = { draft = draft.copy(rating = it) },
                    valueRange = 0f..5f,
                    steps = 4,
                )
                TextFieldLine(draft.notes, { draft = draft.copy(notes = it) }, R.string.field_notes, singleLine = false)

                Text(stringResource(R.string.section_location), style = MaterialTheme.typography.titleSmall)
                TextFieldLine(draft.room, { draft = draft.copy(room = it) }, R.string.field_room)
                TextFieldLine(draft.unit, { draft = draft.copy(unit = it) }, R.string.field_unit)
                TextFieldLine(draft.shelf, { draft = draft.copy(shelf = it) }, R.string.field_shelf)
                TextFieldLine(draft.positionNote, { draft = draft.copy(positionNote = it) }, R.string.field_position_note)
            }
        },
    )

    if (showScanner) {
        BarcodeScannerSheet(
            onBarcode = { draft = draft.copy(isbn = it) },
            onDismiss = { showScanner = false },
        )
    }
}

@Composable
private fun TextFieldLine(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes label: Int,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
    )
}

@Composable
private fun BookDetailDialog(
    item: BookListItem,
    loans: List<LoanEntity>,
    onDismiss: () -> Unit,
    onLoan: () -> Unit,
    onReturn: (Long) -> Unit,
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (item.activeLoan != null) {
                TextButton(onClick = { onReturn(item.activeLoan.id) }) {
                    Text(stringResource(R.string.action_mark_returned))
                }
            } else {
                TextButton(onClick = onLoan) {
                    Text(stringResource(R.string.action_loan_book))
                }
            }
        },
        dismissButton = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.content_description_close))
            }
        },
        title = { Text(stringResource(R.string.book_detail)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md)),
            ) {
                BookCover(
                    book = item.book,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(R.dimen.cover_grid_height)),
                )
                Text(item.book.title, style = MaterialTheme.typography.titleLarge)
                item.book.subtitle?.let { Text(it, style = MaterialTheme.typography.titleMedium) }
                Text(item.book.authors.displayAuthors(context), style = MaterialTheme.typography.bodyLarge)
                LoanStatusChip(item)
                Text(item.location.displayBreadcrumb(context), style = MaterialTheme.typography.bodyMedium)
                item.book.positionNote?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                item.book.publisher?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                item.book.publishedYear?.let { Text(stringResource(R.string.published_year_label, it)) }
                item.book.pageCount?.let { Text(stringResource(R.string.pages_label, it)) }
                item.book.rating?.let { Text(stringResource(R.string.rating_value, it)) }
                item.book.notes?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                Text(stringResource(R.string.loan_history), style = MaterialTheme.typography.titleMedium)
                loans.forEach { loan ->
                    Text(
                        text = stringResource(R.string.loaned_to, loan.borrowerName),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = stringResource(R.string.due_on, loan.expectedReturnDateEpochMillis.displayDate(context)),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
    )
}

@Composable
private fun LoanBookDialog(
    book: BookEntity,
    onDismiss: () -> Unit,
    onSave: (LoanDraft) -> Unit,
) {
    var draft by remember { mutableStateOf(LoanDraft(bookId = book.id)) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onSave(draft) }) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        title = { Text(stringResource(R.string.loan_book_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_md))) {
                Text(book.title, style = MaterialTheme.typography.titleMedium)
                TextFieldLine(draft.borrowerName, { draft = draft.copy(borrowerName = it) }, R.string.field_borrower_name)
                TextFieldLine(draft.borrowerContact, { draft = draft.copy(borrowerContact = it) }, R.string.field_borrower_contact)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = draft.dueDate,
                    onValueChange = { draft = draft.copy(dueDate = it) },
                    label = { Text(stringResource(R.string.field_due_date)) },
                    placeholder = { Text(stringResource(R.string.field_due_date_hint)) },
                    singleLine = true,
                )
                TextFieldLine(draft.notes, { draft = draft.copy(notes = it) }, R.string.field_notes, singleLine = false)
            }
        },
    )
}
