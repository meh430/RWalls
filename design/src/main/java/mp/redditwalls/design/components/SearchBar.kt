package mp.redditwalls.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mp.redditwalls.design.R
import mp.redditwalls.design.RwTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = stringResource(R.string.search),
    value: String = "",
    enabled: Boolean = true,
    onValueChanged: (String) -> Unit = {},
    onSearch: () -> Unit = {},
    leadingIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.padding(start = 16.dp),
            tint = MaterialTheme.colorScheme.outline
        )
    },
    trailingIcon: (@Composable () -> Unit)? = null,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 24.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon()
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            BasicTextField(
                modifier = Modifier.focusRequester(focusRequester),
                enabled = enabled,
                value = value,
                onValueChange = onValueChanged,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions {
                    keyboardController?.hide()
                    onSearch()
                }
            )
            if (value.isEmpty()) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        if (trailingIcon != null) {
            trailingIcon()
        }
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    var query by remember { mutableStateOf("") }
    RwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SearchBar(
                value = query,
                onValueChanged = { query = it },
                onSearch = {},
                hint = stringResource(id = R.string.search)
            )
        }
    }
}