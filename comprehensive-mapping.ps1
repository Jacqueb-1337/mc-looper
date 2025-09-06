# Comprehensive Mapping for Minecraft MC Looper Mod
# This script maps all obfuscated references to their proper names

param(
    [string]$SourceDir = "src\main\java\com\jacqueb\mclooper"
)

# Comprehensive mappings array
$mappings = @(
    # Classes
    @{ Old = "class_304"; New = "KeyBinding" },
    @{ Old = "class_1268"; New = "Hand" },
    @{ Old = "class_1657"; New = "PlayerEntity" },
    @{ Old = "class_2338"; New = "BlockPos" },
    @{ Old = "class_2350"; New = "Direction" },
    @{ Old = "class_243"; New = "Vec3d" },
    @{ Old = "class_3965"; New = "BlockHitResult" },
    @{ Old = "class_3966"; New = "EntityHitResult" },
    @{ Old = "class_465"; New = "HandledScreen" },
    @{ Old = "class_364"; New = "ClickableWidget" },
    @{ Old = "class_124"; New = "Formatting" },

    # Fields
    @{ Old = "field_1755"; New = "currentScreen" },
    @{ Old = "field_1724"; New = "player" },
    @{ Old = "field_1687"; New = "world" },
    @{ Old = "field_1761"; New = "interactionManager" },
    @{ Old = "field_1765"; New = "crosshairTarget" },
    @{ Old = "field_3944"; New = "networkHandler" },
    @{ Old = "field_7871"; New = "inventory" },
    @{ Old = "field_7763"; New = "syncId" },
    @{ Old = "field_7520"; New = "experienceLevel" },
    @{ Old = "field_7495"; New = "experienceProgress" },
    @{ Old = "field_7477"; New = "allowsFlying" },
    @{ Old = "field_3761"; New = "address" },
    @{ Old = "field_3752"; New = "name" },
    @{ Old = "field_1332"; New = "BLOCK" },
    @{ Old = "field_1331"; New = "ENTITY" },
    @{ Old = "field_5808"; New = "MAIN_HAND" },
    @{ Old = "field_11036"; New = "UP" },
    @{ Old = "field_22789"; New = "width" },
    @{ Old = "field_22793"; New = "textRenderer" },
    @{ Old = "field_22785"; New = "title" },
    @{ Old = "field_22787"; New = "client" },
    @{ Old = "field_22763"; New = "active" },
    @{ Old = "field_1060"; New = "GREEN" },
    @{ Old = "field_1061"; New = "RED" },
    @{ Old = "field_1054"; New = "YELLOW" },

    # Methods - Key Binding & Client
    @{ Old = "method_1436"; New = "wasPressed" },
    @{ Old = "method_1507"; New = "setScreen" },
    @{ Old = "method_1551"; New = "getInstance" },
    @{ Old = "method_1548"; New = "getSession" },
    @{ Old = "method_1558"; New = "getCurrentServerEntry" },

    # Methods - Text & Translation
    @{ Old = "method_43470"; New = "literal" },
    @{ Old = "method_27692"; New = "formatted" },
    @{ Old = "method_10852"; New = "append" },

    # Methods - Network & Communication  
    @{ Old = "method_45731"; New = "sendChatCommand" },
    @{ Old = "method_45729"; New = "sendChatMessage" },

    # Methods - Item Stack
    @{ Old = "method_7960"; New = "isEmpty" },
    @{ Old = "method_7964"; New = "getName" },
    @{ Old = "method_7985"; New = "hasNbt" },
    @{ Old = "method_7969"; New = "getNbt" },
    @{ Old = "method_7909"; New = "getItem" },
    @{ Old = "method_7947"; New = "getCount" },

    # Methods - NBT Operations
    @{ Old = "method_10545"; New = "contains" },
    @{ Old = "method_10562"; New = "getCompound" },
    @{ Old = "method_10554"; New = "getList" },
    @{ Old = "method_10608"; New = "getString" },

    # Methods - Inventory & Slots
    @{ Old = "method_31548"; New = "getInventory" },
    @{ Old = "method_5439"; New = "size" },
    @{ Old = "method_5438"; New = "getStack" },
    @{ Old = "method_34266"; New = "getIndex" },

    # Methods - Interaction & Clicks
    @{ Old = "method_2906"; New = "clickSlot" },
    @{ Old = "method_2910"; New = "attackBlock" },
    @{ Old = "method_2918"; New = "attackEntity" },
    @{ Old = "method_2919"; New = "interactItem" },
    @{ Old = "method_2896"; New = "interactBlock" },

    # Methods - Hit Results  
    @{ Old = "method_17783"; New = "getType" },
    @{ Old = "method_17777"; New = "getBlockPos" },
    @{ Old = "method_17780"; New = "getSide" },
    @{ Old = "method_17782"; New = "getEntity" },

    # Methods - Player Data
    @{ Old = "method_1676"; New = "getName" },
    @{ Old = "method_44717"; New = "getUuid" },
    @{ Old = "method_6032"; New = "getHealth" },
    @{ Old = "method_7344"; New = "getHungerManager" },
    @{ Old = "method_7586"; New = "getFoodLevel" },
    @{ Old = "method_31549"; New = "getAbilities" },
    @{ Old = "method_5864"; New = "getDisplayName" },

    # Methods - Position & Movement
    @{ Old = "method_23317"; New = "getX" },
    @{ Old = "method_23318"; New = "getY" },
    @{ Old = "method_23321"; New = "getZ" },

    # Methods - World & Time
    @{ Old = "method_8532"; New = "getTimeOfDay" },
    @{ Old = "method_8597"; New = "getRegistryKey" },
    @{ Old = "method_27983"; New = "getDimensionKey" },
    @{ Old = "method_29177"; New = "getValue" },

    # Methods - UI & Screens
    @{ Old = "method_25426"; New = "init" },
    @{ Old = "method_37067"; New = "clearChildren" },
    @{ Old = "method_37063"; New = "addDrawableChild" },
    @{ Old = "method_25420"; New = "renderBackground" },
    @{ Old = "method_25402"; New = "mouseClicked" },
    @{ Old = "method_25401"; New = "mouseScrolled" },
    @{ Old = "method_25421"; New = "shouldPause" },

    # Methods - Text Field
    @{ Old = "method_1880"; New = "setMaxLength" },
    @{ Old = "method_1852"; New = "setText" },
    @{ Old = "method_1882"; New = "getText" },
    @{ Old = "method_1875"; New = "setCursor" },
    @{ Old = "method_1884"; New = "setSelectionStart" },
    @{ Old = "method_47404"; New = "setPlaceholder" },

    # Methods - Button & Widget
    @{ Old = "method_48229"; New = "setPosition" },

    # Methods - Graphics & Rendering
    @{ Old = "method_27534"; New = "drawCenteredTextWithShadow" },
    @{ Old = "method_51439"; New = "drawTextWithShadow" },
    @{ Old = "method_25294"; New = "fill" },
    @{ Old = "method_49601"; New = "drawBorder" },

    # Methods - Player Messages
    @{ Old = "method_7353"; New = "sendMessage" }
)

Write-Host "Starting comprehensive mapping of obfuscated references..." -ForegroundColor Green
Write-Host "Found $($mappings.Count) mappings to apply" -ForegroundColor Yellow

$totalReplacements = 0

# Apply all mappings
foreach ($mapping in $mappings) {
    $oldRef = $mapping.Old
    $newRef = $mapping.New
    
    Write-Host "Mapping: $oldRef -> $newRef" -ForegroundColor Cyan
    
    # Find all Java files
    $javaFiles = Get-ChildItem -Path $SourceDir -Filter "*.java" -Recurse
    
    foreach ($file in $javaFiles) {
        $content = Get-Content $file.FullName -Raw
        $originalContent = $content
        
        # Replace obfuscated reference with proper name
        $content = $content -replace [regex]::Escape($oldRef), $newRef
        
        if ($content -ne $originalContent) {
            Set-Content -Path $file.FullName -Value $content -NoNewline
            $replacementCount = ([regex]::Matches($originalContent, [regex]::Escape($oldRef))).Count
            $totalReplacements += $replacementCount
            Write-Host "  Updated $($file.Name): $replacementCount replacements" -ForegroundColor Green
        }
    }
}

Write-Host "`nMapping complete!" -ForegroundColor Green
Write-Host "Total replacements made: $totalReplacements" -ForegroundColor Yellow
Write-Host "`nAll obfuscated references have been mapped to proper names." -ForegroundColor Green
