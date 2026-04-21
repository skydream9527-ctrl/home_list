package com.family.recipe.presentation.screens.upload

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.runtime.LaunchedEffect
import com.family.recipe.domain.model.Ingredient
import com.family.recipe.domain.model.RecipeContent
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

sealed interface ContentDraft {
    data class StepDraft(val value: String = "") : ContentDraft
    data class TextDraft(val value: String = "") : ContentDraft
    data class ImageDraft(val uri: String = "", val caption: String = "") : ContentDraft
    data class VideoDraft(val uri: String = "", val caption: String = "") : ContentDraft
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    categoryId: String,
    editingRecipeId: String? = null,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: UploadViewModel = koinViewModel { parametersOf(categoryId) }
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var coverImageUri by remember { mutableStateOf<String?>(null) }
    var ingredients by remember { mutableStateOf(listOf(IngredientDraft())) }
    var contentDrafts by remember { mutableStateOf(listOf<ContentDraft>(ContentDraft.StepDraft())) }

    LaunchedEffect(editingRecipeId) {
        val id = editingRecipeId ?: return@LaunchedEffect
        viewModel.loadRecipeForEdit(id) { recipe, ings, contents ->
            name = recipe.name
            description = recipe.description
            coverImageUri = recipe.coverImageUri
            ingredients = if (ings.isEmpty()) listOf(IngredientDraft()) else ings.map { IngredientDraft(it.name, it.amount) }
            contentDrafts = if (contents.isEmpty()) listOf(ContentDraft.StepDraft()) else contents.map { content ->
                when (content) {
                    is RecipeContent.Step -> ContentDraft.StepDraft(content.value)
                    is RecipeContent.Text -> ContentDraft.TextDraft(content.value)
                    is RecipeContent.Image -> ContentDraft.ImageDraft(content.uri, content.caption)
                    is RecipeContent.Video -> ContentDraft.VideoDraft(content.uri, content.caption)
                }
            }
        }
    }

    val coverLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> coverImageUri = uri?.toString() }

    val imageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            contentDrafts = contentDrafts.map { draft ->
                if (draft is ContentDraft.ImageDraft && draft.uri.isEmpty()) draft.copy(uri = it.toString())
                else draft
            }
        }
    }

    val videoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            contentDrafts = contentDrafts.map { draft ->
                if (draft is ContentDraft.VideoDraft && draft.uri.isEmpty()) draft.copy(uri = it.toString())
                else draft
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editingRecipeId != null) "编辑菜谱" else "添加菜谱") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        if (viewModel.isSaving) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("菜谱名称") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("简介") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                item {
                    Column {
                        Text("封面图片", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))
                        if (coverImageUri != null) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(Uri.parse(coverImageUri))
                                        .crossfade(true).build(),
                                    contentDescription = "封面",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(onClick = { coverImageUri = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "移除")
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    coverLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("选择封面图片")
                            }
                        }
                    }
                }

                item {
                    Column {
                        Text("食材清单", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))
                        ingredients.forEachIndexed { index, draft ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = draft.name,
                                    onValueChange = { newName ->
                                        ingredients = ingredients.toMutableList().apply {
                                            this[index] = draft.copy(name = newName)
                                        }
                                    },
                                    label = { Text("食材") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = draft.amount,
                                    onValueChange = { newAmount ->
                                        ingredients = ingredients.toMutableList().apply {
                                            this[index] = draft.copy(amount = newAmount)
                                        }
                                    },
                                    label = { Text("用量") },
                                    modifier = Modifier.width(100.dp),
                                    singleLine = true
                                )
                                IconButton(onClick = {
                                    ingredients = ingredients.toMutableList().apply { removeAt(index) }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        OutlinedButton(
                            onClick = { ingredients = ingredients + IngredientDraft() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("添加食材")
                        }
                    }
                }

                item {
                    Text("制作步骤", style = MaterialTheme.typography.titleSmall)
                }

                itemsIndexed(contentDrafts) { index, draft ->
                    ContentDraftCard(
                        index = index,
                        draft = draft,
                        onUpdate = { updated ->
                            contentDrafts = contentDrafts.toMutableList().apply { this[index] = updated }
                        },
                        onRemove = {
                            contentDrafts = contentDrafts.toMutableList().apply { removeAt(index) }
                        },
                        onPickImage = { imageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        onPickVideo = { videoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)) }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(onClick = {
                            contentDrafts = contentDrafts + ContentDraft.StepDraft()
                        }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("步骤")
                        }
                        OutlinedButton(onClick = {
                            contentDrafts = contentDrafts + ContentDraft.ImageDraft()
                        }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("图片")
                        }
                        OutlinedButton(onClick = {
                            contentDrafts = contentDrafts + ContentDraft.VideoDraft()
                        }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("视频")
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            val finalIngredients = ingredients
                                .filter { it.name.isNotBlank() }
                                .map { Ingredient(name = it.name, amount = it.amount) }
                            val finalContents = contentDrafts.mapNotNull { draft ->
                                when (draft) {
                                    is ContentDraft.StepDraft -> if (draft.value.isNotBlank()) RecipeContent.Step(draft.value) else null
                                    is ContentDraft.TextDraft -> if (draft.value.isNotBlank()) RecipeContent.Text(draft.value) else null
                                    is ContentDraft.ImageDraft -> if (draft.uri.isNotEmpty()) RecipeContent.Image(draft.uri, draft.caption) else null
                                    is ContentDraft.VideoDraft -> if (draft.uri.isNotEmpty()) RecipeContent.Video(draft.uri, draft.caption) else null
                                }
                            }
                            if (editingRecipeId != null) {
                                viewModel.updateRecipe(
                                    editingRecipeId, name, description, coverImageUri,
                                    finalIngredients, finalContents, onSuccess
                                ) { /* error handled */ }
                            } else {
                                viewModel.saveRecipe(
                                    name, description, coverImageUri,
                                    finalIngredients, finalContents, onSuccess
                                ) { /* error handled */ }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = name.isNotBlank()
                    ) {
                        Text(if (editingRecipeId != null) "保存修改" else "添加菜谱")
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

data class IngredientDraft(val name: String = "", val amount: String = "")

@Composable
private fun ContentDraftCard(
    index: Int,
    draft: ContentDraft,
    onUpdate: (ContentDraft) -> Unit,
    onRemove: () -> Unit,
    onPickImage: () -> Unit,
    onPickVideo: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DragHandle, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        when (draft) {
                            is ContentDraft.StepDraft -> "步骤 ${index + 1}"
                            is ContentDraft.TextDraft -> "文本"
                            is ContentDraft.ImageDraft -> "图片"
                            is ContentDraft.VideoDraft -> "视频"
                        },
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Close, contentDescription = "移除", modifier = Modifier.size(20.dp))
                }
            }

            when (draft) {
                is ContentDraft.StepDraft -> {
                    OutlinedTextField(
                        value = draft.value,
                        onValueChange = { onUpdate(draft.copy(value = it)) },
                        label = { Text("步骤描述") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                is ContentDraft.TextDraft -> {
                    OutlinedTextField(
                        value = draft.value,
                        onValueChange = { onUpdate(draft.copy(value = it)) },
                        label = { Text("文本内容") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                is ContentDraft.ImageDraft -> {
                    if (draft.uri.isNotEmpty()) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(Uri.parse(draft.uri)).crossfade(true).build(),
                                contentDescription = "图片预览",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(MaterialTheme.shapes.small),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(onClick = { onUpdate(draft.copy(uri = "")) }) {
                                Icon(Icons.Default.Close, contentDescription = "移除图片")
                            }
                        }
                    } else {
                        OutlinedButton(onClick = onPickImage, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("选择图片")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = draft.caption,
                        onValueChange = { onUpdate(draft.copy(caption = it)) },
                        label = { Text("图片说明（可选）") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                is ContentDraft.VideoDraft -> {
                    if (draft.uri.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("已选择视频", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { onUpdate(draft.copy(uri = "")) }) {
                                Icon(Icons.Default.Close, contentDescription = "移除视频")
                            }
                        }
                    } else {
                        OutlinedButton(onClick = onPickVideo, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Movie, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("选择视频")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = draft.caption,
                        onValueChange = { onUpdate(draft.copy(caption = it)) },
                        label = { Text("视频说明（可选）") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}
