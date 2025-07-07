// MainActivity.kt
package com.example.mytodolistapp // Make sure this matches your package name

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mytodolistapp.ui.theme.MyTodoListAppTheme // Make sure this path is correct for your project
import java.util.UUID

// Data class to represent a single To-Do item.
// Each item has a unique ID, a task description, and a completion status.
data class TodoItem(
    val id: String = UUID.randomUUID().toString(), // Unique ID for each task
    val task: String,
    var isCompleted: Boolean = false
)

// Main entry point for the application.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content of the activity using Jetpack Compose.
        setContent {
            // Apply the predefined theme to our application.
            MyTodoListAppTheme {
                // A surface container using the 'background' color from the theme.
                // This surface will fill the entire screen.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Call our main To-Do List composable.
                    TodoListScreen()
                }
            }
        }
    }
}

/**
 * Main composable screen for the To-Do List application.
 * Manages the state of the To-Do items and handles user interactions.
 */
@OptIn(ExperimentalMaterial3Api::class) // Opt-in for experimental Material 3 features like TopAppBar
@Composable
fun TodoListScreen() {
    // This mutableStateListOf will hold our To-Do items.
    // When this list changes, Compose automatically recomposes the UI.
    val todoItems = remember { mutableStateListOf<TodoItem>() }

    // State for the new task input field.
    var newTaskText by remember { mutableStateOf("") }

    // Scaffold provides a basic layout structure for Material Design components.
    Scaffold(
        topBar = {
            // TopAppBar for the title of the app.
            TopAppBar(
                title = { Text("Simple To-Do List") }
            )
        },
        content = { paddingValues -> // paddingValues ensures content is below the TopAppBar
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply padding from Scaffold
                    .padding(16.dp) // Add additional padding for content
            ) {
                // Input field and Add button for new To-Do items.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = newTaskText,
                        onValueChange = { newTaskText = it },
                        label = { Text("New Task") },
                        modifier = Modifier.weight(1f) // Takes up available space
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Space between text field and button
                    Button(
                        onClick = {
                            if (newTaskText.isNotBlank()) { // Only add if text is not empty
                                todoItems.add(TodoItem(task = newTaskText.trim()))
                                newTaskText = "" // Clear input field
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Space below the input area

                // LazyColumn is an efficient way to display a scrollable list of items.
                // It only composes and lays out items that are currently visible on screen.
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(todoItems, key = { it.id }) { todoItem -> // Use ID as key for efficient updates
                        TodoItemCard(
                            todoItem = todoItem,
                            onToggleComplete = { toggledItem ->
                                // Find the item by ID and update its completion status.
                                val index = todoItems.indexOfFirst { it.id == toggledItem.id }
                                if (index != -1) {
                                    todoItems[index] = toggledItem.copy(isCompleted = !toggledItem.isCompleted)
                                }
                            },
                            onDelete = { deletedItem ->
                                // Remove the item from the list.
                                todoItems.remove(deletedItem)
                            }
                        )
                    }
                }
            }
        }
    )
}

/**
 * Composable for displaying a single To-Do item.
 * Includes a checkbox, task text, and a delete button.
 */
@Composable
fun TodoItemCard(
    todoItem: TodoItem,
    onToggleComplete: (TodoItem) -> Unit, // Callback for toggling completion
    onDelete: (TodoItem) -> Unit // Callback for deleting the item
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Padding for each card
        elevation = CardDefaults.cardElevation(2.dp) // Shadow effect for the card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleComplete(todoItem) } // Make the whole row clickable to toggle
                .padding(16.dp), // Padding inside the card
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Checkbox to mark as completed.
            Checkbox(
                checked = todoItem.isCompleted,
                onCheckedChange = { /* Handled by row clickable */ }
            )

            // Task text, with strike-through if completed.
            Text(
                text = todoItem.task,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                textDecoration = if (todoItem.isCompleted) TextDecoration.LineThrough else null,
                style = MaterialTheme.typography.bodyLarge
            )

            // Delete button.
            IconButton(onClick = { onDelete(todoItem) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Task")
            }
        }
    }
}

/**
 * Preview function to see how the TodoListScreen looks in Android Studio's design pane.
 */
@Preview(showBackground = true, widthDp = 320)
@Composable
fun PreviewTodoListScreen() {
    MyTodoListAppTheme {
        TodoListScreen()
    }
}