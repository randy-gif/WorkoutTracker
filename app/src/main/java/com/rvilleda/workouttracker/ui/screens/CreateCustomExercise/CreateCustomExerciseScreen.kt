import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomExerciseScreen(
    onNavigateBack: () -> Unit,
) {
    var exerciseName by remember { mutableStateOf("") }
    var selectedMuscle by remember { mutableStateOf("") }
    var selectedEquipment by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val muscleGroups = listOf("Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Full Body")
    val equipmentList = listOf("Barbell", "Dumbbell", "Machine", "Cables", "Bodyweight", "Bands", "Other")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Custom Exercise") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {  },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = exerciseName.isNotBlank() && selectedMuscle.isNotBlank() && selectedEquipment.isNotBlank()
                ) {
                    Text("Save Exercise", modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OutlinedTextField(
                value = exerciseName,
                onValueChange = { exerciseName = it },
                label = { Text("Exercise Name") },
                placeholder = { Text("e.g., Deficit Deadlift") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Muscle Group Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Primary Muscle Group",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    muscleGroups.forEach { muscle ->
                        CustomCategoryChip(
                            label = muscle,
                            isHighlighted = selectedMuscle == muscle,
                            onClick = { selectedMuscle = muscle }
                        )
                    }
                }
            }

            // Equipment Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Equipment",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    equipmentList.forEach { equip ->
                        CustomCategoryChip(
                            label = equip,
                            isHighlighted = selectedEquipment == equip,
                            onClick = { selectedEquipment = equip }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Personal Notes & Cues (Optional)") },
                placeholder = { Text("e.g., Focus on a slow eccentric motion.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )
        }
    }
}

// Extracted Component implementing your exact color logic
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomCategoryChip(
    label: String,
    isHighlighted: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isHighlighted) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isHighlighted) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        onClick = onClick,
        color = backgroundColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.medium, // Gives it a nice rounded chip look
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            if (isHighlighted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}