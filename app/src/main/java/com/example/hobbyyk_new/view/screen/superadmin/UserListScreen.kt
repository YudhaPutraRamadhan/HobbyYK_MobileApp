package com.example.hobbyyk_new.view.screen.superadmin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hobbyyk_new.data.model.User
import com.example.hobbyyk_new.viewmodel.UserListViewModel
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(navController: NavController) {
    val viewModel: UserListViewModel = viewModel()

    var showEditDialog by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }

    if (showEditDialog && selectedUser != null) {
        EditUserDialog(
            user = selectedUser!!,
            onDismiss = { showEditDialog = false },
            onSave = { id, newName, newEmail, newRole, newVerified ->
                viewModel.updateUser(id, newName, newEmail, newRole, newVerified)
                showEditDialog = false
            }
        )
    }

    if (showCreateDialog) {
        CreateUserDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { name, email, role ->
                viewModel.createUser(name, email, role)
                showCreateDialog = false
            }
        )
    }

    if (showDeleteDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Pengguna?") },
            text = { Text("Apakah Anda yakin ingin memblokir/menghapus user '${selectedUser!!.username}'? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(selectedUser!!.id) // Eksekusi Hapus
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daftar Pengguna") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah User", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.users) { user ->
                        UserCard(
                            user = user,
                            onDelete = {
                                selectedUser = user
                                showDeleteDialog = true
                            },
                            onEdit = {
                                selectedUser = user
                                showEditDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.username, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = user.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if(user.role == "super_admin") Color(0xFFD32F2F) else Color(0xFF1976D2)
                        )
                    ) {
                        Text(
                            text = user.role,
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Badge Verified (Penting buat OTP)
                    if (user.isVerified) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Verified", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    } else {
                        Icon(Icons.Default.Warning, contentDescription = "Unverified", tint = Color(0xFFFF9800), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Belum OTP", color = Color(0xFFFF9800), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    if (user.role != "super_admin") {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Ban User", tint = Color.Red)
                        }
                    }
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Blue)
                    }
                }
            }
        }
    }
}

@Composable
fun EditUserDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (Int, String, String, String, Boolean) -> Unit
) {
    var username by remember { mutableStateOf(user.username) }
    var email by remember { mutableStateOf(user.email) }
    var role by remember { mutableStateOf(user.role) }
    var isVerified by remember { mutableStateOf(user.isVerified) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Pengguna") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })

                Text("Role:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = role == "user", onClick = { role = "user" })
                    Text("User")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = role == "admin", onClick = { role = "admin" })
                    Text("Admin Komunitas")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isVerified, onCheckedChange = { isVerified = it })
                    Text("Sudah Verifikasi (Verified)")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(user.id, username, email, role, isVerified) }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun CreateUserDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("user") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Pengguna Baru") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Password default: 123456", fontSize = 12.sp, color = Color.Gray)
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })

                Text("Role:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = role == "user", onClick = { role = "user" })
                    Text("User")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = role == "admin", onClick = { role = "admin" })
                    Text("Admin")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(username, email, role) }) { Text("Buat Akun") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}