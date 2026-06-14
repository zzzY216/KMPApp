package org.example.kmpapp.ui.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpapp.shared.generated.resources.Res
import kmpapp.shared.generated.resources.compose_multiplatform
import org.example.kmpapp.domain.Product
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProductDetailScreen(
    name: String,
    price: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "商品详情") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackClick
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(16.dp)
            ) {
                Button(
                    onClick = {
                        // TODO 加入购物车
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "立即购买:${price}", fontSize = 18.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        ProductDetailContent(
            modifier = Modifier.padding(innerPadding),
            product = Product(
                id = 1,
                name = "你好呀",
                price = "9999￥"
            )
        )
    }
}

@Composable
fun ProductDetailContent(
    modifier: Modifier,
    product: Product
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(Res.drawable.compose_multiplatform),
            contentDescription = product.name,
            modifier = Modifier.fillMaxWidth()
                .aspectRatio(1.2f),
            contentScale = ContentScale.Fit
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = product.price,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(83.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Text(
                text = "商品详情描述",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "这是一段关于 ${product.name} 的详细介绍。采用了 Compose Multiplatform 技术开发，具备高性能、跨平台的特性。不仅在 Android 上表现出色，在 iOS 和 Desktop 也能流畅运行。",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}