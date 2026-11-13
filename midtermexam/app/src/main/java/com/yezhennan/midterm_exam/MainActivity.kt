package com.yezhennan.midterm_exam

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ListItem
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yezhennan.midterm_exam.entity.Book
import com.yezhennan.midterm_exam.ui.theme.MidtermexamTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        title = getString(R.string.screen_title)
        setContent {
            MidtermexamTheme {
                BookListScreen(
                    books = createBooks().sortedBy { it.price },
                    onBookClick = { book ->
                        startActivity(
                            Intent(this, BookDetailActivity::class.java)
                                .putExtra(getString(R.string.book_extra), book)
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookListScreen(
    books: List<Book>,
    onBookClick: (Book) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.book_list_title))
                        Text(
                            text = stringResource(R.string.book_list_summary, books.size),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(books, key = { it.isbn }) { book ->
                BookListItem(book = book, onClick = { onBookClick(book) })
            }
        }
    }
}

@Composable
private fun BookListItem(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(book.title, fontWeight = FontWeight.SemiBold)
            },
            supportingContent = {
                Text(stringResource(R.string.author_publisher, book.author, book.publisher))
            },
            trailingContent = {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text(
                        text = stringResource(R.string.book_price, book.price),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.forward_arrow),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}

private fun MainActivity.createBooks() = listOf(
    Book(
        title = getString(R.string.book_alive),
        author = getString(R.string.author_yu_hua),
        publisher = getString(R.string.publisher_writer),
        price = 25.00,
        isbn = getString(R.string.isbn_alive),
        description = getString(R.string.description_alive)
    ),
    Book(
        title = getString(R.string.book_sapiens),
        author = getString(R.string.author_harari),
        publisher = getString(R.string.publisher_citic),
        price = 68.00,
        isbn = getString(R.string.isbn_sapiens),
        description = getString(R.string.description_sapiens)
    ),
    Book(
        title = getString(R.string.book_little_prince),
        author = getString(R.string.author_exupery),
        publisher = getString(R.string.publisher_people),
        price = 32.00,
        isbn = getString(R.string.isbn_little_prince),
        description = getString(R.string.description_little_prince)
    ),
    Book(
        title = getString(R.string.book_one_hundred_years),
        author = getString(R.string.author_marquez),
        publisher = getString(R.string.publisher_nanhai),
        price = 55.00,
        isbn = getString(R.string.isbn_one_hundred_years),
        description = getString(R.string.description_one_hundred_years)
    )
)