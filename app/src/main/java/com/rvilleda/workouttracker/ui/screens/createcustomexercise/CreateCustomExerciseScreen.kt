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
import com.rvilleda.workouttracker.model.Equipment
import com.rvilleda.workouttracker.model.MuscleGroup
import com.rvilleda.workouttracker.model.MovementType
import com.rvilleda.workouttracker.ui.screens.exercises.CreateCustomExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomExerciseScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateCustomExerciseViewModel // Inject the ViewModel here
) {
    // 1. Observe the state from the ViewModel
    // Using .collectAsState() automatically triggers a UI recomposition when the data changes
    val exerciseName by viewModel.exerciseName.collectAsState()
    val selectedMuscle by viewModel.selectedMuscle.collectAsState()
    val selectedEquipment by viewModel.selectedEquipment.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val isSaveEnabled by viewModel.isSaveEnabled.collectAsState()

    val muscleGroups = MuscleGroup.values()
    val equipmentList = Equipment.values()

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
                    // 2. Call the ViewModel to save, passing the back navigation as the success callback
                    onClick = { viewModel.saveExercise(onSuccess = onNavigateBack) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isSaveEnabled // 3. Bound directly to the ViewModel's calculation
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
                // 4. Send user input back to the ViewModel
                onValueChange = { viewModel.updateName(it) },
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
                        FilterChip(
                            selected = selectedMuscle == muscle,
                            onClick = { viewModel.updateMuscle(muscle) },
                            label = { Text(muscle.displayName) },
                            leadingIcon = if (selectedMuscle == muscle) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
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
                        FilterChip(
                            selected = selectedEquipment == equip,
                            onClick = { viewModel.updateEquipment(equip) },
                            label = { Text(equip.displayName) },
                            leadingIcon = if (selectedEquipment == equip) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { viewModel.updateNotes(it) }, // Send input to ViewModel
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