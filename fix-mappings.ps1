# Fix Minecraft Class Mappings
param(
    [string]$FilePath
)

if (-not (Test-Path $FilePath)) {
    Write-Error "File not found: $FilePath"
    exit 1
}

$content = Get-Content $FilePath -Raw

# Core class mappings
$content = $content -replace 'class_310', 'MinecraftClient'
$content = $content -replace 'class_304', 'KeyBinding'
$content = $content -replace 'class_2561', 'Text'
$content = $content -replace 'class_332', 'DrawContext'
$content = $content -replace 'class_342', 'TextFieldWidget'
$content = $content -replace 'class_4185', 'ButtonWidget'
$content = $content -replace 'class_437', 'Screen'

# Items and Inventory
$content = $content -replace 'class_1799', 'ItemStack'
$content = $content -replace 'class_2487', 'ItemStack'
$content = $content -replace 'class_2499', 'Item'
$content = $content -replace 'class_1735', 'Slot'
$content = $content -replace 'class_1713', 'ClickType'

# GUI and Screens
$content = $content -replace 'class_465', 'HandledScreen'
$content = $content -replace 'class_490', 'InventoryScreen'

# World and Blocks
$content = $content -replace 'class_2338', 'BlockPos'
$content = $content -replace 'class_2350', 'Direction'
$content = $content -replace 'class_243', 'Vec3d'
$content = $content -replace 'class_3965', 'BlockHitResult'
$content = $content -replace 'class_3966', 'EntityHitResult'
$content = $content -replace 'class_1268', 'Hand'

# Input utilities
$content = $content -replace 'class_3675', 'InputUtil'

# Method mappings
$content = $content -replace 'method_1551\(\)', 'getInstance()'
$content = $content -replace 'method_1507', 'setScreen'
$content = $content -replace 'method_1436\(\)', 'wasPressed()'
$content = $content -replace 'method_25394', 'render'
$content = $content -replace 'method_25426\(\)', 'init()'
$content = $content -replace 'method_37067\(\)', 'clearChildren()'
$content = $content -replace 'method_37063', 'addDrawableChild'
$content = $content -replace 'method_1880', 'setMaxLength'
$content = $content -replace 'method_1852', 'setText'
$content = $content -replace 'method_1882\(\)', 'getText()'
$content = $content -replace 'method_1875', 'setSelectionStart'
$content = $content -replace 'method_1884', 'setSelectionEnd'
$content = $content -replace 'method_43470', 'literal'

# Field mappings
$content = $content -replace 'field_22789', 'width'
$content = $content -replace 'field_22793', 'textRenderer'
$content = $content -replace 'field_1755', 'currentScreen'
$content = $content -replace 'field_1724', 'player'
$content = $content -replace 'field_1687', 'world'

# Import fixes
$content = $content -replace 'import net\.minecraft\.class_(\d+);', 'import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.Hand;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.option.KeyBinding;'

Set-Content $FilePath $content -Encoding UTF8
Write-Host "Fixed mappings in: $FilePath"
